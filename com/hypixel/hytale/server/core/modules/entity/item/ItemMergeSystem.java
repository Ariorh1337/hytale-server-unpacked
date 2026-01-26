/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.item;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ColorLight;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.DespawnComponent;
import com.hypixel.hytale.server.core.modules.entity.component.DynamicLight;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventItemMerging;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import javax.annotation.Nonnull;

public class ItemMergeSystem
extends EntityTickingSystem<EntityStore> {
    public static final float RADIUS = 2.0f;
    @Nonnull
    private final ComponentType<EntityStore, ItemComponent> itemComponentComponentType;
    @Nonnull
    private final ComponentType<EntityStore, Interactable> interactableComponentType;
    @Nonnull
    private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> itemSpatialComponent;
    @Nonnull
    private final Query<EntityStore> query;

    public ItemMergeSystem(@Nonnull ComponentType<EntityStore, ItemComponent> itemComponentComponentType, @Nonnull ComponentType<EntityStore, Interactable> interactableComponentType, @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> itemSpatialComponent) {
        this.itemComponentComponentType = itemComponentComponentType;
        this.itemSpatialComponent = itemSpatialComponent;
        this.interactableComponentType = interactableComponentType;
        this.query = Query.and(itemComponentComponentType, TransformComponent.getComponentType(), Query.not(interactableComponentType), Query.not(PreventItemMerging.getComponentType()));
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return false;
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        ItemComponent itemComponent = archetypeChunk.getComponent(index, this.itemComponentComponentType);
        assert (itemComponent != null);
        ItemStack itemStack = itemComponent.getItemStack();
        if (itemStack == null) {
            return;
        }
        Item itemAsset = itemStack.getItem();
        int maxStack = itemAsset.getMaxStack();
        if (maxStack <= 1 || itemStack.getQuantity() >= maxStack) {
            return;
        }
        if (!itemComponent.pollMergeDelay(dt)) {
            return;
        }
        SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(this.itemSpatialComponent);
        TimeResource timeResource = store.getResource(TimeResource.getResourceType());
        TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d position = transformComponent.getPosition();
        ObjectList results = SpatialResource.getThreadLocalReferenceList();
        spatialResource.getSpatialStructure().ordered(position, 2.0, results);
        Ref<EntityStore> reference = archetypeChunk.getReferenceTo(index);
        for (Ref ref : results) {
            float newLifetime;
            int otherQuantity;
            if (!ref.isValid() || ref.equals(reference)) continue;
            ItemComponent otherItemComponent = store.getComponent(ref, this.itemComponentComponentType);
            assert (otherItemComponent != null);
            ItemStack otherItemStack = otherItemComponent.getItemStack();
            if (otherItemStack == null || commandBuffer.getArchetype(ref).contains(this.interactableComponentType) || !itemStack.isStackableWith(otherItemStack) || (otherQuantity = otherItemStack.getQuantity()) >= maxStack) continue;
            int combinedTotal = itemStack.getQuantity() + otherQuantity;
            if (combinedTotal <= maxStack) {
                commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
                otherItemComponent.setItemStack(null);
                itemStack = itemStack.withQuantity(combinedTotal);
            } else {
                otherItemComponent.setItemStack(itemStack.withQuantity(combinedTotal - maxStack));
                newLifetime = otherItemComponent.computeLifetimeSeconds(commandBuffer);
                DespawnComponent.trySetDespawn(commandBuffer, timeResource, ref, commandBuffer.getComponent(ref, DespawnComponent.getComponentType()), Float.valueOf(newLifetime));
                ColorLight otherItemDynamicLight = otherItemComponent.computeDynamicLight();
                if (otherItemDynamicLight != null) {
                    DynamicLight otherDynamicLightComponent = commandBuffer.getComponent(ref, DynamicLight.getComponentType());
                    if (otherDynamicLightComponent != null) {
                        otherDynamicLightComponent.setColorLight(otherItemDynamicLight);
                    } else {
                        commandBuffer.putComponent(ref, DynamicLight.getComponentType(), new DynamicLight(otherItemDynamicLight));
                    }
                }
                itemStack = itemStack.withQuantity(maxStack);
            }
            itemComponent.setItemStack(itemStack);
            newLifetime = itemComponent.computeLifetimeSeconds(commandBuffer);
            DespawnComponent.trySetDespawn(commandBuffer, timeResource, reference, archetypeChunk.getComponent(index, DespawnComponent.getComponentType()), Float.valueOf(newLifetime));
            if (itemStack.getQuantity() < maxStack) continue;
            break;
        }
    }
}

