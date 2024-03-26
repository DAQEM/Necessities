package com.daqem.necessities.command.teleportation.warp;

import com.daqem.necessities.command.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class WarpCommand implements Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    return 0;
                }));
    }
}
