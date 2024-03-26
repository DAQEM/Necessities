package com.daqem.necessities.command.teleportation.warp;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.NecessitiesCommandSourceStack;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.storage.Warp;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class SetWarpCommand implements Command {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setwarp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(context -> {
                            if (context.getSource() instanceof NecessitiesCommandSourceStack source) {
                                String name = StringArgumentType.getString(context, "name");
                                Warp warp = new Warp(name, source.necessities$getPosition());
                                NecessitiesServerLevel level = source.necessities$getLevel();
                                level.necessities$getLevelData().necessities$addWarp(warp);
                                context.getSource().sendSuccess(() -> Necessities.prefixedTranslatable("commands.warp.set", Necessities.colored(name)), true);
                                return 1;
                            }
                            return 0;
                        })));
    }
}
