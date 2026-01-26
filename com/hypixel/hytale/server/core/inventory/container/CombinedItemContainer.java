/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.inventory.transaction.ClearTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CombinedItemContainer
extends ItemContainer {
    protected final ItemContainer[] containers;

    public CombinedItemContainer(ItemContainer ... containers) {
        this.containers = containers;
    }

    public ItemContainer getContainer(int index) {
        return this.containers[index];
    }

    public int getContainersSize() {
        return this.containers.length;
    }

    @Nullable
    public ItemContainer getContainerForSlot(short slot) {
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                return container;
            }
            slot = (short)(slot - capacity);
        }
        return null;
    }

    @Override
    protected <V> V readAction(@Nonnull Supplier<V> action) {
        return this.readAction0(0, action);
    }

    private <V> V readAction0(int i, @Nonnull Supplier<V> action) {
        if (i >= this.containers.length) {
            return action.get();
        }
        return (V)this.containers[i].readAction(() -> this.readAction0(i + 1, action));
    }

    @Override
    protected <X, V> V readAction(@Nonnull Function<X, V> action, X x) {
        return this.readAction0(0, action, x);
    }

    private <X, V> V readAction0(int i, @Nonnull Function<X, V> action, X x) {
        if (i >= this.containers.length) {
            return action.apply(x);
        }
        return (V)this.containers[i].readAction(() -> this.readAction0(i + 1, action, x));
    }

    @Override
    protected <V> V writeAction(@Nonnull Supplier<V> action) {
        return this.writeAction0(0, action);
    }

    private <V> V writeAction0(int i, @Nonnull Supplier<V> action) {
        if (i >= this.containers.length) {
            return action.get();
        }
        return (V)this.containers[i].writeAction(() -> this.writeAction0(i + 1, action));
    }

    @Override
    protected <X, V> V writeAction(@Nonnull Function<X, V> action, X x) {
        return this.writeAction0(0, action, x);
    }

    private <X, V> V writeAction0(int i, @Nonnull Function<X, V> action, X x) {
        if (i >= this.containers.length) {
            return action.apply(x);
        }
        return (V)this.containers[i].writeAction(() -> this.writeAction0(i + 1, action, x));
    }

    @Override
    @Nonnull
    protected ClearTransaction internal_clear() {
        ItemStack[] itemStacks = new ItemStack[this.getCapacity()];
        int start = 0;
        for (ItemContainer container : this.containers) {
            ClearTransaction clear = container.internal_clear();
            ItemStack[] items = clear.getItems();
            for (int slot = 0; slot < itemStacks.length; slot = (int)((short)(slot + 1))) {
                itemStacks[(short)(start + slot)] = items[slot];
            }
            start = (short)(start + container.getCapacity());
        }
        return new ClearTransaction(true, 0, itemStacks);
    }

    @Override
    @Nullable
    protected ItemStack internal_getSlot(short slot) {
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                return container.internal_getSlot(slot);
            }
            slot = (short)(slot - capacity);
        }
        return null;
    }

    @Override
    @Nullable
    protected ItemStack internal_setSlot(short slot, ItemStack itemStack) {
        if (ItemStack.isEmpty(itemStack)) {
            return this.internal_removeSlot(slot);
        }
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                return container.internal_setSlot(slot, itemStack);
            }
            slot = (short)(slot - capacity);
        }
        return null;
    }

    @Override
    @Nullable
    protected ItemStack internal_removeSlot(short slot) {
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                return container.internal_removeSlot(slot);
            }
            slot = (short)(slot - capacity);
        }
        return null;
    }

    @Override
    protected boolean cantAddToSlot(short slot, ItemStack itemStack, ItemStack slotItemStack) {
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                return container.cantAddToSlot(slot, itemStack, slotItemStack);
            }
            slot = (short)(slot - capacity);
        }
        return true;
    }

    @Override
    protected boolean cantRemoveFromSlot(short slot) {
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                return container.cantRemoveFromSlot(slot);
            }
            slot = (short)(slot - capacity);
        }
        return true;
    }

    @Override
    protected boolean cantDropFromSlot(short slot) {
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                return container.cantDropFromSlot(slot);
            }
            slot = (short)(slot - capacity);
        }
        return true;
    }

    @Override
    protected boolean cantMoveToSlot(ItemContainer fromContainer, short slotFrom) {
        for (ItemContainer container : this.containers) {
            boolean cantMoveToSlot = container.cantMoveToSlot(fromContainer, slotFrom);
            if (!cantMoveToSlot) continue;
            return true;
        }
        return false;
    }

    @Override
    public short getCapacity() {
        short capacity = 0;
        for (ItemContainer container : this.containers) {
            capacity = (short)(capacity + container.getCapacity());
        }
        return capacity;
    }

    @Override
    public CombinedItemContainer clone() {
        throw new UnsupportedOperationException("clone() is not supported for CombinedItemContainer");
    }

    @Override
    @Nonnull
    public EventRegistration registerChangeEvent(short priority, @Nonnull Consumer<ItemContainer.ItemContainerChangeEvent> consumer) {
        EventRegistration thisRegistration = super.registerChangeEvent(priority, consumer);
        EventRegistration[] containerRegistrations = new EventRegistration[this.containers.length];
        short start = 0;
        for (int i = 0; i < this.containers.length; ++i) {
            ItemContainer container = this.containers[i];
            short finalStart = start;
            containerRegistrations[i] = container.internalChangeEventRegistry.register(priority, null, event -> consumer.accept(new ItemContainer.ItemContainerChangeEvent(this, event.transaction().toParent(this, finalStart, container))));
            start = (short)(start + container.getCapacity());
        }
        return EventRegistration.combine(thisRegistration, containerRegistrations);
    }

    @Override
    protected void sendUpdate(@Nonnull Transaction transaction) {
        if (!transaction.succeeded()) {
            return;
        }
        super.sendUpdate(transaction);
        short start = 0;
        for (ItemContainer container : this.containers) {
            Transaction containerTransaction = transaction.fromParent(this, start, container);
            if (containerTransaction != null) {
                if (!containerTransaction.succeeded()) {
                    start = (short)(start + container.getCapacity());
                    continue;
                }
                container.sendUpdate(containerTransaction);
            }
            start = (short)(start + container.getCapacity());
        }
    }

    @Override
    public boolean containsContainer(ItemContainer itemContainer) {
        if (itemContainer == this) {
            return true;
        }
        for (ItemContainer container : this.containers) {
            if (!container.containsContainer(itemContainer)) continue;
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CombinedItemContainer)) {
            return false;
        }
        CombinedItemContainer that = (CombinedItemContainer)o;
        short capacity = this.getCapacity();
        if (capacity != that.getCapacity()) {
            return false;
        }
        return this.readAction(_that -> _that.readAction(_that2 -> {
            for (short i = 0; i < capacity; i = (short)(i + 1)) {
                if (Objects.equals(this.internal_getSlot(i), _that2.internal_getSlot(i))) continue;
                return false;
            }
            return true;
        }, _that), that);
    }

    public int hashCode() {
        short capacity = this.getCapacity();
        int result = this.readAction(() -> {
            int hash = 0;
            for (short i = 0; i < capacity; i = (short)(i + 1)) {
                ItemStack itemStack = this.internal_getSlot(i);
                hash = 31 * hash + (itemStack != null ? itemStack.hashCode() : 0);
            }
            return hash;
        });
        result = 31 * result + capacity;
        return result;
    }

    @Override
    public void setGlobalFilter(FilterType globalFilter) {
        throw new UnsupportedOperationException("setGlobalFilter(FilterType) is not supported in CombinedItemContainer");
    }

    @Override
    public void setSlotFilter(FilterActionType actionType, short slot, SlotFilter filter) {
        for (ItemContainer container : this.containers) {
            short capacity = container.getCapacity();
            if (slot < capacity) {
                container.setSlotFilter(actionType, slot, filter);
                return;
            }
            slot = (short)(slot - capacity);
        }
    }
}

