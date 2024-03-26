package com.daqem.necessities.command.teleportation.warp;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;

public class WarpCommand implements Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("warp", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                return SharedSuggestionProvider.suggest(serverPlayer.necessities$getLevel().necessities$getLevelData().necessities$getWarps().stream().map(warp -> warp.name), builder);
                            }
                            return SharedSuggestionProvider.suggest(new ArrayList<>(), builder);
                        })
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                String warpName = StringArgumentType.getString(context, "warp");
                                serverPlayer.necessities$getLevel().necessities$getLevelData().necessities$getWarp(warpName).ifPresentOrElse(warp -> {
                                    serverPlayer.necessities$teleport(warp.position);
                                    context.getSource().sendSuccess(() -> Necessities.prefixedTranslatable("commands.warp", Necessities.colored(warp.name)), true);
                                }, () -> {
                                    context.getSource().sendFailure(Necessities.prefixedFailureTranslatable("commands.warp.not_found", Necessities.coloredFailure(warpName)));
                                });
                                return 1;
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        })));
    }
}
