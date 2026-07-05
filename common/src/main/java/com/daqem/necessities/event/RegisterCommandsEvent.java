package com.daqem.necessities.event;

import com.daqem.knot.events.EventsService;
import com.daqem.necessities.command.CommandRegistry;

public class RegisterCommandsEvent {

    public static void registerEvent() {
        EventsService.Server.COMMAND_REGISTER.register((dispatcher, registry, selection) ->
                CommandRegistry.COMMANDS.forEach(command -> command.register(dispatcher)));
    }
}
