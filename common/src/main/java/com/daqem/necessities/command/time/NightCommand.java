package com.daqem.necessities.command.time;

import com.daqem.necessities.NecessitiesPermissions;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class NightCommand extends TimeCommand {

    public static final String TYPE = "night";

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(TYPE)
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.time.night", 2))
                .executes(context -> setTime(context.getSource(), TYPE, 13000)));
    }
}
