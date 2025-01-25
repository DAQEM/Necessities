package com.daqem.necessities.networking;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.networking.clientbound.ClientboundNecessitiesHandshakePacket;
import com.daqem.necessities.networking.serverbound.ServerboundNecessitiesHandshakePacket;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface NecessitiesNetworking {

    CustomPacketPayload.Type<ClientboundNecessitiesHandshakePacket> CLIENTBOUND_NECESSITIES_HANDSHAKE = new CustomPacketPayload.Type<>(Necessities.getId("clientbound_necessities_handshake"));

    CustomPacketPayload.Type<ServerboundNecessitiesHandshakePacket> SERVERBOUND_NECESSITIES_HANDSHAKE = new CustomPacketPayload.Type<>(Necessities.getId("serverbound_necessities_handshake"));

    static void initClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CLIENTBOUND_NECESSITIES_HANDSHAKE, ClientboundNecessitiesHandshakePacket.STREAM_CODEC, ClientboundNecessitiesHandshakePacket::handleClientSide);
    }

    static void initCommon() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SERVERBOUND_NECESSITIES_HANDSHAKE, ServerboundNecessitiesHandshakePacket.STREAM_CODEC, ServerboundNecessitiesHandshakePacket::handleServerSide);
    }

    static void initServer() {
        NetworkManager.registerS2CPayloadType(CLIENTBOUND_NECESSITIES_HANDSHAKE, ClientboundNecessitiesHandshakePacket.STREAM_CODEC);
    }

    static void init() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> NecessitiesNetworking::initClient);
        EnvExecutor.runInEnv(Env.SERVER, () -> NecessitiesNetworking::initServer);
        initCommon();
    }
}
