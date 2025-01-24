package com.daqem.necessities.model;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;

public class Home {

    public final String name;
    public final Position position;

    public Home(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public static Home deserialize(CompoundTag tag) {
        String name = tag.getString("Name");
        Position position = Position.deserialize(tag.getCompound("Position"));
        return new Home(name, position);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);
        tag.put("Position", position.serialize());
        return tag;
    }
}
