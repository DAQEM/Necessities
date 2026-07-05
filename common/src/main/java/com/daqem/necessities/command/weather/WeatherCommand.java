package com.daqem.necessities.command.weather;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.saveddata.WeatherData;

public abstract class WeatherCommand implements Command {

    protected static int setWeather(CommandSourceStack source, String type, int clearTime, int rainTime, boolean isRaining, boolean isThundering) {
        WeatherData weatherData = source.getLevel().getWeatherData();
        weatherData.setClearWeatherTime(clearTime);
        weatherData.setRainTime(rainTime);
        weatherData.setRaining(isRaining);
        weatherData.setThundering(isThundering);

        if (source.getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
            serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.weather.set." + type), false);
        } else {
            source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.weather.set." + type), true);
        }
        return 1;
    }
}
