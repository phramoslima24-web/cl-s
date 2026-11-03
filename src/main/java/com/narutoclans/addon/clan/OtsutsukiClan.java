package com.narutoclans.addon.clan;

import com.narutoclans.addon.Config;
import com.narutoclans.addon.bridge.NarutotoBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Cla Otsutsuki: chakra quase ilimitado, Byakugan, Rinnegan, Tenseigan, Jogan, voo, adaptacao espacial,
 * absorcao de jutsus, Marca da Karma (renascimento) e Fruto de Chakra.
 */
public final class OtsutsukiClan {
    /** Modo de dojutsu ativo: 0 = desligado, 1 = Tenseigan, 2 = Jogan. */
    public static final String TAG_MODE = "nc_dojutsu";
    public static final String TAG_FLIGHT = "nc_flight_granted";
    public static final String TAG_KARMA_READY = "nc_karma_ready";

    public static final int MODE_OFF = 0;
    public static final int MODE_TENSEIGAN = 1;
    public static final int MODE_JOGAN = 2;

    private static final String[] NATURE_ADVANCEMENTS = {
            "nature_fire", "nature_water", "nature_earth", "nature_wind", "nature_lightning"
    };
    /** Pergaminhos do Narutoto entregues ao entrar no cla (espaco-tempo / gravidade / visao). */
    private static final String[] STARTER_SCROLLS = {
            "shinra_tensei_scroll", "bansho_tenin_scroll", "chibaku_tensei_scroll", "byakugan_telescope_scroll"
    };

    private OtsutsukiClan() {
    }

    // ------------------------------------------------------------------ entrada no cla

    public static void onJoin(ServerPlayer player) {
        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars != null) {
            double max = Config.OTSUTSUKI_CHAKRA_MAX.get();
            if (vars.getDouble("chakra_maximum") < max) {
                vars.setDouble("chakra_maximum", max);
            }
            vars.setDouble("chakra", vars.getDouble("chakra_maximum"));
            vars.sync();
        }

