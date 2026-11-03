package com.narutoclans.addon.event;

import com.narutoclans.addon.Config;
import com.narutoclans.addon.NarutoClansAddon;
import com.narutoclans.addon.clan.Clan;
import com.narutoclans.addon.clan.ClanManager;
import com.narutoclans.addon.clan.NamikazeClan;
import com.narutoclans.addon.clan.OtsutsukiClan;
import com.narutoclans.addon.command.ClanCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Eventos de servidor/comuns: passivas dos clas, absorcao, Karma, kunai no respawn e comandos. */
@Mod.EventBusSubscriber(modid = NarutoClansAddon.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonEvents {
    private CommonEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player) || player.tickCount % 20 != 0) {
            return;
        }
        NamikazeClan.applyPassive(player, ClanManager.has(player, Clan.NAMIKAZE));
        OtsutsukiClan.secondTick(player, ClanManager.has(player, Clan.OTSUTSUKI));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !ClanManager.has(player, Clan.OTSUTSUKI)) {
            return;
        }
        DamageSource source = event.getSource();
        if (OtsutsukiClan.isEnvironmentalDamageImmune(source)) {
            event.setCanceled(true);
            return;
        }
        if (Config.ABSORB_JUTSU.get() && OtsutsukiClan.isNarutotoJutsu(source)) {
            event.setCanceled(true);
            OtsutsukiClan.absorb(player, source, event.getAmount());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !ClanManager.has(player, Clan.OTSUTSUKI)) {
            return;
        }
        if (OtsutsukiClan.tryKarma(player, event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (Config.NAMIKAZE_KUNAI_ON_RESPAWN.get()
                && ClanManager.has(player, Clan.NAMIKAZE)
                && !NamikazeClan.hasKunai(player)) {
            NamikazeClan.giveKunai(player);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ClanCommand.register(event.getDispatcher());
    }
}
