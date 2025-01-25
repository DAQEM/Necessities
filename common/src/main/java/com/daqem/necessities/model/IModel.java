package com.daqem.necessities.model;

import net.minecraft.nbt.CompoundTag;

public interface IModel<T extends IModel<T>> {

    T deserialize(CompoundTag tag);

    CompoundTag serialize();
}
