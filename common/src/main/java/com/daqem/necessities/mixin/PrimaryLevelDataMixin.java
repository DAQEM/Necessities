package com.daqem.necessities.mixin;

import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.level.storage.SpawnPosition;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements ServerLevelData, WorldData, NecessitiesLevelData {

    @Unique private static SpawnPosition necessities$spawnPosition;

    @Override
    public SpawnPosition necessities$getSpawnPosition() {
        return necessities$spawnPosition;
    }

    @Override
    public void necessities$setSpawnPosition(SpawnPosition spawnPosition) {
        necessities$spawnPosition = spawnPosition;
    }

    @Inject(method = "parse", at = @At("HEAD"))
    private static <T> void parse(Dynamic<T> dynamic, DataFixer dataFixer, int i, CompoundTag compoundTag, LevelSettings levelSettings, LevelVersion levelVersion, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        OptionalDynamic<T> necessitiesTag = dynamic.get("Necessities");

        double spawnX = necessitiesTag.get("SpawnX").asDouble(0);
        double spawnY = necessitiesTag.get("SpawnY").asDouble(64);
        double spawnZ = necessitiesTag.get("SpawnZ").asDouble(0);
        float spawnYaw = necessitiesTag.get("SpawnYaw").asFloat(0);
        float spawnPitch = necessitiesTag.get("SpawnPitch").asFloat(0);

        necessities$spawnPosition = new SpawnPosition(spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
    }

    @Inject(method = "setTagData", at = @At("HEAD"))
    private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        CompoundTag necessitiesTag = new CompoundTag();

        necessitiesTag.putDouble("SpawnX", necessities$spawnPosition.x);
        necessitiesTag.putDouble("SpawnY", necessities$spawnPosition.y);
        necessitiesTag.putDouble("SpawnZ", necessities$spawnPosition.z);
        necessitiesTag.putFloat("SpawnYaw", necessities$spawnPosition.yaw);
        necessitiesTag.putFloat("SpawnPitch", necessities$spawnPosition.pitch);

        compoundTag.put("Necessities", necessitiesTag);
    }
}
