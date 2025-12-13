package com.daqem.necessities.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Kit {

    private final ResourceLocation id;
    private final List<ItemStack> items;
    private final long cooldown;

    public static final Codec<Kit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(Kit::getItems),
            Codec.LONG.optionalFieldOf("cooldown", 0L).forGetter(Kit::getCooldown)
    ).apply(instance, (items, cooldown) -> new Kit(null, items, cooldown)));

    public Kit(ResourceLocation id, List<ItemStack> items, long cooldown) {
        this.id = id;
        this.items = items;
        this.cooldown = cooldown;
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public long getCooldown() {
        return cooldown;
    }

    public Kit withId(ResourceLocation id) {
        return new Kit(id, this.items, this.cooldown);
    }
}