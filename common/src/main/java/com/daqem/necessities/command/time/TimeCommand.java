package com.daqem.necessities.command.time;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.dimension.DimensionType;

public abstract class TimeCommand implements Command {

    public static int setTime(CommandSourceStack source, String type, int time) {
        ServerClockManager clockManager = source.getServer().clockManager();
        Holder<DimensionType> dimensionType = source.getLevel().dimensionTypeRegistration();

        dimensionType.value().defaultClock().ifPresent(clock ->
                clockManager.setTotalTicks(clock, time));

        if (source.getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
            serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.time.set." + type), false);
        } else {
            source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.time.set." + type), true);
        }

        return 1;
    }
}