package com.daqem.necessities.command.player;

import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class DelNickCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
         CommandManager.register(dispatcher, "delnick", literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.delnick", 0))
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        serverPlayer.necessities$removeNick();
                        return 1;
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                })
                .then(Commands.argument("target", EntityArgument.player())
                        .requires(source -> NecessitiesPermissions.check(source, "necessities.command.delnick.others", 2))
                        .executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            if (target instanceof NecessitiesServerPlayer serverPlayer) {
                                serverPlayer.necessities$removeNick();
                                return 1;
                            }
                            return 0;
                        })));
    }
}
