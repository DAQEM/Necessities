package com.daqem.necessities;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;

public class NecessitiesPermissions {

    @ExpectPlatform
    public static boolean check(CommandSourceStack source, String permissionNode, int fallbackLevel) {
        throw new AssertionError();
    }
}