package com.daqem.necessities.mixin;

import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Position;
import com.daqem.necessities.model.Warp;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements ServerLevelData, WorldData, NecessitiesLevelData {

    @Unique
    private Position necessities$spawnPosition = Position.ZERO;

    @Unique
    private Map<String, Warp> necessities$Warps = new HashMap<>();

    @Override
    public Position necessities$getSpawnPosition() {
        return necessities$spawnPosition;
    }

    @Override
    public void necessities$setSpawnPosition(Position position) {
        necessities$spawnPosition = position;
    }

    @Override
    public List<Warp> necessities$getWarps() {
        return new ArrayList<>(necessities$Warps.values());
    }

    @Override
    public void necessities$setWarps(List<Warp> warps) {
        necessities$Warps = warps.stream().collect(Collectors.toMap(warp -> warp.name, warp -> warp));
    }

    @Override
    public void necessities$addWarp(Warp warp) {
        necessities$Warps.put(warp.name, warp);
    }

    @Override
    public Optional<Warp> necessities$getWarp(String name) {
        return necessities$Warps.containsKey(name) ? Optional.of(necessities$Warps.get(name)) : Optional.empty();
    }

    // Only fired when creating a new world.
    @Inject(method = "<init>(Lnet/minecraft/world/level/LevelSettings;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/storage/PrimaryLevelData$SpecialWorldProperty;Lcom/mojang/serialization/Lifecycle;)V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        necessities$spawnPosition = Position.ZERO;
        necessities$Warps = new HashMap<>();
    }

    @Inject(method = "parse", at = @At("RETURN"))
    private static <T> void parse(Dynamic<T> dynamic, DataFixer dataFixer, int i, CompoundTag compoundTag, LevelSettings levelSettings, LevelVersion levelVersion, @SuppressWarnings("deprecation") PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        OptionalDynamic<T> necessities = dynamic.get("Necessities");

        Position necessities$spawnPosition = Position.deserialize(necessities.get("Spawn").orElseEmptyMap());
        List<Warp> necessities$Warps = necessities.get("Warps").asStream().map(Warp::deserialize)
                .collect(Collectors.toList());

        if (cir.getReturnValue() instanceof NecessitiesLevelData data) {
            data.necessities$setSpawnPosition(necessities$spawnPosition);
            data.necessities$setWarps(necessities$Warps);
        }
    }

    @Inject(method = "setTagData", at = @At("HEAD"))
    private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        CompoundTag necessitiesTag = new CompoundTag();

        necessitiesTag.put("Spawn", necessities$spawnPosition.serialize());
        necessitiesTag.put("Warps", necessities$Warps.values().stream().map(Warp::serialize)
                .collect(Collectors.toCollection(ListTag::new)));

        compoundTag.put("Necessities", necessitiesTag);
    }
}
