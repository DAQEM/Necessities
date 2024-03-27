package com.daqem.necessities.config;

import com.daqem.necessities.Necessities;
import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class NecessitiesConfig {

    public static final Supplier<Integer> primaryColor;

    public static final Supplier<Integer> sunnyTime;
    public static final Supplier<Integer> rainyTime;
    public static final Supplier<Integer> thunderTime;

    public static final Supplier<Integer> homesLimit;

    static {
        IConfigBuilder config = ConfigBuilders.newTomlConfig(Necessities.MOD_ID, null, false);
        config.push("general");
            primaryColor = config.comment("The primary color of the mod.").define("primaryColor", 0x29A3F0, 0x000000, 0xFFFFFF);
        config.pop();

        config.push("commands");

            config.push("weather");
                sunnyTime = config.comment("The time in ticks it will be sunny when using the /sun command.").define("sunnyTime", 60000, 0, Integer.MAX_VALUE);
                rainyTime = config.comment("The time in ticks it will be rainy when using the /rain command.").define("rainyTime", 6000, 0, Integer.MAX_VALUE);
                thunderTime = config.comment("The time in ticks it will be thunder when using the /thunder command.").define("thunderTime", 6000, 0, Integer.MAX_VALUE);
            config.pop();

            config.push("homes");
                homesLimit = config.comment("The maximum amount of homes a player can have.").define("homesLimit", 5, 0, Integer.MAX_VALUE);
            config.pop();

        config.pop();

        config.build();
    }

    public static void init() {
    }
}
