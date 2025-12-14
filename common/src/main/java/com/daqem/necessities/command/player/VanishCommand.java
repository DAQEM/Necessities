package com.daqem.necessities.command.player;

import java.util.Collection;
import java.util.Collections;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class VanishCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
         CommandManager.register(dispatcher, "vanish", literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.vanish", 2))
                .executes(context -> vanish(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> vanish(context.getSource(), EntityArgument.getPlayers(context, "targets")))));
        }

    private int vanish(CommandSourceStack source, Collection<ServerPlayer> targets) {
        int successCount = 0;
        for (ServerPlayer player : targets) {
            if (player instanceof NecessitiesServerPlayer necessitiesPlayer) {
                boolean isVanished = !necessitiesPlayer.necessities$isVanished();
                necessitiesPlayer.necessities$setVanished(isVanished);
                successCount++;
            }
        }
        return successCount;
    }
}