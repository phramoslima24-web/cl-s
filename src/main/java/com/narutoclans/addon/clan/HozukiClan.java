package com.narutoclans.addon.clan;

import com.narutoclans.addon.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

/** Cla Hozuki: Suiton (Suiro, Suiryudan, Mizuranpa, Suidanha) e corpo de agua. */
public final class HozukiClan {
    private HozukiClan() {
    }

    public static void onJoin(ServerPlayer player) {
        ClanKit.join(player,
                new String[]{"nature_water"},
                new String[]{"jutsu.suiro.no.jutsu", "jutsu.suiryudan", "jutsu.mizuranpa", "jutsu.suidanha"},
                new String[]{"suiro_scroll", "suiryudan_scroll", "mizuranpa_scroll", "suidanha_scroll"});
    }

    public static void secondTick(ServerPlayer player, boolean active) {
        if (!active) {
            return;
        }
        // Corpo de agua: respira e nada muito rapido dentro d'agua.
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false, false));
        if (Config.HOZUKI_WATER_SPEED.get() && player.isInWaterOrBubble()) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 60, 1, true, false, false));
        }
    }
}
