package com.daqem.necessities.command.inventory;

import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;

public class EnderChestCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("enderchest")
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.enderchest", 0))
                .executes(context -> openEnderChest(context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("target", EntityArgument.player())
                        .requires(source -> NecessitiesPermissions.check(source, "necessities.command.enderchest.others", 2))
                        .executes(context -> openEnderChest(context.getSource(), EntityArgument.getPlayer(context, "target")))
                ));

        dispatcher.register(Commands.literal("ec")
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.enderchest", 0))
                .executes(context -> openEnderChest(context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("target", EntityArgument.player())
                        .requires(source -> NecessitiesPermissions.check(source, "necessities.command.enderchest.others", 2))
                        .executes(context -> openEnderChest(context.getSource(), EntityArgument.getPlayer(context, "target")))
                ));

        dispatcher.register(Commands.literal("ecsee")
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.enderchest.others", 2))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> openEnderChest(context.getSource(), EntityArgument.getPlayer(context, "target")))
                ));
    }

    private int openEnderChest(CommandSourceStack source, ServerPlayer target) {
        if (source.getPlayer() instanceof NecessitiesServerPlayer viewer) {
            Component title = target == viewer ?
                    Component.translatable("container.enderchest") :
                    target.getDisplayName();

            ((ServerPlayer) viewer).openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> ChestMenu.threeRows(id, inventory, target.getEnderChestInventory()),
                    title
            ));
            return 1;
        }
        source.sendFailure(NEEDS_PLAYER_ERROR);
        return 0;
    }
}