package com.daqem.necessities.model;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class Position {

    public static final Position ZERO = new Position(0, 0, 0, 0, 0, new ResourceLocation("minecraft", "overworld"));

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public ResourceLocation dimension;

    public Position(double x, double y, double z, float yaw, float pitch, ResourceLocation dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }

    public static Position deserialize(Dynamic<?> dynamic) {
        double x = dynamic.get("X").asDouble(0);
        double y = dynamic.get("Y").asDouble(0);
        double z = dynamic.get("Z").asDouble(0);
        float yaw = dynamic.get("Yaw").asFloat(0);
        float pitch = dynamic.get("Pitch").asFloat(0);
        ResourceLocation dimension = new ResourceLocation(dynamic.get("Dimension").asString("minecraft:overworld"));
        return new Position(x, y, z, yaw, pitch, dimension);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("X", x);
        tag.putDouble("Y", y);
        tag.putDouble("Z", z);
        tag.putFloat("Yaw", yaw);
        tag.putFloat("Pitch", pitch);
        tag.putString("Dimension", dimension.toString());
        return tag;
    }
}
