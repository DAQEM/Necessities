package com.daqem.necessities.command.teleportation.warp;

import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.storage.Warp;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class WarpArgument implements ArgumentType<Warp> {

    private final NecessitiesServerLevel level;

    public WarpArgument(NecessitiesServerLevel level) {
        this.level = level;
    }

    public static WarpArgument warp(NecessitiesServerLevel level) {
        return new WarpArgument(level);
    }

    @Override
    public Warp parse(StringReader reader) throws CommandSyntaxException {
        return level.necessities$getLevelData().necessities$getWarp(reader.readQuotedString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(level.necessities$getLevelData().necessities$getWarps().stream().map(x -> x.name).toList(), builder);
    }

    public static Warp getWarp(CommandContext<?> context, String name) {
        return context.getArgument(name, Warp.class);
    }
}
