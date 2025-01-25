package com.daqem.necessities.neoforge;

import com.daqem.necessities.Necessities;
import dev.architectury.utils.EnvExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Necessities.MOD_ID)
public class NecessitiesNeoForge {
    public NecessitiesNeoForge() {
        EnvExecutor.getEnvSpecific(
                () -> SideProxyNeoForge.Client::new,
                () -> SideProxyNeoForge.Server::new
        );
    }
}
