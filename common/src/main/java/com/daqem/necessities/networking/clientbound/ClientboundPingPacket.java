package com.daqem.necessities.networking.clientbound;

import com.daqem.necessities.Necessities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundPingPacket implements CustomPacketPayload {

    public static final Type<ClientboundPingPacket> TYPE = new Type<>(Necessities.API.getId("ping"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPingPacket> CODEC =
            StreamCodec.unit(new ClientboundPingPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}