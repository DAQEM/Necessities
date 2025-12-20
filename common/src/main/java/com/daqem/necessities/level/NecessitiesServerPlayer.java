package com.daqem.necessities.level;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.daqem.necessities.exception.HomeLimitReachedException;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.DelayedTeleport;
import com.daqem.necessities.model.Home;
import com.daqem.necessities.model.Position;
import com.daqem.necessities.model.TPARequest;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelData;

public interface NecessitiesServerPlayer {

    UUID necessities$getUUID();
    Component necessities$getName();
    boolean necessities$isOnline();
    void necessities$sendSystemMessage(Component message, boolean actionBar);
    void necessities$broadcastSystemMessage(Component message, boolean actionBar);
    void necessities$sendFailedSystemMessage(Component message);

    boolean necessities$hasNecessitiesInstalled();
    void necessities$setNecessitiesInstalled(boolean installed);

    NecessitiesServerLevel necessities$getLevel();
    ServerLevel necessities$getLevel(Identifier dimension);
    NecessitiesServerLevel necessities$getOverworld();

    NecessitiesLevelData necessities$getLevelData();
    Position necessities$getPosition();
    void necessities$teleport(Position position);
    void necessities$scheduleTeleport(Position position, int delaySeconds, String cooldownType, int cooldownSeconds, java.util.function.Consumer<NecessitiesServerPlayer> onComplete);
    DelayedTeleport necessities$getDelayedTeleport();
    void necessities$cancelDelayedTeleport();

    long necessities$getTeleportCooldown(String type);
    void necessities$setTeleportCooldown(String type, int cooldownSeconds);
    Map<String, Long> necessities$getTeleportCooldowns();
    void necessities$setTeleportCooldowns(Map<String, Long> cooldowns);

    List<Home> necessities$getHomes();
    void necessities$setHomes(List<Home> homes);
    Optional<Home> necessities$getHome(String name);
    void necessities$addHome(Home home) throws HomeLimitReachedException;
    void necessities$removeHome(String name);

    Position necessities$getLastPosition();
    void necessities$setLastPosition(Position position);
    void necessities$setLastPosition();
    boolean necessities$hasLastPosition();

    List<TPARequest> necessities$getTPARequests();
    void necessities$addTPARequest(TPARequest request);
    void necessities$removeTPARequest(TPARequest request);
    void necessities$sendTPARequest(NecessitiesServerPlayer player, boolean isHere);
    void necessities$receiveTPARequest(TPARequest request);
    void necessities$acceptTPARequest(TPARequest request);
    void necessities$acceptTPARequest();
    void necessities$denyTPARequest(TPARequest request);
    void necessities$denyTPARequest();
    void necessities$toggleTPARequests();
    boolean necessities$acceptsTPARequests();

    String necessities$getNick();
    String necessities$getNonNullNick();
    boolean necessities$hasNick();
    void necessities$setNick(String nick);
    void necessities$removeNick();
    void necessities$broadcastNickChange();

    long necessities$getLastRTPTime();
    void necessities$setLastRTPTime(long time);

    boolean necessities$isAFK();
    void necessities$setAFK(boolean afk);

    Optional<NecessitiesServerPlayer> necessities$getLastMessageSender();
    void necessities$setLastMessageSender(UUID senderUUID);

    boolean necessities$hasGodMode();
    void necessities$setGodMode(boolean godMode);
    void necessities$toggleGodMode();

    boolean necessities$isVanished();
    void necessities$setVanished(boolean vanished);

    LevelData.RespawnData necessities$getNewRespawnData();

    Map<Identifier, Long> necessities$getKitCooldowns();
    void necessities$setKitCooldowns(Map<Identifier, Long> cooldowns);
    long necessities$getKitCooldown(Identifier kitId);
    void necessities$setKitCooldown(Identifier kitId, long timestamp);

    int necessities$getHomeLimit();
    int necessities$getMaxNickLength();
}