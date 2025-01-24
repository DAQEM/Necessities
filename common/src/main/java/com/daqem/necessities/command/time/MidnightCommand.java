package com.daqem.necessities.command.time;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class MidnightCommand extends TimeCommand {

    public static final String TYPE = "midnight";

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(TYPE)
            .requires(source -> source.hasPermission(2))
            .executes(context -> setTime(context.getSource(), TYPE, 18000)));
    }
}
