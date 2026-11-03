package com.narutoclans.addon.clan;

import com.narutoclans.addon.NarutoClansAddon;
import com.narutoclans.addon.bridge.NarutotoBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/** Entrada/saida de clas e consulta de pertencimento. */
public final class ClanManager {
    /** Valor de chakra maximo inicial do Narutoto (usado ao sair de um cla com chakra gigante). */
    private static final double BASE_CHAKRA_MAX = 2000.0D;

    private ClanManager() {
    }

    // ------------------------------------------------------------------ advancements

    public static boolean hasAdvancement(ServerPlayer player, ResourceLocation id) {
        Advancement adv = player.server.getAdvancements().getAdvancement(id);
        return adv != null && player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    public static boolean award(ServerPlayer player, ResourceLocation id) {
        Advancement adv = player.server.getAdvancements().getAdvancement(id);
        if (adv == null) {
            NarutoClansAddon.LOGGER.warn("Advancement '{}' nao encontrada.", id);
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        if (progress.isDone()) {
            return true;
        }
        List<String> remaining = new ArrayList<>();
        progress.getRemainingCriteria().forEach(remaining::add);
        for (String criterion : remaining) {
            player.getAdvancements().award(adv, criterion);
        }
        return true;
    }

    public static void revoke(ServerPlayer player, ResourceLocation id) {
        Advancement adv = player.server.getAdvancements().getAdvancement(id);
        if (adv == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        List<String> done = new ArrayList<>();
        progress.getCompletedCriteria().forEach(done::add);
        for (String criterion : done) {
            player.getAdvancements().revoke(adv, criterion);
        }
    }

    // ------------------------------------------------------------------ cla

    public static boolean has(ServerPlayer player, Clan clan) {
        return hasAdvancement(player, clan.advancement());
    }

    public static Clan current(ServerPlayer player) {
        for (Clan c : Clan.values()) {
            if (has(player, c)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Faz o jogador entrar em um cla.
     *
     * @param force ignora a verificacao "ja possui cla" (usado por comandos de operador).
     * @return true se o jogador entrou no cla.
     */
    public static boolean tryJoin(ServerPlayer player, Clan clan, boolean force) {
        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars == null) {
            player.displayClientMessage(Component.translatable("message.narutoto_clans.bridge_error")
                    .withStyle(ChatFormatting.RED), false);
            return false;
        }

        if (!force && vars.getBoolean("have_clan")) {
            player.displayClientMessage(Component.translatable("message.narutoto_clans.already_have_clan")
                    .withStyle(ChatFormatting.RED), true);
            return false;
        }

        if (force) {
            for (Clan c : Clan.values()) {
                revoke(player, c.advancement());
            }
        }

        // Mesmo fluxo do BecomeUzumakiProcedure do Narutoto.
        vars.setBoolean("have_clan", true);
        boolean firstTime = vars.getDouble("ninja") <= 0.0D;
        if (firstTime) {
            vars.setDouble("ninja", 1.0D);
        }
        vars.sync();

        award(player, clan.advancement());

        if (firstTime) {
            NarutotoBridge.runProcedure("GetScrollProcedure", player);
        }

        switch (clan) {
            case NAMIKAZE -> NamikazeClan.onJoin(player);
            case OTSUTSUKI -> OtsutsukiClan.onJoin(player);
        }

        player.displayClientMessage(Component.translatable("message.narutoto_clans.joined", clan.displayName())
                .withStyle(ChatFormatting.GOLD), false);
        return true;
    }

    /** Remove o jogador de um cla do addon (comando de operador). */
    public static void leave(ServerPlayer player) {
        Clan current = current(player);
        if (current == null) {
            return;
        }
        revoke(player, current.advancement());
        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars != null) {
            vars.setBoolean("have_clan", false);
            if (current == Clan.OTSUTSUKI) {
                vars.setDouble("chakra_maximum", BASE_CHAKRA_MAX);
                vars.setDouble("chakra", Math.min(vars.getDouble("chakra"), BASE_CHAKRA_MAX));
            }
            vars.sync();
        }
    }
}
