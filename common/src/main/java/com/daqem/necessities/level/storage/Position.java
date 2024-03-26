package com.daqem.necessities.level.storage;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;

public class Position {

    public static final Position ZERO = new Position(0, 0, 0, 0, 0);

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public Position(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static Position deserialize(Dynamic<?> dynamic) {
        double x = dynamic.get("X").asDouble(0);
        double y = dynamic.get("Y").asDouble(0);
        double z = dynamic.get("Z").asDouble(0);
        float yaw = dynamic.get("Yaw").asFloat(0);
        float pitch = dynamic.get("Pitch").asFloat(0);
        return new Position(x, y, z, yaw, pitch);
    }

    public void serialize(CompoundTag tag) {
        tag.putDouble("X", x);
        tag.putDouble("Y", y);
        tag.putDouble("Z", z);
        tag.putFloat("Yaw", yaw);
        tag.putFloat("Pitch", pitch);
    }
}
