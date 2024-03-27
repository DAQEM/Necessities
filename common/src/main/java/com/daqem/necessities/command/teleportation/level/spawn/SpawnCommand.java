package com.daqem.necessities.command.teleportation.level.spawn;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.model.Position;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SpawnCommand implements Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spawn")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    if (context.getSource().getPlayer() != null) {
                        if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                            Position spawnPos = serverPlayer.necessities$getLevelData().necessities$getSpawnPosition();
                            serverPlayer.necessities$teleport(spawnPos);
                            context.getSource().sendSuccess(() -> Necessities.prefixedTranslatable("commands.spawn"), true);
                            return 1;
                        }
                    } else {
                        context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    }
                    return 0;
                }));
    }
}
