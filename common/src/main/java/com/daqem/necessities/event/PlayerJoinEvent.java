package com.daqem.necessities.event;

import com.daqem.necessities.networking.NecessitiesNetworking;
import com.daqem.necessities.networking.clientbound.ClientboundNecessitiesHandshakePacket;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;

public class PlayerJoinEvent {

    public static void registerEvent() {
        PlayerEvent.PLAYER_JOIN.register((player) -> {
            if (NetworkManager.canPlayerReceive(player, NecessitiesNetworking.CLIENTBOUND_NECESSITIES_HANDSHAKE)) {
                NetworkManager.sendToPlayer(player, new ClientboundNecessitiesHandshakePacket());
            }
        });
    }
}
