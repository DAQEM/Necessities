package com.daqem.necessities.command;

import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Position;

public interface NecessitiesCommandSourceStack {

    NecessitiesServerLevel necessities$getLevel();
    Position necessities$getPosition();

    NecessitiesLevelData necessities$getLevelData();

    NecessitiesServerLevel necessities$getOverworld();
}
