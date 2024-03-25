package com.daqem.necessities;

import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.event.RegisterCommandsEvent;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class Necessities {
    public static final String MOD_ID = "necessities";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        NecessitiesConfig.init();
        registerEvents();
    }

    private static void registerEvents() {
        RegisterCommandsEvent.registerEvent();
    }

    public static MutableComponent prefixedTranslatable(String str) {
        return getPrefix().append(translatable(str));
    }

    public static MutableComponent prefixedTranslatable(String str, Object... args) {
        return getPrefix().append(translatable(str, args));
    }

    public static MutableComponent translatable(String str) {
        return Component.translatable(MOD_ID + "." + str, new Object[0]);
    }

    public static MutableComponent translatable(String str, Object... args) {
        return Component.translatable(MOD_ID + "." + str, args);
    }

    public static MutableComponent getPrefix() {
        return translatable("prefix.left_bracket")
                .append(colored(translatable("prefix.name")))
                .append(translatable("prefix.right_bracket"))
                .append(translatable("prefix.space"));
    }

    public static MutableComponent colored(MutableComponent component) {
        return component.withStyle(style -> style.withColor(NecessitiesConfig.primaryColor.get()));
    }

    public static MutableComponent colored(MutableComponent component, int color) {
        return component.withStyle(style -> style.withColor(color));
    }
}
