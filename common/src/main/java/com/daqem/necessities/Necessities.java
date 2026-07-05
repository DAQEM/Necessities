package com.daqem.necessities;

import com.daqem.knot.Knot;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.data.KitManager;
import com.daqem.necessities.event.PlayerDeathEvent;
import com.daqem.necessities.event.RegisterCommandsEvent;
import com.daqem.necessities.networking.NecessitiesNetworking;
import com.daqem.necessities.utils.ChatFormatter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Necessities {
    public static final String MOD_ID = "necessities";
    public static final Knot API = new Knot(MOD_ID);

    public static void init() {
        NecessitiesConfig.init();
        NecessitiesNetworking.init();
        registerEvents();
        Knot.RELOAD_REGISTRY.registerData(API.getId("kits"), new KitManager());
    }

    private static void registerEvents() {
        RegisterCommandsEvent.registerEvent();
        PlayerDeathEvent.registerEvent();
    }

    public static MutableComponent prefixedTranslatable(String str) {
        return getPrefix().append(API.translatable(str));
    }

    public static MutableComponent prefixedTranslatable(String str, Object... args) {
        return getPrefix().append(API.translatable(str, args));
    }

    public static MutableComponent prefixedVanillaTranslatable(String str, Object... args) {
        return getPrefix().append(Component.translatable(str, args));
    }

    public static MutableComponent prefixedFailureTranslatable(String str) {
        return getFailurePrefix().append(API.translatable(str));
    }

    public static MutableComponent prefixedFailureTranslatable(String str, Object... args) {
        return getFailurePrefix().append(colored(API.translatable(str, args), 0xFF5555));
    }

    public static MutableComponent getPrefix(int color) {
        return colored(API.translatable("prefix.left_bracket"), 0xFFFFFF)
                .append(colored(
                        NecessitiesConfig.prefix.get().isEmpty() ?
                                API.translatable("prefix.name") :
                                ChatFormatter.format(NecessitiesConfig.prefix.get())
                        , color))
                .append(colored(API.translatable("prefix.right_bracket"), 0xFFFFFF))
                .append(API.translatable("prefix.space"));
    }

    public static MutableComponent getPrefix() {
        return getPrefix(NecessitiesConfig.primaryColor.get());
    }

    public static MutableComponent getFailurePrefix() {
        return getPrefix(0xFF5555);
    }

    public static MutableComponent colored(String str) {
        return Component.literal(str).withStyle(style -> style.withColor(NecessitiesConfig.primaryColor.get()));
    }

    public static MutableComponent colored(MutableComponent component) {
        return component.withStyle(style -> style.withColor(NecessitiesConfig.primaryColor.get()));
    }

    public static MutableComponent colored(MutableComponent component, int color) {
        return component.withStyle(style -> style.withColor(color));
    }

    public static MutableComponent coloredFailure(String str) {
        return Component.literal(str).withStyle(style -> style.withColor(0xFFFFFF));
    }

    public static MutableComponent coloredLiteral(String str) {
        return Component.literal(str).withStyle(style -> style.withColor(NecessitiesConfig.primaryColor.get()));
    }
}