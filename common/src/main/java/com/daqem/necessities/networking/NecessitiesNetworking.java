package com.daqem.necessities.networking;

import com.daqem.knot.Knot;
import com.daqem.necessities.networking.clientbound.ClientboundPingPacket;

public interface NecessitiesNetworking {

    static void init() {
        Knot.NETWORKING.registerClientbound(ClientboundPingPacket.TYPE, ClientboundPingPacket.CODEC, () -> (_, _) -> {
        });
    }
}
