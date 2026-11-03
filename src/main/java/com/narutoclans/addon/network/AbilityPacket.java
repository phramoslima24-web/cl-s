package com.narutoclans.addon.network;

import com.narutoclans.addon.ability.OtsutsukiAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Cliente -> servidor: pedido para usar uma habilidade (o servidor valida cla, chakra e recarga). */
public final class AbilityPacket {
    private final int abilityId;

    public AbilityPacket(int abilityId) {
        this.abilityId = abilityId;
    }

    public static void encode(AbilityPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.abilityId);
    }

    public static AbilityPacket decode(FriendlyByteBuf buffer) {
        return new AbilityPacket(buffer.readVarInt());
    }

    public static void handle(AbilityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                OtsutsukiAbilities.handle(player, packet.abilityId);
            }
        });
        context.setPacketHandled(true);
    }
}
