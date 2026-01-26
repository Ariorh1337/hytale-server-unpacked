/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Short2ObjectMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.inventory.transaction.ClearTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleItemContainer
extends ItemContainer {
    public static final BuilderCodec<SimpleItemContainer> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SimpleItemContainer.class, SimpleItemContainer::new).append(new KeyedCodec<Short>("Capacity", Codec.SHORT), (o, i) -> {
        o.capacity = i;
    }, o -> o.capacity).addValidator(Validators.greaterThanOrEqual((short)0)).add()).append(new KeyedCodec<ItemStack>("Items", new Short2ObjectMapCodec<ItemStack>(ItemStack.CODEC, Short2ObjectOpenHashMap::new, false)), (o, i) -> {
        o.items = i;
    }, o -> o.items).add()).afterDecode(i -> {
        if (i.items == null) {
            i.items = new Short2ObjectOpenHashMap<ItemStack>(i.capacity);
        }
        i.items.short2ObjectEntrySet().removeIf(e -> e.getShortKey() < 0 || e.getShortKey() >= i.capacity || ItemStack.isEmpty((ItemStack)e.getValue()));
    })).build();
    protected short capacity;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected Short2ObjectMap<ItemStack> items;
    private final Map<FilterActionType, Int2ObjectConcurrentHashMap<SlotFilter>> slotFilters = new ConcurrentHashMap<FilterActionType, Int2ObjectConcurrentHashMap<SlotFilter>>();
    private FilterType globalFilter = FilterType.ALLOW_ALL;

    protected SimpleItemContainer() {
    }

    public SimpleItemContainer(short capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity is less than or equal zero! " + capacity + " <= 0");
        }
        this.capacity = capacity;
        this.items = new Short2ObjectOpenHashMap<ItemStack>(capacity);
    }

    public SimpleItemContainer(@Nonnull SimpleItemContainer other) {
        this.capacity = other.capacity;
        other.lock.readLock().lock();
        try {
            this.items = new Short2ObjectOpenHashMap<ItemStack>(other.items);
        }
        finally {
            other.lock.readLock().unlock();
        }
        this.slotFilters.putAll(other.slotFilters);
        this.globalFilter = other.globalFilter;
    }

    @Override
    protected <V> V readAction(@Nonnull Supplier<V> action) {
        this.lock.readLock().lock();
        try {
            V v = action.get();
            return v;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected <X, V> V readAction(@Nonnull Function<X, V> action, X x) {
        this.lock.readLock().lock();
        try {
            V v = action.apply(x);
            return v;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    protected <V> V writeAction(@Nonnull Supplier<V> action) {
        this.lock.writeLock().lock();
        try {
            V v = action.get();
            return v;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected <X, V> V writeAction(@Nonnull Function<X, V> action, X x) {
        this.lock.writeLock().lock();
        try {
            V v = action.apply(x);
            return v;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    protected ItemStack internal_getSlot(short slot) {
        return (ItemStack)this.items.get(slot);
    }

    @Override
    protected ItemStack internal_setSlot(short slot, ItemStack itemStack) {
        if (ItemStack.isEmpty(itemStack)) {
            return this.internal_removeSlot(slot);
        }
        return this.items.put(slot, itemStack);
    }

    @Override
    protected ItemStack internal_removeSlot(short slot) {
        return (ItemStack)this.items.remove(slot);
    }

    @Override
    protected boolean cantAddToSlot(short slot, ItemStack itemStack, ItemStack slotItemStack) {
        if (!this.globalFilter.allowInput()) {
            return true;
        }
        return this.testFilter(FilterActionType.ADD, slot, itemStack);
    }

    @Override
    protected boolean cantRemoveFromSlot(short slot) {
        if (!this.globalFilter.allowOutput()) {
            return true;
        }
        return this.testFilter(FilterActionType.REMOVE, slot, null);
    }

    @Override
    protected boolean cantDropFromSlot(short slot) {
        return this.testFilter(FilterActionType.DROP, slot, null);
    }

    @Override
    protected boolean cantMoveToSlot(ItemContainer fromContainer, short slotFrom) {
        return false;
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
        return this.capacity;
    }

    @Override
    @Nonnull
    protected ClearTransaction internal_clear() {
        ItemStack[] itemStacks = new ItemStack[this.getCapacity()];
        for (short i = 0; i < itemStacks.length; i = (short)(i + 1)) {
            itemStacks[i] = (ItemStack)this.items.get(i);
        }
        this.items.clear();
        return new ClearTransaction(true, 0, itemStacks);
    }

    @Override
    @Nonnull
    public SimpleItemContainer clone() {
        return new SimpleItemContainer(this);
    }

    @Override
    public boolean isEmpty() {
        this.lock.readLock().lock();
        try {
            if (this.items.isEmpty()) {
                boolean bl = true;
                return bl;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        return super.isEmpty();
    }

    @Override
    public void setGlobalFilter(@Nonnull FilterType globalFilter) {
        this.globalFilter = Objects.requireNonNull(globalFilter);
    }

    @Override
    public void setSlotFilter(FilterActionType actionType, short slot, @Nullable SlotFilter filter) {
        SimpleItemContainer.validateSlotIndex(slot, this.getCapacity());
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
    @Nullable
    public ItemStack getItemStack(short slot) {
        SimpleItemContainer.validateSlotIndex(slot, this.getCapacity());
        this.lock.readLock().lock();
        try {
            ItemStack itemStack = this.internal_getSlot(slot);
            return itemStack;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleItemContainer)) {
            return false;
        }
        SimpleItemContainer that = (SimpleItemContainer)o;
        if (this.capacity != that.capacity) {
            return false;
        }
        this.lock.readLock().lock();
        try {
            boolean bl = this.items.equals(that.items);
            return bl;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public int hashCode() {
        int result;
        this.lock.readLock().lock();
        try {
            result = this.items.hashCode();
        }
        finally {
            this.lock.readLock().unlock();
        }
        result = 31 * result + this.capacity;
        return result;
    }

    public static ItemContainer getNewContainer(short capacity) {
        return ItemContainer.getNewContainer(capacity, SimpleItemContainer::new);
    }

    public static boolean addOrDropItemStack(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull ItemContainer itemContainer, @Nonnull ItemStack itemStack) {
        ItemStackTransaction transaction = itemContainer.addItemStack(itemStack);
        ItemStack remainder = transaction.getRemainder();
        if (!ItemStack.isEmpty(remainder)) {
            ItemUtils.dropItem(ref, remainder, store);
            return true;
        }
        return false;
    }

    public static boolean addOrDropItemStack(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull ItemContainer itemContainer, short slot, @Nonnull ItemStack itemStack) {
        ItemStackSlotTransaction transaction = itemContainer.addItemStackToSlot(slot, itemStack);
        ItemStack remainder = transaction.getRemainder();
        if (!ItemStack.isEmpty(remainder)) {
            return SimpleItemContainer.addOrDropItemStack(store, ref, itemContainer, itemStack);
        }
        return false;
    }

    public static boolean addOrDropItemStacks(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull ItemContainer itemContainer, List<ItemStack> itemStacks) {
        ListTransaction<ItemStackTransaction> transaction = itemContainer.addItemStacks(itemStacks);
        boolean droppedItem = false;
        for (ItemStackTransaction stackTransaction : transaction.getList()) {
            ItemStack remainder = stackTransaction.getRemainder();
            if (ItemStack.isEmpty(remainder)) continue;
            ItemUtils.dropItem(ref, remainder, store);
            droppedItem = true;
        }
        return droppedItem;
    }

    public static boolean tryAddOrderedOrDropItemStacks(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull ItemContainer itemContainer, List<ItemStack> itemStacks) {
        ListTransaction<ItemStackSlotTransaction> transaction = itemContainer.addItemStacksOrdered(itemStacks);
        ObjectArrayList remainderItemStacks = null;
        for (ItemStackSlotTransaction stackTransaction : transaction.getList()) {
            ItemStack remainder = stackTransaction.getRemainder();
            if (ItemStack.isEmpty(remainder)) continue;
            if (remainderItemStacks == null) {
                remainderItemStacks = new ObjectArrayList();
            }
            remainderItemStacks.add(remainder);
        }
        return SimpleItemContainer.addOrDropItemStacks(store, ref, itemContainer, remainderItemStacks);
    }
}

