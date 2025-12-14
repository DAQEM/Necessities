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
import net.minecraft.server.level.ServerPlayer;

public class    FeedCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
         CommandManager.register(dispatcher, "feed", literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.feed", 2))
                .executes(context -> feed(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> feed(context.getSource(), EntityArgument.getPlayers(context, "targets")))));
    }

    private int feed(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0F);

            if (source.getEntity() == player) {
                if (player instanceof NecessitiesServerPlayer serverPlayer) {
                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.feed"), false);
                }
            } else {
                if (player instanceof NecessitiesServerPlayer serverPlayer) {
                    serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.feed"), false);
                }
                source.sendSuccess(() -> Necessities.prefixedTranslatable("commands.feed.other", player.getDisplayName()), true);
            }
        }
        return targets.size();
    }
}