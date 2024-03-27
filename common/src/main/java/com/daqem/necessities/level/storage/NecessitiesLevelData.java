package com.daqem.necessities.level.storage;

import com.daqem.necessities.model.Position;
import com.daqem.necessities.model.Warp;

import java.util.List;
import java.util.Optional;

public interface NecessitiesLevelData {

    Position necessities$getSpawnPosition();

    void necessities$setSpawnPosition(Position position);

    List<Warp> necessities$getWarps();

    void necessities$setWarps(List<Warp> warps);

    Optional<Warp> necessities$getWarp(String name);

    void necessities$addWarp(Warp warp);


}
