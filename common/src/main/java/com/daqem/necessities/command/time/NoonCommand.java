package com.daqem.necessities.command.time;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.CommandManager;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class NoonCommand extends TimeCommand {

    public static final String TYPE = "noon";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, TYPE, literal -> literal
                .requires(source -> Necessities.API.hasPermission(source, "command.time.noon"))
                .executes(context -> setTime(context.getSource(), TYPE, 6000)));
    }
}
