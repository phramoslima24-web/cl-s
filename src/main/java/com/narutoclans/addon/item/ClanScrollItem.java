package com.narutoclans.addon.item;

import com.narutoclans.addon.NarutoClansAddon;
import com.narutoclans.addon.clan.Clan;
import com.narutoclans.addon.clan.ClanManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Pergaminho de cla: clique direito para entrar no cla (apenas quem ainda nao tem cla). */
public class ClanScrollItem extends Item {
    private final Clan clan;

    public ClanScrollItem(Clan clan, Properties properties) {
        super(properties);
        this.clan = clan;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        if (ClanManager.tryJoin(serverPlayer, clan, false)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String base = "item." + NarutoClansAddon.MODID + "." + clan.id + "_clan_scroll.tooltip";
        tooltip.add(Component.translatable(base + ".1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(base + ".2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item." + NarutoClansAddon.MODID + ".clan_scroll.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
