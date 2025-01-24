package com.daqem.necessities.mixin;

import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.exception.HomeLimitReachedException;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Home;
import com.daqem.necessities.model.Position;
import com.daqem.necessities.model.Warp;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements NecessitiesServerPlayer {

    @Shadow public abstract void teleportTo(ServerLevel serverLevel, double d, double e, double f, float g, float h);

    @Shadow public abstract ServerLevel serverLevel();

    @Unique
    private Map<String, Home> necessities$Homes = new HashMap<>();

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Override
    public NecessitiesServerLevel necessities$getLevel() {
        return (NecessitiesServerLevel) this.serverLevel();
    }

    @Override
    public ServerLevel necessities$getLevel(ResourceLocation dimension) {
        if (this.getServer() == null) return (ServerLevel) necessities$getOverworld();
        return this.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
    }

    @Override
    public NecessitiesServerLevel necessities$getOverworld() {
        if (this.getServer() == null) return necessities$getLevel();
        return (NecessitiesServerLevel) this.getServer().getLevel(Level.OVERWORLD);
    }

    @Override
    public NecessitiesLevelData necessities$getLevelData() {
        return necessities$getOverworld().necessities$getLevelData();
    }

    @Override
    public Position necessities$getPosition() {
        Vec3 vec3 = this.position();
        return new Position(vec3.x, vec3.y, vec3.z, this.getYRot(), this.getXRot(), necessities$getLevel().necessities$getDimension());
    }

    @Override
    public void necessities$teleport(Position position) {
        ServerLevel serverLevel = necessities$getLevel(position.dimension);
        this.teleportTo(serverLevel, position.x, position.y, position.z, position.yaw, position.pitch);
    }

    @Override
    public List<Home> necessities$getHomes() {
        return new ArrayList<>(necessities$Homes.values());
    }

    @Override
    public Optional<Home> necessities$getHome(String name) {
        return necessities$Homes.containsKey(name) ? Optional.of(necessities$Homes.get(name)) : Optional.empty();
    }

    @Override
    public void necessities$addHome(Home home) throws HomeLimitReachedException {
        Integer homesLimit = NecessitiesConfig.homesLimit.get();
        if (homesLimit > 0 && necessities$Homes.size() >= homesLimit) {
            throw new HomeLimitReachedException();
        }
        necessities$Homes.put(home.name, home);
    }

    @Override
    public void necessities$removeHome(String name) {
        necessities$Homes.remove(name);
    }

    @Override
    public void necessities$setHomes(List<Home> homes) {
        necessities$Homes = homes.stream().collect(Collectors.toMap(home -> home.name, home -> home));
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof NecessitiesServerPlayer oldNecessitiesServerPlayer) {
            this.necessities$Homes = oldNecessitiesServerPlayer.necessities$getHomes().stream()
                    .collect(Collectors.toMap(home -> home.name, home -> home));
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag necessitiesTag = new CompoundTag();

        necessitiesTag.put("Warps", necessities$Homes.values().stream().map(Home::serialize)
                .collect(Collectors.toCollection(ListTag::new)));

        compoundTag.put("Necessities", necessitiesTag);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag necessitiesTag = compoundTag.getCompound("Necessities");

        this.necessities$Homes = necessitiesTag.getList("Warps", 10).stream()
                .map(tag -> Home.deserialize((CompoundTag) tag))
                .collect(Collectors.toMap(home -> home.name, home -> home));

    }
}
