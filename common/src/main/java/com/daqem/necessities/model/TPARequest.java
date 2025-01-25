package com.daqem.necessities.model;

import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;

public class TPARequest {

    public final NecessitiesServerPlayer sender;
    public final NecessitiesServerPlayer receiver;
    public final long timestamp;
    public final boolean isHere;

    public TPARequest(NecessitiesServerPlayer sender, NecessitiesServerPlayer receiver, long timestamp, boolean isHere) {
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.isHere = isHere;
    }

    public boolean isPending() {
        return System.currentTimeMillis() - timestamp < (NecessitiesConfig.tpaTimeout.get() * 1000);
    }
}
