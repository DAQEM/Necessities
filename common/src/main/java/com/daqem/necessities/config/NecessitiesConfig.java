package com.daqem.necessities.config;

import com.daqem.necessities.Necessities;
import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class NecessitiesConfig {

    public static final Supplier<Integer> primaryColor;

    static {
        IConfigBuilder config = ConfigBuilders.newTomlConfig(Necessities.MOD_ID, null, false);
        config.push("general");
        primaryColor = config.comment("The primary color of the mod.").define("primaryColor", 0x29A3F0, 0x000000, 0xFFFFFF);
        config.pop();
        config.build();
    }

    public static void init() {
    }
}
