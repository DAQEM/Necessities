package com.daqem.necessities.mixin;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.exception.HomeLimitReachedException;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Home;
import com.daqem.necessities.model.Position;
import com.daqem.necessities.model.ServerPlayerData;
import com.daqem.necessities.model.TPARequest;
import com.daqem.necessities.utils.ChatFormatter;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
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

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow
    public abstract void sendSystemMessage(Component arg, boolean bl);

    @Shadow
    private boolean disconnected;

    @Shadow
    protected abstract boolean acceptsChatMessages();

    @Shadow public abstract boolean teleportTo(ServerLevel arg, double d, double e, double f, Set<Relative> set, float g, float h, boolean bl);

    @Shadow public abstract ServerLevel level();

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
    private boolean necessities$hasNecessitiesInstalled = false;

    @Unique
    private boolean necessities$isAFK = false;

    @Unique
    private Position necessities$AFKPosition = Position.ZERO;

    @Unique
    private @Nullable UUID necessities$lastMessageSender = null;

    @Unique
    private boolean necessities$hasGodMode = false;

    @Override
    public UUID necessities$getUUID() {
        return this.getUUID();
    }

    @Override
    public Component necessities$getName() {
        if (this.necessities$getNick() != null && !this.necessities$getNick().isEmpty()) {
            return ChatFormatter.format(this.necessities$getNick());
        }
        return Necessities.coloredLiteral(this.getGameProfile().getName());
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
        if (this.getServer() instanceof MinecraftServer server) {
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
    public void necessities$addHome(Home home) throws HomeLimitReachedException {
        Integer homesLimit = NecessitiesConfig.homesLimit.get();
        if (homesLimit > 0 && necessities$Homes.size() >= homesLimit) {
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
            if (request.isHere) {
                this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.here", request.sender.necessities$getName()), false);
                request.sender.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted.here.sender", this.necessities$getName()), false);
                this.necessities$teleport(request.sender.necessities$getPosition());
            } else {
                this.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.tpa.accepted", request.sender.necessities$getName()), false);
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
        if (this.getServer() instanceof MinecraftServer server) {
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

        if (this.getServer() instanceof MinecraftServer server) {
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

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof NecessitiesServerPlayer oldNecessitiesServerPlayer) {
            this.necessities$Homes = oldNecessitiesServerPlayer.necessities$getHomes().stream()
                    .collect(Collectors.toMap(home -> home.name, home -> home));
            this.necessities$LastPosition = oldNecessitiesServerPlayer.necessities$getLastPosition();
            this.necessities$acceptsTPARequests = oldNecessitiesServerPlayer.necessities$acceptsTPARequests();
            this.necessities$Nick = oldNecessitiesServerPlayer.necessities$getNick();
            this.necessities$hasGodMode = oldNecessitiesServerPlayer.necessities$hasGodMode();
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    public void addAdditionalSaveData(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.store("Necessities", ServerPlayerData.CODEC, new ServerPlayerData(
                this.necessities$getHomes(),
                this.necessities$getLastPosition(),
                this.necessities$acceptsTPARequests(),
                this.necessities$getNonNullNick(),
                this.necessities$hasGodMode()
        ));
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    public void readAdditionalSaveData(ValueInput valueInput, CallbackInfo ci) {
        valueInput.read("Necessities", ServerPlayerData.CODEC).ifPresent(data -> {
            this.necessities$Homes = data.homes().stream()
                    .collect(Collectors.toMap(home -> home.name, home -> home));
            this.necessities$LastPosition = data.lastPosition();
            this.necessities$acceptsTPARequests = data.acceptsTPARequests();
            if (data.nick() != null && !data.nick().isEmpty()) {
                this.necessities$Nick = data.nick();
            }
            this.necessities$hasGodMode = data.hasGodMode();
        });
    }

    @Inject(at = @At("TAIL"), method = "getTabListDisplayName()Lnet/minecraft/network/chat/Component;", cancellable = true)
    public void getTabListDisplayName(CallbackInfoReturnable<Component> cir) {
        if (this.necessities$hasNick()) {
            cir.setReturnValue(ChatFormatter.format(this.necessities$getNick()));
        }
    }

    @Inject(at = @At("HEAD"), method = "sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V")
    public void sendChatMessage(OutgoingChatMessage message, boolean bl, ChatType.Bound bound, CallbackInfo ci) {
        if (necessities$isAFK() && necessities$sendsMessageThemself(bound.chatType())) {
            necessities$setAFK(false);
        }

        if (this.acceptsChatMessages()) {
            if (bound.chatType().is(ChatType.MSG_COMMAND_INCOMING) || bound.chatType().is(ChatType.TEAM_MSG_COMMAND_INCOMING)) {
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
    public void isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (necessities$hasGodMode()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo ci) {
        if (this.necessities$isAFK() && !this.necessities$getPosition().equals(this.necessities$AFKPosition)) {
            this.necessities$setAFK(false);
        }
    }
}
