package com.daqem.necessities.mixin;

import com.daqem.knot.Knot;
import com.daqem.necessities.Necessities;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.exception.HomeLimitReachedException;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.*;
import com.daqem.necessities.networking.clientbound.ClientboundPingPacket;
import com.daqem.necessities.utils.ChatFormatter;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements NecessitiesServerPlayer {

    @Unique
    private DelayedTeleport necessities$DelayedTeleport = null;

    @Unique
    private Map<String, Long> necessities$TeleportCooldowns = new HashMap<>();

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow
    public abstract void sendSystemMessage(Component message, boolean overlay);

    @Shadow
    private boolean disconnected;

    @Shadow
    protected abstract boolean acceptsChatMessages();

    @Shadow
    public abstract boolean teleportTo(@NonNull ServerLevel level, double x, double y, double z, @NonNull Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera);

    @Shadow
    public abstract @NonNull ServerLevel level();

    @Shadow
    public abstract CommandSourceStack createCommandSourceStack();

    @Unique
    private Map<String, Home> necessities$Homes = new HashMap<>();

    @Unique
    private Position necessities$LastPosition = Position.ZERO;

    @Unique
    private final List<TPARequest> necessities$TPARequests = new ArrayList<>();

    @Unique
    private boolean necessities$acceptsTPARequests = true;

    @Unique
    private String necessities$Nick = null;

    @Unique
    @Nullable
    private Boolean necessities$hasNecessitiesInstalled = false;

    @Unique
    private boolean necessities$isAFK = false;

    @Unique
    private Position necessities$AFKPosition = Position.ZERO;

    @Unique
    private @Nullable UUID necessities$lastMessageSender = null;

    @Unique
    private boolean necessities$hasGodMode = false;

    @Unique
    private boolean necessities$vanished = false;

    @Unique
    private long necessities$LastRTPTime = 0;

    @Unique
    private Map<Identifier, Long> necessities$KitCooldowns = new HashMap<>();

    @Override
    public UUID necessities$getUUID() {
        return this.getUUID();
    }

    @Override
    public Component necessities$getName() {
        if (this.necessities$getNick() != null && !this.necessities$getNick().isEmpty()) {
            return ChatFormatter.format(this.necessities$getNick());
        }
        return Necessities.coloredLiteral(this.getGameProfile().name());
    }

    @Override
    public boolean necessities$isOnline() {
        return !this.disconnected;
    }

    @Override
    public void necessities$sendSystemMessage(Component message, boolean actionBar) {
        if (necessities$hasNecessitiesInstalled()) {
            this.sendSystemMessage(message, actionBar);
        } else {
            this.sendSystemMessage(ChatFormatter.flattenToLiteral(message), actionBar);
        }
    }

    @Override
    public void necessities$broadcastSystemMessage(Component message, boolean actionBar) {
        if (this.level().getServer() instanceof MinecraftServer server) {
            server.sendSystemMessage(message);

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player instanceof NecessitiesServerPlayer necessitiesServerPlayer) {
                    necessitiesServerPlayer.necessities$sendSystemMessage(message, actionBar);
                }
            }
        }
    }

    @Override
    public void necessities$sendFailedSystemMessage(Component message) {
        this.necessities$sendSystemMessage(Component.empty().append(message).withStyle(ChatFormatting.RED), false);
    }

    @Override
    public boolean necessities$hasNecessitiesInstalled() {
        if (necessities$hasNecessitiesInstalled == null) {
            boolean installed = Knot.NETWORKING.canSendToPlayer((ServerPlayer) (Object) this, ClientboundPingPacket.TYPE);
            this.necessities$setNecessitiesInstalled(installed);
        }
        return necessities$hasNecessitiesInstalled;
    }

    @Override
    public void necessities$setNecessitiesInstalled(boolean installed) {
        necessities$hasNecessitiesInstalled = installed;
    }

    @Override
    public NecessitiesServerLevel necessities$getLevel() {
        return (NecessitiesServerLevel) this.level();
    }

    @Override
    public ServerLevel necessities$getLevel(Identifier dimension) {
        return this.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
    }

    @Override
    public NecessitiesServerLevel necessities$getOverworld() {
        return (NecessitiesServerLevel) this.level().getServer().getLevel(Level.OVERWORLD);
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
        this.teleportTo(serverLevel, position.x, position.y, position.z, Set.of(), position.yaw, position.pitch, true);
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
    public void necessities$scheduleTeleport(Position position, int delaySeconds, String cooldownType, int cooldownSeconds, java.util.function.Consumer<NecessitiesServerPlayer> onComplete) {
        if (delaySeconds <= 0) {
            if (cooldownSeconds > 0) {
                this.necessities$setTeleportCooldown(cooldownType, cooldownSeconds);
            }
            this.necessities$teleport(position);
            if (onComplete != null) {
                onComplete.accept(this);
            }
        } else {
            this.necessities$DelayedTeleport = new DelayedTeleport(position, necessities$getPosition(), System.currentTimeMillis() + (delaySeconds * 1000L), cooldownType, cooldownSeconds, onComplete);
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("teleport.delayed", delaySeconds), false);
        }
    }

    @Override
    public DelayedTeleport necessities$getDelayedTeleport() {
        return necessities$DelayedTeleport;
    }

    @Override
    public void necessities$cancelDelayedTeleport() {
        if (necessities$DelayedTeleport != null) {
            this.necessities$DelayedTeleport = null;
            this.necessities$sendSystemMessage(Necessities.prefixedFailureTranslatable("teleport.canceled"), false);
        }
    }

    @Override
    public long necessities$getTeleportCooldown(String type) {
        return necessities$TeleportCooldowns.getOrDefault(type, 0L);
    }

    @Override
    public void necessities$setTeleportCooldown(String type, int cooldownSeconds) {
        if (cooldownSeconds > 0) {
            necessities$TeleportCooldowns.put(type, System.currentTimeMillis() + (cooldownSeconds * 1000L));
        }
    }

    @Override
    public Map<String, Long> necessities$getTeleportCooldowns() {
        return necessities$TeleportCooldowns;
    }

    @Override
    public void necessities$setTeleportCooldowns(Map<String, Long> cooldowns) {
        this.necessities$TeleportCooldowns = cooldowns;
    }

    @Override
    public int necessities$getHomeLimit() {
        if (Necessities.API.hasPermission(this.createCommandSourceStack(), "home.limit.unlimited")) {
            return -1;
        }
        for (int i = 50; i >= 1; i--) {
            if (Necessities.API.hasPermission(this.createCommandSourceStack(), "home.limit." + i)) {
                return i;
            }
        }
        return NecessitiesConfig.homesLimit.get();
    }

    @Override
    public int necessities$getMaxNickLength() {
        if (Necessities.API.hasPermission(this.createCommandSourceStack(), "nick.length.unlimited")) {
            return 256;
        }
        for (int i = 32; i >= 1; i--) {
            if (Necessities.API.hasPermission(this.createCommandSourceStack(), "nick.length." + i)) {
                return i;
            }
        }
        return NecessitiesConfig.maxNickLength.get();
    }

    @Override
    public void necessities$addHome(Home home) throws HomeLimitReachedException {
        int homesLimit = necessities$getHomeLimit();
        if (homesLimit >= 0 && necessities$Homes.size() >= homesLimit) {
            if (!necessities$Homes.containsKey(home.name)) {
                throw new HomeLimitReachedException();
            }
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
            this.necessities$sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.receiver_offline", player.necessities$getName()), false);
            return;
        }

        if (!player.necessities$acceptsTPARequests()) {
            this.necessities$sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.receiver_requests_disabled", player.necessities$getName()), false);
            return;
        }

        TPARequest request = new TPARequest(this, player, System.currentTimeMillis(), isHere);
        player.necessities$receiveTPARequest(request);
        if (request.isHere) {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.sent.here", player.necessities$getName()), false);
        } else {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.sent", player.necessities$getName()), false);
        }
    }

    @Override
    public void necessities$receiveTPARequest(TPARequest request) {
        this.necessities$addTPARequest(request);
        if (request.isHere) {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.received.here", request.sender.necessities$getName()), false);
        } else {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.received", request.sender.necessities$getName()), false);
        }
    }

    @Override
    public void necessities$acceptTPARequest(TPARequest request) {
        if (!request.sender.necessities$isOnline()) {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.sender_offline", request.sender.necessities$getName()), false);
        } else {
            Integer delay = NecessitiesConfig.tpaTeleportDelay.get();
            if (request.isHere) {
                this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.here", request.sender.necessities$getName()), false);
                request.sender.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.here.sender", this.necessities$getName()), false);
                this.necessities$scheduleTeleport(request.sender.necessities$getPosition(), delay, null, 0, null);
            } else {
                this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted", request.sender.necessities$getName()), false);
                request.sender.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.sender", this.necessities$getName()), false);
                request.sender.necessities$scheduleTeleport(this.necessities$getPosition(), delay, null, 0, null);
            }
            this.necessities$removeTPARequest(request);
        }
    }

    @Override
    public void necessities$acceptTPARequest() {
        List<TPARequest> requests = this.necessities$getTPARequests();
        if (requests.isEmpty()) {
            this.necessities$sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.no_requests"), false);
        } else {
            TPARequest request = requests.getFirst();
            this.necessities$acceptTPARequest(request);
        }
    }

    @Override
    public void necessities$denyTPARequest(TPARequest request) {
        this.necessities$removeTPARequest(request);
        request.sender.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.denied", this.necessities$getName()), false);
        this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.denied.sender", request.sender.necessities$getName()), false);
    }

    @Override
    public void necessities$denyTPARequest() {
        List<TPARequest> requests = this.necessities$getTPARequests();
        if (requests.isEmpty()) {
            this.necessities$sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.tpa.no_requests"), false);
        } else {
            TPARequest request = requests.getFirst();
            this.necessities$denyTPARequest(request);
        }
    }

    @Override
    public void necessities$toggleTPARequests() {
        this.necessities$acceptsTPARequests = !this.necessities$acceptsTPARequests;
        if (this.necessities$acceptsTPARequests) {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.requests_enabled"), false);
        } else {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.requests_disabled"), false);
        }
    }

    @Override
    public boolean necessities$acceptsTPARequests() {
        return necessities$acceptsTPARequests;
    }

    @Override
    public String necessities$getNick() {
        return this.necessities$Nick;
    }

    @Override
    public String necessities$getNonNullNick() {
        return this.necessities$Nick == null ? "" : this.necessities$Nick;
    }

    @Override
    public boolean necessities$hasNick() {
        return this.necessities$Nick != null && !this.necessities$Nick.isEmpty();
    }

    @Override
    public void necessities$setNick(String nick) {
        this.necessities$Nick = nick;
        this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.nick.set", necessities$getName()), false);
        this.necessities$broadcastNickChange();
    }

    @Override
    public void necessities$removeNick() {
        this.necessities$Nick = "";
        this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.nick.removed"), false);
        this.necessities$broadcastNickChange();
    }

    @Override
    public void necessities$broadcastNickChange() {
        if (this.level().getServer() instanceof MinecraftServer server) {
            server.getPlayerList().broadcastAll(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of((ServerPlayer) (Object) this)));
        }
    }

    @Override
    public boolean necessities$isAFK() {
        return necessities$isAFK;
    }

    @Override
    public void necessities$setAFK(boolean afk) {
        if (afk && necessities$isAFK) {
            this.necessities$sendSystemMessage(Necessities.prefixedFailureTranslatable("commands.afk.already"), false);
            return;
        }
        necessities$isAFK = afk;
        necessities$AFKPosition = necessities$getPosition();
        if (afk) {
            this.necessities$broadcastSystemMessage(Necessities.prefixedTranslatable("commands.afk.set", necessities$getName()), false);
        } else {
            this.necessities$broadcastSystemMessage(Necessities.prefixedTranslatable("commands.afk.removed", necessities$getName()), false);
        }
    }

    @Override
    public Optional<NecessitiesServerPlayer> necessities$getLastMessageSender() {
        if (necessities$lastMessageSender == null) {
            return Optional.empty();
        }

        if (this.level().getServer() instanceof MinecraftServer server) {
            ServerPlayer player = server.getPlayerList().getPlayer(necessities$lastMessageSender);
            if (player instanceof NecessitiesServerPlayer necessitiesServerPlayer) {
                return Optional.of(necessitiesServerPlayer);
            }
        }
        return Optional.empty();
    }

    @Override
    public void necessities$setLastMessageSender(UUID senderUUID) {
        necessities$lastMessageSender = senderUUID;
    }

    @Override
    public boolean necessities$hasGodMode() {
        return necessities$hasGodMode;
    }

    @Override
    public void necessities$setGodMode(boolean godMode) {
        necessities$hasGodMode = godMode;
        if (godMode) {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.god.toggled.on"), false);
        } else {
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.god.toggled.off"), false);
        }
    }

    @Override
    public void necessities$toggleGodMode() {
        necessities$setGodMode(!necessities$hasGodMode);
    }

    @Override
    public boolean necessities$isVanished() {
        return necessities$vanished;
    }

    @Override
    public void necessities$setVanished(boolean vanished) {
        this.necessities$vanished = vanished;
        // Update invisibility metadata first so spawn packets are correct
        this.setInvisible(vanished);

        MinecraftServer server = this.level().getServer();

        if (vanished) {
            ClientboundPlayerInfoRemovePacket removePacket = new ClientboundPlayerInfoRemovePacket(List.of(this.getUUID()));
            ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(this.getId());
            Component leftMessage = Component.translatable("multiplayer.player.left", this.getDisplayName()).withStyle(ChatFormatting.YELLOW);

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player == (Object) this) continue;

                if (!player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                    player.connection.send(removePacket);
                    player.connection.send(removeEntitiesPacket);
                    player.sendSystemMessage(leftMessage);
                } else {
                    player.sendSystemMessage(Necessities.prefixedTranslatable("commands.vanish.notify.enabled", this.getDisplayName()).withStyle(ChatFormatting.GRAY));
                }
            }
            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.vanish.enabled"), false);
        } else {
            ClientboundPlayerInfoUpdatePacket addPacket = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of((ServerPlayer) (Object) this));
            Component joinMessage = Component.translatable("multiplayer.player.joined", this.getDisplayName()).withStyle(ChatFormatting.YELLOW);

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player == (Object) this) continue;

                if (!player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                    // Send tab list packet BEFORE spawning the entity
                    player.connection.send(addPacket);
                    player.sendSystemMessage(joinMessage);
                } else {
                    player.sendSystemMessage(Necessities.prefixedTranslatable("commands.vanish.notify.disabled", this.getDisplayName()).withStyle(ChatFormatting.GRAY));
                }
            }

            // Refresh entity tracking for everyone after confirming they have the tab info
            this.level().getChunkSource().removeEntity(this);
            this.level().getChunkSource().addEntity(this);

            this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.vanish.disabled"), false);
        }
    }

    @Override
    public long necessities$getLastRTPTime() {
        return necessities$LastRTPTime;
    }

    @Override
    public void necessities$setLastRTPTime(long time) {
        necessities$LastRTPTime = time;
    }

    @Override
    public LevelData.RespawnData necessities$getNewRespawnData() {
        return new LevelData.RespawnData(
                new GlobalPos(
                        this.level().dimension(),
                        this.blockPosition()
                ),
                this.getYRot(),
                this.getXRot()
        );
    }

    @Override
    public Map<Identifier, Long> necessities$getKitCooldowns() {
        return necessities$KitCooldowns;
    }

    @Override
    public void necessities$setKitCooldowns(Map<Identifier, Long> cooldowns) {
        this.necessities$KitCooldowns = cooldowns;
    }

    @Override
    public long necessities$getKitCooldown(Identifier kitId) {
        return necessities$KitCooldowns.getOrDefault(kitId, 0L);
    }

    @Override
    public void necessities$setKitCooldown(Identifier kitId, long timestamp) {
        necessities$KitCooldowns.put(kitId, timestamp);
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (oldPlayer instanceof NecessitiesServerPlayer oldNecessitiesServerPlayer) {
            this.necessities$Homes = oldNecessitiesServerPlayer.necessities$getHomes().stream()
                    .collect(Collectors.toMap(home -> home.name, home -> home));
            this.necessities$LastPosition = oldNecessitiesServerPlayer.necessities$getLastPosition();
            this.necessities$acceptsTPARequests = oldNecessitiesServerPlayer.necessities$acceptsTPARequests();
            this.necessities$Nick = oldNecessitiesServerPlayer.necessities$getNick();
            this.necessities$hasGodMode = oldNecessitiesServerPlayer.necessities$hasGodMode();
            this.necessities$vanished = oldNecessitiesServerPlayer.necessities$isVanished();
            this.necessities$LastRTPTime = oldNecessitiesServerPlayer.necessities$getLastRTPTime();
            this.necessities$KitCooldowns = new HashMap<>(oldNecessitiesServerPlayer.necessities$getKitCooldowns());
            this.necessities$TeleportCooldowns = new HashMap<>(oldNecessitiesServerPlayer.necessities$getTeleportCooldowns());
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.store("Necessities", ServerPlayerData.CODEC, new ServerPlayerData(
                this.necessities$getHomes(),
                this.necessities$getLastPosition(),
                this.necessities$acceptsTPARequests(),
                this.necessities$getNonNullNick(),
                this.necessities$hasGodMode(),
                this.necessities$isVanished(),
                this.necessities$getLastRTPTime(),
                this.necessities$getKitCooldowns(),
                this.necessities$getTeleportCooldowns()
        ));
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        input.read("Necessities", ServerPlayerData.CODEC).ifPresent(data -> {
            this.necessities$Homes = data.homes().stream()
                    .collect(Collectors.toMap(home -> home.name, home -> home));
            this.necessities$LastPosition = data.lastPosition();
            this.necessities$acceptsTPARequests = data.acceptsTPARequests();
            if (data.nick() != null && !data.nick().isEmpty()) {
                this.necessities$Nick = data.nick();
            }
            this.necessities$hasGodMode = data.hasGodMode();
            this.necessities$vanished = data.vanished();
            this.necessities$LastRTPTime = data.lastRTPTime();
            this.necessities$KitCooldowns = new HashMap<>(data.kitCooldowns());
            this.necessities$TeleportCooldowns = new HashMap<>(data.teleportCooldowns());
        });
    }

    @Inject(at = @At("TAIL"), method = "getTabListDisplayName()Lnet/minecraft/network/chat/Component;", cancellable = true)
    public void getTabListDisplayName(CallbackInfoReturnable<Component> cir) {
        if (this.necessities$hasNick()) {
            cir.setReturnValue(ChatFormatter.format(this.necessities$getNick()));
        }
    }

    @Inject(at = @At("HEAD"), method = "sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V")
    public void sendChatMessage(OutgoingChatMessage message, boolean filtered, ChatType.Bound chatType, CallbackInfo ci) {
        if (necessities$isAFK() && necessities$sendsMessageThemself(chatType.chatType())) {
            necessities$setAFK(false);
        }

        if (this.acceptsChatMessages()) {
            if (chatType.chatType().is(ChatType.MSG_COMMAND_INCOMING) || chatType.chatType().is(ChatType.TEAM_MSG_COMMAND_INCOMING)) {
                if (message instanceof OutgoingChatMessage.Player(
                        PlayerChatMessage playerMessage
                )) {
                    necessities$setLastMessageSender(playerMessage.link().sender());
                }
            }
        }
    }

    @Unique
    private boolean necessities$sendsMessageThemself(Holder<ChatType> type) {
        return type.is(ChatType.CHAT)
                || type.is(ChatType.MSG_COMMAND_OUTGOING)
                || type.is(ChatType.TEAM_MSG_COMMAND_OUTGOING)
                || type.is(ChatType.SAY_COMMAND)
                || type.is(ChatType.EMOTE_COMMAND);
    }

    @Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
    public void isInvulnerableTo(ServerLevel level, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (necessities$hasGodMode()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo ci) {
        if (this.necessities$isAFK() && !this.necessities$getPosition().equals(this.necessities$AFKPosition)) {
            this.necessities$setAFK(false);
        }
        if (this.necessities$isVanished()) {
            this.setInvisible(true); // Enforce invisibility
        }

        if (this.necessities$DelayedTeleport != null) {
            if (!this.necessities$getPosition().equalsIgnoreAngle(this.necessities$DelayedTeleport.startPos())) {
                this.necessities$cancelDelayedTeleport();
            } else if (System.currentTimeMillis() >= this.necessities$DelayedTeleport.executeAt()) {
                if (this.necessities$DelayedTeleport.cooldownSeconds() > 0) {
                    this.necessities$setTeleportCooldown(this.necessities$DelayedTeleport.cooldownType(), this.necessities$DelayedTeleport.cooldownSeconds());
                }
                this.necessities$teleport(this.necessities$DelayedTeleport.target());
                if (this.necessities$DelayedTeleport.onComplete() != null) {
                    this.necessities$DelayedTeleport.onComplete().accept(this);
                }
                this.necessities$DelayedTeleport = null;
            }
        }
    }
}