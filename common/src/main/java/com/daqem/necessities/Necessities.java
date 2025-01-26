package com.daqem.necessities;

import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.event.PlayerJoinEvent;
import com.daqem.necessities.event.RegisterCommandsEvent;
import com.daqem.necessities.networking.NecessitiesNetworking;
import com.daqem.necessities.utils.ChatFormatter;
import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class Necessities {
    public static final String MOD_ID = "necessities";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));


    public static void init() {
        NecessitiesConfig.init();
        NecessitiesNetworking.init();
        registerEvents();
    }

    private static void registerEvents() {
        RegisterCommandsEvent.registerEvent();
        PlayerJoinEvent.registerEvent();
    }

    public static ResourceLocation getId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static MutableComponent prefixedTranslatable(String str) {
        return getPrefix().append(translatable(str));
    }

    public static MutableComponent prefixedTranslatable(String str, Object... args) {
        return getPrefix().append(translatable(str, args));
    }

    public static MutableComponent prefixedFailureTranslatable(String str) {
        return getFailurePrefix().append(translatable(str));
    }

    public static MutableComponent prefixedFailureTranslatable(String str, Object... args) {
        return getFailurePrefix().append(colored(translatable(str, args), 0xFF5555));
    }

    public static MutableComponent translatable(String str) {
        return Component.translatable(MOD_ID + "." + str, new Object[0]);
    }

    public static MutableComponent translatable(String str, Object... args) {
        return Component.translatable(MOD_ID + "." + str, args);
    }

    public static MutableComponent getPrefix(int color) {
        return colored(translatable("prefix.left_bracket"), 0xFFFFFF)
                .append(colored(
                        NecessitiesConfig.prefix.get().isEmpty() ?
                                translatable("prefix.name") :
                                ChatFormatter.format(NecessitiesConfig.prefix.get())
                        , color))
                .append(colored(translatable("prefix.right_bracket"), 0xFFFFFF))
                .append(translatable("prefix.space"));
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

    public static MutableComponent coloredFailure(MutableComponent component) {
        return component.withStyle(style -> style.withColor(0xFFFFFF));
    }

    public static MutableComponent literal(String str) {
        return Component.literal(str);
    }

    public static MutableComponent coloredLiteral(String str) {
        return Component.literal(str).withStyle(style -> style.withColor(NecessitiesConfig.primaryColor.get()));
    }
}
