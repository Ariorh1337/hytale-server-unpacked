/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.inventory.transaction.ClearTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DelegateItemContainer<T extends ItemContainer>
extends ItemContainer {
    private T delegate;
    private final Map<FilterActionType, Int2ObjectConcurrentHashMap<SlotFilter>> slotFilters = new ConcurrentHashMap<FilterActionType, Int2ObjectConcurrentHashMap<SlotFilter>>();
    @Nonnull
    private FilterType globalFilter = FilterType.ALLOW_ALL;

    public DelegateItemContainer(T delegate) {
        Objects.requireNonNull(delegate, "Delegate can't be null!");
        this.delegate = delegate;
    }

    public T getDelegate() {
        return this.delegate;
    }

    @Override
    protected <V> V readAction(Supplier<V> action) {
        return ((ItemContainer)this.delegate).readAction(action);
    }

    @Override
    protected <X, V> V readAction(Function<X, V> action, X x) {
        return ((ItemContainer)this.delegate).readAction(action, x);
    }

    @Override
    protected <V> V writeAction(Supplier<V> action) {
        return ((ItemContainer)this.delegate).writeAction(action);
    }

    @Override
    protected <X, V> V writeAction(Function<X, V> action, X x) {
        return ((ItemContainer)this.delegate).writeAction(action, x);
    }

    @Override
    protected ClearTransaction internal_clear() {
        return ((ItemContainer)this.delegate).internal_clear();
    }

    @Override
    protected ItemStack internal_getSlot(short slot) {
        return ((ItemContainer)this.delegate).internal_getSlot(slot);
    }

    @Override
    protected ItemStack internal_setSlot(short slot, ItemStack itemStack) {
        return ((ItemContainer)this.delegate).internal_setSlot(slot, itemStack);
    }

    @Override
    protected ItemStack internal_removeSlot(short slot) {
        return ((ItemContainer)this.delegate).internal_removeSlot(slot);
    }

    @Override
    protected boolean cantAddToSlot(short slot, ItemStack itemStack, ItemStack slotItemStack) {
        if (!this.globalFilter.allowInput()) {
            return true;
        }
        if (this.testFilter(FilterActionType.ADD, slot, itemStack)) {
            return true;
        }
        return ((ItemContainer)this.delegate).cantAddToSlot(slot, itemStack, slotItemStack);
    }

    @Override
    protected boolean cantRemoveFromSlot(short slot) {
        if (!this.globalFilter.allowOutput()) {
            return true;
        }
        if (this.testFilter(FilterActionType.REMOVE, slot, null)) {
            return true;
        }
        return ((ItemContainer)this.delegate).cantRemoveFromSlot(slot);
    }

    @Override
    protected boolean cantDropFromSlot(short slot) {
        if (this.testFilter(FilterActionType.DROP, slot, null)) {
            return true;
        }
        return ((ItemContainer)this.delegate).cantDropFromSlot(slot);
    }

    @Override
    protected boolean cantMoveToSlot(ItemContainer fromContainer, short slotFrom) {
        return ((ItemContainer)this.delegate).cantMoveToSlot(fromContainer, slotFrom);
    }

    private boolean testFilter(FilterActionType actionType, short slot, ItemStack itemStack) {
        Int2ObjectConcurrentHashMap<SlotFilter> map = this.slotFilters.get((Object)actionType);
        if (map == null) {
            return false;
        }
        SlotFilter filter = map.get(slot);
        if (filter == null) {
            return false;
        }
        return !filter.test(actionType, this, slot, itemStack);
    }

    @Override
    public short getCapacity() {
        return ((ItemContainer)this.delegate).getCapacity();
    }

    @Override
    public ClearTransaction clear() {
        return ((ItemContainer)this.delegate).clear();
    }

    @Override
    @Nonnull
    public DelegateItemContainer<T> clone() {
        return new DelegateItemContainer<T>(this.delegate);
    }

    @Override
    public boolean isEmpty() {
        return ((ItemContainer)this.delegate).isEmpty();
    }

    @Override
    public void setGlobalFilter(@Nonnull FilterType globalFilter) {
        this.globalFilter = Objects.requireNonNull(globalFilter);
    }

    @Override
    public void setSlotFilter(FilterActionType actionType, short slot, @Nullable SlotFilter filter) {
        DelegateItemContainer.validateSlotIndex(slot, this.getCapacity());
        if (filter != null) {
            this.slotFilters.computeIfAbsent(actionType, k -> new Int2ObjectConcurrentHashMap()).put(slot, filter);
        } else {
            this.slotFilters.computeIfPresent(actionType, (k, map) -> {
                map.remove(slot);
                return map.isEmpty() ? null : map;
            });
        }
    }

    @Override
    @Nonnull
    public EventRegistration registerChangeEvent(short priority, @Nonnull Consumer<ItemContainer.ItemContainerChangeEvent> consumer) {
        EventRegistration thisRegistration = super.registerChangeEvent(priority, consumer);
        EventRegistration[] delegateRegistration = new EventRegistration[]{((ItemContainer)this.delegate).internalChangeEventRegistry.register(priority, null, event -> consumer.accept(new ItemContainer.ItemContainerChangeEvent(this, event.transaction().toParent(this, (short)0, (ItemContainer)this.delegate))))};
        return EventRegistration.combine(thisRegistration, delegateRegistration);
    }

    @Override
    protected void sendUpdate(@Nonnull Transaction transaction) {
        if (!transaction.succeeded()) {
            return;
        }
        super.sendUpdate(transaction);
        ((ItemContainer)this.delegate).externalChangeEventRegistry.dispatchFor(null).dispatch(new ItemContainer.ItemContainerChangeEvent((ItemContainer)this.delegate, transaction));
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DelegateItemContainer that = (DelegateItemContainer)o;
        if (this.delegate != null ? !this.delegate.equals(that.delegate) : that.delegate != null) {
            return false;
        }
        if (this.slotFilters != null ? !this.slotFilters.equals(that.slotFilters) : that.slotFilters != null) {
            return false;
        }
        return this.globalFilter == that.globalFilter;
    }

    public int hashCode() {
        int result = this.delegate != null ? this.delegate.hashCode() : 0;
        result = 31 * result + (this.slotFilters != null ? this.slotFilters.hashCode() : 0);
        result = 31 * result + (this.globalFilter != null ? this.globalFilter.hashCode() : 0);
        return result;
    }
}

