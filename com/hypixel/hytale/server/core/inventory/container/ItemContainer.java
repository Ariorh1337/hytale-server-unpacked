/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.fastutil.shorts.Short2ObjectConcurrentHashMap;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.SyncEventBusRegistry;
import com.hypixel.hytale.function.consumer.ShortObjectConsumer;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InventorySection;
import com.hypixel.hytale.protocol.ItemResourceType;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.ResourceQuantity;
import com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilItemStack;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilMaterial;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilResource;
import com.hypixel.hytale.server.core.inventory.container.InternalContainerUtilTag;
import com.hypixel.hytale.server.core.inventory.container.SlotReplacementFunction;
import com.hypixel.hytale.server.core.inventory.container.SortType;
import com.hypixel.hytale.server.core.inventory.container.TestRemoveItemSlotResult;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ClearTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MoveTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MoveType;
import com.hypixel.hytale.server.core.inventory.transaction.ResourceSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ResourceTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.TagSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.TagTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ItemContainer {
    public static final CodecMapCodec<ItemContainer> CODEC = new CodecMapCodec(true);
    public static final boolean DEFAULT_ADD_ALL_OR_NOTHING = false;
    public static final boolean DEFAULT_REMOVE_ALL_OR_NOTHING = true;
    public static final boolean DEFAULT_FULL_STACKS = false;
    public static final boolean DEFAULT_EXACT_AMOUNT = true;
    public static final boolean DEFAULT_FILTER = true;
    protected static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    protected final SyncEventBusRegistry<Void, ItemContainerChangeEvent> externalChangeEventRegistry = new SyncEventBusRegistry(LOGGER, ItemContainerChangeEvent.class);
    protected final SyncEventBusRegistry<Void, ItemContainerChangeEvent> internalChangeEventRegistry = new SyncEventBusRegistry(LOGGER, ItemContainerChangeEvent.class);

    public abstract short getCapacity();

    public abstract void setGlobalFilter(FilterType var1);

    public abstract void setSlotFilter(FilterActionType var1, short var2, SlotFilter var3);

    public abstract ItemContainer clone();

    protected abstract <V> V readAction(Supplier<V> var1);

    protected abstract <X, V> V readAction(Function<X, V> var1, X var2);

    protected abstract <V> V writeAction(Supplier<V> var1);

    protected abstract <X, V> V writeAction(Function<X, V> var1, X var2);

    protected abstract ClearTransaction internal_clear();

    @Nullable
    protected abstract ItemStack internal_getSlot(short var1);

    @Nullable
    protected abstract ItemStack internal_setSlot(short var1, ItemStack var2);

    @Nullable
    protected abstract ItemStack internal_removeSlot(short var1);

    protected abstract boolean cantAddToSlot(short var1, ItemStack var2, ItemStack var3);

    protected abstract boolean cantRemoveFromSlot(short var1);

    protected abstract boolean cantDropFromSlot(short var1);

    protected abstract boolean cantMoveToSlot(ItemContainer var1, short var2);

    @Nonnull
    public InventorySection toPacket() {
        InventorySection packet = new InventorySection();
        packet.capacity = this.getCapacity();
        packet.items = this.toProtocolMap();
        return packet;
    }

    @Nonnull
    public Map<Integer, ItemWithAllMetadata> toProtocolMap() {
        Int2ObjectOpenHashMap<ItemWithAllMetadata> map = new Int2ObjectOpenHashMap<ItemWithAllMetadata>();
        this.forEachWithMeta((slot, itemStack, _map) -> {
            if (ItemStack.isEmpty(itemStack) || !itemStack.isValid()) {
                return;
            }
            _map.put(Integer.valueOf(slot), itemStack.toPacket());
        }, map);
        return map;
    }

    public EventRegistration registerChangeEvent(@Nonnull Consumer<ItemContainerChangeEvent> consumer) {
        return this.registerChangeEvent((short)0, consumer);
    }

    public EventRegistration registerChangeEvent(@Nonnull EventPriority priority, @Nonnull Consumer<ItemContainerChangeEvent> consumer) {
        return this.registerChangeEvent(priority.getValue(), consumer);
    }

    public EventRegistration registerChangeEvent(short priority, @Nonnull Consumer<ItemContainerChangeEvent> consumer) {
        return this.externalChangeEventRegistry.register(priority, null, consumer);
    }

    public ClearTransaction clear() {
        ClearTransaction transaction = this.writeAction(this::internal_clear);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canAddItemStackToSlot(short slot, @Nonnull ItemStack itemStack, boolean allOrNothing, boolean filter) {
        ItemContainer.validateSlotIndex(slot, this.getCapacity());
        return this.writeAction(() -> {
            int quantityRemaining = itemStack.getQuantity();
            ItemStack slotItemStack = this.internal_getSlot(slot);
            if (filter && this.cantAddToSlot(slot, itemStack, slotItemStack)) {
                return false;
            }
            if (slotItemStack == null) {
                return true;
            }
            if (!itemStack.isStackableWith(slotItemStack)) {
                return false;
            }
            int quantity = slotItemStack.getQuantity();
            int quantityAdjustment = Math.min(slotItemStack.getItem().getMaxStack() - quantity, quantityRemaining);
            int newQuantityRemaining = quantityRemaining - quantityAdjustment;
            if (allOrNothing) {
                return quantityRemaining <= 0;
            }
            return quantityRemaining != newQuantityRemaining;
        });
    }

    @Nonnull
    public ItemStackSlotTransaction addItemStackToSlot(short slot, @Nonnull ItemStack itemStack) {
        return this.addItemStackToSlot(slot, itemStack, false, true);
    }

    @Nonnull
    public ItemStackSlotTransaction addItemStackToSlot(short slot, @Nonnull ItemStack itemStack, boolean allOrNothing, boolean filter) {
        ItemStackSlotTransaction transaction = InternalContainerUtilItemStack.internal_addItemStackToSlot(this, slot, itemStack, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Nonnull
    public ItemStackSlotTransaction setItemStackForSlot(short slot, ItemStack itemStack) {
        return this.setItemStackForSlot(slot, itemStack, true);
    }

    @Nonnull
    public ItemStackSlotTransaction setItemStackForSlot(short slot, ItemStack itemStack, boolean filter) {
        ItemStackSlotTransaction transaction = InternalContainerUtilItemStack.internal_setItemStackForSlot(this, slot, itemStack, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Nullable
    public ItemStack getItemStack(short slot) {
        ItemContainer.validateSlotIndex(slot, this.getCapacity());
        return this.readAction(() -> this.internal_getSlot(slot));
    }

    @Nonnull
    public ItemStackSlotTransaction replaceItemStackInSlot(short slot, ItemStack itemStackToRemove, ItemStack itemStack) {
        ItemStackSlotTransaction transaction = this.internal_replaceItemStack(slot, itemStackToRemove, itemStack);
        this.sendUpdate(transaction);
        return transaction;
    }

    public ListTransaction<ItemStackSlotTransaction> replaceAll(SlotReplacementFunction func) {
        return this.replaceAll(func, true);
    }

    private ListTransaction<ItemStackSlotTransaction> replaceAll(SlotReplacementFunction func, boolean ignoreEmpty) {
        ListTransaction transaction = this.writeAction(() -> {
            short capacity = this.getCapacity();
            ObjectArrayList<ItemStackSlotTransaction> transactionsList = new ObjectArrayList<ItemStackSlotTransaction>(capacity);
            for (short slot = 0; slot < capacity; slot = (short)(slot + 1)) {
                ItemStack existing = this.internal_getSlot(slot);
                if (ignoreEmpty && ItemStack.isEmpty(existing)) continue;
                ItemStack replacement = func.replace(slot, existing);
                this.internal_setSlot(slot, replacement);
                transactionsList.add(new ItemStackSlotTransaction(true, ActionType.REPLACE, slot, existing, replacement, existing, true, false, false, false, replacement, replacement));
            }
            return new ListTransaction(true, transactionsList);
        });
        this.sendUpdate(transaction);
        return transaction;
    }

    protected ItemStackSlotTransaction internal_replaceItemStack(short slot, @Nullable ItemStack itemStackToRemove, ItemStack itemStack) {
        ItemContainer.validateSlotIndex(slot, this.getCapacity());
        return this.writeAction(() -> {
            ItemStack slotItemStack = this.internal_getSlot(slot);
            if (slotItemStack == null && itemStackToRemove != null || slotItemStack != null && itemStackToRemove == null || slotItemStack != null && !itemStackToRemove.isStackableWith(slotItemStack)) {
                return new ItemStackSlotTransaction(false, ActionType.REPLACE, slot, slotItemStack, slotItemStack, null, true, false, false, false, itemStack, itemStack);
            }
            this.internal_setSlot(slot, itemStack);
            return new ItemStackSlotTransaction(true, ActionType.REPLACE, slot, slotItemStack, itemStack, slotItemStack, true, false, false, false, itemStack, null);
        });
    }

    @Nonnull
    public SlotTransaction removeItemStackFromSlot(short slot) {
        return this.removeItemStackFromSlot(slot, true);
    }

    @Nonnull
    public SlotTransaction removeItemStackFromSlot(short slot, boolean filter) {
        SlotTransaction transaction = InternalContainerUtilItemStack.internal_removeItemStackFromSlot(this, slot, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Nonnull
    public ItemStackSlotTransaction removeItemStackFromSlot(short slot, int quantityToRemove) {
        return this.removeItemStackFromSlot(slot, quantityToRemove, true, true);
    }

    @Nonnull
    public ItemStackSlotTransaction removeItemStackFromSlot(short slot, int quantityToRemove, boolean allOrNothing, boolean filter) {
        ItemStackSlotTransaction transaction = InternalContainerUtilItemStack.internal_removeItemStackFromSlot(this, slot, quantityToRemove, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Deprecated
    public ItemStackSlotTransaction internal_removeItemStack(short slot, int quantityToRemove) {
        return InternalContainerUtilItemStack.internal_removeItemStackFromSlot(this, slot, quantityToRemove, true, true);
    }

    @Nonnull
    public ItemStackSlotTransaction removeItemStackFromSlot(short slot, ItemStack itemStackToRemove, int quantityToRemove) {
        return this.removeItemStackFromSlot(slot, itemStackToRemove, quantityToRemove, true, true);
    }

    @Nonnull
    public ItemStackSlotTransaction removeItemStackFromSlot(short slot, ItemStack itemStackToRemove, int quantityToRemove, boolean allOrNothing, boolean filter) {
        ItemStackSlotTransaction transaction = InternalContainerUtilItemStack.internal_removeItemStackFromSlot(this, slot, itemStackToRemove, quantityToRemove, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Nonnull
    public MaterialSlotTransaction removeMaterialFromSlot(short slot, @Nonnull MaterialQuantity material) {
        return this.removeMaterialFromSlot(slot, material, true, true, true);
    }

    @Nonnull
    public MaterialSlotTransaction removeMaterialFromSlot(short slot, @Nonnull MaterialQuantity material, boolean allOrNothing, boolean exactAmount, boolean filter) {
        MaterialSlotTransaction transaction = InternalContainerUtilMaterial.internal_removeMaterialFromSlot(this, slot, material, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Nonnull
    public ResourceSlotTransaction removeResourceFromSlot(short slot, @Nonnull ResourceQuantity resource) {
        return this.removeResourceFromSlot(slot, resource, true, true, true);
    }

    @Nonnull
    public ResourceSlotTransaction removeResourceFromSlot(short slot, @Nonnull ResourceQuantity resource, boolean allOrNothing, boolean exactAmount, boolean filter) {
        ResourceSlotTransaction transaction = InternalContainerUtilResource.internal_removeResourceFromSlot(this, slot, resource, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Nonnull
    public TagSlotTransaction removeTagFromSlot(short slot, int tagIndex, int quantity) {
        return this.removeTagFromSlot(slot, tagIndex, quantity, true, true);
    }

    @Nonnull
    public TagSlotTransaction removeTagFromSlot(short slot, int tagIndex, int quantity, boolean allOrNothing, boolean filter) {
        TagSlotTransaction transaction = InternalContainerUtilTag.internal_removeTagFromSlot(this, slot, tagIndex, quantity, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    @Nonnull
    public MoveTransaction<ItemStackTransaction> moveItemStackFromSlot(short slot, @Nonnull ItemContainer containerTo) {
        return this.moveItemStackFromSlot(slot, containerTo, true);
    }

    @Nonnull
    public MoveTransaction<ItemStackTransaction> moveItemStackFromSlot(short slot, @Nonnull ItemContainer containerTo, boolean filter) {
        return this.moveItemStackFromSlot(slot, containerTo, false, filter);
    }

    @Nonnull
    public MoveTransaction<ItemStackTransaction> moveItemStackFromSlot(short slot, @Nonnull ItemContainer containerTo, boolean allOrNothing, boolean filter) {
        MoveTransaction<ItemStackTransaction> transaction = this.internal_moveItemStackFromSlot(slot, containerTo, allOrNothing, filter);
        this.sendUpdate(transaction);
        containerTo.sendUpdate(transaction.toInverted(this));
        return transaction;
    }

    protected MoveTransaction<ItemStackTransaction> internal_moveItemStackFromSlot(short slot, @Nonnull ItemContainer containerTo, boolean allOrNothing, boolean filter) {
        ItemContainer.validateSlotIndex(slot, this.getCapacity());
        return this.writeAction(() -> containerTo.writeAction(() -> {
            if (filter && this.cantRemoveFromSlot(slot)) {
                return null;
            }
            ItemStack itemFrom = this.internal_removeSlot(slot);
            if (ItemStack.isEmpty(itemFrom)) {
                SlotTransaction slotTransaction = new SlotTransaction(false, ActionType.REMOVE, slot, null, null, null, false, false, filter);
                return new MoveTransaction<ItemStackTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, ItemStackTransaction.FAILED_ADD);
            }
            SlotTransaction fromTransaction = new SlotTransaction(true, ActionType.REMOVE, slot, itemFrom, null, null, false, false, filter);
            ItemStackTransaction addTransaction = InternalContainerUtilItemStack.internal_addItemStack(containerTo, itemFrom, allOrNothing, false, filter);
            ItemStack remainder = addTransaction.getRemainder();
            if (!ItemStack.isEmpty(remainder)) {
                InternalContainerUtilItemStack.internal_addItemStackToSlot(this, slot, remainder, allOrNothing, false);
            }
            return new MoveTransaction<ItemStackTransaction>(addTransaction.succeeded(), fromTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
        }));
    }

    @Nonnull
    public MoveTransaction<ItemStackTransaction> moveItemStackFromSlot(short slot, int quantity, @Nonnull ItemContainer containerTo) {
        return this.moveItemStackFromSlot(slot, quantity, containerTo, false, true);
    }

    @Nonnull
    public MoveTransaction<ItemStackTransaction> moveItemStackFromSlot(short slot, int quantity, @Nonnull ItemContainer containerTo, boolean allOrNothing, boolean filter) {
        MoveTransaction<ItemStackTransaction> transaction = this.internal_moveItemStackFromSlot(slot, quantity, containerTo, allOrNothing, filter);
        this.sendUpdate(transaction);
        containerTo.sendUpdate(transaction.toInverted(this));
        return transaction;
    }

    protected MoveTransaction<ItemStackTransaction> internal_moveItemStackFromSlot(short slot, int quantity, @Nonnull ItemContainer containerTo, boolean allOrNothing, boolean filter) {
        ItemContainer.validateSlotIndex(slot, this.getCapacity());
        ItemContainer.validateQuantity(quantity);
        return this.writeAction(() -> containerTo.writeAction(() -> {
            if (filter && this.cantRemoveFromSlot(slot)) {
                return null;
            }
            if (filter && containerTo.cantMoveToSlot(this, slot)) {
                ItemStack itemStack = this.internal_getSlot(slot);
                SlotTransaction slotTransaction = new SlotTransaction(false, ActionType.REMOVE, slot, itemStack, itemStack, null, false, false, filter);
                return new MoveTransaction<ItemStackTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, ItemStackTransaction.FAILED_ADD);
            }
            ItemStackSlotTransaction fromTransaction = this.internal_removeItemStack(slot, quantity);
            if (!fromTransaction.succeeded()) {
                SlotTransaction slotTransaction = new SlotTransaction(false, ActionType.REMOVE, slot, null, null, null, false, false, filter);
                return new MoveTransaction<ItemStackTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, ItemStackTransaction.FAILED_ADD);
            }
            ItemStack itemFrom = fromTransaction.getOutput();
            if (ItemStack.isEmpty(itemFrom)) {
                SlotTransaction slotTransaction = new SlotTransaction(false, ActionType.REMOVE, slot, null, null, null, false, false, filter);
                return new MoveTransaction<ItemStackTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, ItemStackTransaction.FAILED_ADD);
            }
            ItemStackTransaction addTransaction = InternalContainerUtilItemStack.internal_addItemStack(containerTo, itemFrom, allOrNothing, false, filter);
            ItemStack remainder = addTransaction.getRemainder();
            if (!ItemStack.isEmpty(remainder)) {
                InternalContainerUtilItemStack.internal_addItemStackToSlot(this, slot, remainder, allOrNothing, false);
            }
            return new MoveTransaction<ItemStackTransaction>(addTransaction.succeeded(), fromTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
        }));
    }

    @Nonnull
    public ListTransaction<MoveTransaction<ItemStackTransaction>> moveItemStackFromSlot(short slot, ItemContainer ... containerTo) {
        return this.moveItemStackFromSlot(slot, false, true, containerTo);
    }

    @Nonnull
    public ListTransaction<MoveTransaction<ItemStackTransaction>> moveItemStackFromSlot(short slot, boolean allOrNothing, boolean filter, ItemContainer ... containerTo) {
        ListTransaction<MoveTransaction<ItemStackTransaction>> transaction = this.internal_moveItemStackFromSlot(slot, allOrNothing, filter, containerTo);
        this.sendUpdate(transaction);
        for (MoveTransaction<ItemStackTransaction> moveItemStackTransaction : transaction.getList()) {
            moveItemStackTransaction.getOtherContainer().sendUpdate(moveItemStackTransaction.toInverted(this));
        }
        return transaction;
    }

    @Nonnull
    private ListTransaction<MoveTransaction<ItemStackTransaction>> internal_moveItemStackFromSlot(short slot, boolean allOrNothing, boolean filter, @Nonnull ItemContainer[] containerTo) {
        ObjectArrayList transactions = new ObjectArrayList();
        for (ItemContainer itemContainer : containerTo) {
            ItemStackTransaction addTransaction;
            MoveTransaction<ItemStackTransaction> transaction = this.internal_moveItemStackFromSlot(slot, itemContainer, allOrNothing, filter);
            transactions.add(transaction);
            if (transaction.succeeded() && ItemStack.isEmpty((addTransaction = transaction.getAddTransaction()).getRemainder())) break;
        }
        return new ListTransaction<MoveTransaction<ItemStackTransaction>>(!transactions.isEmpty(), transactions);
    }

    @Nonnull
    public ListTransaction<MoveTransaction<ItemStackTransaction>> moveItemStackFromSlot(short slot, int quantity, ItemContainer ... containerTo) {
        return this.moveItemStackFromSlot(slot, quantity, false, true, containerTo);
    }

    @Nonnull
    public ListTransaction<MoveTransaction<ItemStackTransaction>> moveItemStackFromSlot(short slot, int quantity, boolean allOrNothing, boolean filter, ItemContainer ... containerTo) {
        ListTransaction<MoveTransaction<ItemStackTransaction>> transaction = this.internal_moveItemStackFromSlot(slot, quantity, allOrNothing, filter, containerTo);
        this.sendUpdate(transaction);
        for (MoveTransaction<ItemStackTransaction> moveItemStackTransaction : transaction.getList()) {
            moveItemStackTransaction.getOtherContainer().sendUpdate(moveItemStackTransaction.toInverted(this));
        }
        return transaction;
    }

    @Nonnull
    private ListTransaction<MoveTransaction<ItemStackTransaction>> internal_moveItemStackFromSlot(short slot, int quantity, boolean allOrNothing, boolean filter, @Nonnull ItemContainer[] containerTo) {
        ObjectArrayList transactions = new ObjectArrayList();
        for (ItemContainer itemContainer : containerTo) {
            ItemStackTransaction addTransaction;
            MoveTransaction<ItemStackTransaction> transaction = this.internal_moveItemStackFromSlot(slot, quantity, itemContainer, allOrNothing, filter);
            transactions.add(transaction);
            if (transaction.succeeded() && ItemStack.isEmpty((addTransaction = transaction.getAddTransaction()).getRemainder())) break;
        }
        return new ListTransaction<MoveTransaction<ItemStackTransaction>>(!transactions.isEmpty(), transactions);
    }

    @Nonnull
    public MoveTransaction<SlotTransaction> moveItemStackFromSlotToSlot(short slot, int quantity, @Nonnull ItemContainer containerTo, short slotTo) {
        return this.moveItemStackFromSlotToSlot(slot, quantity, containerTo, slotTo, true);
    }

    @Nonnull
    public MoveTransaction<SlotTransaction> moveItemStackFromSlotToSlot(short slot, int quantity, @Nonnull ItemContainer containerTo, short slotTo, boolean filter) {
        MoveTransaction<SlotTransaction> transaction = this.internal_moveItemStackFromSlot(slot, quantity, containerTo, slotTo, filter);
        this.sendUpdate(transaction);
        containerTo.sendUpdate(transaction.toInverted(this));
        return transaction;
    }

    protected MoveTransaction<SlotTransaction> internal_moveItemStackFromSlot(short slot, int quantity, @Nonnull ItemContainer containerTo, short slotTo, boolean filter) {
        ItemContainer.validateSlotIndex(slot, this.getCapacity());
        ItemContainer.validateSlotIndex(slotTo, containerTo.getCapacity());
        ItemContainer.validateQuantity(quantity);
        return this.writeAction(() -> containerTo.writeAction(() -> {
            if (filter && this.cantRemoveFromSlot(slot)) {
                ItemStack itemStack = this.internal_getSlot(slot);
                SlotTransaction slotTransaction = new SlotTransaction(false, ActionType.REMOVE, slot, itemStack, itemStack, null, false, false, filter);
                return new MoveTransaction<SlotTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, SlotTransaction.FAILED_ADD);
            }
            if (filter && containerTo.cantMoveToSlot(this, slot)) {
                ItemStack itemStack = this.internal_getSlot(slot);
                SlotTransaction slotTransaction = new SlotTransaction(false, ActionType.REMOVE, slot, itemStack, itemStack, null, false, false, filter);
                return new MoveTransaction<SlotTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, SlotTransaction.FAILED_ADD);
            }
            ItemStackSlotTransaction fromTransaction = this.internal_removeItemStack(slot, quantity);
            if (!fromTransaction.succeeded()) {
                return new MoveTransaction<SlotTransaction>(false, fromTransaction, MoveType.MOVE_FROM_SELF, containerTo, SlotTransaction.FAILED_ADD);
            }
            ItemStack itemFrom = fromTransaction.getOutput();
            if (ItemStack.isEmpty(itemFrom)) {
                return new MoveTransaction<SlotTransaction>(true, fromTransaction, MoveType.MOVE_FROM_SELF, containerTo, SlotTransaction.FAILED_ADD);
            }
            ItemStack itemTo = containerTo.getItemStack(slotTo);
            if (filter && containerTo.cantAddToSlot(slotTo, itemFrom, itemTo)) {
                this.internal_setSlot(slot, fromTransaction.getSlotBefore());
                SlotTransaction slotTransaction = new SlotTransaction(true, ActionType.REMOVE, slot, fromTransaction.getSlotBefore(), fromTransaction.getSlotAfter(), null, false, false, filter);
                SlotTransaction addTransaction = new SlotTransaction(false, ActionType.ADD, slotTo, itemTo, itemTo, null, false, false, filter);
                return new MoveTransaction<SlotTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
            }
            if (ItemStack.isEmpty(itemTo)) {
                ItemStackSlotTransaction addTransaction = InternalContainerUtilItemStack.internal_setItemStackForSlot(containerTo, slotTo, itemFrom, filter);
                return new MoveTransaction<ItemStackSlotTransaction>(true, fromTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
            }
            if (!itemFrom.isStackableWith(itemTo)) {
                if (ItemStack.isEmpty(fromTransaction.getSlotAfter())) {
                    if (filter && this.cantAddToSlot(slot, itemTo, itemFrom)) {
                        this.internal_setSlot(slot, fromTransaction.getSlotBefore());
                        SlotTransaction slotTransaction = new SlotTransaction(true, ActionType.REMOVE, slot, fromTransaction.getSlotBefore(), fromTransaction.getSlotAfter(), null, false, false, filter);
                        SlotTransaction addTransaction = new SlotTransaction(false, ActionType.ADD, slotTo, itemTo, itemTo, null, false, false, filter);
                        return new MoveTransaction<SlotTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
                    }
                    this.internal_setSlot(slot, itemTo);
                    containerTo.internal_setSlot(slotTo, itemFrom);
                    SlotTransaction from = new SlotTransaction(true, ActionType.REPLACE, slot, itemFrom, itemTo, null, false, false, filter);
                    SlotTransaction to = new SlotTransaction(true, ActionType.REPLACE, slotTo, itemTo, itemFrom, null, false, false, filter);
                    return new MoveTransaction<SlotTransaction>(true, from, MoveType.MOVE_FROM_SELF, containerTo, to);
                }
                this.internal_setSlot(slot, fromTransaction.getSlotBefore());
                SlotTransaction slotTransaction = new SlotTransaction(true, ActionType.REMOVE, slot, fromTransaction.getSlotBefore(), fromTransaction.getSlotAfter(), null, false, false, filter);
                SlotTransaction addTransaction = new SlotTransaction(false, ActionType.ADD, slotTo, itemTo, itemTo, null, false, false, filter);
                return new MoveTransaction<SlotTransaction>(false, slotTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
            }
            int maxStack = itemFrom.getItem().getMaxStack();
            int newQuantity = itemFrom.getQuantity() + itemTo.getQuantity();
            if (newQuantity <= maxStack) {
                ItemStackSlotTransaction addTransaction = InternalContainerUtilItemStack.internal_setItemStackForSlot(containerTo, slotTo, itemTo.withQuantity(newQuantity), filter);
                return new MoveTransaction<ItemStackSlotTransaction>(true, fromTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
            }
            ItemStackSlotTransaction addTransaction = InternalContainerUtilItemStack.internal_setItemStackForSlot(containerTo, slotTo, itemTo.withQuantity(maxStack), filter);
            int remainder = newQuantity - maxStack;
            int quantityLeft = !ItemStack.isEmpty(fromTransaction.getSlotAfter()) ? fromTransaction.getSlotAfter().getQuantity() : 0;
            this.internal_setSlot(slot, itemFrom.withQuantity(remainder + quantityLeft));
            return new MoveTransaction<ItemStackSlotTransaction>(true, fromTransaction, MoveType.MOVE_FROM_SELF, containerTo, addTransaction);
        }));
    }

    @Nonnull
    public ListTransaction<MoveTransaction<ItemStackTransaction>> moveAllItemStacksTo(ItemContainer ... containerTo) {
        return this.moveAllItemStacksTo((Predicate<ItemStack>)null, containerTo);
    }

    @Nonnull
    public ListTransaction<MoveTransaction<ItemStackTransaction>> moveAllItemStacksTo(Predicate<ItemStack> itemPredicate, ItemContainer ... containerTo) {
        ListTransaction<MoveTransaction<ItemStackTransaction>> transaction = this.internal_moveAllItemStacksTo(itemPredicate, containerTo);
        this.sendUpdate(transaction);
        for (MoveTransaction<ItemStackTransaction> moveItemStackTransaction : transaction.getList()) {
            moveItemStackTransaction.getOtherContainer().sendUpdate(moveItemStackTransaction.toInverted(this));
        }
        return transaction;
    }

    @Nonnull
    protected ListTransaction<MoveTransaction<ItemStackTransaction>> internal_moveAllItemStacksTo(@Nullable Predicate<ItemStack> itemPredicate, ItemContainer[] containerTo) {
        return this.writeAction(() -> {
            ObjectArrayList transactions = new ObjectArrayList();
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                ItemStack checkedItem;
                if (this.cantRemoveFromSlot(i) || ItemStack.isEmpty(checkedItem = this.internal_getSlot(i)) || itemPredicate != null && !itemPredicate.test(checkedItem)) continue;
                transactions.addAll(this.moveItemStackFromSlot(i, containerTo).getList());
            }
            return new ListTransaction(true, transactions);
        });
    }

    @Nonnull
    public ListTransaction<MoveTransaction<ItemStackTransaction>> quickStackTo(ItemContainer ... containerTo) {
        return this.moveAllItemStacksTo((ItemStack itemStack) -> {
            for (ItemContainer itemContainer : containerTo) {
                if (!itemContainer.containsItemStacksStackableWith((ItemStack)itemStack)) continue;
                return true;
            }
            return false;
        }, containerTo);
    }

    @Nonnull
    public ListTransaction<MoveTransaction<SlotTransaction>> combineItemStacksIntoSlot(@Nonnull ItemContainer containerTo, short slotTo) {
        ListTransaction<MoveTransaction<SlotTransaction>> transaction = this.internal_combineItemStacksIntoSlot(containerTo, slotTo);
        this.sendUpdate(transaction);
        for (MoveTransaction<SlotTransaction> moveSlotTransaction : transaction.getList()) {
            moveSlotTransaction.getOtherContainer().sendUpdate(moveSlotTransaction.toInverted(this));
        }
        return transaction;
    }

    @Nonnull
    protected ListTransaction<MoveTransaction<SlotTransaction>> internal_combineItemStacksIntoSlot(@Nonnull ItemContainer containerTo, short slotTo) {
        ItemContainer.validateSlotIndex(slotTo, containerTo.getCapacity());
        return this.writeAction(() -> {
            ItemStack itemStack = containerTo.internal_getSlot(slotTo);
            Item item = itemStack.getItem();
            int maxStack = item.getMaxStack();
            if (ItemStack.isEmpty(itemStack) || itemStack.getQuantity() >= maxStack) {
                return new ListTransaction(false, Collections.emptyList());
            }
            int count = 0;
            int[] quantities = new int[this.getCapacity()];
            int[] indexes = new int[this.getCapacity()];
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                ItemStack itemFrom;
                if (this.cantRemoveFromSlot(i) || itemStack == (itemFrom = this.internal_getSlot(i)) || ItemStack.isEmpty(itemFrom) || !itemFrom.isStackableWith(itemStack)) continue;
                indexes[count] = i;
                quantities[count] = itemFrom.getQuantity();
                ++count;
            }
            IntArrays.quickSort(quantities, indexes, 0, count);
            int quantity = itemStack.getQuantity();
            ObjectArrayList list = new ObjectArrayList();
            for (int ai = 0; ai < count && quantity < maxStack; ++ai) {
                short i = (short)indexes[ai];
                ItemStack itemFrom = this.internal_getSlot(i);
                MoveTransaction<SlotTransaction> transaction = this.internal_moveItemStackFromSlot(i, itemFrom.getQuantity(), containerTo, slotTo, true);
                list.add(transaction);
                quantity = !ItemStack.isEmpty(transaction.getAddTransaction().getSlotAfter()) ? transaction.getAddTransaction().getSlotAfter().getQuantity() : 0;
            }
            return new ListTransaction(true, list);
        });
    }

    @Nonnull
    public ListTransaction<MoveTransaction<SlotTransaction>> swapItems(short srcPos, @Nonnull ItemContainer containerTo, short destPos, short length) {
        ListTransaction<MoveTransaction<SlotTransaction>> transaction = this.internal_swapItems(srcPos, containerTo, destPos, length);
        this.sendUpdate(transaction);
        for (MoveTransaction<SlotTransaction> moveItemStackTransaction : transaction.getList()) {
            moveItemStackTransaction.getOtherContainer().sendUpdate(moveItemStackTransaction.toInverted(this));
        }
        return transaction;
    }

    @Nonnull
    protected ListTransaction<MoveTransaction<SlotTransaction>> internal_swapItems(short srcPos, @Nonnull ItemContainer containerTo, short destPos, short length) {
        if (srcPos < 0) {
            throw new IndexOutOfBoundsException("srcPos < 0");
        }
        if (srcPos + length > this.getCapacity()) {
            throw new IndexOutOfBoundsException("srcPos + length > capacity");
        }
        if (destPos < 0) {
            throw new IndexOutOfBoundsException("destPos < 0");
        }
        if (destPos + length > containerTo.getCapacity()) {
            throw new IndexOutOfBoundsException("destPos + length > dest.capacity");
        }
        return this.writeAction(() -> containerTo.writeAction(() -> {
            ObjectArrayList list = new ObjectArrayList(length);
            for (short slot = 0; slot < length; slot = (short)(slot + 1)) {
                list.add(this.internal_swapItems(containerTo, (short)(srcPos + slot), (short)(destPos + slot)));
            }
            return new ListTransaction(true, list);
        }));
    }

    @Nonnull
    protected MoveTransaction<SlotTransaction> internal_swapItems(@Nonnull ItemContainer containerTo, short slotFrom, short slotTo) {
        ItemStack itemFrom = this.internal_removeSlot(slotFrom);
        ItemStack itemTo = containerTo.internal_removeSlot(slotTo);
        if (itemTo != null && !itemTo.isEmpty()) {
            this.internal_setSlot(slotFrom, itemTo);
        }
        if (itemFrom != null && !itemFrom.isEmpty()) {
            containerTo.internal_setSlot(slotTo, itemFrom);
        }
        SlotTransaction from = new SlotTransaction(true, ActionType.REPLACE, slotFrom, itemFrom, itemTo, null, false, false, false);
        SlotTransaction to = new SlotTransaction(true, ActionType.REPLACE, slotTo, itemTo, itemFrom, null, false, false, false);
        return new MoveTransaction<SlotTransaction>(true, from, MoveType.MOVE_FROM_SELF, containerTo, to);
    }

    public boolean canAddItemStack(@Nonnull ItemStack itemStack) {
        return this.canAddItemStack(itemStack, false, true);
    }

    public boolean canAddItemStack(@Nonnull ItemStack itemStack, boolean fullStacks, boolean filter) {
        Item item = itemStack.getItem();
        if (item == null) {
            throw new IllegalArgumentException(itemStack.getItemId() + " is an invalid item!");
        }
        int itemMaxStack = item.getMaxStack();
        return this.readAction(() -> {
            int testQuantityRemaining = itemStack.getQuantity();
            if (!fullStacks) {
                testQuantityRemaining = InternalContainerUtilItemStack.testAddToExistingItemStacks(this, itemStack, itemMaxStack, testQuantityRemaining, filter);
            }
            return (testQuantityRemaining = InternalContainerUtilItemStack.testAddToEmptySlots(this, itemStack, itemMaxStack, testQuantityRemaining, filter)) <= 0;
        });
    }

    @Nonnull
    public ItemStackTransaction addItemStack(@Nonnull ItemStack itemStack) {
        return this.addItemStack(itemStack, false, false, true);
    }

    @Nonnull
    public ItemStackTransaction addItemStack(@Nonnull ItemStack itemStack, boolean allOrNothing, boolean fullStacks, boolean filter) {
        ItemStackTransaction transaction = InternalContainerUtilItemStack.internal_addItemStack(this, itemStack, allOrNothing, fullStacks, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canAddItemStacks(List<ItemStack> itemStacks) {
        return this.canAddItemStacks(itemStacks, false, true);
    }

    public boolean canAddItemStacks(@Nullable List<ItemStack> itemStacks, boolean fullStacks, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return true;
        }
        ObjectArrayList tempItemDataList = new ObjectArrayList(itemStacks.size());
        for (ItemStack itemStack : itemStacks) {
            Item item = itemStack.getItem();
            if (item == null) {
                throw new IllegalArgumentException(itemStack.getItemId() + " is an invalid item!");
            }
            tempItemDataList.add(new TempItemData(itemStack, item));
        }
        return this.readAction(() -> {
            for (TempItemData tempItemData : tempItemDataList) {
                int itemMaxStack = tempItemData.item().getMaxStack();
                ItemStack itemStack = tempItemData.itemStack();
                int testQuantityRemaining = itemStack.getQuantity();
                if (!fullStacks) {
                    testQuantityRemaining = InternalContainerUtilItemStack.testAddToExistingItemStacks(this, itemStack, itemMaxStack, testQuantityRemaining, filter);
                }
                if ((testQuantityRemaining = InternalContainerUtilItemStack.testAddToEmptySlots(this, itemStack, itemMaxStack, testQuantityRemaining, filter)) <= 0) continue;
                return false;
            }
            return true;
        });
    }

    public ListTransaction<ItemStackTransaction> addItemStacks(List<ItemStack> itemStacks) {
        return this.addItemStacks(itemStacks, false, false, true);
    }

    public ListTransaction<ItemStackTransaction> addItemStacks(@Nullable List<ItemStack> itemStacks, boolean allOrNothing, boolean fullStacks, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        ListTransaction<ItemStackTransaction> transaction = InternalContainerUtilItemStack.internal_addItemStacks(this, itemStacks, allOrNothing, fullStacks, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public ListTransaction<ItemStackSlotTransaction> addItemStacksOrdered(List<ItemStack> itemStacks) {
        return this.addItemStacksOrdered(itemStacks, false, true);
    }

    public ListTransaction<ItemStackSlotTransaction> addItemStacksOrdered(short offset, List<ItemStack> itemStacks) {
        return this.addItemStacksOrdered(offset, itemStacks, false, true);
    }

    public ListTransaction<ItemStackSlotTransaction> addItemStacksOrdered(List<ItemStack> itemStacks, boolean allOrNothing, boolean filter) {
        return this.addItemStacksOrdered((short)0, itemStacks, allOrNothing, filter);
    }

    public ListTransaction<ItemStackSlotTransaction> addItemStacksOrdered(short offset, @Nullable List<ItemStack> itemStacks, boolean allOrNothing, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        ListTransaction<ItemStackSlotTransaction> transaction = InternalContainerUtilItemStack.internal_addItemStacksOrdered(this, offset, itemStacks, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canRemoveItemStack(ItemStack itemStack) {
        return this.canRemoveItemStack(itemStack, true, true);
    }

    public boolean canRemoveItemStack(@Nullable ItemStack itemStack, boolean exactAmount, boolean filter) {
        if (itemStack == null) {
            return true;
        }
        return this.readAction(() -> {
            int testQuantityRemaining = InternalContainerUtilItemStack.testRemoveItemStackFromItems(this, itemStack, itemStack.getQuantity(), filter);
            if (testQuantityRemaining > 0) {
                return false;
            }
            return !exactAmount || testQuantityRemaining >= 0;
        });
    }

    @Nonnull
    public ItemStackTransaction removeItemStack(@Nonnull ItemStack itemStack) {
        return this.removeItemStack(itemStack, true, true);
    }

    @Nonnull
    public ItemStackTransaction removeItemStack(@Nonnull ItemStack itemStack, boolean allOrNothing, boolean filter) {
        ItemStackTransaction transaction = InternalContainerUtilItemStack.internal_removeItemStack(this, itemStack, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canRemoveItemStacks(List<ItemStack> itemStacks) {
        return this.canRemoveItemStacks(itemStacks, true, true);
    }

    public boolean canRemoveItemStacks(@Nullable List<ItemStack> itemStacks, boolean exactAmount, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return true;
        }
        return this.readAction(() -> {
            for (ItemStack itemStack : itemStacks) {
                int testQuantityRemaining = InternalContainerUtilItemStack.testRemoveItemStackFromItems(this, itemStack, itemStack.getQuantity(), filter);
                if (testQuantityRemaining > 0) {
                    return false;
                }
                if (!exactAmount || testQuantityRemaining >= 0) continue;
                return false;
            }
            return true;
        });
    }

    public ListTransaction<ItemStackTransaction> removeItemStacks(List<ItemStack> itemStacks) {
        return this.removeItemStacks(itemStacks, true, true);
    }

    public ListTransaction<ItemStackTransaction> removeItemStacks(@Nullable List<ItemStack> itemStacks, boolean allOrNothing, boolean filter) {
        if (itemStacks == null || itemStacks.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        ListTransaction<ItemStackTransaction> transaction = InternalContainerUtilItemStack.internal_removeItemStacks(this, itemStacks, allOrNothing, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canRemoveTag(int tagIndex, int quantity) {
        return this.canRemoveTag(tagIndex, quantity, true, true);
    }

    public boolean canRemoveTag(int tagIndex, int quantity, boolean exactAmount, boolean filter) {
        return this.readAction(() -> {
            int testQuantityRemaining = InternalContainerUtilTag.testRemoveTagFromItems(this, tagIndex, quantity, filter);
            if (testQuantityRemaining > 0) {
                return false;
            }
            return !exactAmount || testQuantityRemaining >= 0;
        });
    }

    @Nonnull
    public TagTransaction removeTag(int tagIndex, int quantity) {
        return this.removeTag(tagIndex, quantity, true, true, true);
    }

    @Nonnull
    public TagTransaction removeTag(int tagIndex, int quantity, boolean allOrNothing, boolean exactAmount, boolean filter) {
        TagTransaction transaction = InternalContainerUtilTag.internal_removeTag(this, tagIndex, quantity, allOrNothing, exactAmount, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canRemoveResource(ResourceQuantity resource) {
        return this.canRemoveResource(resource, true, true);
    }

    public boolean canRemoveResource(@Nullable ResourceQuantity resource, boolean exactAmount, boolean filter) {
        if (resource == null) {
            return true;
        }
        return this.readAction(() -> {
            int testQuantityRemaining = InternalContainerUtilResource.testRemoveResourceFromItems(this, resource, resource.getQuantity(), filter);
            if (testQuantityRemaining > 0) {
                return false;
            }
            return !exactAmount || testQuantityRemaining >= 0;
        });
    }

    @Nonnull
    public ResourceTransaction removeResource(@Nonnull ResourceQuantity resource) {
        return this.removeResource(resource, true, true, true);
    }

    @Nonnull
    public ResourceTransaction removeResource(@Nonnull ResourceQuantity resource, boolean allOrNothing, boolean exactAmount, boolean filter) {
        ResourceTransaction transaction = InternalContainerUtilResource.internal_removeResource(this, resource, allOrNothing, exactAmount, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canRemoveResources(List<ResourceQuantity> resources) {
        return this.canRemoveResources(resources, true, true);
    }

    public boolean canRemoveResources(@Nullable List<ResourceQuantity> resources, boolean exactAmount, boolean filter) {
        if (resources == null || resources.isEmpty()) {
            return true;
        }
        return this.readAction(() -> {
            for (ResourceQuantity resource : resources) {
                int testQuantityRemaining = InternalContainerUtilResource.testRemoveResourceFromItems(this, resource, resource.getQuantity(), filter);
                if (testQuantityRemaining > 0) {
                    return false;
                }
                if (!exactAmount || testQuantityRemaining >= 0) continue;
                return false;
            }
            return true;
        });
    }

    public ListTransaction<ResourceTransaction> removeResources(List<ResourceQuantity> resources) {
        return this.removeResources(resources, true, true, true);
    }

    public ListTransaction<ResourceTransaction> removeResources(@Nullable List<ResourceQuantity> resources, boolean allOrNothing, boolean exactAmount, boolean filter) {
        if (resources == null || resources.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        ListTransaction<ResourceTransaction> transaction = InternalContainerUtilResource.internal_removeResources(this, resources, allOrNothing, exactAmount, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canRemoveMaterial(MaterialQuantity material) {
        return this.canRemoveMaterial(material, true, true);
    }

    public boolean canRemoveMaterial(@Nullable MaterialQuantity material, boolean exactAmount, boolean filter) {
        if (material == null) {
            return true;
        }
        return this.readAction(() -> {
            int testQuantityRemaining = InternalContainerUtilMaterial.testRemoveMaterialFromItems(this, material, material.getQuantity(), filter);
            if (testQuantityRemaining > 0) {
                return false;
            }
            return !exactAmount || testQuantityRemaining >= 0;
        });
    }

    @Nonnull
    public MaterialTransaction removeMaterial(@Nonnull MaterialQuantity material) {
        return this.removeMaterial(material, true, true, true);
    }

    @Nonnull
    public MaterialTransaction removeMaterial(@Nonnull MaterialQuantity material, boolean allOrNothing, boolean exactAmount, boolean filter) {
        MaterialTransaction transaction = InternalContainerUtilMaterial.internal_removeMaterial(this, material, allOrNothing, exactAmount, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean canRemoveMaterials(List<MaterialQuantity> materials) {
        return this.canRemoveMaterials(materials, true, true);
    }

    public boolean canRemoveMaterials(@Nullable List<MaterialQuantity> materials, boolean exactAmount, boolean filter) {
        if (materials == null || materials.isEmpty()) {
            return true;
        }
        return this.readAction(() -> {
            for (MaterialQuantity material : materials) {
                int testQuantityRemaining = InternalContainerUtilMaterial.testRemoveMaterialFromItems(this, material, material.getQuantity(), filter);
                if (testQuantityRemaining > 0) {
                    return false;
                }
                if (!exactAmount || testQuantityRemaining >= 0) continue;
                return false;
            }
            return true;
        });
    }

    public List<TestRemoveItemSlotResult> getSlotMaterialsToRemove(@Nullable List<MaterialQuantity> materials, boolean exactAmount, boolean filter) {
        ObjectArrayList<TestRemoveItemSlotResult> slotMaterials = new ObjectArrayList<TestRemoveItemSlotResult>();
        if (materials == null || materials.isEmpty()) {
            return slotMaterials;
        }
        return this.readAction(() -> {
            for (MaterialQuantity material : materials) {
                TestRemoveItemSlotResult testResult = InternalContainerUtilMaterial.getTestRemoveMaterialFromItems(this, material, material.getQuantity(), filter);
                if (testResult.quantityRemaining > 0) {
                    slotMaterials.clear();
                    return slotMaterials;
                }
                if (exactAmount && testResult.quantityRemaining < 0) {
                    slotMaterials.clear();
                    return slotMaterials;
                }
                slotMaterials.add(testResult);
            }
            return slotMaterials;
        });
    }

    public ListTransaction<MaterialTransaction> removeMaterials(List<MaterialQuantity> materials) {
        return this.removeMaterials(materials, true, true, true);
    }

    public ListTransaction<MaterialTransaction> removeMaterials(@Nullable List<MaterialQuantity> materials, boolean allOrNothing, boolean exactAmount, boolean filter) {
        if (materials == null || materials.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        ListTransaction<MaterialTransaction> transaction = InternalContainerUtilMaterial.internal_removeMaterials(this, materials, allOrNothing, exactAmount, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public ListTransaction<MaterialSlotTransaction> removeMaterialsOrdered(short offset, List<MaterialQuantity> materials) {
        return this.removeMaterialsOrdered(offset, materials, true, true, true);
    }

    public ListTransaction<MaterialSlotTransaction> removeMaterialsOrdered(List<MaterialQuantity> materials, boolean allOrNothing, boolean exactAmount, boolean filter) {
        return this.removeMaterialsOrdered((short)0, materials, allOrNothing, exactAmount, filter);
    }

    public ListTransaction<MaterialSlotTransaction> removeMaterialsOrdered(short offset, @Nullable List<MaterialQuantity> materials, boolean allOrNothing, boolean exactAmount, boolean filter) {
        if (materials == null || materials.isEmpty()) {
            return ListTransaction.getEmptyTransaction(true);
        }
        if (offset + materials.size() > this.getCapacity()) {
            return ListTransaction.getEmptyTransaction(false);
        }
        ListTransaction<MaterialSlotTransaction> transaction = InternalContainerUtilMaterial.internal_removeMaterialsOrdered(this, offset, materials, allOrNothing, exactAmount, filter);
        this.sendUpdate(transaction);
        return transaction;
    }

    public boolean isEmpty() {
        return this.readAction(() -> {
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                ItemStack itemStack = this.internal_getSlot(i);
                if (itemStack == null || itemStack.isEmpty()) continue;
                return false;
            }
            return true;
        });
    }

    public int countItemStacks(@Nonnull Predicate<ItemStack> itemPredicate) {
        return this.readAction(() -> {
            int count = 0;
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                ItemStack itemStack = this.internal_getSlot(i);
                if (ItemStack.isEmpty(itemStack) || !itemPredicate.test(itemStack)) continue;
                count += itemStack.getQuantity();
            }
            return count;
        });
    }

    public boolean containsItemStacksStackableWith(@Nonnull ItemStack itemStack) {
        return this.readAction(() -> {
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                ItemStack checked = this.internal_getSlot(i);
                if (ItemStack.isEmpty(checked) || !itemStack.isStackableWith(checked)) continue;
                return true;
            }
            return false;
        });
    }

    public void forEach(@Nonnull ShortObjectConsumer<ItemStack> action) {
        for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
            ItemStack itemStack = this.getItemStack(i);
            if (ItemStack.isEmpty(itemStack)) continue;
            action.accept(i, itemStack);
        }
    }

    public <T> void forEachWithMeta(@Nonnull Short2ObjectConcurrentHashMap.ShortBiObjConsumer<ItemStack, T> consumer, T meta) {
        for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
            ItemStack itemStack = this.getItemStack(i);
            if (ItemStack.isEmpty(itemStack)) continue;
            consumer.accept(i, itemStack, meta);
        }
    }

    @Nonnull
    public List<ItemStack> removeAllItemStacks() {
        ObjectArrayList<ItemStack> items = new ObjectArrayList<ItemStack>();
        ListTransaction transaction = this.writeAction(() -> {
            ObjectArrayList transactions = new ObjectArrayList();
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                ItemStack itemStack;
                if (this.cantRemoveFromSlot(i) || ItemStack.isEmpty(itemStack = this.internal_removeSlot(i))) continue;
                items.add(itemStack);
                transactions.add(new SlotTransaction(true, ActionType.REMOVE, i, itemStack, null, itemStack, false, false, true));
            }
            return new ListTransaction(true, transactions);
        });
        this.sendUpdate(transaction);
        return items;
    }

    @Nonnull
    public List<ItemStack> dropAllItemStacks() {
        return this.dropAllItemStacks(true);
    }

    @Nonnull
    public List<ItemStack> dropAllItemStacks(boolean filter) {
        ObjectArrayList<ItemStack> items = new ObjectArrayList<ItemStack>();
        ListTransaction transaction = this.writeAction(() -> {
            ObjectArrayList transactions = new ObjectArrayList();
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                ItemStack itemStack;
                if (filter && this.cantDropFromSlot(i) || ItemStack.isEmpty(itemStack = this.internal_removeSlot(i))) continue;
                items.add(itemStack);
                transactions.add(new SlotTransaction(true, ActionType.REMOVE, i, itemStack, null, itemStack, false, false, true));
            }
            return new ListTransaction(true, transactions);
        });
        this.sendUpdate(transaction);
        return items;
    }

    @Nonnull
    public ListTransaction<SlotTransaction> sortItems(@Nonnull SortType sort) {
        ListTransaction<SlotTransaction> transaction = this.internal_sortItems(sort);
        this.sendUpdate(transaction);
        return transaction;
    }

    protected ListTransaction<SlotTransaction> internal_sortItems(@Nonnull SortType sort) {
        return this.writeAction(() -> {
            int i;
            ItemStack[] stacks = new ItemStack[this.getCapacity()];
            int stackOffset = 0;
            for (short i2 = 0; i2 < stacks.length; i2 = (short)(i2 + 1)) {
                ItemStack slot;
                if (this.cantRemoveFromSlot(i2) || (slot = this.internal_getSlot(i2)) == null) continue;
                Item item = slot.getItem();
                int maxStack = item.getMaxStack();
                int slotQuantity = slot.getQuantity();
                if (maxStack > 1) {
                    for (int j = 0; j < stackOffset && slotQuantity > 0; ++j) {
                        int stackQuantity;
                        ItemStack stack = stacks[j];
                        if (!slot.isStackableWith(stack) || (stackQuantity = stack.getQuantity()) >= maxStack) continue;
                        int adjust = Math.min(slotQuantity, maxStack - stackQuantity);
                        slotQuantity -= adjust;
                        stacks[j] = stack.withQuantity(stackQuantity + adjust);
                    }
                }
                if (slotQuantity <= 0) continue;
                stacks[stackOffset++] = slotQuantity != slot.getQuantity() ? slot.withQuantity(slotQuantity) : slot;
            }
            Arrays.sort(stacks, sort.getComparator());
            ObjectArrayList transactions = new ObjectArrayList(stacks.length);
            stackOffset = 0;
            for (i = 0; i < stacks.length; i = (int)((short)(i + 1))) {
                ItemStack existing;
                ItemStack replacement;
                if (this.cantRemoveFromSlot((short)i) || this.cantAddToSlot((short)i, replacement = stacks[stackOffset], existing = this.internal_getSlot((short)i))) continue;
                ++stackOffset;
                if (existing == replacement) continue;
                this.internal_setSlot((short)i, replacement);
                transactions.add(new SlotTransaction(true, ActionType.REMOVE, (short)i, existing, null, replacement, false, false, true));
            }
            for (i = stackOffset; i < stacks.length; ++i) {
                if (stacks[i] == null) continue;
                throw new IllegalStateException("Had leftover stacks that didn't get sorted!");
            }
            return new ListTransaction(true, transactions);
        });
    }

    protected void sendUpdate(@Nonnull Transaction transaction) {
        if (!transaction.succeeded()) {
            return;
        }
        ItemContainerChangeEvent event = new ItemContainerChangeEvent(this, transaction);
        this.externalChangeEventRegistry.dispatchFor(null).dispatch(event);
        this.internalChangeEventRegistry.dispatchFor(null).dispatch(event);
    }

    public boolean containsContainer(ItemContainer itemContainer) {
        return itemContainer == this;
    }

    public void doMigration(Function<String, String> blockMigration) {
        Objects.requireNonNull(blockMigration);
        this.writeAction(_blockMigration -> {
            for (short i = 0; i < this.getCapacity(); i = (short)(i + 1)) {
                String newItemId;
                String oldItemId;
                ItemStack slot = this.internal_getSlot(i);
                if (ItemStack.isEmpty(slot) || (oldItemId = slot.getItemId()).equals(newItemId = (String)_blockMigration.apply(slot.getItemId()))) continue;
                this.internal_setSlot(i, new ItemStack(newItemId, slot.getQuantity(), slot.getMetadata()));
            }
            return null;
        }, blockMigration);
    }

    @Nullable
    public static ItemResourceType getMatchingResourceType(@Nonnull Item item, @Nonnull String resourceId) {
        ItemResourceType[] resourceTypes = item.getResourceTypes();
        if (resourceTypes == null) {
            return null;
        }
        for (ItemResourceType resourceType : resourceTypes) {
            if (!resourceId.equals(resourceType.id)) continue;
            return resourceType;
        }
        return null;
    }

    public static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity is less than zero! " + quantity + " < 0");
        }
    }

    public static void validateSlotIndex(short slot, int capacity) {
        if (slot < 0) {
            throw new IllegalArgumentException("Slot is less than zero! " + slot + " < 0");
        }
        if (slot >= capacity) {
            throw new IllegalArgumentException("Slot is outside capacity! " + slot + " >= " + capacity);
        }
    }

    @Nonnull
    public static <T extends ItemContainer> T copy(@Nonnull ItemContainer from, @Nonnull T to, @Nullable List<ItemStack> remainder) {
        from.forEach((slot, itemStack) -> {
            if (slot >= to.getCapacity()) {
                if (remainder != null) {
                    remainder.add((ItemStack)itemStack);
                }
                return;
            }
            if (ItemStack.isEmpty(itemStack)) {
                return;
            }
            to.setItemStackForSlot(slot, (ItemStack)itemStack);
        });
        return to;
    }

    public static <T extends ItemContainer> T ensureContainerCapacity(@Nullable T inputContainer, short capacity, @Nonnull Short2ObjectConcurrentHashMap.ShortFunction<T> newContainerSupplier, List<ItemStack> remainder) {
        if (inputContainer == null) {
            return (T)((ItemContainer)newContainerSupplier.apply(capacity));
        }
        if (inputContainer.getCapacity() == capacity) {
            return inputContainer;
        }
        return (T)ItemContainer.copy(inputContainer, (ItemContainer)newContainerSupplier.apply(capacity), remainder);
    }

    public static ItemContainer getNewContainer(short capacity, @Nonnull Short2ObjectConcurrentHashMap.ShortFunction<ItemContainer> supplier) {
        return capacity > 0 ? supplier.apply(capacity) : EmptyItemContainer.INSTANCE;
    }

    public record ItemContainerChangeEvent(ItemContainer container, Transaction transaction) implements IEvent<Void>
    {
        @Override
        @Nonnull
        public String toString() {
            return "ItemContainerChangeEvent{container=" + String.valueOf(this.container) + ", transaction=" + String.valueOf(this.transaction) + "}";
        }
    }

    public record TempItemData(ItemStack itemStack, Item item) {
    }
}

