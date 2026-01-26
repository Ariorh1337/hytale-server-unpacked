/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilItemStack;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilResource;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilTag;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.TestRemoveItemSlotResult;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ResourceSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.TagSlotTransaction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InternalContainerUtilMaterial {
    @Nonnull
    protected static MaterialSlotTransaction internal_removeMaterialFromSlot(@Nonnull ItemContainer itemContainer, short slot, @Nonnull MaterialQuantity material, boolean allOrNothing, boolean filter) {
        ItemContainer.validateSlotIndex(slot, itemContainer.getCapacity());
        ItemContainer.validateQuantity(material.getQuantity());
        if (material.getItemId() != null) {
            ItemStackSlotTransaction slotTransaction = InternalContainerUtilItemStack.internal_removeItemStackFromSlot(itemContainer, slot, material.toItemStack(), material.getQuantity(), allOrNothing, filter, (a, b) -> ItemStack.isEquivalentType(a, b));
            return new MaterialSlotTransaction(material, slotTransaction.getRemainder() != null ? slotTransaction.getRemainder().getQuantity() : 0, slotTransaction);
        }
        if (material.getTagIndex() != Integer.MIN_VALUE) {
            TagSlotTransaction tagTransaction = InternalContainerUtilTag.internal_removeTagFromSlot(itemContainer, slot, material.getTagIndex(), material.getQuantity(), allOrNothing, filter);
            return new MaterialSlotTransaction(material, tagTransaction.getRemainder(), tagTransaction);
        }
        ResourceSlotTransaction resourceTransaction = InternalContainerUtilResource.internal_removeResourceFromSlot(itemContainer, slot, material.toResource(), allOrNothing, filter);
        return new MaterialSlotTransaction(material, resourceTransaction.getRemainder(), resourceTransaction);
    }

    protected static MaterialTransaction internal_removeMaterial(@Nonnull ItemContainer itemContainer, @Nonnull MaterialQuantity material, boolean allOrNothing, boolean exactAmount, boolean filter) {
        return itemContainer.writeAction(() -> {
            if (allOrNothing || exactAmount) {
                int testQuantityRemaining = InternalContainerUtilMaterial.testRemoveMaterialFromItems(itemContainer, material, material.getQuantity(), filter);
                if (testQuantityRemaining > 0) {
                    return new MaterialTransaction(false, ActionType.REMOVE, material, material.getQuantity(), allOrNothing, exactAmount, filter, Collections.emptyList());
                }
                if (exactAmount && testQuantityRemaining < 0) {
                    return new MaterialTransaction(false, ActionType.REMOVE, material, material.getQuantity(), allOrNothing, exactAmount, filter, Collections.emptyList());
                }
            }
            ObjectArrayList<MaterialSlotTransaction> list = new ObjectArrayList<MaterialSlotTransaction>();
            int quantityRemaining = material.getQuantity();
            for (short i = 0; i < itemContainer.getCapacity() && quantityRemaining > 0; i = (short)(i + 1)) {
                MaterialQuantity clone = material.clone(quantityRemaining);
                MaterialSlotTransaction transaction = InternalContainerUtilMaterial.internal_removeMaterialFromSlot(itemContainer, i, clone, false, filter);
                if (!transaction.succeeded()) continue;
                list.add(transaction);
                quantityRemaining = transaction.getRemainder();
            }
            return new MaterialTransaction(quantityRemaining != material.getQuantity(), ActionType.REMOVE, material, material.getQuantity(), allOrNothing, exactAmount, filter, list);
        });
    }

    protected static ListTransaction<MaterialTransaction> internal_removeMaterials(@Nonnull ItemContainer itemContainer, @Nullable List<MaterialQuantity> materials, boolean allOrNothing, boolean exactAmount, boolean filter) {
        if (materials == null || materials.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        return itemContainer.writeAction(() -> {
            if (allOrNothing || exactAmount) {
                for (MaterialQuantity material : materials) {
                    int testQuantityRemaining = InternalContainerUtilMaterial.testRemoveMaterialFromItems(itemContainer, material, material.getQuantity(), filter);
                    if (testQuantityRemaining > 0) {
                        return new ListTransaction(false, materials.stream().map(remainder -> new MaterialTransaction(false, ActionType.REMOVE, material, material.getQuantity(), allOrNothing, exactAmount, filter, Collections.emptyList())).collect(Collectors.toList()));
                    }
                    if (!exactAmount || testQuantityRemaining >= 0) continue;
                    return new ListTransaction(false, materials.stream().map(remainder -> new MaterialTransaction(false, ActionType.REMOVE, material, material.getQuantity(), allOrNothing, exactAmount, filter, Collections.emptyList())).collect(Collectors.toList()));
                }
            }
            ObjectArrayList transactions = new ObjectArrayList();
            for (MaterialQuantity material : materials) {
                transactions.add(InternalContainerUtilMaterial.internal_removeMaterial(itemContainer, material, allOrNothing, exactAmount, filter));
            }
            return new ListTransaction(true, transactions);
        });
    }

    public static int testRemoveMaterialFromItems(@Nonnull ItemContainer container, @Nonnull MaterialQuantity material, int testQuantityRemaining, boolean filter) {
        if (material.getItemId() != null) {
            return InternalContainerUtilItemStack.testRemoveItemStackFromItems(container, material.toItemStack(), testQuantityRemaining, filter);
        }
        if (material.getTagIndex() != Integer.MIN_VALUE) {
            return InternalContainerUtilTag.testRemoveTagFromItems(container, material.getTagIndex(), testQuantityRemaining, filter);
        }
        return InternalContainerUtilResource.testRemoveResourceFromItems(container, material.toResource(), testQuantityRemaining, filter);
    }

    public static TestRemoveItemSlotResult getTestRemoveMaterialFromItems(@Nonnull ItemContainer container, @Nonnull MaterialQuantity material, int testQuantityRemaining, boolean filter) {
        if (material.getItemId() != null) {
            return InternalContainerUtilItemStack.testRemoveItemStackSlotFromItems(container, material.toItemStack(), testQuantityRemaining, filter, (a, b) -> ItemStack.isEquivalentType(a, b));
        }
        if (material.getTagIndex() != Integer.MIN_VALUE) {
            return InternalContainerUtilTag.testRemoveTagSlotFromItems(container, material.getTagIndex(), testQuantityRemaining, filter);
        }
        return InternalContainerUtilResource.testRemoveResourceSlotFromItems(container, material.toResource(), testQuantityRemaining, filter);
    }

    protected static ListTransaction<MaterialSlotTransaction> internal_removeMaterialsOrdered(@Nonnull ItemContainer itemContainer, short offset, @Nullable List<MaterialQuantity> materials, boolean allOrNothing, boolean exactAmount, boolean filter) {
        if (materials == null || materials.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        if (offset + materials.size() > itemContainer.getCapacity()) {
            return ListTransaction.getEmptyTransaction(false);
        }
        return itemContainer.writeAction(() -> {
            if (allOrNothing || exactAmount) {
                for (int i = 0; i < materials.size(); i = (int)((short)(i + 1))) {
                    short slot = (short)(offset + i);
                    MaterialQuantity material = (MaterialQuantity)materials.get(i);
                    int testQuantityRemaining = InternalContainerUtilMaterial.testRemoveMaterialFromSlot(itemContainer, slot, material, material.getQuantity(), filter);
                    if (testQuantityRemaining > 0) {
                        ObjectArrayList list = new ObjectArrayList();
                        for (int i1 = 0; i1 < materials.size(); i1 = (int)((short)(i1 + 1))) {
                            short islot = (short)(offset + i1);
                            list.add(new MaterialSlotTransaction(material, material.getQuantity(), new SlotTransaction(false, ActionType.REMOVE, islot, null, null, null, allOrNothing, exactAmount, filter)));
                        }
                        return new ListTransaction(false, list);
                    }
                    if (!exactAmount || testQuantityRemaining >= 0) continue;
                    ObjectArrayList list = new ObjectArrayList();
                    for (int i1 = 0; i1 < materials.size(); i1 = (int)((short)(i1 + 1))) {
                        short islot = (short)(offset + i1);
                        list.add(new MaterialSlotTransaction(material, material.getQuantity(), new SlotTransaction(false, ActionType.REMOVE, islot, null, null, null, allOrNothing, exactAmount, filter)));
                    }
                    return new ListTransaction(false, list);
                }
            }
            ObjectArrayList transactions = new ObjectArrayList();
            for (int i = 0; i < materials.size(); i = (int)((short)(i + 1))) {
                short slot = (short)(offset + i);
                MaterialQuantity material = (MaterialQuantity)materials.get(i);
                transactions.add(InternalContainerUtilMaterial.internal_removeMaterialFromSlot(itemContainer, slot, material, allOrNothing, filter));
            }
            return new ListTransaction(true, transactions);
        });
    }

    public static int testRemoveMaterialFromSlot(@Nonnull ItemContainer container, short slot, @Nonnull MaterialQuantity material, int testQuantityRemaining, boolean filter) {
        if (material.getItemId() != null) {
            return InternalContainerUtilItemStack.testRemoveItemStackFromSlot(container, slot, material.toItemStack(), testQuantityRemaining, filter, (a, b) -> ItemStack.isEquivalentType(a, b));
        }
        if (material.getTagIndex() != Integer.MIN_VALUE) {
            return InternalContainerUtilTag.testRemoveTagFromSlot(container, slot, material.getTagIndex(), testQuantityRemaining, filter);
        }
        return InternalContainerUtilResource.testRemoveResourceFromSlot(container, slot, material.toResource(), testQuantityRemaining, filter);
    }
}

