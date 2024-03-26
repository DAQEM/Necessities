package com.daqem.necessities.level.storage;

import java.util.List;

public interface NecessitiesLevelData {

    Position necessities$getSpawnPosition();

    void necessities$setSpawnPosition(Position position);

    List<Warp> necessities$getWarps();

    Warp necessities$getWarp(String name);

    void necessities$addWarp(Warp warp);


}
