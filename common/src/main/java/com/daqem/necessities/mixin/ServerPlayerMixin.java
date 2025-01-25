package com.daqem.necessities.mixin;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.exception.HomeLimitReachedException;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Home;
import com.daqem.necessities.model.Position;
import com.daqem.necessities.model.TPARequest;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
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

    @Shadow
    public abstract void teleportTo(ServerLevel serverLevel, double d, double e, double f, float g, float h);

    @Shadow
    public abstract ServerLevel serverLevel();

    @Shadow
    public abstract void sendSystemMessage(Component arg, boolean bl);

    @Shadow private boolean disconnected;

    @Unique
    private Map<String, Home> necessities$Homes = new HashMap<>();

    @Unique
    private Position necessities$LastPosition = Position.ZERO;

    @Unique
    private final List<TPARequest> necessities$TPARequests = new ArrayList<>();

    @Unique
    private boolean necessities$acceptsTPARequests = true;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Override
    public UUID necessities$getUUID() {
        return this.getUUID();
    }

    @Override
    public Component necessities$getName() {
        return Necessities.coloredLiteral(this.getGameProfile().getName());
    }

    @Override
    public boolean necessities$isOnline() {
        return !this.disconnected;
    }

    @Override
    public void necessities$sendSystemMessage(Component message, boolean log) {
        this.sendSystemMessage(message, log);
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
        this.necessities$setLastPosition();
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

    @Override
    public Position necessities$getLastPosition() {
        return necessities$LastPosition;
    }

    @Override
    public void necessities$setLastPosition(Position position) {
        necessities$LastPosition = position;
    }

    @Override
    public void necessities$setLastPosition() {
        necessities$setLastPosition(necessities$getPosition());
    }

    @Override
    public boolean necessities$hasLastPosition() {
        return !necessities$LastPosition.equals(Position.ZERO);
    }

    @Override
    public List<TPARequest> necessities$getTPARequests() {
        necessities$TPARequests.removeIf(request -> !request.isPending());
        return necessities$TPARequests;
    }

    @Override
    public void necessities$addTPARequest(TPARequest request) {
        this.necessities$TPARequests.add(request);
    }

    @Override
    public void necessities$removeTPARequest(TPARequest request) {
        this.necessities$TPARequests.remove(request);
    }

    @Override
    public void necessities$sendTPARequest(NecessitiesServerPlayer player, boolean isHere) {
        if (!player.necessities$isOnline()) {
            this.sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.receiver_offline", player.necessities$getName()), false);
            return;
        }

        if (!player.necessities$acceptsTPARequests()) {
            this.sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.receiver_requests_disabled", player.necessities$getName()), false);
            return;
        }

        TPARequest request = new TPARequest(this, player, System.currentTimeMillis(), isHere);
        player.necessities$receiveTPARequest(request);
        if (request.isHere) {
            this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.sent.here", player.necessities$getName()), false);
        } else {
            this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.sent", player.necessities$getName()), false);
        }
    }

    @Override
    public void necessities$receiveTPARequest(TPARequest request) {
        this.necessities$addTPARequest(request);
        if (request.isHere) {
            this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.received.here", request.sender.necessities$getName()), false);
        } else {
            this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.received", request.sender.necessities$getName()), false);
        }
    }

    @Override
    public void necessities$acceptTPARequest(TPARequest request) {
        if (!request.sender.necessities$isOnline()) {
            this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.sender_offline", request.sender.necessities$getName()), false);
        } else {
            if (request.isHere) {
                this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.here", request.sender.necessities$getName()), false);
                request.sender.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.here.sender", this.necessities$getName()), false);
                this.necessities$teleport(request.sender.necessities$getPosition());
            } else {
                this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted", request.sender.necessities$getName()), false);
                request.sender.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.sender", this.necessities$getName()), false);
                request.sender.necessities$teleport(this.necessities$getPosition());
            }
            this.necessities$removeTPARequest(request);
        }
    }

    @Override
    public void necessities$acceptTPARequest() {
        List<TPARequest> requests = this.necessities$getTPARequests();
        if (requests.isEmpty()) {
            this.sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.no_requests"), false);
        } else {
            TPARequest request = requests.getFirst();
            this.necessities$acceptTPARequest(request);
        }
    }

    @Override
    public void necessities$denyTPARequest(TPARequest request) {
        this.necessities$removeTPARequest(request);
        request.sender.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.denied", this.necessities$getName()), false);
        this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.denied.sender", request.sender.necessities$getName()), false);
    }

    @Override
    public void necessities$denyTPARequest() {
        List<TPARequest> requests = this.necessities$getTPARequests();
        if (requests.isEmpty()) {
            this.sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.no_requests"), false);
        } else {
            TPARequest request = requests.getFirst();
            this.necessities$denyTPARequest(request);
        }
    }

    @Override
    public void necessities$toggleTPARequests() {
        this.necessities$acceptsTPARequests = !this.necessities$acceptsTPARequests;
        if (this.necessities$acceptsTPARequests) {
            this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.requests_enabled"), false);
        } else {
            this.sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.requests_disabled"), false);
        }
    }

    @Override
    public boolean necessities$acceptsTPARequests() {
        return necessities$acceptsTPARequests;
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof NecessitiesServerPlayer oldNecessitiesServerPlayer) {
            this.necessities$Homes = oldNecessitiesServerPlayer.necessities$getHomes().stream()
                    .collect(Collectors.toMap(home -> home.name, home -> home));
            this.necessities$LastPosition = oldNecessitiesServerPlayer.necessities$getLastPosition();
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag necessitiesTag = new CompoundTag();

        necessitiesTag.put("Warps", necessities$Homes.values().stream().map(Home::serialize)
                .collect(Collectors.toCollection(ListTag::new)));
        necessitiesTag.put("LastPosition", necessities$LastPosition.serialize());

        compoundTag.put("Necessities", necessitiesTag);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag necessitiesTag = compoundTag.getCompound("Necessities");

        this.necessities$Homes = necessitiesTag.getList("Warps", 10).stream()
                .map(tag -> Home.deserialize((CompoundTag) tag))
                .collect(Collectors.toMap(home -> home.name, home -> home));
        this.necessities$LastPosition = Position.deserialize(necessitiesTag.getCompound("LastPosition"));
    }
}
