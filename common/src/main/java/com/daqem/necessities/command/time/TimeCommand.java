package com.daqem.necessities.command.time;

import com.daqem.necessities.Necessities;
import net.minecraft.commands.CommandSourceStack;

public abstract class TimeCommand {

    public static int setTime(CommandSourceStack source, String type, int time) {
        source.getLevel().setDayTime(time);
        source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.time.set." + type), true);
        return 1;
    }
}
