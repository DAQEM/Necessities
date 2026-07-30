package com.daqem.necessities.mixin;

import com.daqem.necessities.level.NecessitiesServerPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Unique
    private final ThreadLocal<ServerPlayer> necessities$joiningPlayer = new ThreadLocal<>();

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    public void placeNewPlayerHead(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        necessities$joiningPlayer.set(player);
    }

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    public void placeNewPlayerReturn(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        necessities$joiningPlayer.remove();
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;createPlayerInitializing(Ljava/util/Collection;)Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;"))
    public ClientboundPlayerInfoUpdatePacket redirectCreatePlayerInitializing(Collection<ServerPlayer> players) {
        ServerPlayer joining = necessities$joiningPlayer.get();
        if (joining != null && !joining.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            List<ServerPlayer> filtered = new ArrayList<>();
            for (ServerPlayer p : players) {
                if (p instanceof NecessitiesServerPlayer nsp && nsp.necessities$isVanished()) {
                    continue;
                }
                filtered.add(p);
            }
            return ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(filtered);
        }
        return ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(players);
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public void redirectBroadcastSystemMessage(PlayerList instance, Component message, boolean overlay, Connection connection, ServerPlayer player, CommonListenerCookie cookie) {
        if (player instanceof NecessitiesServerPlayer serverPlayer && serverPlayer.necessities$isVanished()) {
            for (ServerPlayer p : instance.getPlayers()) {
                if (p.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                    p.sendSystemMessage(message);
                }
            }
            return;
        }
        instance.broadcastSystemMessage(message, overlay);
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    public void redirectBroadcastAll(PlayerList instance, Packet<?> packet, Connection connection, ServerPlayer player, CommonListenerCookie cookie) {
        if (player instanceof NecessitiesServerPlayer serverPlayer && serverPlayer.necessities$isVanished()) {
            if (packet instanceof ClientboundPlayerInfoUpdatePacket) {
                for (ServerPlayer p : instance.getPlayers()) {
                    if (p.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                        p.connection.send(packet);
                    }
                }
                return;
            }
        }
        instance.broadcastAll(packet);
    }
}