package com.daqem.necessities.command.teleportation.player.home;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.exception.HomeLimitReachedException;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.model.Home;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;

public class SetHomeCommand implements Command {

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sethome")
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                String name = StringArgumentType.getString(context, "name");
                                Home home = new Home(name, serverPlayer.necessities$getPosition());
                                try {
                                    serverPlayer.necessities$addHome(home);
                                } catch (HomeLimitReachedException e) {
                                    context.getSource().sendFailure(Necessities.prefixedFailureTranslatable("commands.home.limit"));
                                    return 0;
                                }
                                context.getSource().sendSuccess(() -> Necessities.prefixedTranslatable("commands.home.set", Necessities.colored(name)), true);
                                return 1;
                            } else {
                                context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                                return 0;
                            }
                        }))
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        if (serverPlayer.necessities$getHomes().isEmpty() || serverPlayer.necessities$getHomes().size() == 1) {
                            Home home = new Home("home", serverPlayer.necessities$getPosition());
                            try {
                                serverPlayer.necessities$addHome(home);
                            } catch (HomeLimitReachedException e) {
                                context.getSource().sendFailure(Necessities.prefixedFailureTranslatable("commands.home.limit"));
                                return 0;
                            }
                            context.getSource().sendSuccess(() -> Necessities.prefixedTranslatable("commands.home.set", Necessities.colored("home")), true);
                            return 1;
                        } else if (serverPlayer.necessities$getHomes().size() == 1) {
                            Home home = new Home("home", serverPlayer.necessities$getPosition());
                            serverPlayer.necessities$setHomes(List.of(home));
                            context.getSource().sendSuccess(() -> Necessities.prefixedTranslatable("commands.home.set", Necessities.colored("home")), true);
                            return 1;
                        } else {
                            context.getSource().sendFailure(Necessities.prefixedFailureTranslatable("commands.home.multiple_homes"));
                            return 0;
                        }
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                }));
    }
}
