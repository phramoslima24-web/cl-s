package com.narutoclans.addon;

import net.minecraftforge.common.ForgeConfigSpec;

/** Configuracao COMMON (arquivo config/narutoto_clans-common.toml). */
public final class Config {
    public static final ForgeConfigSpec SPEC;

    // ---- Namikaze ----
    public static final ForgeConfigSpec.IntValue NAMIKAZE_KUNAI_COUNT;
    public static final ForgeConfigSpec.BooleanValue NAMIKAZE_KUNAI_ON_RESPAWN;
    public static final ForgeConfigSpec.DoubleValue NAMIKAZE_SPEED_BONUS;

    // ---- Otsutsuki ----
    public static final ForgeConfigSpec.IntValue OTSUTSUKI_CHAKRA_MAX;
    public static final ForgeConfigSpec.DoubleValue OTSUTSUKI_REGEN_PERCENT_PER_SEC;
    public static final ForgeConfigSpec.BooleanValue OTSUTSUKI_FLIGHT;
    public static final ForgeConfigSpec.BooleanValue OTSUTSUKI_SPACE_ADAPTATION;
    public static final ForgeConfigSpec.BooleanValue ABSORB_JUTSU;
    public static final ForgeConfigSpec.DoubleValue ABSORB_CHAKRA_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue ABSORB_REFLECT_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue KARMA_COOLDOWN_SECONDS;
    public static final ForgeConfigSpec.IntValue SHIFT_RANGE;
    public static final ForgeConfigSpec.IntValue SHIFT_CHAKRA_COST;
    public static final ForgeConfigSpec.IntValue SHIFT_COOLDOWN_SECONDS;
    public static final ForgeConfigSpec.IntValue TENSEIGAN_DRAIN_PER_SEC;
    public static final ForgeConfigSpec.IntValue JOGAN_DRAIN_PER_SEC;
    public static final ForgeConfigSpec.IntValue JOGAN_RADIUS;

    // ---- Fruto de Chakra ----
    public static final ForgeConfigSpec.IntValue FRUIT_CHAKRA_MAX_BONUS;
    public static final ForgeConfigSpec.IntValue FRUIT_ATTRIBUTE_POINTS;
    public static final ForgeConfigSpec.IntValue FRUIT_JUTSU_POINTS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("namikaze");
        NAMIKAZE_KUNAI_COUNT = b.comment("Quantidade de Kunais Hiraishin entregues ao entrar no cla (e ao renascer).")
                .defineInRange("kunaiCount", 1, 1, 64);
        NAMIKAZE_KUNAI_ON_RESPAWN = b.comment("Entrega Kunai Hiraishin sempre que o jogador Namikaze renasce.")
                .define("kunaiOnRespawn", true);
        NAMIKAZE_SPEED_BONUS = b.comment("Bonus de velocidade de movimento (0.15 = +15%).")
                .defineInRange("speedBonus", 0.15D, 0.0D, 2.0D);
        b.pop();

        b.push("otsutsuki");
        OTSUTSUKI_CHAKRA_MAX = b.comment("Chakra maximo concedido ao entrar no cla (quase ilimitado).")
                .defineInRange("chakraMaximum", 1_000_000, 1_000, 2_000_000_000);
        OTSUTSUKI_REGEN_PERCENT_PER_SEC = b.comment("Regeneracao de chakra por segundo, em % do maximo.")
                .defineInRange("chakraRegenPercentPerSecond", 2.0D, 0.0D, 100.0D);
        OTSUTSUKI_FLIGHT = b.comment("Permite voar livremente (modo survival).")
                .define("flight", true);
        OTSUTSUKI_SPACE_ADAPTATION = b.comment("Adaptacao espacial: sem afogamento, sem dano de queda/congelamento/asfixia em blocos, resistencia a fogo.")
                .define("spaceAdaptation", true);
        ABSORB_JUTSU = b.comment("Absorve jutsus do Narutoto, convertendo o dano em chakra e devolvendo o golpe.")
                .define("absorbJutsu", true);
        ABSORB_CHAKRA_MULTIPLIER = b.comment("Chakra recuperado = dano absorvido x este valor.")
                .defineInRange("absorbChakraMultiplier", 50.0D, 0.0D, 100000.0D);
        ABSORB_REFLECT_MULTIPLIER = b.comment("Dano devolvido ao atacante = dano absorvido x este valor (2.0 = o dobro).")
                .defineInRange("absorbReflectMultiplier", 2.0D, 0.0D, 100.0D);
        KARMA_COOLDOWN_SECONDS = b.comment("Recarga da Marca da Karma (renascimento ao morrer).")
                .defineInRange("karmaCooldownSeconds", 600, 0, 86400);
        SHIFT_RANGE = b.comment("Alcance da Viagem Dimensional (Yomotsu Hirasaka), em blocos.")
                .defineInRange("dimensionalShiftRange", 96, 4, 512);
        SHIFT_CHAKRA_COST = b.comment("Custo de chakra da Viagem Dimensional.")
                .defineInRange("dimensionalShiftChakraCost", 2000, 0, 2_000_000_000);
        SHIFT_COOLDOWN_SECONDS = b.comment("Recarga da Viagem Dimensional.")
                .defineInRange("dimensionalShiftCooldownSeconds", 3, 0, 3600);
        TENSEIGAN_DRAIN_PER_SEC = b.comment("Consumo de chakra por segundo do Tenseigan.")
                .defineInRange("tenseiganDrainPerSecond", 500, 0, 2_000_000_000);
        JOGAN_DRAIN_PER_SEC = b.comment("Consumo de chakra por segundo do Jogan.")
                .defineInRange("joganDrainPerSecond", 100, 0, 2_000_000_000);
        JOGAN_RADIUS = b.comment("Raio (blocos) em que o Jogan revela seres vivos atraves de paredes.")
                .defineInRange("joganRadius", 48, 4, 256);
        b.pop();

        b.push("chakraFruit");
        FRUIT_CHAKRA_MAX_BONUS = b.comment("Chakra maximo permanente concedido por Fruto de Chakra.")
                .defineInRange("chakraMaximumBonus", 50_000, 0, 2_000_000_000);
        FRUIT_ATTRIBUTE_POINTS = b.comment("Pontos de atributo concedidos por Fruto.")
                .defineInRange("attributePoints", 5, 0, 1000);
        FRUIT_JUTSU_POINTS = b.comment("Pontos de jutsu concedidos por Fruto.")
                .defineInRange("jutsuPoints", 3, 0, 1000);
        b.pop();

        SPEC = b.build();
    }

    private Config() {
    }
}
