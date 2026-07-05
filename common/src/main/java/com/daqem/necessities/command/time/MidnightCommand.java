package com.daqem.necessities.command.time;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.CommandManager;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class MidnightCommand extends TimeCommand {

    public static final String TYPE = "midnight";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, TYPE, literal -> literal
                .requires(source -> Necessities.API.hasPermission(source, "command.time.midnight"))
                .executes(context -> setTime(context.getSource(), TYPE, 18000)));
    }
}
