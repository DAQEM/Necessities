package com.daqem.necessities.command.kit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.command.CommandManager;
import com.daqem.necessities.data.KitManager;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.model.Kit;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.item.ItemStack;

public class KitCommand implements Command {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_KITS = (context, builder) -> {
        Set<String> suggestions = new HashSet<>();
        for (Kit kit : KitManager.getInstance().getKits()) {
            suggestions.add(kit.getId().getPath());
        }
        return SharedSuggestionProvider.suggest(suggestions, builder);
    };

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandManager.register(dispatcher, "kit", literal -> literal
                .requires(source -> Necessities.API.hasPermission(source, "command.kit", PermissionLevel.ALL))
                .then(Commands.argument("kit", StringArgumentType.string())
                        .suggests(SUGGEST_KITS)
                        .executes(context -> giveKit(context, StringArgumentType.getString(context, "kit"))))
                .executes(this::listKits));
    }

    private int listKits(CommandContext<CommandSourceStack> context) {
        List<Kit> kits = KitManager.getInstance().getKits();
        if (kits.isEmpty()) {
            context.getSource().sendFailure(Necessities.prefixedFailureTranslatable("commands.kits.none"));
            return 0;
        }

        Set<String> kitsSet = new HashSet<>();
        for (Kit kit : KitManager.getInstance().getKits()) {
            kitsSet.add(kit.getId().getPath());
        }

        MutableComponent message = Necessities.prefixedTranslatable("commands.kits.list");

        List<String> kitList = new ArrayList<>(kitsSet);
        for (int i = 0; i < kitList.size(); i++) {
            message.append(Necessities.colored(kitList.get(i)));
            if (i < kits.size() - 1) {
                message.append(Component.literal(", "));
            }
        }
        context.getSource().sendSuccess(() -> message, false);
        return 1;
    }

    private int giveKit(CommandContext<CommandSourceStack> context, String kitName) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (player instanceof NecessitiesServerPlayer serverPlayer) {
            Kit kit = findKit(kitName);

            if (kit == null) {
                serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.kit.not_found", Necessities.coloredFailure(kitName)));
                return 0;
            }

            Identifier kitId = kit.getId();
            long cooldownMillis = kit.getCooldown() * 1000L;
            long lastUsed = serverPlayer.necessities$getKitCooldown(kitId);
            long now = System.currentTimeMillis();
            long remainingMillis = cooldownMillis - (now - lastUsed);

            if (remainingMillis > 0 && !Necessities.API.hasPermission(context.getSource(), "command.kit.bypass_cooldown")) {
                String timeString = getDurationBreakdown(remainingMillis);
                serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.kit.cooldown", Necessities.coloredFailure(timeString)));
                return 0;
            }

            for (ItemStack stack : kit.getItems()) {
                if (!player.getInventory().add(stack.copy())) {
                    player.drop(stack.copy(), false);
                }
            }

            serverPlayer.necessities$setKitCooldown(kitId, now);
            serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.kit.received", Necessities.colored(kitId.toString())), false);
            return 1;
        }
        return 0;
    }

    private Kit findKit(String input) {
        for (Kit kit : KitManager.getInstance().getKits()) {
            if (kit.getId().getPath().equals(input)) {
                return kit;
            }
        }

        return null;
    }

    public static String getDurationBreakdown(long millis) {
        if (millis <= 0) {
            return "0 " + Necessities.API.translatable("time.seconds").getString();
        }
        long seconds = millis / 1000;
        final long ONE_MINUTE = 60;
        final long ONE_HOUR = 3600;
        final long ONE_DAY = 86400;
        final long ONE_WEEK = 604800;
        final long ONE_MONTH = 2592000;
        final long ONE_YEAR = 31536000;

        Map<String, Long> units = new LinkedHashMap<>();
        long years = seconds / ONE_YEAR; seconds %= ONE_YEAR;
        if (years > 0) units.put(years == 1 ? "time.year" : "time.years", years);
        long months = seconds / ONE_MONTH; seconds %= ONE_MONTH;
        if (months > 0) units.put(months == 1 ? "time.month" : "time.months", months);
        long weeks = seconds / ONE_WEEK; seconds %= ONE_WEEK;
        if (weeks > 0) units.put(weeks == 1 ? "time.week" : "time.weeks", weeks);
        long days = seconds / ONE_DAY; seconds %= ONE_DAY;
        if (days > 0) units.put(days == 1 ? "time.day" : "time.days", days);
        long hours = seconds / ONE_HOUR; seconds %= ONE_HOUR;
        if (hours > 0) units.put(hours == 1 ? "time.hour" : "time.hours", hours);
        long minutes = seconds / ONE_MINUTE; seconds %= ONE_MINUTE;
        if (minutes > 0) units.put(minutes == 1 ? "time.minute" : "time.minutes", minutes);
        if (seconds > 0) units.put(seconds == 1 ? "time.second" : "time.seconds", seconds);

        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Long> entry : units.entrySet()) {
            parts.add(entry.getValue() + " " + Necessities.API.translatable(entry.getKey()).getString());
        }
        if (parts.isEmpty()) return "0 " + Necessities.API.translatable("time.seconds").getString();
        return String.join(", ", parts);
    }
}