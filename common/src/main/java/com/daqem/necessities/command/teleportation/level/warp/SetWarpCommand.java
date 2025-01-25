package com.daqem.necessities.command.teleportation.level.warp;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.NecessitiesCommandSourceStack;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.model.Warp;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SetWarpCommand implements Command {

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setwarp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPLayer) {
                                String name = StringArgumentType.getString(context, "name");
                                Warp warp = new Warp(name, serverPLayer.necessities$getPosition());
                                serverPLayer.necessities$getLevelData().necessities$addWarp(warp);
                                serverPLayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.warp.set", Necessities.colored(name)), false);
                                return 1;
                            } else {
                                context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                                return 0;
                            }
                        })));
    }
}
