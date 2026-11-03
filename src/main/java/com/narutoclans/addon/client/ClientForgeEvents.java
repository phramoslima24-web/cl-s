package com.narutoclans.addon.client;

import com.narutoclans.addon.NarutoClansAddon;
import com.narutoclans.addon.ability.OtsutsukiAbilities;
import com.narutoclans.addon.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NarutoClansAddon.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientForgeEvents {
    private ClientForgeEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }
        while (ModKeys.DIMENSIONAL_SHIFT.consumeClick()) {
            ModNetwork.sendAbility(OtsutsukiAbilities.DIMENSIONAL_SHIFT);
        }
        while (ModKeys.CYCLE_DOJUTSU.consumeClick()) {
            ModNetwork.sendAbility(OtsutsukiAbilities.CYCLE_DOJUTSU);
        }
    }
}
