package com.daqem.necessities.command.teleportation.level.warp;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.NecessitiesCommandSourceStack;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;

public class DeleteWarpCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("delwarp")
                .then(Commands.argument("warp", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                return SharedSuggestionProvider.suggest(serverPlayer.necessities$getLevelData().necessities$getWarps().stream().map(warp -> warp.name), builder);
                            }
                            return SharedSuggestionProvider.suggest(new ArrayList<>(), builder);
                        })
                        .executes(context -> {
                            if (context.getSource() instanceof NecessitiesCommandSourceStack source && context.getSource() instanceof NecessitiesServerPlayer serverPlayer) {
                                String warpName = StringArgumentType.getString(context, "warp");
                                source.necessities$getLevelData().necessities$getWarp(warpName).ifPresentOrElse(warp -> {
                                    source.necessities$getLevelData().necessities$removeWarp(warp.name);
                                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.warp.delete", Necessities.colored(warp.name)), false);
                                }, () -> {
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.warp.not_found", Necessities.coloredFailure(warpName)));
                                });
                                return 1;
                            }
                            return 0;
                        })));
    }
}
