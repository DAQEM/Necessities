package com.daqem.necessities.command.time;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import net.minecraft.commands.CommandSourceStack;

public abstract class TimeCommand implements Command {

    public static int setTime(CommandSourceStack source, String type, int time) {
        source.getLevel().setDayTime(time);
        if (source.getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
            serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.time.set." + type), false);
        } else {
            source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.time.set." + type), true);
        }
        return 1;
    }
}