        // Dojutsus (mesma advancement que o Narutoto usa para liberar a habilidade).
        award(player, "learn_byakugan");
        award(player, "learn_rinnegan");
        // Dominio de todas as naturezas de chakra.
        for (String nature : NATURE_ADVANCEMENTS) {
            award(player, nature);
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
        updateFlight(player, active);
        if (!active) {
            player.getPersistentData().putInt(TAG_MODE, MODE_OFF);
            return;
        }

        if (Config.OTSUTSUKI_SPACE_ADAPTATION.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false, false));
            player.setAirSupply(player.getMaxAirSupply());
            player.setTicksFrozen(0);
        }

        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars == null) {
            return;
        }
        double max = vars.getDouble("chakra_maximum");
        double chakra = vars.getDouble("chakra");

        // Regeneracao de chakra.
        double regen = max * Config.OTSUTSUKI_REGEN_PERCENT_PER_SEC.get() / 100.0D;
        chakra = Math.min(max, chakra + regen);

        // Dojutsu ativo: consome chakra e aplica efeitos.
        CompoundTag data = player.getPersistentData();
        int mode = data.getInt(TAG_MODE);
        if (mode != MODE_OFF) {
            double drain = mode == MODE_TENSEIGAN ? Config.TENSEIGAN_DRAIN_PER_SEC.get() : Config.JOGAN_DRAIN_PER_SEC.get();
            if (chakra < drain) {
                data.putInt(TAG_MODE, MODE_OFF);
                player.displayClientMessage(Component.translatable("message.narutoto_clans.dojutsu_no_chakra")
                        .withStyle(ChatFormatting.RED), true);
            } else {
                chakra -= drain;
                if (mode == MODE_TENSEIGAN) {
                    applyTenseigan(player);
                } else {
                    applyJogan(player);
                }
            }
        }

        if (chakra != vars.getDouble("chakra")) {
            vars.setDouble("chakra", chakra);
            vars.sync();
        }
    }

    private static void updateFlight(ServerPlayer player, boolean active) {
        CompoundTag data = player.getPersistentData();
        Abilities abilities = player.getAbilities();
        boolean survival = !player.isCreative() && !player.isSpectator();
        boolean wantFlight = active && Config.OTSUTSUKI_FLIGHT.get() && survival;

        if (wantFlight) {
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                player.onUpdateAbilities();
            }
            data.putBoolean(TAG_FLIGHT, true);
        } else if (data.getBoolean(TAG_FLIGHT)) {
            data.putBoolean(TAG_FLIGHT, false);
            if (survival) {
                abilities.mayfly = false;
                abilities.flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    private static void applyTenseigan(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 2, true, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1, true, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1, true, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false, false));
    }

    private static void applyJogan(ServerPlayer player) {
        // Visao atraves de objetos: o contorno dos seres vivos e revelado atraves das paredes.
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 320, 0, true, false, false));
        double r = Config.JOGAN_RADIUS.get();
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(r), e -> e != player && e.isAlive());
        for (LivingEntity target : targets) {
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, true, false, false));
        }
    }

    // ------------------------------------------------------------------ danos: adaptacao e absorcao

    /** Danos ambientais ignorados pela adaptacao espacial. */
    public static boolean isEnvironmentalDamageImmune(DamageSource source) {
        return Config.OTSUTSUKI_SPACE_ADAPTATION.get()
                && (source.is(DamageTypes.FALL)
                || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.FREEZE)
                || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.FLY_INTO_WALL)
                || source.is(DamageTypes.CRAMMING)
                || source.is(DamageTypes.DRY_OUT));
    }

    /** O dano veio de um jutsu do Narutoto (tipo de dano ou entidade nao-viva do mod)? */
    public static boolean isNarutotoJutsu(DamageSource source) {
        ResourceLocation type = source.typeHolder().unwrapKey().map(k -> k.location()).orElse(null);
        if (type != null && NarutotoBridge.NARUTOTO.equals(type.getNamespace())) {
            // "nine_tails_cancel" e similares sao mecanicas internas do mod, nao ataques.
            return !type.getPath().contains("cancel");
        }
        Entity direct = source.getDirectEntity();
        if (direct != null && !(direct instanceof LivingEntity)) {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(direct.getType());
            return key != null && NarutotoBridge.NARUTOTO.equals(key.getNamespace());
        }
        return false;
    }

    /** Absorve o jutsu: o dano vira chakra e o atacante recebe o golpe de volta (multiplicado). */
    public static void absorb(ServerPlayer player, DamageSource source, float amount) {
        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars != null) {
            double max = vars.getDouble("chakra_maximum");
            double gain = amount * Config.ABSORB_CHAKRA_MULTIPLIER.get();
            vars.setDouble("chakra", Math.min(max, vars.getDouble("chakra") + gain));
            vars.sync();
        }
        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.0D, player.getZ(),
                    30, 0.5D, 0.8D, 0.5D, 0.6D);
        }
        player.displayClientMessage(Component.translatable("message.narutoto_clans.absorbed")
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);

        Entity attacker = source.getEntity();
        double reflect = Config.ABSORB_REFLECT_MULTIPLIER.get();
        if (reflect > 0.0D && attacker instanceof LivingEntity target && target != player) {
            target.hurt(player.damageSources().indirectMagic(player, player), (float) (amount * reflect));
        }
    }

    // ------------------------------------------------------------------ Marca da Karma

    /**
     * Tenta renascer em vez de morrer (compactacao da essencia em Karma).
     *
     * @return true se a morte foi evitada.
     */
    public static boolean tryKarma(ServerPlayer player, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false; // /kill e vazio nao sao evitados
        }
        long now = player.level().getGameTime();
        CompoundTag data = player.getPersistentData();
        if (now < data.getLong(TAG_KARMA_READY)) {
            return false;
        }
        data.putLong(TAG_KARMA_READY, now + Config.KARMA_COOLDOWN_SECONDS.get() * 20L);

        player.setHealth(player.getMaxHealth());
        player.removeAllEffects();
        player.clearFire();
        player.getFoodData().setFoodLevel(20);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 4));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 4));
        player.invulnerableTime = 60;

        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars != null) {
            vars.setDouble("chakra", vars.getDouble("chakra_maximum"));
            vars.sync();
        }

        if (player.level() instanceof ServerLevel level) {
            level.broadcastEntityEvent(player, (byte) 35); // animacao do Totem da Imortalidade
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1.0D, player.getZ(),
                    80, 0.4D, 0.9D, 0.4D, 0.4D);
        }
        player.displayClientMessage(Component.translatable("message.narutoto_clans.karma_revive")
                .withStyle(ChatFormatting.DARK_PURPLE), false);
        return true;
    }

    // ------------------------------------------------------------------ Fruto de Chakra

    public static void applyChakraFruit(ServerPlayer player) {
        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        if (vars != null) {
            vars.addDouble("chakra_maximum", Config.FRUIT_CHAKRA_MAX_BONUS.get());
            vars.setDouble("chakra", vars.getDouble("chakra_maximum"));
            vars.addDouble("attribute_point", Config.FRUIT_ATTRIBUTE_POINTS.get());
            vars.addDouble("jutsu_point", Config.FRUIT_JUTSU_POINTS.get());
            vars.sync();
        }
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 6000, 4));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0D, player.getZ(),
                    60, 0.5D, 1.0D, 0.5D, 0.15D);
        }
        player.displayClientMessage(Component.translatable("message.narutoto_clans.fruit_eaten")
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
    }
}
