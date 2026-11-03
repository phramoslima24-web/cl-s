package com.narutoclans.addon.clan;

import com.narutoclans.addon.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

/** Cla Hatake: Raiton (Chidori, Raikyu, Raiju Tsuiga), velocidade e ataque de lamina de chakra. */
public final class HatakeClan {
    private static final UUID SPEED_ID = UUID.fromString("a1d3c5e7-1b2c-4d3e-8f40-5a6b7c8d9e01");
    private static final UUID DAMAGE_ID = UUID.fromString("a1d3c5e7-1b2c-4d3e-8f40-5a6b7c8d9e02");

    private HatakeClan() {
    }

    public static void onJoin(ServerPlayer player) {
        ClanKit.join(player,
                new String[]{"nature_lightning"},
                new String[]{"jutsu.chidori", "jutsu.raikyu", "jutsu.raiju_tsuiga"},
                new String[]{"chidori_scroll", "raikyu_scroll", "raiju_tsuiga_scroll"});
    }

    public static void secondTick(ServerPlayer player, boolean active) {
        ClanKit.modifier(player, active, Attributes.MOVEMENT_SPEED, SPEED_ID, "Hatake speed",
                Config.HATAKE_SPEED_BONUS.get(), AttributeModifier.Operation.MULTIPLY_BASE);
        ClanKit.modifier(player, active, Attributes.ATTACK_DAMAGE, DAMAGE_ID, "Hatake blade",
                Config.HATAKE_ATTACK_BONUS.get(), AttributeModifier.Operation.ADDITION);
    }
}
