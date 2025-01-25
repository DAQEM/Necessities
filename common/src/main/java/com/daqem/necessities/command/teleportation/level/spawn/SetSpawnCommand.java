package com.daqem.necessities.command.teleportation.level.spawn;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Position;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.Vec3;

public class SetSpawnCommand implements Command {

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setspawn")
            .requires(source -> source.hasPermission(2))
            .executes(context -> {
                if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer){
                    serverPlayer.necessities$getLevelData().necessities$setSpawnPosition(serverPlayer.necessities$getPosition());
                    context.getSource().getLevel().setDefaultSpawnPos(context.getSource().getPlayer().blockPosition(), 0.0F);
                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.spawn.set"), false);
                    return 1;
                } else {
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                }
            }));
    }
}
