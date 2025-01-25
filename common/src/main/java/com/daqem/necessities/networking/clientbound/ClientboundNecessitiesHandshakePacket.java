package com.daqem.necessities.networking.clientbound;

import com.daqem.necessities.networking.NecessitiesNetworking;
import com.daqem.necessities.networking.serverbound.ServerboundNecessitiesHandshakePacket;
import com.google.common.graph.Network;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class ClientboundNecessitiesHandshakePacket implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundNecessitiesHandshakePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ClientboundNecessitiesHandshakePacket decode(RegistryFriendlyByteBuf buf) {
            return new ClientboundNecessitiesHandshakePacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ClientboundNecessitiesHandshakePacket packet) {
        }
    };

    public ClientboundNecessitiesHandshakePacket() {
    }

    public ClientboundNecessitiesHandshakePacket(RegistryFriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return NecessitiesNetworking.CLIENTBOUND_NECESSITIES_HANDSHAKE;
    }

    public static void handleClientSide(ClientboundNecessitiesHandshakePacket packet, NetworkManager.PacketContext context) {
        NetworkManager.sendToServer(new ServerboundNecessitiesHandshakePacket());
    }
}
