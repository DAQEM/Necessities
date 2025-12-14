package com.daqem.necessities.command.player;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Collections;

public class HealCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("heal")
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.heal", 2))
                .executes(context -> heal(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> heal(context.getSource(), EntityArgument.getPlayers(context, "targets"))))
        );
    }

    private int heal(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0F);
            player.clearFire();
            player.removeAllEffects();

            if (source.getEntity() == player) {
                if (player instanceof NecessitiesServerPlayer serverPlayer) {
                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.heal"), false);
                }
            } else {
                if (player instanceof NecessitiesServerPlayer serverPlayer) {
                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.heal"), false);
                }
                source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.heal.other", player.getDisplayName()), true);
            }
        }
        return targets.size();
    }
}