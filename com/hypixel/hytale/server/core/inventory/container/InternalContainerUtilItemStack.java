/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.TestRemoveItemSlotResult;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InternalContainerUtilItemStack {
    protected static int testAddToExistingSlot(@Nonnull ItemContainer abstractItemContainer, short slot, ItemStack itemStack, int itemMaxStack, int testQuantityRemaining, boolean filter) {
        ItemStack slotItemStack = abstractItemContainer.internal_getSlot(slot);
        if (ItemStack.isEmpty(slotItemStack)) {
            return testQuantityRemaining;
        }
        if (!slotItemStack.isStackableWith(itemStack)) {
            return testQuantityRemaining;
        }
        if (filter && abstractItemContainer.cantAddToSlot(slot, itemStack, slotItemStack)) {
            return testQuantityRemaining;
        }
        int quantity = slotItemStack.getQuantity();
        int quantityAdjustment = Math.min(itemMaxStack - quantity, testQuantityRemaining);
        return testQuantityRemaining -= quantityAdjustment;
    }

    @Nonnull
    protected static ItemStackSlotTransaction internal_addToExistingSlot(@Nonnull ItemContainer container, short slot, @Nonnull ItemStack itemStack, int itemMaxStack, boolean filter) {
        ItemStack slotItemStack = container.internal_getSlot(slot);
        if (ItemStack.isEmpty(slotItemStack)) {
            return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, false, false, filter, true, itemStack, itemStack);
        }
        if (!slotItemStack.isStackableWith(itemStack)) {
            return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, false, false, filter, true, itemStack, itemStack);
        }
        if (filter && container.cantAddToSlot(slot, itemStack, slotItemStack)) {
            return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, false, false, filter, true, itemStack, itemStack);
        }
        int quantityRemaining = itemStack.getQuantity();
        int quantity = slotItemStack.getQuantity();
        int quantityAdjustment = Math.min(itemMaxStack - quantity, quantityRemaining);
        int newQuantity = quantity + quantityAdjustment;
        quantityRemaining -= quantityAdjustment;
        if (quantityAdjustment <= 0) {
            return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, false, false, filter, true, itemStack, itemStack);
        }
        ItemStack slotNew = slotItemStack.withQuantity(newQuantity);
        if (newQuantity > 0) {
            container.internal_setSlot(slot, slotNew);
        } else {
            container.internal_removeSlot(slot);
        }
        ItemStack remainder = quantityRemaining != itemStack.getQuantity() ? itemStack.withQuantity(quantityRemaining) : itemStack;
        return new ItemStackSlotTransaction(true, ActionType.ADD, slot, slotItemStack, slotNew, null, false, false, filter, true, itemStack, remainder);
    }

    @Nonnull
    protected static ItemStackSlotTransaction internal_addToEmptySlot(@Nonnull ItemContainer container, short slot, @Nonnull ItemStack itemStack, int itemMaxStack, boolean filter) {
        ItemStack slotItemStack = container.internal_getSlot(slot);
        if (slotItemStack != null && !slotItemStack.isEmpty()) {
            return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, false, false, filter, false, itemStack, itemStack);
        }
        if (filter && container.cantAddToSlot(slot, itemStack, slotItemStack)) {
            return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, false, false, filter, false, itemStack, itemStack);
        }
        int quantityRemaining = itemStack.getQuantity();
        int quantityAdjustment = Math.min(itemMaxStack, quantityRemaining);
        ItemStack slotNew = itemStack.withQuantity(quantityAdjustment);
        container.internal_setSlot(slot, slotNew);
        ItemStack remainder = itemStack.getQuantity() != (quantityRemaining -= quantityAdjustment) ? itemStack.withQuantity(quantityRemaining) : itemStack;
        return new ItemStackSlotTransaction(true, ActionType.ADD, slot, slotItemStack, slotNew, null, false, false, filter, false, itemStack, remainder);
    }

    protected static int testAddToEmptySlots(@Nonnull ItemContainer container, ItemStack itemStack, int itemMaxStack, int testQuantityRemaining, boolean filter) {
        for (short i = 0; i < container.getCapacity() && testQuantityRemaining > 0; i = (short)(i + 1)) {
            ItemStack slotItemStack = container.internal_getSlot(i);
            if (slotItemStack != null && !slotItemStack.isEmpty() || filter && container.cantAddToSlot(i, itemStack, slotItemStack)) continue;
            int quantityAdjustment = Math.min(itemMaxStack, testQuantityRemaining);
            testQuantityRemaining -= quantityAdjustment;
        }
        return testQuantityRemaining;
    }

    protected static ItemStackSlotTransaction internal_addItemStackToSlot(@Nonnull ItemContainer itemContainer, short slot, @Nonnull ItemStack itemStack, boolean allOrNothing, boolean filter) {
        ItemContainer.validateSlotIndex(slot, itemContainer.getCapacity());
        return itemContainer.writeAction(() -> {
            int quantityRemaining = itemStack.getQuantity();
            ItemStack slotItemStack = itemContainer.internal_getSlot(slot);
            if (filter && itemContainer.cantAddToSlot(slot, itemStack, slotItemStack)) {
                return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, allOrNothing, false, filter, false, itemStack, itemStack);
            }
            if (slotItemStack == null) {
                itemContainer.internal_setSlot(slot, itemStack);
                return new ItemStackSlotTransaction(true, ActionType.ADD, slot, null, itemStack, null, allOrNothing, false, filter, false, itemStack, null);
            }
            int quantity = slotItemStack.getQuantity();
            if (!itemStack.isStackableWith(slotItemStack)) {
                return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, allOrNothing, false, filter, false, itemStack, itemStack);
            }
            int quantityAdjustment = Math.min(slotItemStack.getItem().getMaxStack() - quantity, quantityRemaining);
            int newQuantity = quantity + quantityAdjustment;
            if (allOrNothing && (quantityRemaining -= quantityAdjustment) > 0) {
                return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, allOrNothing, false, filter, false, itemStack, itemStack);
            }
            if (quantityAdjustment <= 0) {
                return new ItemStackSlotTransaction(false, ActionType.ADD, slot, slotItemStack, slotItemStack, null, allOrNothing, false, filter, false, itemStack, itemStack);
            }
            ItemStack newItemStack = slotItemStack.withQuantity(newQuantity);
            itemContainer.internal_setSlot(slot, newItemStack);
            ItemStack remainder = itemStack.withQuantity(quantityRemaining);
            return new ItemStackSlotTransaction(true, ActionType.ADD, slot, slotItemStack, newItemStack, null, allOrNothing, false, filter, false, itemStack, remainder);
        });
    }

    @Nonnull
    protected static ItemStackSlotTransaction internal_setItemStackForSlot(@Nonnull ItemContainer itemContainer, short slot, ItemStack itemStack, boolean filter) {
        ItemContainer.validateSlotIndex(slot, itemContainer.getCapacity());
        return itemContainer.writeAction(() -> {
            ItemStack slotItemStack = itemContainer.internal_getSlot(slot);
            if (filter && itemContainer.cantAddToSlot(slot, itemStack, slotItemStack)) {
                return new ItemStackSlotTransaction(false, ActionType.SET, slot, slotItemStack, slotItemStack, null, false, false, filter, false, itemStack, itemStack);
            }
            ItemStack oldItemStack = itemContainer.internal_setSlot(slot, itemStack);
            return new ItemStackSlotTransaction(true, ActionType.SET, slot, oldItemStack, itemStack, null, false, false, filter, false, itemStack, null);
        });
    }

    @Nonnull
    protected static SlotTransaction internal_removeItemStackFromSlot(@Nonnull ItemContainer itemContainer, short slot, boolean filter) {
        ItemContainer.validateSlotIndex(slot, itemContainer.getCapacity());
        return itemContainer.writeAction(() -> {
            if (filter && itemContainer.cantRemoveFromSlot(slot)) {
                ItemStack itemStack = itemContainer.internal_getSlot(slot);
                return new ItemStackSlotTransaction(false, ActionType.REMOVE, slot, itemStack, itemStack, null, false, false, filter, false, null, itemStack);
            }
            ItemStack oldItemStack = itemContainer.internal_removeSlot(slot);
            return new SlotTransaction(true, ActionType.REMOVE, slot, oldItemStack, null, oldItemStack, false, false, false);
        });
    }

    protected static ItemStackSlotTransaction internal_removeItemStackFromSlot(@Nonnull ItemContainer itemContainer, short slot, int quantityToRemove, boolean allOrNothing, boolean filter) {
        ItemContainer.validateSlotIndex(slot, itemContainer.getCapacity());
        ItemContainer.validateQuantity(quantityToRemove);
        return itemContainer.writeAction(() -> {
            int quantityRemaining = quantityToRemove;
            if (filter && itemContainer.cantRemoveFromSlot(slot)) {
                ItemStack itemStack = itemContainer.internal_getSlot(slot);
                return new ItemStackSlotTransaction(false, ActionType.REMOVE, slot, itemStack, itemStack, null, allOrNothing, false, filter, false, null, itemStack.withQuantity(quantityRemaining));
            }
            ItemStack slotItemStack = itemContainer.internal_getSlot(slot);
            if (slotItemStack == null) {
                return new ItemStackSlotTransaction(false, ActionType.REMOVE, slot, null, null, null, allOrNothing, false, filter, false, null, null);
            }
            int quantity = slotItemStack.getQuantity();
            int quantityAdjustment = Math.min(quantity, quantityRemaining);
            int newQuantity = quantity - quantityAdjustment;
            if (allOrNothing && (quantityRemaining -= quantityAdjustment) > 0) {
                return new ItemStackSlotTransaction(false, ActionType.REMOVE, slot, slotItemStack, slotItemStack, null, allOrNothing, false, filter, false, null, slotItemStack.withQuantity(quantityRemaining));
            }
            ItemStack itemStack = slotItemStack.withQuantity(newQuantity);
            itemContainer.internal_setSlot(slot, itemStack);
            ItemStack newStack = slotItemStack.withQuantity(quantityAdjustment);
            ItemStack remainder = slotItemStack.withQuantity(quantityRemaining);
            return new ItemStackSlotTransaction(true, ActionType.REMOVE, slot, slotItemStack, itemStack, newStack, allOrNothing, false, filter, false, null, remainder);
        });
    }

    protected static ItemStackSlotTransaction internal_removeItemStackFromSlot(@Nonnull ItemContainer itemContainer, short slot, @Nullable ItemStack itemStackToRemove, int quantityToRemove, boolean allOrNothing, boolean filter) {
        return InternalContainerUtilItemStack.internal_removeItemStackFromSlot(itemContainer, slot, itemStackToRemove, quantityToRemove, allOrNothing, filter, (a, b) -> ItemStack.isStackableWith(a, b));
    }

    protected static ItemStackSlotTransaction internal_removeItemStackFromSlot(@Nonnull ItemContainer itemContainer, short slot, @Nullable ItemStack itemStackToRemove, int quantityToRemove, boolean allOrNothing, boolean filter, BiPredicate<ItemStack, ItemStack> predicate) {
        ItemContainer.validateSlotIndex(slot, itemContainer.getCapacity());
        ItemContainer.validateQuantity(quantityToRemove);
        return itemContainer.writeAction(() -> {
            int quantityRemaining = quantityToRemove;
            if (filter && itemContainer.cantRemoveFromSlot(slot)) {
                ItemStack itemStack = itemContainer.internal_getSlot(slot);
                return new ItemStackSlotTransaction(false, ActionType.REMOVE, slot, itemStack, itemStack, null, allOrNothing, false, filter, false, itemStackToRemove, itemStackToRemove);
            }
            ItemStack slotItemStack = itemContainer.internal_getSlot(slot);
            if (slotItemStack == null && itemStackToRemove != null || slotItemStack != null && itemStackToRemove == null || slotItemStack != null && !predicate.test(slotItemStack, itemStackToRemove)) {
                return new ItemStackSlotTransaction(false, ActionType.REMOVE, slot, slotItemStack, slotItemStack, null, allOrNothing, false, filter, false, itemStackToRemove, itemStackToRemove);
            }
            if (slotItemStack == null) {
                return new ItemStackSlotTransaction(true, ActionType.REMOVE, slot, null, null, null, allOrNothing, false, filter, false, itemStackToRemove, itemStackToRemove);
            }
            int quantity = slotItemStack.getQuantity();
            int quantityAdjustment = Math.min(quantity, quantityRemaining);
            int newQuantity = quantity - quantityAdjustment;
            if (allOrNothing && (quantityRemaining -= quantityAdjustment) > 0) {
                return new ItemStackSlotTransaction(false, ActionType.REMOVE, slot, slotItemStack, slotItemStack, null, allOrNothing, false, filter, false, itemStackToRemove, itemStackToRemove);
            }
            ItemStack itemStack = slotItemStack.withQuantity(newQuantity);
            itemContainer.internal_setSlot(slot, itemStack);
            ItemStack newStack = slotItemStack.withQuantity(quantityAdjustment);
            ItemStack remainder = itemStackToRemove.withQuantity(quantityRemaining);
            return new ItemStackSlotTransaction(true, ActionType.REMOVE, slot, slotItemStack, itemStack, newStack, allOrNothing, false, filter, false, itemStackToRemove, remainder);
        });
    }

    protected static int testRemoveItemStackFromSlot(@Nonnull ItemContainer container, short slot, ItemStack itemStack, int testQuantityRemaining, boolean filter) {
        return InternalContainerUtilItemStack.testRemoveItemStackFromSlot(container, slot, itemStack, testQuantityRemaining, filter, (a, b) -> ItemStack.isStackableWith(a, b));
    }

    protected static int testRemoveItemStackFromSlot(@Nonnull ItemContainer container, short slot, ItemStack itemStack, int testQuantityRemaining, boolean filter, BiPredicate<ItemStack, ItemStack> predicate) {
        if (filter && container.cantRemoveFromSlot(slot)) {
            return testQuantityRemaining;
        }
        ItemStack slotItemStack = container.internal_getSlot(slot);
        if (ItemStack.isEmpty(slotItemStack)) {
            return testQuantityRemaining;
        }
        if (!predicate.test(slotItemStack, itemStack)) {
            return testQuantityRemaining;
        }
        int quantity = slotItemStack.getQuantity();
        int quantityAdjustment = Math.min(quantity, testQuantityRemaining);
        return testQuantityRemaining -= quantityAdjustment;
    }

    protected static ItemStackTransaction internal_addItemStack(@Nonnull ItemContainer itemContainer, @Nonnull ItemStack itemStack, boolean allOrNothing, boolean fullStacks, boolean filter) {
        Item item = itemStack.getItem();
        if (item == null) {
            throw new IllegalArgumentException(itemStack.getItemId() + " is an invalid item!");
        }
        int itemMaxStack = item.getMaxStack();
        return itemContainer.writeAction(() -> {
            ItemStackSlotTransaction transaction;
            short i;
            if (allOrNothing) {
                int testQuantityRemaining = itemStack.getQuantity();
                if (!fullStacks) {
                    testQuantityRemaining = InternalContainerUtilItemStack.testAddToExistingItemStacks(itemContainer, itemStack, itemMaxStack, testQuantityRemaining, filter);
                }
                if ((testQuantityRemaining = InternalContainerUtilItemStack.testAddToEmptySlots(itemContainer, itemStack, itemMaxStack, testQuantityRemaining, filter)) > 0) {
                    return new ItemStackTransaction(false, ActionType.ADD, itemStack, itemStack, allOrNothing, filter, Collections.emptyList());
                }
            }
            ObjectArrayList<ItemStackSlotTransaction> list = new ObjectArrayList<ItemStackSlotTransaction>();
            ItemStack remaining = itemStack;
            if (!fullStacks) {
                for (i = 0; i < itemContainer.getCapacity() && !ItemStack.isEmpty(remaining); i = (short)(i + 1)) {
                    transaction = InternalContainerUtilItemStack.internal_addToExistingSlot(itemContainer, i, remaining, itemMaxStack, filter);
                    list.add(transaction);
                    remaining = transaction.getRemainder();
                }
            }
            for (i = 0; i < itemContainer.getCapacity() && !ItemStack.isEmpty(remaining); i = (short)(i + 1)) {
                transaction = InternalContainerUtilItemStack.internal_addToEmptySlot(itemContainer, i, remaining, itemMaxStack, filter);
                list.add(transaction);
                remaining = transaction.getRemainder();
            }
            return new ItemStackTransaction(true, ActionType.ADD, itemStack, remaining, allOrNothing, filter, list);
        });
    }

    protected static ListTransaction<ItemStackTransaction> internal_addItemStacks(@Nonnull ItemContainer itemContainer, @Nullable List<ItemStack> itemStacks, boolean allOrNothing, boolean fullStacks, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        return itemContainer.writeAction(() -> {
            if (allOrNothing) {
                for (ItemStack itemStack : itemStacks) {
                    int itemMaxStack = itemStack.getItem().getMaxStack();
                    int testQuantityRemaining = itemStack.getQuantity();
                    if (!fullStacks) {
                        testQuantityRemaining = InternalContainerUtilItemStack.testAddToExistingItemStacks(itemContainer, itemStack, itemMaxStack, testQuantityRemaining, filter);
                    }
                    if ((testQuantityRemaining = InternalContainerUtilItemStack.testAddToEmptySlots(itemContainer, itemStack, itemMaxStack, testQuantityRemaining, filter)) <= 0) continue;
                    return new ListTransaction(false, itemStacks.stream().map(i -> new ItemStackTransaction(false, ActionType.ADD, itemStack, itemStack, allOrNothing, filter, Collections.emptyList())).collect(Collectors.toList()));
                }
            }
            ObjectArrayList remainingItemStacks = new ObjectArrayList();
            for (ItemStack itemStack : itemStacks) {
                remainingItemStacks.add(InternalContainerUtilItemStack.internal_addItemStack(itemContainer, itemStack, allOrNothing, fullStacks, filter));
            }
            return new ListTransaction(true, remainingItemStacks);
        });
    }

    protected static ListTransaction<ItemStackSlotTransaction> internal_addItemStacksOrdered(@Nonnull ItemContainer itemContainer, short offset, @Nullable List<ItemStack> itemStacks, boolean allOrNothing, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        ItemContainer.validateSlotIndex(offset, itemContainer.getCapacity());
        ItemContainer.validateSlotIndex((short)(offset + itemStacks.size()), itemContainer.getCapacity());
        return itemContainer.writeAction(() -> {
            if (allOrNothing) {
                for (int i = 0; i < itemStacks.size(); i = (int)((short)(i + 1))) {
                    short slot = (short)(offset + i);
                    ItemStack itemStack = (ItemStack)itemStacks.get(i);
                    int itemMaxStack = itemStack.getItem().getMaxStack();
                    int testQuantityRemaining = itemStack.getQuantity();
                    if ((testQuantityRemaining = InternalContainerUtilItemStack.testAddToExistingSlot(itemContainer, slot, itemStack, itemMaxStack, testQuantityRemaining, filter)) <= 0) continue;
                    ObjectArrayList list = new ObjectArrayList();
                    for (int i1 = 0; i1 < itemStacks.size(); i1 = (int)((short)(i1 + 1))) {
                        short islot = (short)(offset + i1);
                        list.add(new ItemStackSlotTransaction(false, ActionType.ADD, islot, null, null, null, allOrNothing, false, filter, false, itemStack, itemStack));
                    }
                    return new ListTransaction(false, list);
                }
            }
            ObjectArrayList remainingItemStacks = new ObjectArrayList();
            for (int i = 0; i < itemStacks.size(); i = (int)((short)(i + 1))) {
                short slot = (short)(offset + i);
                remainingItemStacks.add(InternalContainerUtilItemStack.internal_addItemStackToSlot(itemContainer, slot, (ItemStack)itemStacks.get(i), allOrNothing, filter));
            }
            return new ListTransaction(true, remainingItemStacks);
        });
    }

    protected static int testAddToExistingItemStacks(@Nonnull ItemContainer container, ItemStack itemStack, int itemMaxStack, int testQuantityRemaining, boolean filter) {
        for (short i = 0; i < container.getCapacity() && testQuantityRemaining > 0; i = (short)(i + 1)) {
            testQuantityRemaining = InternalContainerUtilItemStack.testAddToExistingSlot(container, i, itemStack, itemMaxStack, testQuantityRemaining, filter);
        }
        return testQuantityRemaining;
    }

    protected static ItemStackTransaction internal_removeItemStack(@Nonnull ItemContainer itemContainer, @Nonnull ItemStack itemStack, boolean allOrNothing, boolean filter) {
        Item item = itemStack.getItem();
        if (item == null) {
            throw new IllegalArgumentException(itemStack.getItemId() + " is an invalid item!");
        }
        return itemContainer.writeAction(() -> {
            int testQuantityRemaining;
            if (allOrNothing && (testQuantityRemaining = InternalContainerUtilItemStack.testRemoveItemStackFromItems(itemContainer, itemStack, itemStack.getQuantity(), filter)) > 0) {
                return new ItemStackTransaction(false, ActionType.REMOVE, itemStack, itemStack, allOrNothing, filter, Collections.emptyList());
            }
            ObjectArrayList<ItemStackSlotTransaction> transactions = new ObjectArrayList<ItemStackSlotTransaction>();
            int quantityRemaining = itemStack.getQuantity();
            for (short i = 0; i < itemContainer.getCapacity() && quantityRemaining > 0; i = (short)(i + 1)) {
                ItemStack slotItemStack = itemContainer.internal_getSlot(i);
                if (ItemStack.isEmpty(slotItemStack) || !slotItemStack.isStackableWith(itemStack)) continue;
                ItemStackSlotTransaction transaction = InternalContainerUtilItemStack.internal_removeItemStackFromSlot(itemContainer, i, quantityRemaining, false, filter);
                transactions.add(transaction);
                quantityRemaining = transaction.getRemainder() != null ? transaction.getRemainder().getQuantity() : 0;
            }
            ItemStack remainder = quantityRemaining > 0 ? itemStack.withQuantity(quantityRemaining) : null;
            return new ItemStackTransaction(true, ActionType.REMOVE, itemStack, remainder, allOrNothing, filter, transactions);
        });
    }

    protected static ListTransaction<ItemStackTransaction> internal_removeItemStacks(@Nonnull ItemContainer itemContainer, @Nullable List<ItemStack> itemStacks, boolean allOrNothing, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        for (ItemStack itemStack : itemStacks) {
            Item item = itemStack.getItem();
            if (item != null) continue;
            throw new IllegalArgumentException(itemStack.getItemId() + " is an invalid item!");
        }
        return itemContainer.writeAction(() -> {
            if (allOrNothing) {
                for (ItemStack itemStack : itemStacks) {
                    int testQuantityRemaining = InternalContainerUtilItemStack.testRemoveItemStackFromItems(itemContainer, itemStack, itemStack.getQuantity(), filter);
                    if (testQuantityRemaining <= 0) continue;
                    return new ListTransaction(false, itemStacks.stream().map(i -> new ItemStackTransaction(false, ActionType.ADD, itemStack, itemStack, allOrNothing, filter, Collections.emptyList())).collect(Collectors.toList()));
                }
            }
            ObjectArrayList transactions = new ObjectArrayList();
            for (int i2 = 0; i2 < itemStacks.size(); i2 = (int)((short)(i2 + 1))) {
                transactions.add(InternalContainerUtilItemStack.internal_removeItemStack(itemContainer, (ItemStack)itemStacks.get(i2), allOrNothing, filter));
            }
            return new ListTransaction(true, transactions);
        });
    }

    protected static int testRemoveItemStackFromItems(@Nonnull ItemContainer container, ItemStack itemStack, int testQuantityRemaining, boolean filter) {
        for (short i = 0; i < container.getCapacity() && testQuantityRemaining > 0; i = (short)(i + 1)) {
            ItemStack slotItemStack;
            if (filter && container.cantRemoveFromSlot(i) || ItemStack.isEmpty(slotItemStack = container.internal_getSlot(i)) || !slotItemStack.isStackableWith(itemStack)) continue;
            int quantity = slotItemStack.getQuantity();
            int quantityAdjustment = Math.min(quantity, testQuantityRemaining);
            testQuantityRemaining -= quantityAdjustment;
        }
        return testQuantityRemaining;
    }

    protected static TestRemoveItemSlotResult testRemoveItemStackSlotFromItems(@Nonnull ItemContainer container, ItemStack itemStack, int testQuantityRemaining, boolean filter) {
        return InternalContainerUtilItemStack.testRemoveItemStackSlotFromItems(container, itemStack, testQuantityRemaining, filter, (a, b) -> ItemStack.isStackableWith(a, b));
    }

    protected static TestRemoveItemSlotResult testRemoveItemStackSlotFromItems(@Nonnull ItemContainer container, ItemStack itemStack, int testQuantityRemaining, boolean filter, BiPredicate<ItemStack, ItemStack> predicate) {
        TestRemoveItemSlotResult result = new TestRemoveItemSlotResult(testQuantityRemaining);
        for (short i = 0; i < container.getCapacity() && result.quantityRemaining > 0; i = (short)(i + 1)) {
            ItemStack slotItemStack;
            if (filter && container.cantRemoveFromSlot(i) || ItemStack.isEmpty(slotItemStack = container.internal_getSlot(i)) || !predicate.test(slotItemStack, itemStack)) continue;
            int quantity = slotItemStack.getQuantity();
            int quantityAdjustment = Math.min(quantity, result.quantityRemaining);
            result.quantityRemaining -= quantityAdjustment;
            result.picked.put(i, quantityAdjustment);
        }
        return result;
    }
}

