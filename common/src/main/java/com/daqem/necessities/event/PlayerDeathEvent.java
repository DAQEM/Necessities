package com.daqem.necessities.event;

import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;

public class PlayerDeathEvent {

    public static void registerEvent() {
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity instanceof NecessitiesServerPlayer serverPlayer && NecessitiesConfig.allowBackOnDeath.get()) {
                serverPlayer.necessities$setLastPosition();
            }
            return EventResult.pass();
        });
    }
}