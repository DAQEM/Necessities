package com.daqem.necessities.command.chat;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.NecessitiesPermissions;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.utils.ChatFormatter;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.server.level.ServerPlayer;

public class BroadcastCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
         CommandManager.register(dispatcher, "broadcast", literal -> literal
                .requires(source -> NecessitiesPermissions.check(source, "necessities.command.broadcast", 2))
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
    }
}
