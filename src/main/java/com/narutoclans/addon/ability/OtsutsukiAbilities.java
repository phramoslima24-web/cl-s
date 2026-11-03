package com.narutoclans.addon.ability;

import com.narutoclans.addon.Config;
import com.narutoclans.addon.bridge.NarutotoBridge;
import com.narutoclans.addon.clan.Clan;
import com.narutoclans.addon.clan.ClanManager;
import com.narutoclans.addon.clan.OtsutsukiClan;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Habilidades ativas do cla Otsutsuki (acionadas por teclas, validadas no servidor). */
public final class OtsutsukiAbilities {
    public static final int DIMENSIONAL_SHIFT = 0;
    public static final int CYCLE_DOJUTSU = 1;

    private static final String TAG_SHIFT_READY = "nc_shift_ready";

    private OtsutsukiAbilities() {
    }

    public static void handle(ServerPlayer player, int abilityId) {
        if (!ClanManager.has(player, Clan.OTSUTSUKI)) {
            player.displayClientMessage(Component.translatable("message.narutoto_clans.not_otsutsuki")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }
        switch (abilityId) {
            case DIMENSIONAL_SHIFT -> dimensionalShift(player);
            case CYCLE_DOJUTSU -> cycleDojutsu(player);
            default -> {
            }
        }
    }

    /** Viagem Dimensional (estilo Yomotsu Hirasaka): atravessa o espaco ate onde o jogador olha. */
    private static void dimensionalShift(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        CompoundTag data = player.getPersistentData();
        long now = level.getGameTime();
        if (now < data.getLong(TAG_SHIFT_READY)) {
            player.displayClientMessage(Component.translatable("message.narutoto_clans.on_cooldown")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        NarutotoBridge.Vars vars = NarutotoBridge.vars(player);
        double cost = Config.SHIFT_CHAKRA_COST.get();
        if (vars != null && vars.getDouble("chakra") < cost) {
            player.displayClientMessage(Component.translatable("message.narutoto_clans.no_chakra")
                    .withStyle(ChatFormatting.RED), true);
            return;
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(Config.SHIFT_RANGE.get()));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, player));

        Vec3 eyeTarget = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation().subtract(look.scale(0.6D));
        Vec3 feet = eyeTarget.subtract(0.0D, player.getEyeHeight(), 0.0D);

        // Recua ate haver espaco livre para o corpo do jogador.
        for (int i = 0; i < 12; i++) {
            AABB box = player.getBoundingBox().move(feet.subtract(player.position()));
            if (level.noCollision(player, box)) {
                break;
            }
            feet = feet.subtract(look.scale(0.5D));
        }

        Vec3 from = player.position();
        level.sendParticles(ParticleTypes.PORTAL, from.x, from.y + 1.0D, from.z, 60, 0.3D, 0.7D, 0.3D, 0.4D);
        level.playSound(null, from.x, from.y, from.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.6F);

        player.connection.teleport(feet.x, feet.y, feet.z, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0F;

        level.sendParticles(ParticleTypes.REVERSE_PORTAL, feet.x, feet.y + 1.0D, feet.z, 60, 0.3D, 0.7D, 0.3D, 0.4D);
        level.playSound(null, feet.x, feet.y, feet.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);

        if (vars != null) {
            vars.setDouble("chakra", vars.getDouble("chakra") - cost);
            vars.sync();
        }
        data.putLong(TAG_SHIFT_READY, now + Config.SHIFT_COOLDOWN_SECONDS.get() * 20L);
    }

    /** Alterna: desligado -> Tenseigan -> Jogan -> desligado. */
    private static void cycleDojutsu(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        int next = (data.getInt(OtsutsukiClan.TAG_MODE) + 1) % 3;
        data.putInt(OtsutsukiClan.TAG_MODE, next);
        String key = switch (next) {
            case OtsutsukiClan.MODE_TENSEIGAN -> "message.narutoto_clans.mode_tenseigan";
            case OtsutsukiClan.MODE_JOGAN -> "message.narutoto_clans.mode_jogan";
            default -> "message.narutoto_clans.mode_off";
        };
        player.displayClientMessage(Component.translatable(key).withStyle(ChatFormatting.AQUA), true);
    }
}
