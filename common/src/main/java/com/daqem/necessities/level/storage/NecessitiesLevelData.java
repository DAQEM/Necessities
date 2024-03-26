package com.daqem.necessities.level.storage;

import java.util.List;
import java.util.Optional;

public interface NecessitiesLevelData {

    Position necessities$getSpawnPosition();

    void necessities$setSpawnPosition(Position position);

    List<Warp> necessities$getWarps();

    Optional<Warp> necessities$getWarp(String name);

    void necessities$addWarp(Warp warp);


}
