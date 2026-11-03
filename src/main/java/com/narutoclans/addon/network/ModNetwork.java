package com.narutoclans.addon.network;

import com.narutoclans.addon.NarutoClansAddon;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NarutoClansAddon.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, AbilityPacket.class,
                AbilityPacket::encode, AbilityPacket::decode, AbilityPacket::handle);
    }

    public static void sendAbility(int abilityId) {
        CHANNEL.sendToServer(new AbilityPacket(abilityId));
    }
}
