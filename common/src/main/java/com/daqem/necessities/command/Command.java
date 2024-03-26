package com.daqem.necessities.command;

import com.daqem.necessities.Necessities;
import net.minecraft.network.chat.Component;

public interface Command {

    Component NEEDS_PLAYER_ERROR = Necessities.translatable("commands.error.needs_player");
}
