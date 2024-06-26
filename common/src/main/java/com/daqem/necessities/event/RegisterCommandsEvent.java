package com.daqem.necessities.event;

import com.daqem.necessities.command.teleportation.level.spawn.SetSpawnCommand;
import com.daqem.necessities.command.teleportation.level.spawn.SpawnCommand;
import com.daqem.necessities.command.teleportation.level.warp.SetWarpCommand;
import com.daqem.necessities.command.teleportation.level.warp.WarpCommand;
import com.daqem.necessities.command.time.DayCommand;
import com.daqem.necessities.command.time.MidnightCommand;
import com.daqem.necessities.command.time.NightCommand;
import com.daqem.necessities.command.time.NoonCommand;
import com.daqem.necessities.command.weather.RainCommand;
import com.daqem.necessities.command.weather.SunCommand;
import com.daqem.necessities.command.weather.ThunderCommand;
import dev.architectury.event.events.common.CommandRegistrationEvent;

public class RegisterCommandsEvent {

    public static void registerEvent() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {

            // Weather commands
            SunCommand.register(dispatcher);
            RainCommand.register(dispatcher);
            ThunderCommand.register(dispatcher);

            // Time commands
            DayCommand.register(dispatcher);
            NoonCommand.register(dispatcher);
            NightCommand.register(dispatcher);
            MidnightCommand.register(dispatcher);

            // Spawn commands
            SpawnCommand.register(dispatcher);
            SetSpawnCommand.register(dispatcher);

            // Warp commands
            WarpCommand.register(dispatcher);
            SetWarpCommand.register(dispatcher);
        });
    }
}
