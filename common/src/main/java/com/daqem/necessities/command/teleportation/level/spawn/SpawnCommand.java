package com.daqem.necessities.command.teleportation.level.spawn;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.model.Position;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class SpawnCommand implements Command {

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
         CommandManager.register(dispatcher, "spawn", literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.spawn", 0))
                .executes(context -> {
                    if (context.getSource().getPlayer() != null) {
                        if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                            Position spawnPos = serverPlayer.necessities$getLevelData().necessities$getSpawnPosition();
                            Integer cooldown = NecessitiesConfig.spawnCooldown.get();
                            Integer delay = NecessitiesConfig.spawnTeleportDelay.get();

                            if (cooldown > 0) {
                                long cooldownTime = serverPlayer.necessities$getTeleportCooldown("spawn");
                                if (System.currentTimeMillis() < cooldownTime) {
                                    long secondsLeft = (cooldownTime - System.currentTimeMillis()) / 1000;
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("teleport.cooldown", secondsLeft));
                                    return 0;
                                }
                            }

                            serverPlayer.necessities$scheduleTeleport(spawnPos, delay, "spawn", cooldown, (player) -> {
                                player.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.spawn"), false);
                            });
                            return 1;
                        }
                    } else {
                        context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    }
                    return 0;
                }));
    }
}
