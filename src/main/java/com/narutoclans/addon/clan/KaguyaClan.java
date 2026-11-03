package com.narutoclans.addon.clan;

import com.narutoclans.addon.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

/** Cla Kaguya: Shikotsumyaku (esqueleto reforcado): armadura natural, resistencia a empurrao, vida e ataque; Doton de apoio. */
public final class KaguyaClan {
    private static final UUID ARMOR_ID = UUID.fromString("b2e4d6f8-2c3d-4e4f-9051-6b7c8d9e0f01");
    private static final UUID TOUGH_ID = UUID.fromString("b2e4d6f8-2c3d-4e4f-9051-6b7c8d9e0f02");
    private static final UUID KB_ID = UUID.fromString("b2e4d6f8-2c3d-4e4f-9051-6b7c8d9e0f03");
    private static final UUID HEALTH_ID = UUID.fromString("b2e4d6f8-2c3d-4e4f-9051-6b7c8d9e0f04");
    private static final UUID DAMAGE_ID = UUID.fromString("b2e4d6f8-2c3d-4e4f-9051-6b7c8d9e0f05");

    private KaguyaClan() {
    }

    public static void onJoin(ServerPlayer player) {
        ClanKit.join(player,
                new String[]{"nature_earth"},
                new String[]{"jutsu.doryuheki"},
                new String[]{"doryuheki_scroll"});
    }

    public static void secondTick(ServerPlayer player, boolean active) {
        AttributeModifier.Operation add = AttributeModifier.Operation.ADDITION;
        ClanKit.modifier(player, active, Attributes.ARMOR, ARMOR_ID, "Kaguya bone armor", Config.KAGUYA_ARMOR.get(), add);
        ClanKit.modifier(player, active, Attributes.ARMOR_TOUGHNESS, TOUGH_ID, "Kaguya bone toughness", Config.KAGUYA_TOUGHNESS.get(), add);
        ClanKit.modifier(player, active, Attributes.KNOCKBACK_RESISTANCE, KB_ID, "Kaguya bone weight", Config.KAGUYA_KNOCKBACK_RESISTANCE.get(), add);
        ClanKit.modifier(player, active, Attributes.MAX_HEALTH, HEALTH_ID, "Kaguya vitality", Config.KAGUYA_EXTRA_HEALTH.get(), add);
        ClanKit.modifier(player, active, Attributes.ATTACK_DAMAGE, DAMAGE_ID, "Kaguya bone blade", Config.KAGUYA_ATTACK_BONUS.get(), add);
        if (!active && player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
