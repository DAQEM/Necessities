package com.daqem.necessities.command;

import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.Position;
import net.minecraft.server.level.ServerLevel;

public interface NecessitiesCommandSourceStack {

    NecessitiesServerLevel necessities$getLevel();
    Position necessities$getPosition();
}
