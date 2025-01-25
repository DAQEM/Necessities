package com.daqem.necessities.model;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class Position {

    public static final Position ZERO = new Position(0, 0, 0, 0, 0, ResourceLocation.parse("overworld"));

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
        ResourceLocation dimension = ResourceLocation.parse(dynamic.get("Dimension").asString("minecraft:overworld"));
        return new Position(x, y, z, yaw, pitch, dimension);
    }

    public static Position deserialize(CompoundTag tag) {
        double x = tag.getDouble("X");
        double y = tag.getDouble("Y");
        double z = tag.getDouble("Z");
        float yaw = tag.getFloat("Yaw");
        float pitch = tag.getFloat("Pitch");
        ResourceLocation dimension = ResourceLocation.parse(tag.getString("Dimension"));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        boolean xEquals = Double.compare(position.x, x) == 0;
        boolean yEquals = Double.compare(position.y, y) == 0;
        boolean zEquals = Double.compare(position.z, z) == 0;
        boolean yawEquals = Float.compare(position.yaw, yaw) == 0;
        boolean pitchEquals = Float.compare(position.pitch, pitch) == 0;
        boolean dimensionEquals = dimension.equals(position.dimension);
        return xEquals && yEquals && zEquals && yawEquals && pitchEquals && dimensionEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, yaw, pitch, dimension);
    }
}
