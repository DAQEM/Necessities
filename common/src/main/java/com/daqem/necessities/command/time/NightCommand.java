package com.daqem.necessities.command.time;

import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.CommandManager;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class NightCommand extends TimeCommand {

    public static final String TYPE = "night";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, TYPE, literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.time.night", 2))
                .executes(context -> setTime(context.getSource(), TYPE, 13000)));
    }
}
