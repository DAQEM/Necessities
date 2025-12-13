package com.daqem.necessities.command.player;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class FlyCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fly")
                .requires(source -> source.hasPermission(2))
                .executes(context -> toggleFlight(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> toggleFlight(context.getSource(), EntityArgument.getPlayers(context, "targets"))))
        );
    }

    private int toggleFlight(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            boolean canFly = !player.getAbilities().mayfly;
            player.getAbilities().mayfly = canFly;
            if (!canFly) {
                player.getAbilities().flying = false;
            }
            player.onUpdateAbilities();

            String status = canFly ? "enabled" : "disabled";
            if (source.getEntity() == player) {
                if (player instanceof NecessitiesServerPlayer serverPlayer) {
                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.fly." + status), false);
                }
            } else {
                if (player instanceof NecessitiesServerPlayer serverPlayer) {
                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.fly." + status), false);
                }
                source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.fly.other." + status, player.getDisplayName()), true);
            }
        }
        return targets.size();
    }
}