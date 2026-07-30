package com.daqem.necessities.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

public record Kit(Identifier id, List<ItemStackTemplate> items, long cooldown) {

    public static final Codec<Kit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.listOf().fieldOf("items").forGetter(Kit::items),
            Codec.LONG.optionalFieldOf("cooldown", 0L).forGetter(Kit::cooldown)
    ).apply(instance, (items, cooldown) -> new Kit(null, items, cooldown)));

    public List<ItemStack> createItems() {
        return items.stream().map(ItemStackTemplate::create).toList();
    }

    public Kit withId(Identifier id) {
        return new Kit(id, this.items, this.cooldown);
    }
}