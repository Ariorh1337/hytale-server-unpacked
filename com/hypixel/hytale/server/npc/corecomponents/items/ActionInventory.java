/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.items;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.items.builders.BuilderActionInventory;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import java.util.EnumSet;
import java.util.function.Supplier;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionInventory
extends ActionBase {
    private static final EnumSet<Operation> ITEM_FREE_OPERATIONS = EnumSet.of(Operation.ClearHeldItem, Operation.RemoveHeldItem, Operation.EquipHotbar, Operation.EquipOffHand);
    protected final Operation operation;
    protected final String item;
    protected final int count;
    protected final boolean useTarget;
    protected final byte slot;

    public ActionInventory(@Nonnull BuilderActionInventory builder, @Nonnull BuilderSupport support) {
        super(builder);
        this.operation = builder.getOperation(support);
        this.count = builder.getCount(support);
        this.item = builder.getItem(support);
        this.useTarget = builder.getUseTarget(support);
        this.slot = (byte)builder.getSlot(support);
    }

    @Override
    public boolean canExecute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nullable InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        if (!super.canExecute(ref, role, sensorInfo, dt, store)) {
            return false;
        }
        return (!this.useTarget || sensorInfo != null && sensorInfo.hasPosition()) && (ITEM_FREE_OPERATIONS.contains(this.operation) || this.item != null && !this.item.isEmpty());
    }

    @Override
    public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nonnull InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);
        Ref<EntityStore> entityRef = this.useTarget ? sensorInfo.getPositionProvider().getTarget() : ref;
        LivingEntity entity = (LivingEntity)EntityUtils.getEntity(entityRef, store);
        if (entity == null) {
            return false;
        }
        Inventory inventory = entity.getInventory();
        if (this.operation == Operation.ClearHeldItem) {
            InventoryHelper.clearItemInHand(inventory, (byte)-1);
            return true;
        }
        if (this.operation == Operation.RemoveHeldItem) {
            InventoryHelper.removeItemInHand(inventory, this.count);
            return true;
        }
        if (this.operation == Operation.EquipHotbar && (this.item == null || this.item.isEmpty())) {
            if (inventory.getActiveHotbarSlot() == this.slot) {
                return true;
            }
            if (InventoryHelper.checkHotbarSlot(inventory, this.slot)) {
                inventory.setActiveHotbarSlot(this.slot);
            }
            return true;
        }
        if (this.operation == Operation.EquipOffHand && (this.item == null || this.item.isEmpty())) {
            InventoryHelper.setOffHandSlot(inventory, this.slot);
            return true;
        }
        String itemStackKey = this.item;
        if (itemStackKey == null || "Empty".equals(itemStackKey) || "Unknown".equals(itemStackKey) || !ItemModule.exists(itemStackKey)) {
            NPCPlugin.get().getLogger().at(Level.WARNING).log("Unknown item %s in Inventory action", this.item);
            return true;
        }
        CombinedItemContainer combinedStorage = inventory.getCombinedHotbarFirst();
        ItemStack itemStack = new ItemStack(itemStackKey, this.count);
        switch (this.operation.ordinal()) {
            case 0: {
                if (this.count <= 0) break;
                combinedStorage.addItemStack(itemStack);
                break;
            }
            case 1: {
                if (this.count <= 0) break;
                combinedStorage.removeItemStack(itemStack);
                break;
            }
            case 2: {
                Item item = itemStack.getItem();
                if (item.getArmor() != null) {
                    InventoryHelper.useArmor(inventory.getArmor(), itemStack);
                    break;
                }
                InventoryHelper.useItem(inventory, item.getId());
                break;
            }
            case 5: {
                if (!InventoryHelper.checkHotbarSlot(inventory, this.slot)) break;
                inventory.getHotbar().setItemStackForSlot(this.slot, itemStack);
                break;
            }
            case 7: {
                if (!InventoryHelper.checkOffHandSlot(inventory, this.slot)) break;
                inventory.getUtility().setItemStackForSlot(this.slot, itemStack);
                break;
            }
            case 6: {
                if (InventoryHelper.checkHotbarSlot(inventory, this.slot)) {
                    inventory.getHotbar().setItemStackForSlot(this.slot, itemStack);
                }
                if (inventory.getActiveHotbarSlot() == this.slot || !InventoryHelper.checkHotbarSlot(inventory, this.slot)) break;
                inventory.setActiveHotbarSlot(this.slot);
                break;
            }
            case 8: {
                if (InventoryHelper.checkOffHandSlot(inventory, this.slot)) {
                    inventory.getUtility().setItemStackForSlot(this.slot, itemStack);
                }
                InventoryHelper.setOffHandSlot(inventory, this.slot);
            }
        }
        return true;
    }

    public static enum Operation implements Supplier<String>
    {
        Add("Add items to inventory"),
        Remove("Remove items from inventory"),
        Equip("Equip item as weapon or armour"),
        ClearHeldItem("Clear the held item"),
        RemoveHeldItem("Destroy the held item"),
        SetHotbar("Sets the hotbar item in a specific slot"),
        EquipHotbar("Equips the item from a specific hotbar slot"),
        SetOffHand("Sets the off-hand item in a specific slot"),
        EquipOffHand("Equips the item from a specific off-hand slot");

        private final String description;

        private Operation(String description) {
            this.description = description;
        }

        @Override
        public String get() {
            return this.description;
        }
    }
}

