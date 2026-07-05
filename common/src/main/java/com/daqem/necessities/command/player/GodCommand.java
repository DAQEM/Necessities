package com.daqem.necessities.command.player;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class GodCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, "god", literal -> literal
                .requires(source -> Necessities.API.hasPermission(source, "command.god"))
                .then(Commands.argument("player", StringArgumentType.string())
                        .suggests((context, builder) ->
                                SharedSuggestionProvider.suggest(
                                        context.getSource().getServer().getPlayerList().getPlayers().stream()
                                                .filter(player -> player != context.getSource().getPlayer())
                                                .map(player -> player.getGameProfile().name()), builder))
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                if (!NecessitiesConfig.godModeAllow.get()) {
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.god.disabled"));
                                    return 0;
                                }

                                String playerName = StringArgumentType.getString(context, "player");
                                ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayers().stream()
                                        .filter(player -> player != context.getSource().getPlayer())
                                        .filter(player -> player.getGameProfile().name().equals(playerName)).findFirst().orElse(null);
                                if (target instanceof NecessitiesServerPlayer targetServerPlayer) {
                                    targetServerPlayer.necessities$toggleGodMode();
                                    if (targetServerPlayer.necessities$hasGodMode()) {
                                        serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.god.toggled.other.on", targetServerPlayer.necessities$getName()), false);
                                    } else {
                                        serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.god.toggled.other.off", targetServerPlayer.necessities$getName()), false);
                                    }
                                    return 1;
                                } else {
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.god.player_not_found", Necessities.coloredFailure(playerName)));
                                    return 0;
                                }
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        }))
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        if (!NecessitiesConfig.godModeAllow.get()) {
                            serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.god.disabled"));
                            return 0;
                        }
                        serverPlayer.necessities$toggleGodMode();
                        return 1;
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                }));
    }
}
