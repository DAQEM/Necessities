package com.daqem.necessities.forge;

import dev.architectury.platform.forge.EventBuses;
import com.daqem.necessities.Necessities;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Necessities.MOD_ID)
public class NecessitiesForge {
    public NecessitiesForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Necessities.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Necessities.init();
    }
}
