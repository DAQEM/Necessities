package com.daqem.necessities.networking.serverbound;

import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.networking.NecessitiesNetworking;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class ServerboundNecessitiesHandshakePacket implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundNecessitiesHandshakePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ServerboundNecessitiesHandshakePacket decode(RegistryFriendlyByteBuf buf) {
            return new ServerboundNecessitiesHandshakePacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ServerboundNecessitiesHandshakePacket packet) {
        }
    };

    public ServerboundNecessitiesHandshakePacket() {
    }

    public ServerboundNecessitiesHandshakePacket(RegistryFriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return NecessitiesNetworking.SERVERBOUND_NECESSITIES_HANDSHAKE;
    }

    public static void handleServerSide(ServerboundNecessitiesHandshakePacket packet, NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
            serverPlayer.necessities$setNecessitiesInstalled(true);
        }
    }
}
