package com.daqem.necessities.command.player;

import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class AFKCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
         CommandManager.register(dispatcher, "afk", literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.afk", 0)));
    }
}
