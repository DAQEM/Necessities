package com.daqem.necessities.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ServerPlayerData(
        List<Home> homes,
        Position lastPosition,
        boolean acceptsTPARequests,
        String nick,
        boolean hasGodMode,
        boolean vanished,
        long lastRTPTime,
        Map<ResourceLocation, Long> kitCooldowns

) {
    public static final Codec<ServerPlayerData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Home.CODEC.listOf().fieldOf("Homes").forGetter(ServerPlayerData::homes),
                    Position.CODEC.fieldOf("LastPosition").forGetter(ServerPlayerData::lastPosition),
                    Codec.BOOL.fieldOf("AcceptsTPARequests").forGetter(ServerPlayerData::acceptsTPARequests),
                    Codec.STRING.fieldOf("Nick").forGetter(ServerPlayerData::nick),
                    Codec.BOOL.fieldOf("GodMode").forGetter(ServerPlayerData::hasGodMode),
                    Codec.BOOL.optionalFieldOf("Vanished", false).forGetter(ServerPlayerData::vanished),
                    Codec.LONG.fieldOf("LastRTPTime").forGetter(ServerPlayerData::lastRTPTime),
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.LONG).optionalFieldOf("KitCooldowns", new HashMap<>()).forGetter(ServerPlayerData::kitCooldowns)
            ).apply(instance, ServerPlayerData::new)
    );
}