package com.daqem.necessities.command.inventory;

import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.inventory.InvseeContainer;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;

public class InvseeCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("invsee")
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.invsee", 2))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer viewer) {
                                ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                InvseeContainer container = new InvseeContainer(target.getInventory());

                                ((ServerPlayer) viewer).openMenu(new SimpleMenuProvider(
                                        (id, inventory, p) -> ChestMenu.sixRows(id, inventory, container),
                                        target.getDisplayName()
                                ));
                                return 1;
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        })
                ));
    }
}