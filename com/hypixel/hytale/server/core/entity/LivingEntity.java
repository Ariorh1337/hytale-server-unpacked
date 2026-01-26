/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.StatModifiersManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.modules.collision.WorldUtil;
import com.hypixel.hytale.server.core.modules.entity.BlockMigrationExtraInfo;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class LivingEntity
extends Entity {
    @Nonnull
    public static final BuilderCodec<LivingEntity> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(LivingEntity.class, Entity.CODEC).append(new KeyedCodec<Inventory>("Inventory", Inventory.CODEC), (livingEntity, inventory, extraInfo) -> {
        livingEntity.setInventory((Inventory)inventory);
        if (extraInfo instanceof BlockMigrationExtraInfo) {
            livingEntity.inventory.doMigration(((BlockMigrationExtraInfo)extraInfo).getBlockMigration());
        }
    }, (livingEntity, extraInfo) -> livingEntity.inventory).add()).afterDecode(livingEntity -> {
        if (livingEntity.inventory == null) {
            livingEntity.setInventory(livingEntity.createDefaultInventory());
        }
    })).build();
    public static final int DEFAULT_ITEM_THROW_SPEED = 6;
    @Nonnull
    private final StatModifiersManager statModifiersManager = new StatModifiersManager();
    private Inventory inventory;
    protected double currentFallDistance;
    private EventRegistration armorInventoryChangeEventRegistration;
    private boolean isEquipmentNetworkOutdated;

    public LivingEntity() {
        this.setInventory(this.createDefaultInventory());
    }

    public LivingEntity(@Nonnull World world) {
        super(world);
        this.setInventory(this.createDefaultInventory());
    }

    protected abstract Inventory createDefaultInventory();

    public boolean canBreathe(@Nonnull Ref<EntityStore> ref, @Nonnull BlockMaterial breathingMaterial, int fluidId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean invulnerable = componentAccessor.getArchetype(ref).contains(Invulnerable.getComponentType());
        return invulnerable || breathingMaterial == BlockMaterial.Empty && fluidId == 0;
    }

    public static long getPackedMaterialAndFluidAtBreathingHeight(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        long chunkIndex;
        World world = componentAccessor.getExternalData().getWorld();
        Transform lookVec = TargetUtil.getLook(ref, componentAccessor);
        Vector3d position = lookVec.getPosition();
        ChunkStore chunkStore = world.getChunkStore();
        Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex = ChunkUtil.indexChunkFromBlock(position.x, position.z));
        if (chunkRef == null || !chunkRef.isValid()) {
            return MathUtil.packLong(BlockMaterial.Empty.ordinal(), 0);
        }
        return WorldUtil.getPackedMaterialAndFluidAtPosition(chunkRef, chunkStore.getStore(), position.x, position.y, position.z);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Nonnull
    public Inventory setInventory(Inventory inventory) {
        return this.setInventory(inventory, false);
    }

    @Nonnull
    public Inventory setInventory(Inventory inventory, boolean ensureCapacity) {
        ObjectArrayList<ItemStack> remainder = ensureCapacity ? new ObjectArrayList<ItemStack>() : null;
        inventory = this.setInventory(inventory, ensureCapacity, remainder);
        if (remainder != null && !remainder.isEmpty()) {
            ListTransaction<ItemStackTransaction> transactionList = inventory.getCombinedHotbarFirst().addItemStacks(remainder);
            for (ItemStackTransaction itemStackTransaction : transactionList.getList()) {
            }
        }
        return inventory;
    }

    @Nonnull
    public Inventory setInventory(Inventory inventory, boolean ensureCapacity, List<ItemStack> remainder) {
        if (this.inventory != null) {
            this.inventory.unregister();
        }
        if (this.armorInventoryChangeEventRegistration != null) {
            this.armorInventoryChangeEventRegistration.unregister();
        }
        if (ensureCapacity) {
            inventory = Inventory.ensureCapacity(inventory, remainder);
        }
        inventory.setEntity(this);
        this.armorInventoryChangeEventRegistration = inventory.getArmor().registerChangeEvent(event -> this.statModifiersManager.setRecalculate(true));
        this.inventory = inventory;
        return inventory;
    }

    @Override
    public void moveTo(@Nonnull Ref<EntityStore> ref, double locX, double locY, double locZ, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean fallDamageActive;
        TransformComponent transformComponent = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        MovementStatesComponent movementStatesComponent = componentAccessor.getComponent(ref, MovementStatesComponent.getComponentType());
        assert (movementStatesComponent != null);
        MovementStates movementStates = movementStatesComponent.getMovementStates();
        boolean bl = fallDamageActive = !movementStates.inFluid && !movementStates.climbing && !movementStates.flying && !movementStates.gliding;
        if (fallDamageActive) {
            Vector3d position = transformComponent.getPosition();
            if (!movementStates.onGround) {
                if (position.getY() > locY) {
                    this.currentFallDistance += position.getY() - locY;
                }
            } else {
                this.currentFallDistance = 0.0;
            }
        } else {
            this.currentFallDistance = 0.0;
        }
        super.moveTo(ref, locX, locY, locZ, componentAccessor);
    }

    public boolean canDecreaseItemStackDurability(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return false;
    }

    public boolean canApplyItemStackPenalties(Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor) {
        return true;
    }

    @Nullable
    public ItemStackSlotTransaction decreaseItemStackDurability(@Nonnull Ref<EntityStore> ref, @Nullable ItemStack itemStack, int inventoryId, int slotId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (!this.canDecreaseItemStackDurability(ref, componentAccessor)) {
            return null;
        }
        if (itemStack == null || itemStack.isEmpty() || itemStack.getItem() == null) {
            return null;
        }
        if (itemStack.isBroken()) {
            return null;
        }
        Item item = itemStack.getItem();
        ItemContainer section = this.inventory.getSectionById(inventoryId);
        if (section == null) {
            return null;
        }
        if (item.getArmor() != null) {
            ItemStackSlotTransaction transaction = this.updateItemStackDurability(ref, itemStack, section, slotId, -item.getDurabilityLossOnHit(), componentAccessor);
            if (transaction.getSlotAfter().isBroken()) {
                this.statModifiersManager.setRecalculate(true);
            }
            return transaction;
        }
        if (item.getWeapon() != null) {
            return this.updateItemStackDurability(ref, itemStack, section, slotId, -item.getDurabilityLossOnHit(), componentAccessor);
        }
        return null;
    }

    @Nullable
    public ItemStackSlotTransaction updateItemStackDurability(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, ItemContainer container, int slotId, double durabilityChange, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        ItemStack updatedItemStack = itemStack.withIncreasedDurability(durabilityChange);
        return container.replaceItemStackInSlot((short)slotId, itemStack, updatedItemStack);
    }

    public void invalidateEquipmentNetwork() {
        this.isEquipmentNetworkOutdated = true;
    }

    public boolean consumeEquipmentNetworkOutdated() {
        boolean temp = this.isEquipmentNetworkOutdated;
        this.isEquipmentNetworkOutdated = false;
        return temp;
    }

    @Nonnull
    public StatModifiersManager getStatModifiersManager() {
        return this.statModifiersManager;
    }

    public double getCurrentFallDistance() {
        return this.currentFallDistance;
    }

    public void setCurrentFallDistance(double currentFallDistance) {
        this.currentFallDistance = currentFallDistance;
    }

    @Override
    @Nonnull
    public String toString() {
        return "LivingEntity{, " + super.toString() + "}";
    }
}

