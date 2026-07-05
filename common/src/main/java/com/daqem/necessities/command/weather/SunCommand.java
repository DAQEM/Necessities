package com.daqem.necessities.command.weather;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.config.NecessitiesConfig;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class SunCommand extends WeatherCommand {

    private static final String TYPE = "sun";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, TYPE, literal -> literal
                .requires(source -> Necessities.API.hasPermission(source, "command.weather.sun"))
                .executes(context -> setWeather(context.getSource(), TYPE, NecessitiesConfig.sunnyTime.get(), 0, false, false)));
    }
}
