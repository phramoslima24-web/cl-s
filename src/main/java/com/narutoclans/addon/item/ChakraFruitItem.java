package com.narutoclans.addon.item;

import com.narutoclans.addon.NarutoClansAddon;
import com.narutoclans.addon.clan.OtsutsukiClan;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Fruto de Chakra da Arvore Divina: concede chakra maximo permanente, pontos e efeitos poderosos. */
public class ChakraFruitItem extends Item {
    public ChakraFruitItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            OtsutsukiClan.applyChakraFruit(player);
        }
        return result;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String base = "item." + NarutoClansAddon.MODID + ".chakra_fruit.tooltip";
        tooltip.add(Component.translatable(base + ".1").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable(base + ".2").withStyle(ChatFormatting.GRAY));
    }
}
