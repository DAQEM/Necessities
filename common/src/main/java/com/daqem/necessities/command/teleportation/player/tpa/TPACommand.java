package com.daqem.necessities.command.teleportation.player.tpa;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class TPACommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpa")
                .then(Commands.argument("player", StringArgumentType.string())
                        .suggests((context, builder) ->
                                SharedSuggestionProvider.suggest(
                                        context.getSource().getServer().getPlayerList().getPlayers().stream()
                                                .filter(player -> player != context.getSource().getPlayer())
                                                .map(player -> player.getGameProfile().getName()), builder))
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                String playerName = StringArgumentType.getString(context, "player");
                                ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayers().stream()
                                        .filter(player -> player != context.getSource().getPlayer())
                                        .filter(player -> player.getGameProfile().getName().equals(playerName)).findFirst().orElse(null);
                                if (target instanceof NecessitiesServerPlayer targetServerPlayer) {
                                    serverPlayer.necessities$sendTPARequest(targetServerPlayer, false);
                                    return 1;
                                } else {
                                    context.getSource().sendFailure(Necessities.prefixedFailureTranslatable("commands.tpa.player_not_found", Necessities.coloredFailure(playerName)));
                                    return 0;
                                }
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        }))
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        serverPlayer.necessities$acceptTPARequest();
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                })
        );
    }
}
