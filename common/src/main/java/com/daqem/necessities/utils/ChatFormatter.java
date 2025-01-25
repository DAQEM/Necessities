package com.daqem.necessities.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;

public class ChatFormatter {

    public static Component formatTest(String input) {
        char[] b = input.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return Component.literal(new String(b));
    }

    public static MutableComponent flattenToLiteral(Component component) {
        StringBuilder builder = new StringBuilder();
        component.getContents().visit(string -> {
            builder.append(string);
            return Optional.empty();
        });
        MutableComponent literalComponent = Component.literal(builder.toString()).withStyle(component.getStyle());

        for (Component sibling : component.getSiblings()) {
            literalComponent.append(flattenToLiteral(sibling));
        }

        return literalComponent;
    }
}
