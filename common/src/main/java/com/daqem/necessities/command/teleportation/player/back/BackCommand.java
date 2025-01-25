package com.daqem.necessities.command.teleportation.player.back;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
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
                            serverPlayer.necessities$teleport(serverPlayer.necessities$getLastPosition());
                            serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.back"), false);
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
