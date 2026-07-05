package com.daqem.necessities.command.weather;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.config.NecessitiesConfig;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;

public class RainCommand extends WeatherCommand {

    private static final String TYPE = "rain";

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, TYPE, literal -> literal
                .requires(source -> Necessities.API.hasPermission(source, "command.weather.rain"))
                .executes(context -> setWeather(context.getSource(), TYPE, 0, NecessitiesConfig.rainyTime.get(), true, false)));
    }
}
