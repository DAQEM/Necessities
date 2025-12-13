package com.daqem.necessities.config;

import com.daqem.necessities.Necessities;
import com.daqem.yamlconfig.YamlConfigExpectPlatform;
import com.daqem.yamlconfig.api.config.ConfigExtension;
import com.daqem.yamlconfig.api.config.ConfigType;
import com.daqem.yamlconfig.api.config.IConfigBuilder;
import com.daqem.yamlconfig.api.config.entry.IConfigEntry;
import com.daqem.yamlconfig.impl.config.ConfigBuilder;

public class NecessitiesConfig {

    public static final IConfigEntry<String> prefix;
    public static final IConfigEntry<Integer> primaryColor;

    public static final IConfigEntry<Integer> sunnyTime;
    public static final IConfigEntry<Integer> rainyTime;
    public static final IConfigEntry<Integer> thunderTime;

    public static final IConfigEntry<Integer> homesLimit;

    public static final IConfigEntry<Boolean> allowBackOnDeath;
    public static final IConfigEntry<Integer> tpaTimeout;

    public static final IConfigEntry<Integer> rtpMinRadius;
    public static final IConfigEntry<Integer> rtpMaxRadius;
    public static final IConfigEntry<Integer> rtpCooldown;
    public static final IConfigEntry<Integer> rtpDelay;

    public static final IConfigEntry<Integer> spawnTeleportDelay;
    public static final IConfigEntry<Integer> spawnCooldown;

    public static final IConfigEntry<Integer> warpTeleportDelay;
    public static final IConfigEntry<Integer> warpCooldown;

    public static final IConfigEntry<Integer> homeTeleportDelay;
    public static final IConfigEntry<Integer> homeCooldown;

    public static final IConfigEntry<Integer> tpaTeleportDelay;
    public static final IConfigEntry<Integer> tpaCooldown;

    public static final IConfigEntry<Integer> backTeleportDelay;
    public static final IConfigEntry<Integer> backCooldown;

    public static final IConfigEntry<Integer> maxNickLength;
    public static final IConfigEntry<Boolean> allowColorsInNick;

    public static final IConfigEntry<Boolean> godModeAllow;
    public static final IConfigEntry<Boolean> flyAllow;

    static {
        IConfigBuilder config = new ConfigBuilder(
                Necessities.MOD_ID,
                "necessities",
                ConfigExtension.YAML,
                ConfigType.COMMON,
                YamlConfigExpectPlatform.getConfigDirectory().resolve(Necessities.MOD_ID)
        );
        config.push("general");
        prefix = config.defineString("prefix", "", 0, 64)
                .withComments("The prefix of the mod.");

        primaryColor = config.defineInteger("primaryColor", 0x29A3F0, 0x000000, 0xFFFFFF)
                .withComments("The primary color of the mod.");

        maxNickLength = config.defineInteger("maxNickLength", 16, 1, 32)
                .withComments("The maximum length of a player's nickname.");
        allowColorsInNick = config.defineBoolean("allowColorsInNick", true)
                .withComments("If true, players can use colors in their nickname.");

        godModeAllow = config.defineBoolean("godModeAllow", true)
                .withComments("If true, players can use the /god command.");
        flyAllow = config.defineBoolean("flyAllow", true)
                .withComments("If true, players can use the /fly command.");
        config.pop();

        config.push("commands");

        config.push("weather");
        sunnyTime = config.defineInteger("sunnyTime", 60000, 0, Integer.MAX_VALUE)
                .withComments("The time in ticks it will be sunny when using the /sun command.");

        rainyTime = config.defineInteger("rainyTime", 6000, 0, Integer.MAX_VALUE)
                .withComments("The time in ticks it will be rainy when using the /rain command.");

        thunderTime = config.defineInteger("thunderTime", 6000, 0, Integer.MAX_VALUE)
                .withComments("The time in ticks it will be thunder when using the /thunder command.");
        config.pop();

        config.push("homes");
        homesLimit = config.defineInteger("homesLimit", 5, 0, Integer.MAX_VALUE)
                .withComments("The maximum amount of homes a player can have.");
        config.pop();

        config.push("teleportation");
        allowBackOnDeath = config.defineBoolean("allowBackOnDeath", true)
                .withComments("If true, players can use /back to return to their death location.");
        tpaTimeout = config.defineInteger("tpaTimeout", 60, 0, Integer.MAX_VALUE)
                .withComments("The time in seconds a TPA request will be pending.");

        spawnTeleportDelay = config.defineInteger("spawnTeleportDelay", 0, 0, Integer.MAX_VALUE)
                .withComments("The delay in seconds before teleporting to spawn.");
        spawnCooldown = config.defineInteger("spawnCooldown", 0, 0, Integer.MAX_VALUE)
                .withComments("The cooldown in seconds for the spawn command.");

        warpTeleportDelay = config.defineInteger("warpTeleportDelay", 0, 0, Integer.MAX_VALUE)
                .withComments("The delay in seconds before teleporting to a warp.");
        warpCooldown = config.defineInteger("warpCooldown", 0, 0, Integer.MAX_VALUE)
                .withComments("The cooldown in seconds for the warp command.");

        homeTeleportDelay = config.defineInteger("homeTeleportDelay", 0, 0, Integer.MAX_VALUE)
                .withComments("The delay in seconds before teleporting to a home.");
        homeCooldown = config.defineInteger("homeCooldown", 0, 0, Integer.MAX_VALUE)
                .withComments("The cooldown in seconds for the home command.");

        tpaTeleportDelay = config.defineInteger("tpaTeleportDelay", 0, 0, Integer.MAX_VALUE)
                .withComments("The delay in seconds before teleporting via TPA.");
        tpaCooldown = config.defineInteger("tpaCooldown", 0, 0, Integer.MAX_VALUE)
                .withComments("The cooldown in seconds for the TPA command.");

        backTeleportDelay = config.defineInteger("backTeleportDelay", 0, 0, Integer.MAX_VALUE)
                .withComments("The delay in seconds before teleporting back.");
        backCooldown = config.defineInteger("backCooldown", 0, 0, Integer.MAX_VALUE)
                .withComments("The cooldown in seconds for the back command.");
        config.pop();

        config.push("rtp");
        rtpMinRadius = config.defineInteger("minRadius", 200, 0, Integer.MAX_VALUE)
                .withComments("The minimum radius for the RTP command.");
        rtpMaxRadius = config.defineInteger("maxRadius", 5000, 0, Integer.MAX_VALUE)
                .withComments("The maximum radius for the RTP command.");
        rtpCooldown = config.defineInteger("cooldown", 60, 0, Integer.MAX_VALUE)
                .withComments("The cooldown in seconds for the RTP command.");
        rtpDelay = config.defineInteger("delay", 0, 0, Integer.MAX_VALUE)
                .withComments("The delay in seconds before teleporting using RTP.");
        config.pop();

        config.pop();

        config.build();
    }

    public static void init() {
    }
}
