package com.daqem.necessities.command.player;

import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AFKCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("afk")
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.afk", 0))
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        serverPlayer.necessities$setAFK(true);
                        return 1;
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                }));
    }
}
