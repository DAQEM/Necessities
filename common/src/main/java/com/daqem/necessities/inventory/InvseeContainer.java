package com.daqem.necessities.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InvseeContainer implements Container {
    private final Container playerInventory;

    public InvseeContainer(Container playerInventory) {
        this.playerInventory = playerInventory;
    }

    // Mapping Logic:
    // GUI 0-26 (Rows 1-3) -> Inventory 9-35 (Main Storage)
    // GUI 27-35 (Row 4)   -> Inventory 0-8 (Hotbar)
    // GUI 36-39 (Row 5)   -> Inventory 39-36 (Armor: Head, Chest, Legs, Feet)
    // GUI 40 (Row 5)      -> Inventory 40 (Offhand)
    // GUI 41-53           -> Empty

    private int mapGuiSlotToInvSlot(int guiSlot) {
        if (guiSlot >= 0 && guiSlot <= 26) {
            return guiSlot + 9;
        } else if (guiSlot >= 27 && guiSlot <= 35) {
            return guiSlot - 27;
        } else if (guiSlot >= 36 && guiSlot <= 39) {
            // 36->39 (Head), 37->38 (Chest), 38->37 (Legs), 39->36 (Feet)
            return 39 - (guiSlot - 36);
        } else if (guiSlot == 40) {
            return 40;
        }
        return -1;
    }

    private int mapInvSlotToGuiSlot(int invSlot) {
        if (invSlot >= 9 && invSlot <= 35) {
            return invSlot - 9;
        } else if (invSlot >= 0 && invSlot <= 8) {
            return invSlot + 27;
        } else if (invSlot >= 36 && invSlot <= 39) {
            return 39 - invSlot + 36;
        } else if (invSlot == 40) {
            return 40;
        }
        return -1;
    }

    @Override
    public int getContainerSize() {
        return 54; // Generic 9x6
    }

    @Override
    public boolean isEmpty() {
        return playerInventory.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        int invSlot = mapGuiSlotToInvSlot(i);
        if (invSlot != -1) {
            return playerInventory.getItem(invSlot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int j) {
        int invSlot = mapGuiSlotToInvSlot(i);
        if (invSlot != -1) {
            return playerInventory.removeItem(invSlot, j);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        int invSlot = mapGuiSlotToInvSlot(i);
        if (invSlot != -1) {
            return playerInventory.removeItemNoUpdate(invSlot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        int invSlot = mapGuiSlotToInvSlot(i);
        if (invSlot != -1) {
            playerInventory.setItem(invSlot, itemStack);
        }
    }

    @Override
    public void setChanged() {
        playerInventory.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        playerInventory.clearContent();
    }
}