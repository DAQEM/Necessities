package com.daqem.necessities.command.teleportation.player.home;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;

public class HomeCommand implements Command {

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home")
                .then(Commands.argument("home", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                return SharedSuggestionProvider.suggest(serverPlayer.necessities$getHomes().stream().map(home -> home.name), builder);
                            }
                            return SharedSuggestionProvider.suggest(new ArrayList<>(), builder);
                        })
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                String homeName = StringArgumentType.getString(context, "home");
                                serverPlayer.necessities$getHome(homeName).ifPresentOrElse(home -> {
                                    serverPlayer.necessities$teleport(home.position);
                                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.home", Necessities.colored(home.name)), false);
                                }, () -> {
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.home.not_found", Necessities.coloredFailure(homeName)));
                                });
                                return 1;
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        }))
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        if (serverPlayer.necessities$getHomes().isEmpty()) {
                            serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.home.no_homes"));
                            return 0;
                        } else if (serverPlayer.necessities$getHomes().size() == 1) {
                            serverPlayer.necessities$teleport(serverPlayer.necessities$getHomes().getFirst().position);
                            serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.home", Necessities.colored(serverPlayer.necessities$getHomes().get(0).name)), false);
                            return 1;
                        } else {
                            serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.home.multiple_homes"));
                            return 0;
                        }
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                }));
    }
}
