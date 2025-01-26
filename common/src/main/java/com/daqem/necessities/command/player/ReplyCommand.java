package com.daqem.necessities.command.player;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class ReplyCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("reply")
                .then(Commands.argument("message", MessageArgument.message())
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                if (serverPlayer.necessities$getLastMessageSender().isPresent()) {
                                    NecessitiesServerPlayer recipient = serverPlayer.necessities$getLastMessageSender().get();
                                    MessageArgument.resolveChatMessage(context, "message", playerChatMessage ->
                                            sendMessage((ServerPlayer) serverPlayer, (ServerPlayer) recipient, playerChatMessage));
                                    return 1;
                                }
                                serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.reply.no_last_sender"));
                                return 0;
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        })));

        dispatcher.register(Commands.literal("r").redirect(command));
    }

    private static void sendMessage(ServerPlayer sender, ServerPlayer recipient, PlayerChatMessage playerChatMessage) {
        ChatType.Bound bound = ChatType.bind(ChatType.MSG_COMMAND_INCOMING, sender);
        OutgoingChatMessage outgoingChatMessage = OutgoingChatMessage.create(playerChatMessage);
        boolean bl;

        ChatType.Bound bound2 = ChatType.bind(ChatType.MSG_COMMAND_OUTGOING, sender).withTargetName(recipient.getDisplayName());
        sender.sendChatMessage(outgoingChatMessage, false, bound2);
        boolean bl2 = sender.shouldFilterMessageTo(recipient);
        recipient.sendChatMessage(outgoingChatMessage, bl2, bound);
        bl = bl2 && playerChatMessage.isFullyFiltered();

        if (bl) {
            sender.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
        }
    }
}
