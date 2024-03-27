package com.daqem.necessities.level;

import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public interface NecessitiesServerPlayer {

    NecessitiesServerLevel necessities$getLevel();
    ServerLevel necessities$getLevel(ResourceLocation dimension);
    NecessitiesServerLevel necessities$getOverworld();

    NecessitiesLevelData necessities$getLevelData();
    Position necessities$getPosition();
    void necessities$teleport(Position position);


}
