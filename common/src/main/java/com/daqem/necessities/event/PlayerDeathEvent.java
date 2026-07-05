package com.daqem.necessities.event;

import com.daqem.knot.events.EventResult;
import com.daqem.knot.events.common.entity.EntityEvent;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;

public class PlayerDeathEvent {

    public static void registerEvent() {
        EntityEvent.PLAYER_DEATH.register((player, source) -> {
            if (player instanceof NecessitiesServerPlayer serverPlayer && NecessitiesConfig.allowBackOnDeath.get()) {
                serverPlayer.necessities$setLastPosition();
            }
            return EventResult.PASS;
        });
    }
}