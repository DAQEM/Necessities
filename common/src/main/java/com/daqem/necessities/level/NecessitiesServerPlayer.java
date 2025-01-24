package com.daqem.necessities.level;

import com.daqem.necessities.exception.HomeLimitReachedException;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Home;
import com.daqem.necessities.model.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.Optional;

public interface NecessitiesServerPlayer {

    NecessitiesServerLevel necessities$getLevel();
    ServerLevel necessities$getLevel(ResourceLocation dimension);
    NecessitiesServerLevel necessities$getOverworld();

    NecessitiesLevelData necessities$getLevelData();
    Position necessities$getPosition();
    void necessities$teleport(Position position);

    List<Home> necessities$getHomes();

    void necessities$setHomes(List<Home> homes);

    Optional<Home> necessities$getHome(String name);

    void necessities$addHome(Home home) throws HomeLimitReachedException;

    void necessities$removeHome(String name);
}
