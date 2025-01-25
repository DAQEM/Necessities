package com.daqem.necessities.command;

import com.daqem.necessities.command.teleportation.level.spawn.SetSpawnCommand;
import com.daqem.necessities.command.teleportation.level.spawn.SpawnCommand;
import com.daqem.necessities.command.teleportation.level.warp.DeleteWarpCommand;
import com.daqem.necessities.command.teleportation.level.warp.SetWarpCommand;
import com.daqem.necessities.command.teleportation.level.warp.WarpCommand;
import com.daqem.necessities.command.teleportation.player.back.BackCommand;
import com.daqem.necessities.command.teleportation.player.home.DeleteHomeCommand;
import com.daqem.necessities.command.teleportation.player.home.HomeCommand;
import com.daqem.necessities.command.teleportation.player.home.SetHomeCommand;
import com.daqem.necessities.command.teleportation.player.tpa.*;
import com.daqem.necessities.command.time.DayCommand;
import com.daqem.necessities.command.time.MidnightCommand;
import com.daqem.necessities.command.time.NightCommand;
import com.daqem.necessities.command.time.NoonCommand;
import com.daqem.necessities.command.weather.RainCommand;
import com.daqem.necessities.command.weather.SunCommand;
import com.daqem.necessities.command.weather.ThunderCommand;

import java.util.ArrayList;
import java.util.List;

public interface CommandRegistry {

    List<Command> COMMANDS = new ArrayList<>();

    Command SPAWN = register(new SpawnCommand());
    Command SET_SPAWN = register(new SetSpawnCommand());

    Command WARP = register(new WarpCommand());
    Command SET_WARP = register(new SetWarpCommand());
    Command DEL_WARP = register(new DeleteWarpCommand());

    Command HOME = register(new HomeCommand());
    Command SET_HOME = register(new SetHomeCommand());
    Command DEL_HOME = register(new DeleteHomeCommand());

    Command BACK = register(new BackCommand());

    Command TPA = register(new TPACommand());
    Command TPA_HERE = register(new TPAHereCommand());
    Command TPA_ACCEPT = register(new TPAcceptCommand());
    Command TPA_DENY = register(new TPADenyCommand());
    Command TPA_TOGGLE = register(new TPAToggleCommand());

    Command DAY = register(new DayCommand());
    Command NOON = register(new NoonCommand());
    Command NIGHT = register(new NightCommand());
    Command MIDNIGHT = register(new MidnightCommand());

    Command SUN = register(new SunCommand());
    Command RAIN = register(new RainCommand());
    Command THUNDER = register(new ThunderCommand());

    static Command register(Command command) {
        COMMANDS.add(command);
        return command;
    }
}
