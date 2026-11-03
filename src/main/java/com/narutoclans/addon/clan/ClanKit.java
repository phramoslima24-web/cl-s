package com.narutoclans.addon.clan;

import com.narutoclans.addon.bridge.NarutotoBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/** Ferramentas compartilhadas pelos clas simples: kit de entrada e bonus de atributo. */
public final class ClanKit {
    private ClanKit() {
    }

    /** Concede advancements do Narutoto, coloca jutsus no livro e entrega pergaminhos. */
    public static void join(ServerPlayer player, String[] advancements, String[] jutsus, String[] scrolls) {
        for (String adv : advancements) {
            ClanManager.award(player, new ResourceLocation(NarutotoBridge.NARUTOTO, adv));
        }
        for (String jutsu : jutsus) {
            NarutotoBridge.bindJutsu(player, jutsu);
        }
        for (String scroll : scrolls) {
            Item item = NarutotoBridge.item(scroll);
            if (item != null) {
                ItemStack stack = new ItemStack(item);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
        }
    }

    /** Liga/desliga um bonus de atributo (chamado 1x por segundo). */
    public static void modifier(ServerPlayer player, boolean active, Attribute attribute, UUID id, String name,
                                double amount, AttributeModifier.Operation op) {
        AttributeInstance inst = player.getAttribute(attribute);
        if (inst == null) {
            return;
        }
        AttributeModifier existing = inst.getModifier(id);
        if (!active || amount == 0.0D) {
            if (existing != null) {
                inst.removeModifier(id);
            }
            return;
        }
        if (existing == null || existing.getAmount() != amount) {
            if (existing != null) {
                inst.removeModifier(id);
            }
            inst.addTransientModifier(new AttributeModifier(id, name, amount, op));
        }
    }
}
