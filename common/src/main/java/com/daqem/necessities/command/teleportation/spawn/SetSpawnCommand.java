package com.daqem.necessities.command.teleportation.spawn;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.level.storage.Position;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec3;

public class SetSpawnCommand implements Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setspawn")
            .requires(source -> source.hasPermission(2))
            .executes(context -> {
                if (context.getSource().getPlayer() != null) {
                    if (context.getSource().getLevel() instanceof NecessitiesServerLevel serverLevel) {
                        NecessitiesLevelData necessitiesLevelData = serverLevel.necessities$getLevelData();
                        Vec3 position = context.getSource().getPlayer().position();
                        var yaw = context.getSource().getPlayer().getYRot();
                        var pitch = context.getSource().getPlayer().getXRot();
                        necessitiesLevelData.necessities$setSpawnPosition(new Position(position.x, position.y, position.z, yaw, pitch));
                        context.getSource().getLevel().setDefaultSpawnPos(context.getSource().getPlayer().blockPosition(), yaw);
                        context.getSource().sendSuccess(() -> Necessities.prefixedTranslatable("commands.spawn.set"), true);
                    }
                    return 1;
                } else {
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                }
            }));
    }
}
