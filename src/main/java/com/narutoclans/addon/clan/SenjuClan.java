package com.narutoclans.addon.clan;

import com.narutoclans.addon.Config;
import com.narutoclans.addon.bridge.NarutotoBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * Cla Senju ("cla das mil habilidades"): reservas gigantes de chakra, grande forca vital (vida extra e
 * regeneracao), Mokuton (Jukai Kotan, Mokujoheki, Mokusatsu Shibari, Sashiki) e Iryo Ninjutsu (Shosen Jutsu).
 */
public final class SenjuClan {
    private static final UUID HEALTH_ID = UUID.fromString("3f6a1d52-8c4e-4b7a-9a1e-5d2c7b9e4f10");

    /** Jutsus (chaves de traducao do Narutoto) colocados no livro de jutsus ao entrar no cla. */
    private static final String[] JUTSUS = {
            "jutsu.jukai_kotan", "jutsu.mokujoheki", "jutsu.mokusatsu_shibari", "jutsu.sashiki", "jutsu.shosen_jutsu"
    };
    /** Pergaminhos do Narutoto entregues (garantem que o jutsu aparece no livro mesmo sem slot livre). */
    private static final String[] STARTER_SCROLLS = {
            "jukai_kotan_scroll", "mokujoheki_scroll", "mokusatsu_shibari_scroll", "sashiki_scroll", "shosen_jutsu_scroll"
    };

    private SenjuClan() {
    }

    // ------------------------------------------------------------------ entrada no cla

    public static void onJoin(ServerPlayer player) {
        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars != null) {
            double max = Config.SENJU_CHAKRA_MAX.get();
            if (vars.getDouble("chakra_maximum") < max) {
                vars.setDouble("chakra_maximum", max);
            }
            vars.setDouble("chakra", vars.getDouble("chakra_maximum"));
            vars.sync();
        }

        // Mokuton (mesma advancement que o Narutoto usa para liberar a natureza Madeira) + Iryo Ninjutsu.
        award(player, "nature_wood");
        award(player, "learn_iryo_ninjutsu");

        if (Config.SENJU_FREE_JUTSUS.get()) {
            for (String jutsu : JUTSUS) {
                NarutotoBridge.bindJutsu(player, jutsu);
            }
        }
        for (String scroll : STARTER_SCROLLS) {
            Item item = NarutotoBridge.item(scroll);
            if (item != null) {
                give(player, new ItemStack(item));
            }
        }
    }

    private static void award(ServerPlayer player, String path) {
        ClanManager.award(player, new ResourceLocation(NarutotoBridge.NARUTOTO, path));
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    // ------------------------------------------------------------------ passivas (1x por segundo)

    public static void secondTick(ServerPlayer player, boolean active) {
        applyHealth(player, active);
        if (!active) {
            return;
        }

        // Forca vital: regeneracao constante.
        int level = Config.SENJU_REGENERATION_LEVEL.get();
        if (level > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, level - 1, true, false, false));
        }

        // Reservas de chakra: regeneracao por segundo, em % do maximo.
        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars == null) {
            return;
        }
        double max = vars.getDouble("chakra_maximum");
        double chakra = vars.getDouble("chakra");
        double regen = max * Config.SENJU_REGEN_PERCENT_PER_SEC.get() / 100.0D;
        double next = Math.min(max, chakra + regen);
        if (next != chakra) {
            vars.setDouble("chakra", next);
            vars.sync();
        }
    }

    private static void applyHealth(ServerPlayer player, boolean active) {
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health == null) {
            return;
        }
        AttributeModifier existing = health.getModifier(HEALTH_ID);
        if (!active) {
            if (existing != null) {
                health.removeModifier(HEALTH_ID);
                if (player.getHealth() > player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth());
                }
            }
            return;
        }
        double bonus = Config.SENJU_EXTRA_HEALTH.get();
        if (existing == null || existing.getAmount() != bonus) {
            if (existing != null) {
                health.removeModifier(HEALTH_ID);
            }
            health.addTransientModifier(new AttributeModifier(HEALTH_ID, "Senju vitality",
                    bonus, AttributeModifier.Operation.ADDITION));
        }
    }
}
