package com.narutoclans.addon.clan;

import com.narutoclans.addon.Config;
import com.narutoclans.addon.bridge.NarutotoBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/** Cla Namikaze: nasce com o Hiraishin no Jutsu e uma Kunai Hiraishin. */
public final class NamikazeClan {
    private static final ResourceLocation LEARN_HIRAISHIN = new ResourceLocation(NarutotoBridge.NARUTOTO, "learn_hiraishin");
    private static final UUID SPEED_ID = UUID.fromString("7c1b5b0e-5d0a-4d3c-9d6a-4e6a1f7a21b1");

    private NamikazeClan() {
    }

    public static void onJoin(ServerPlayer player) {
        // Fluxo oficial do mod base: concede a advancement, troca o icone de treino pelo pergaminho.
        boolean ok = NarutotoBridge.runProcedure("HiraishinGetProcedure", player);
        if (!ok || !ClanManager.hasAdvancement(player, LEARN_HIRAISHIN)) {
            ClanManager.award(player, LEARN_HIRAISHIN); // fallback
        }
        // Ja coloca o jutsu no livro de jutsus (se nao houver slot livre, o pergaminho resolve no livro).
        NarutotoBridge.bindJutsu(player, "jutsu.hiraishin1");

        giveKunai(player);
    }

    /** Entrega a Kunai Hiraishin do Narutoto. */
    public static void giveKunai(ServerPlayer player) {
        Item kunai = NarutotoBridge.item("hiraishin_kunai_weapon");
        if (kunai == null) {
            return;
        }
        ItemStack stack = new ItemStack(kunai, Config.NAMIKAZE_KUNAI_COUNT.get());
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    public static boolean hasKunai(ServerPlayer player) {
        Item kunai = NarutotoBridge.item("hiraishin_kunai_weapon");
        if (kunai == null) {
            return false;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(kunai)) {
                return true;
            }
        }
        return false;
    }

    /** Passiva: bonus de velocidade (Flash Amarelo). Chamado periodicamente. */
    public static void applyPassive(ServerPlayer player, boolean active) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        AttributeModifier existing = speed.getModifier(SPEED_ID);
        if (!active) {
            if (existing != null) {
                speed.removeModifier(SPEED_ID);
            }
            return;
        }
        double bonus = Config.NAMIKAZE_SPEED_BONUS.get();
        if (existing == null || existing.getAmount() != bonus) {
            if (existing != null) {
                speed.removeModifier(SPEED_ID);
            }
            speed.addTransientModifier(new AttributeModifier(SPEED_ID, "Namikaze flash",
                    bonus, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }
}
