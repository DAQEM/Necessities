package com.daqem.necessities.command.weather;

import com.daqem.necessities.Necessities;
import net.minecraft.commands.CommandSourceStack;

public abstract class WeatherCommand {

    protected static int setWeather(CommandSourceStack source, String type, int clearTime, int rainTime, boolean isRaining, boolean isThundering) {
        source.getLevel().setWeatherParameters(clearTime, rainTime, isRaining, isThundering);
        source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.weather.set." + type), true);
        return 1;
    }
}
