package com.daqem.necessities.level;

import com.daqem.necessities.level.storage.NecessitiesLevelData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public interface NecessitiesServerLevel {

    NecessitiesLevelData necessities$getLevelData();

    ResourceLocation necessities$getDimension();
}
