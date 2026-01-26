/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.item;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.iterator.CircleIterator;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemEntityConfig;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.entity.BlockMigrationExtraInfo;
import com.hypixel.hytale.server.core.modules.entity.DespawnComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventItemMerging;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemComponent
implements Component<EntityStore> {
    @Nonnull
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Nonnull
    public static final BuilderCodec<ItemComponent> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ItemComponent.class, ItemComponent::new).append(new KeyedCodec<ItemStack>("Item", ItemStack.CODEC), (item, itemStack, extraInfo) -> {
        String newItemId;
        item.itemStack = itemStack;
        if (extraInfo instanceof BlockMigrationExtraInfo && !(newItemId = ((BlockMigrationExtraInfo)extraInfo).getBlockMigration().apply(itemStack.getItemId())).equals(itemStack.getItemId())) {
            item.itemStack = new ItemStack(newItemId, itemStack.getQuantity(), itemStack.getMetadata());
        }
    }, (item, extraInfo) -> item.itemStack).add()).append(new KeyedCodec<Float>("StackDelay", Codec.FLOAT), (item, v) -> {
        item.mergeDelay = v.floatValue();
    }, item -> Float.valueOf(item.mergeDelay)).add()).append(new KeyedCodec<Float>("PickupDelay", Codec.FLOAT), (item, v) -> {
        item.pickupDelay = v.floatValue();
    }, item -> Float.valueOf(item.pickupDelay)).add()).append(new KeyedCodec<Float>("PickupThrottle", Codec.FLOAT), (item, v) -> {
        item.pickupThrottle = v.floatValue();
    }, item -> Float.valueOf(item.pickupThrottle)).add()).append(new KeyedCodec<Boolean>("RemovedByPlayerPickup", Codec.BOOLEAN), (item, v) -> {
        item.removedByPlayerPickup = v;
    }, item -> item.removedByPlayerPickup).add()).build();
    private static final float DROPPED_ITEM_VERTICAL_BOUNCE_VELOCITY = 3.25f;
    private static final float DROPPED_ITEM_HORIZONTAL_BOUNCE_VELOCITY = 3.0f;
    public static final float DEFAULT_PICKUP_DELAY = 0.5f;
    public static final float PICKUP_DELAY_DROPPED = 1.5f;
    public static final float PICKUP_THROTTLE = 0.25f;
    public static final float DEFAULT_MERGE_DELAY = 1.5f;
    @Nullable
    private ItemStack itemStack;
    private boolean isNetworkOutdated;
    private float mergeDelay = 1.5f;
    private float pickupDelay = 0.5f;
    private float pickupThrottle;
    private boolean removedByPlayerPickup;
    private float pickupRange = -1.0f;

    @Nonnull
    public static ComponentType<EntityStore, ItemComponent> getComponentType() {
        return EntityModule.get().getItemComponentType();
    }

    public ItemComponent() {
    }

    public ItemComponent(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemComponent(@Nullable ItemStack itemStack, float mergeDelay, float pickupDelay, float pickupThrottle, boolean removedByPlayerPickup) {
        this.itemStack = itemStack;
        this.mergeDelay = mergeDelay;
        this.pickupDelay = pickupDelay;
        this.pickupThrottle = pickupThrottle;
        this.removedByPlayerPickup = removedByPlayerPickup;
    }

    @Nullable
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
        this.isNetworkOutdated = true;
        this.pickupRange = -1.0f;
    }

    public void setPickupDelay(float pickupDelay) {
        this.pickupDelay = pickupDelay;
    }

    public float getPickupRadius(@Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (this.pickupRange < 0.0f) {
            World world = componentAccessor.getExternalData().getWorld();
            ItemEntityConfig defaultConfig = world.getGameplayConfig().getItemEntityConfig();
            ItemEntityConfig config = this.itemStack != null ? this.itemStack.getItem().getItemEntityConfig() : null;
            this.pickupRange = config != null && config.getPickupRadius() != -1.0f ? config.getPickupRadius() : defaultConfig.getPickupRadius();
        }
        return this.pickupRange;
    }

    public float computeLifetimeSeconds(@Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        ItemEntityConfig itemEntityConfig = this.itemStack != null ? this.itemStack.getItem().getItemEntityConfig() : null;
        ItemEntityConfig defaultConfig = componentAccessor.getExternalData().getWorld().getGameplayConfig().getItemEntityConfig();
        Float ttl = itemEntityConfig != null && itemEntityConfig.getTtl() != null ? itemEntityConfig.getTtl() : defaultConfig.getTtl();
        return ttl != null ? ttl.floatValue() : 120.0f;
    }

    @Nullable
    public ColorLight computeDynamicLight() {
        Item item;
        ColorLight dynamicLight = null;
        Item item2 = item = this.itemStack != null ? this.itemStack.getItem() : null;
        if (item != null) {
            if (item.hasBlockType()) {
                BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(this.itemStack.getBlockKey());
                if (blockType != null && blockType.getLight() != null) {
                    dynamicLight = blockType.getLight();
                }
            } else if (item.getLight() != null) {
                dynamicLight = item.getLight();
            }
        }
        return dynamicLight;
    }

    public boolean pollPickupDelay(float dt) {
        if (this.pickupDelay <= 0.0f) {
            return true;
        }
        this.pickupDelay -= dt;
        return this.pickupDelay <= 0.0f;
    }

    public boolean pollPickupThrottle(float dt) {
        this.pickupThrottle -= dt;
        if (this.pickupThrottle <= 0.0f) {
            this.pickupThrottle = 0.25f;
            return true;
        }
        return false;
    }

    public boolean pollMergeDelay(float dt) {
        this.mergeDelay -= dt;
        if (this.mergeDelay <= 0.0f) {
            this.mergeDelay = 1.5f;
            return true;
        }
        return false;
    }

    public boolean canPickUp() {
        return this.pickupDelay <= 0.0f;
    }

    public boolean isRemovedByPlayerPickup() {
        return this.removedByPlayerPickup;
    }

    public void setRemovedByPlayerPickup(boolean removedByPlayerPickup) {
        this.removedByPlayerPickup = removedByPlayerPickup;
    }

    public boolean consumeNetworkOutdated() {
        boolean temp = this.isNetworkOutdated;
        this.isNetworkOutdated = false;
        return temp;
    }

    @Nonnull
    public ItemComponent clone() {
        return new ItemComponent(this.itemStack, this.mergeDelay, this.pickupDelay, this.pickupThrottle, this.removedByPlayerPickup);
    }

    @Nonnull
    public static Holder<EntityStore>[] generateItemDrops(@Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull List<ItemStack> itemStacks, @Nonnull Vector3d position, @Nonnull Vector3f rotation) {
        if (itemStacks.size() == 1) {
            Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(accessor, itemStacks.getFirst(), position, rotation, 0.0f, 3.25f, 0.0f);
            if (itemEntityHolder == null) {
                return Holder.emptyArray();
            }
            return new Holder[]{itemEntityHolder};
        }
        float randomAngleOffset = ThreadLocalRandom.current().nextFloat() * ((float)Math.PI * 2);
        CircleIterator iterator = new CircleIterator(Vector3d.ZERO, 3.0, itemStacks.size(), randomAngleOffset);
        return (Holder[])itemStacks.stream().map(item -> {
            Vector3d circlePos = iterator.next();
            return ItemComponent.generateItemDrop(accessor, item, position, rotation, (float)circlePos.getX(), 3.25f, (float)circlePos.getZ());
        }).filter(Objects::nonNull).toArray(Holder[]::new);
    }

    @Nullable
    public static Holder<EntityStore> generateItemDrop(@Nonnull ComponentAccessor<EntityStore> accessor, @Nullable ItemStack itemStack, @Nonnull Vector3d position, @Nonnull Vector3f rotation, float velocityX, float velocityY, float velocityZ) {
        if (itemStack == null || itemStack.isEmpty() || !itemStack.isValid()) {
            LOGGER.at(Level.WARNING).log("Attempted to drop invalid item %s at %s", (Object)itemStack, (Object)position);
            return null;
        }
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        ItemComponent itemComponent = new ItemComponent(itemStack);
        holder.addComponent(ItemComponent.getComponentType(), itemComponent);
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(position, rotation));
        holder.ensureAndGetComponent(Velocity.getComponentType()).set(velocityX, velocityY, velocityZ);
        holder.ensureComponent(PhysicsValues.getComponentType());
        holder.ensureComponent(UUIDComponent.getComponentType());
        holder.ensureComponent(Intangible.getComponentType());
        float tempTtl = itemComponent.computeLifetimeSeconds(accessor);
        TimeResource timeResource = accessor.getResource(TimeResource.getResourceType());
        holder.addComponent(DespawnComponent.getComponentType(), DespawnComponent.despawnInSeconds(timeResource, tempTtl));
        return holder;
    }

    @Nonnull
    public static Holder<EntityStore> generatePickedUpItem(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> targetRef, @Nonnull Vector3d targetPosition) {
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        TransformComponent itemTransformComponent = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert (itemTransformComponent != null);
        ItemComponent itemItemComponent = componentAccessor.getComponent(ref, ItemComponent.getComponentType());
        assert (itemItemComponent != null);
        HeadRotation itemHeadRotationComponent = componentAccessor.getComponent(ref, HeadRotation.getComponentType());
        assert (itemHeadRotationComponent != null);
        PickupItemComponent pickupItemComponent = new PickupItemComponent(targetRef, targetPosition.clone());
        holder.addComponent(PickupItemComponent.getComponentType(), pickupItemComponent);
        holder.addComponent(ItemComponent.getComponentType(), itemItemComponent.clone());
        holder.addComponent(TransformComponent.getComponentType(), itemTransformComponent.clone());
        holder.ensureComponent(PreventItemMerging.getComponentType());
        holder.ensureComponent(Intangible.getComponentType());
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(ref.getStore().getExternalData().takeNextNetworkId()));
        holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
        return holder;
    }

    @Nonnull
    public static Holder<EntityStore> generatePickedUpItem(@Nonnull ItemStack itemStack, @Nonnull Vector3d position, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> targetRef) {
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        PickupItemComponent pickupItemComponent = new PickupItemComponent(targetRef, position.clone());
        holder.addComponent(PickupItemComponent.getComponentType(), pickupItemComponent);
        holder.addComponent(ItemComponent.getComponentType(), new ItemComponent(new ItemStack(itemStack.getItemId())));
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(position.clone(), Vector3f.ZERO.clone()));
        holder.ensureComponent(PreventItemMerging.getComponentType());
        holder.ensureComponent(Intangible.getComponentType());
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(componentAccessor.getExternalData().takeNextNetworkId()));
        holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
        return holder;
    }

    @Nullable
    public static ItemStack addToItemContainer(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> itemRef, @Nonnull ItemContainer itemContainer) {
        if (!itemRef.isValid()) {
            return null;
        }
        ItemComponent itemComponent = store.getComponent(itemRef, ItemComponent.getComponentType());
        if (itemComponent == null || itemComponent.pickupDelay > 0.0f) {
            return null;
        }
        ItemStack itemStack = itemComponent.getItemStack();
        if (itemStack == null) {
            return null;
        }
        ItemStackTransaction transaction = itemContainer.addItemStack(itemStack);
        ItemStack remainder = transaction.getRemainder();
        if (remainder != null && !remainder.isEmpty()) {
            itemComponent.setPickupDelay(0.25f);
            itemComponent.setItemStack(remainder);
            int quantity = itemStack.getQuantity() - remainder.getQuantity();
            if (quantity <= 0) {
                return null;
            }
            return itemStack.withQuantity(quantity);
        }
        store.removeEntity(itemRef, RemoveReason.REMOVE);
        return itemStack;
    }
}

