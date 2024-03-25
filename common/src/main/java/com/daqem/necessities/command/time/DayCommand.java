package com.daqem.necessities.command.time;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DayCommand extends TimeCommand {

    public static final String TYPE = "day";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(TYPE)
            .requires(source -> source.hasPermission(2))
            .executes(context -> setTime(context.getSource(), TYPE, 1000)));
    }
}
