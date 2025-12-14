package com.daqem.necessities.command.weather;

import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.config.NecessitiesConfig;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class ThunderCommand extends WeatherCommand {

    private static final String TYPE = "thunder";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, TYPE, literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.weather.thunder", 2))
                .executes(context -> setWeather(context.getSource(), TYPE, 0, NecessitiesConfig.thunderTime.get(), true, true)));
    }
}
