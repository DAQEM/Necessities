package com.daqem.necessities.mixin;

import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.level.storage.Position;
import com.daqem.necessities.level.storage.Warp;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements ServerLevelData, WorldData, NecessitiesLevelData {

    @Unique
    private static Position necessities$spawnPosition = Position.ZERO;

    @Unique
    private static List<Warp> necessities$Warps = new ArrayList<>();

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
        return necessities$Warps;
    }

    @Override
    public void necessities$addWarp(Warp warp) {
        necessities$Warps.add(warp);
    }

    @Override
    public Optional<Warp> necessities$getWarp(String name) {
        return necessities$Warps.stream().filter(warp -> warp.name.equals(name)).findFirst();
    }

    @Inject(method = "parse", at = @At("HEAD"))
    private static <T> void parse(Dynamic<T> dynamic, DataFixer dataFixer, int i, CompoundTag compoundTag, LevelSettings levelSettings, LevelVersion levelVersion, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        OptionalDynamic<T> necessities = dynamic.get("Necessities");

        necessities$spawnPosition = Position.deserialize(necessities.get("Spawn").orElseEmptyMap());
        necessities$Warps = necessities.get("Warps").asStream().map(dynamic1 -> {
            String name = dynamic1.get("Name").asString("");
            Position position = Position.deserialize(dynamic1.get("Position").orElseEmptyMap());
            return new Warp(name, position);
        }).collect(Collectors.toList());

    }

    @Inject(method = "setTagData", at = @At("HEAD"))
    private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        CompoundTag necessitiesTag = new CompoundTag();
        CompoundTag spawnTag = new CompoundTag();
        necessities$spawnPosition.serialize(spawnTag);
        necessitiesTag.put("Spawn", spawnTag);

        ListTag warpsTag = new ListTag();
        necessities$Warps.forEach(warp -> {
            CompoundTag warpTag = new CompoundTag();
            warpTag.putString("Name", warp.name);
            CompoundTag positionTag = new CompoundTag();
            warp.position.serialize(positionTag);
            warpTag.put("Position", positionTag);
            warpsTag.add(warpTag);
        });
        necessitiesTag.put("Warps", warpsTag);

        compoundTag.put("Necessities", necessitiesTag);
    }
}
