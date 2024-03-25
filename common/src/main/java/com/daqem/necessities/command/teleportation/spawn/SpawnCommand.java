package com.daqem.necessities.command.teleportation.spawn;

import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.storage.SpawnPosition;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class SpawnCommand implements Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spawn")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    if (context.getSource().getPlayer() != null) {
                        ServerPlayer player = context.getSource().getPlayer();
                        if (player.level() instanceof NecessitiesServerLevel serverLevel) {
                            SpawnPosition spawnPos = serverLevel.necessities$getLevelData().necessities$getSpawnPosition();
                            player.teleportTo((ServerLevel) serverLevel, spawnPos.x, spawnPos.y, spawnPos.z, spawnPos.yaw, spawnPos.pitch);
                            return 1;
                        }
                    } else {
                        context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    }
                    return 0;
                }));
    }
}
