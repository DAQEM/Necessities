package com.daqem.necessities.model;

import java.util.function.Consumer;

import com.daqem.necessities.level.NecessitiesServerPlayer;

public record DelayedTeleport(Position target, Position startPos, long executeAt, String cooldownType, int cooldownSeconds, Consumer<NecessitiesServerPlayer> onComplete) {
}
