package com.daqem.necessities.fabric;

import com.daqem.necessities.Necessities;
import net.fabricmc.api.ModInitializer;

public class NecessitiesCommonFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Necessities.init();
    }

}
