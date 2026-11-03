package com.narutoclans.addon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.narutoclans.addon.clan.Clan;
import com.narutoclans.addon.clan.ClanManager;
import com.narutoclans.addon.clan.NamikazeClan;
import com.narutoclans.addon.clan.OtsutsukiClan;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * /narutoclans info                      - mostra seu cla
 * /narutoclans kunai                     - (Namikaze) recupera a Kunai Hiraishin
 * /narutoclans set <jogador> <cla>       - (op) coloca o jogador no cla, mesmo que ja tenha outro
 * /narutoclans remove <jogador>          - (op) remove o jogador do cla do addon
 * /narutoclans fruit <jogador>           - (op) aplica os efeitos de um Fruto de Chakra
 */
public final class ClanCommand {
    private ClanCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("narutoclans");

        root.then(Commands.literal("info").executes(ctx -> info(ctx.getSource())));
        root.then(Commands.literal("kunai").executes(ctx -> kunai(ctx.getSource())));

        LiteralArgumentBuilder<CommandSourceStack> set = Commands.literal("set")
                .requires(src -> src.hasPermission(2));
        // /narutoclans set <jogador> namikaze|otsutsuki|senju
        var playerArg = Commands.argument("player", EntityArgument.player());
        for (Clan clan : Clan.values()) {
            playerArg.then(Commands.literal(clan.id).executes(ctx -> set(ctx.getSource(),
                    EntityArgument.getPlayer(ctx, "player"), clan)));
        }
        set.then(playerArg);
        root.then(set);

        root.then(Commands.literal("remove").requires(src -> src.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> remove(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        root.then(Commands.literal("fruit").requires(src -> src.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> fruit(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))));

        dispatcher.register(root);
    }

    private static int info(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Clan clan = ClanManager.current(player);
        Component message = clan == null
                ? Component.translatable("command.narutoto_clans.info.none")
                : Component.translatable("command.narutoto_clans.info.clan", clan.displayName());
        source.sendSuccess(() -> message, false);
        return 1;
    }

    private static int kunai(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!ClanManager.has(player, Clan.NAMIKAZE)) {
            source.sendFailure(Component.translatable("command.narutoto_clans.kunai.not_namikaze"));
            return 0;
        }
        NamikazeClan.giveKunai(player);
        source.sendSuccess(() -> Component.translatable("command.narutoto_clans.kunai.given"), false);
        return 1;
    }

    private static int set(CommandSourceStack source, ServerPlayer target, Clan clan) {
        if (ClanManager.tryJoin(target, clan, true)) {
            source.sendSuccess(() -> Component.translatable("command.narutoto_clans.set.ok",
                    target.getDisplayName(), clan.displayName()), true);
            return 1;
        }
        source.sendFailure(Component.translatable("command.narutoto_clans.set.fail", target.getDisplayName()));
        return 0;
    }

    private static int remove(CommandSourceStack source, ServerPlayer target) {
        if (ClanManager.current(target) == null) {
            source.sendFailure(Component.translatable("command.narutoto_clans.remove.none", target.getDisplayName()));
            return 0;
        }
        ClanManager.leave(target);
        source.sendSuccess(() -> Component.translatable("command.narutoto_clans.remove.ok",
                target.getDisplayName()), true);
        return 1;
    }

    private static int fruit(CommandSourceStack source, ServerPlayer target) {
        OtsutsukiClan.applyChakraFruit(target);
        source.sendSuccess(() -> Component.translatable("command.narutoto_clans.fruit.ok",
                target.getDisplayName()), true);
        return 1;
    }
}
