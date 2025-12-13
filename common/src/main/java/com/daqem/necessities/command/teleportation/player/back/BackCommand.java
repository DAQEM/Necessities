package com.daqem.necessities.command.teleportation.player.back;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BackCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("back")
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        if (serverPlayer.necessities$hasLastPosition()) {
                            Integer cooldown = NecessitiesConfig.backCooldown.get();
                            Integer delay = NecessitiesConfig.backTeleportDelay.get();

                            if (cooldown > 0) {
                                long cooldownTime = serverPlayer.necessities$getTeleportCooldown("back");
                                if (System.currentTimeMillis() < cooldownTime) {
                                    long secondsLeft = (cooldownTime - System.currentTimeMillis()) / 1000;
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("teleport.cooldown", secondsLeft));
                                    return 0;
                                }
                            }

                            serverPlayer.necessities$scheduleTeleport(serverPlayer.necessities$getLastPosition(), delay, "back", cooldown, (player) -> {
                                player.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.back"), false);
                            });
                            return 1;
                        } else {
                            serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.back.no_last_position"));
                            return 0;
                        }
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                }));
    }
}
