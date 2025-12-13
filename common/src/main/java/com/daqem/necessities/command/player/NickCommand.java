
package com.daqem.necessities.command.player;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class NickCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nick")
                .then(Commands.argument("nickname", StringArgumentType.string())
                        .executes(context -> {
                            if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                                String nickname = StringArgumentType.getString(context, "nickname");
                                int maxLength = NecessitiesConfig.maxNickLength.get();
                                if (nickname.length() > maxLength) {
                                    serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.nick.too_long", maxLength));
                                    return 0;
                                }
                                if (!NecessitiesConfig.allowColorsInNick.get()) {
                                    if (nickname.contains("&") || nickname.contains("\u00A7")) {
                                        serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.nick.colors_disabled"));
                                        return 0;
                                    }
                                }
                                serverPlayer.necessities$setNick(nickname);
                                return 1;
                            }
                            context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                            return 0;
                        })
                )
                .executes(context -> {
                    if (context.getSource().getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
                        serverPlayer.necessities$removeNick();
                        return 1;
                    }
                    context.getSource().sendFailure(NEEDS_PLAYER_ERROR);
                    return 0;
                })
        );
    }
}
