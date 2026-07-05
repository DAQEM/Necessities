package com.daqem.necessities.command.player;

import java.util.Collection;
import java.util.Collections;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.GameType;

public class GamemodeCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
         CommandManager.register(dispatcher, "gamemode", literal -> literal
                .requires(source -> Necessities.API.hasPermission(source, "command.gamemode"))
                .then(Commands.argument("gamemode", GameModeArgument.gameMode())
                        .executes(context -> setMode(context, Collections.singleton(context.getSource().getPlayerOrException()), GameModeArgument.getGameMode(context, "gamemode")))
                        .then(Commands.argument("target", EntityArgument.players())
                                .executes(context -> setMode(context, EntityArgument.getPlayers(context, "target"), GameModeArgument.getGameMode(context, "gamemode"))))));
    }

    private static void logGamemodeChange(CommandSourceStack commandSourceStack, ServerPlayer serverPlayer, GameType gameType) {
        Component component = Necessities.colored(Component.translatable("gameMode." + gameType.getName()));
        if (commandSourceStack.getEntity() == serverPlayer) {
            if (commandSourceStack.getPlayer() instanceof NecessitiesServerPlayer necessitiesServerPlayer) {
                necessitiesServerPlayer.necessities$sendSystemMessage(Necessities.prefixedVanillaTranslatable("gameMode.changed", component), false);
            } else {
                commandSourceStack.sendSuccess(() -> Necessities.prefixedVanillaTranslatable("commands.gamemode.success.self", component), true);
            }
        } else {
            if (commandSourceStack.getLevel().getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK)) {
                if (serverPlayer instanceof NecessitiesServerPlayer necessitiesServerPlayer) {
                    necessitiesServerPlayer.necessities$sendSystemMessage(Necessities.prefixedVanillaTranslatable("gameMode.changed", component), false);
                } else {
                    serverPlayer.sendSystemMessage(Necessities.prefixedVanillaTranslatable("gameMode.changed", component));
                }
            }

            if (commandSourceStack.getPlayer() instanceof NecessitiesServerPlayer necessitiesServerPlayer) {
                necessitiesServerPlayer.necessities$sendSystemMessage(Necessities.prefixedVanillaTranslatable("commands.gamemode.success.other", serverPlayer.getDisplayName(), component), false);
            } else {
                commandSourceStack.sendSuccess(() -> Necessities.prefixedVanillaTranslatable("commands.gamemode.success.other", serverPlayer.getDisplayName(), component), true);
            }
        }
    }

    private static int setMode(CommandContext<CommandSourceStack> commandContext, Collection<ServerPlayer> collection, GameType gameType) {
        int i = 0;

        for (ServerPlayer serverPlayer : collection) {
            if (serverPlayer.setGameMode(gameType)) {
                logGamemodeChange(commandContext.getSource(), serverPlayer, gameType);
                i++;
            }
        }

        return i;
    }
}
