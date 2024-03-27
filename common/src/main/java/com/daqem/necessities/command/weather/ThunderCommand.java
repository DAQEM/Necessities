package com.daqem.necessities.command.weather;

import com.daqem.necessities.config.NecessitiesConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ThunderCommand extends WeatherCommand {

    private static final String TYPE = "thunder";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(TYPE)
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .executes(context -> setWeather(context.getSource(), TYPE, 0, NecessitiesConfig.thunderTime.get(), true, true)));
    }
}
