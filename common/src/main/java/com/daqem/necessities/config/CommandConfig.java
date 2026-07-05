package com.daqem.necessities.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.daqem.necessities.Necessities;
import com.daqem.yamlconfig.api.config.ConfigExtension;
import com.daqem.yamlconfig.api.config.ConfigType;
import com.daqem.yamlconfig.api.config.IConfigBuilder;
import com.daqem.yamlconfig.api.config.entry.IConfigEntry;
import com.daqem.yamlconfig.impl.config.ConfigBuilder;
import com.daqem.yamlconfig.platform.Services;

public class CommandConfig {

    public static final Map<String, CommandEntry> COMMANDS = new HashMap<>();

    static {
        IConfigBuilder config = new ConfigBuilder(
                Necessities.MOD_ID,
                "commands",
                ConfigExtension.YAML,
                ConfigType.COMMON,
                Services.PLATFORM.getConfigDirectory().resolve(Necessities.MOD_ID)
        );

        // Register configs for known commands
        registerCommand(config, "broadcast", true, "bc");
        registerCommand(config, "reply", true);
        registerCommand(config, "enderchest", true, "ec");
        registerCommand(config, "invsee", true);
        registerCommand(config, "kit", true);
        registerCommand(config, "afk", true);
        registerCommand(config, "delnick", true);
        registerCommand(config, "feed", true);
        registerCommand(config, "fly", true);
        registerCommand(config, "gamemode", true, "gm");
        registerCommand(config, "god", true);
        registerCommand(config, "heal", true);
        registerCommand(config, "nick", true);
        registerCommand(config, "vanish", true, "v");
        registerCommand(config, "rtp", true);
        registerCommand(config, "setspawn", true);
        registerCommand(config, "spawn", true);
        registerCommand(config, "delwarp", true);
        registerCommand(config, "setwarp", true);
        registerCommand(config, "warp", true);
        registerCommand(config, "back", true);
        registerCommand(config, "delhome", true);
        registerCommand(config, "home", true);
        registerCommand(config, "sethome", true);
        registerCommand(config, "tpa", true);
        registerCommand(config, "tpadeny", true);
        registerCommand(config, "tpahere", true);
        registerCommand(config, "tpatoggle", true);
        registerCommand(config, "tpaccept", true);
        registerCommand(config, "day", true);
        registerCommand(config, "midnight", true);
        registerCommand(config, "night", true);
        registerCommand(config, "noon", true);
        registerCommand(config, "rain", true);
        registerCommand(config, "sun", true);
        registerCommand(config, "thunder", true);

        config.build();
    }

    private static void registerCommand(IConfigBuilder config, String commandName, boolean defaultEnabled, String... defaultAliases) {
        config.push(commandName);
        IConfigEntry<Boolean> enabled = config.defineBoolean("enabled", defaultEnabled)
                .withComments("Whether the /" + commandName + " command is enabled.");
        IConfigEntry<List<String>> aliases = config.defineStringList("aliases", List.of(defaultAliases))
                .withComments("Aliases for the /" + commandName + " command. (Redirects)");
        COMMANDS.put(commandName, new CommandEntry(enabled, aliases));
        config.pop();
    }

    public static void init() {
    }

    public record CommandEntry(IConfigEntry<Boolean> enabled, IConfigEntry<List<String>> aliases) {
    }
}
