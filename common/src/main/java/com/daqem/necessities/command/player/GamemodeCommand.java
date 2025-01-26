package com.daqem.necessities.command.player;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

import java.util.Collection;
import java.util.Collections;

public class GamemodeCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        register(dispatcher, "gamemode");
        register(dispatcher, "gm");
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, String name) {
        dispatcher.register(Commands.literal(name)
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("gamemode", GameModeArgument.gameMode())
                        .executes(commandContext -> setMode(
                                        commandContext, Collections.singleton(commandContext.getSource().getPlayerOrException()),
                                        GameModeArgument.getGameMode(commandContext, "gamemode")
                                )
                        )
                        .then(Commands.argument("target", EntityArgument.players())
                                .executes(commandContext -> setMode(
                                                commandContext, EntityArgument.getPlayers(commandContext, "target"),
                                                GameModeArgument.getGameMode(commandContext, "gamemode")
                                        )
                                )
                        )
                )
        );
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
            if (commandSourceStack.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
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
