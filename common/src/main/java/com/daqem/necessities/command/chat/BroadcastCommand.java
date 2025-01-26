package com.daqem.necessities.command.chat;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.utils.ChatFormatter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.server.level.ServerPlayer;

public class BroadcastCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("broadcast")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("message", MessageArgument.message()).executes(commandContext -> {
                    MessageArgument.resolveChatMessage(commandContext, "message", playerChatMessage -> {
                        for (ServerPlayer player : commandContext.getSource().getServer().getPlayerList().getPlayers()) {
                            if (player instanceof NecessitiesServerPlayer serverPlayer) {
                                serverPlayer.necessities$sendSystemMessage(Necessities.getPrefix().append(ChatFormatter.format(playerChatMessage.signedContent())), false);
                            }
                        }
                    });
                    return 1;
                }))
        );

        dispatcher.register(Commands.literal("bc").redirect(command));
    }
}
