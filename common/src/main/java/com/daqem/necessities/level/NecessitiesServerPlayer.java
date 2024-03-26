package com.daqem.necessities.level;

import com.daqem.necessities.level.storage.Position;

public interface NecessitiesServerPlayer {

    NecessitiesServerLevel necessities$getLevel();
    Position necessities$getPosition();

    void necessities$teleport(Position position);
}
