package com.daqem.necessities.command.teleportation.level.warp;

import java.util.ArrayList;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

public class WarpCommand implements Command {

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, "warp", literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.warp", 0))
                .then(Commands.argument("warp", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                return SharedSuggestionProvider.suggest(serverPlayer.necessities$getLevelData().necessities$getWarps().stream().map(warp -> warp.name), builder);
                            }
                            return SharedSuggestionProvider.suggest(new ArrayList<>(), builder);
                        })
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                String warpName = StringArgumentType.getString(context, "warp");
                                serverPlayer.necessities$getLevelData().necessities$getWarp(warpName).ifPresentOrElse(warp -> {
                                    Integer cooldown = NecessitiesConfig.warpCooldown.get();
                                    Integer delay = NecessitiesConfig.warpTeleportDelay.get();

                                    if (cooldown > 0) {
                                        long cooldownTime = serverPlayer.necessities$getTeleportCooldown("warp");
                                        if (System.currentTimeMillis() < cooldownTime) {
                                            long secondsLeft = (cooldownTime - System.currentTimeMillis()) / 1000;
                                            serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("teleport.cooldown", secondsLeft));
                                            return;
                                        }
                                    }

                                    serverPlayer.necessities$scheduleTeleport(warp.position, delay, "warp", cooldown, (player) -> {
                                        player.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.warp", Necessities.colored(warp.name)), false);
                                    });
                                }, () -> {
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.warp.not_found", Necessities.coloredFailure(warpName)));
                                });
                                return 1;
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        })));
    }
}
