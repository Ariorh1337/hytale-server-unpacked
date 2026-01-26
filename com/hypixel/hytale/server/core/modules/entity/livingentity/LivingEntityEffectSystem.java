/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.livingentity;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.DisableProcessingAssert;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LivingEntityEffectSystem
extends EntityTickingSystem<EntityStore>
implements DisableProcessingAssert {
    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return EffectControllerComponent.getComponentType();
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return false;
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        EffectControllerComponent effectControllerComponent = archetypeChunk.getComponent(index, EffectControllerComponent.getComponentType());
        assert (effectControllerComponent != null);
        Int2ObjectMap<ActiveEntityEffect> activeEffects = effectControllerComponent.getActiveEffects();
        if (activeEffects.isEmpty()) {
            return;
        }
        IndexedLookupTableAssetMap<String, EntityEffect> entityEffectAssetMap = EntityEffect.getAssetMap();
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
        Iterator iterator = activeEffects.values().iterator();
        EntityStatMap entityStatMapComponent = commandBuffer.getComponent(entityRef, EntityStatMap.getComponentType());
        boolean invalidated = false;
        boolean invulnerable = false;
        while (iterator.hasNext()) {
            ActiveEntityEffect activeEntityEffect = (ActiveEntityEffect)iterator.next();
            int entityEffectIndex = activeEntityEffect.getEntityEffectIndex();
            EntityEffect entityEffect = entityEffectAssetMap.getAsset(entityEffectIndex);
            if (entityEffect == null) {
                iterator.remove();
                invalidated = true;
                continue;
            }
            if (!LivingEntityEffectSystem.canApplyEffect(entityRef, entityEffect, commandBuffer)) {
                iterator.remove();
                invalidated = true;
                continue;
            }
            float tickDelta = Math.min(activeEntityEffect.getRemainingDuration(), dt);
            activeEntityEffect.tick(commandBuffer, entityRef, entityEffect, entityStatMapComponent, tickDelta);
            if (activeEffects.isEmpty()) {
                return;
            }
            if (!activeEntityEffect.isInfinite() && activeEntityEffect.getRemainingDuration() <= 0.0f) {
                iterator.remove();
                effectControllerComponent.tryResetModelChange(entityRef, activeEntityEffect.getEntityEffectIndex(), commandBuffer);
                invalidated = true;
            }
            if (!activeEntityEffect.isInvulnerable()) continue;
            invulnerable = true;
        }
        effectControllerComponent.setInvulnerable(invulnerable);
        if (invalidated) {
            effectControllerComponent.invalidateCache();
            Entity entity = EntityUtils.getEntity(index, archetypeChunk);
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                livingEntity.getStatModifiersManager().setRecalculate(true);
            }
        }
    }

    @Override
    @Nullable
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getGatherDamageGroup();
    }

    public static boolean canApplyEffect(@Nonnull Ref<EntityStore> ownerRef, @Nonnull EntityEffect entityEffect, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        TransformComponent transformComponent = componentAccessor.getComponent(ownerRef, TransformComponent.getComponentType());
        assert (transformComponent != null);
        BoundingBox boundingBoxComponent = componentAccessor.getComponent(ownerRef, BoundingBox.getComponentType());
        assert (boundingBoxComponent != null);
        Vector3d position = transformComponent.getPosition();
        Box boundingBox = boundingBoxComponent.getBoundingBox();
        World world = componentAccessor.getExternalData().getWorld();
        if ("Burn".equals(entityEffect.getId())) {
            Ref<ChunkStore> chunkRef = transformComponent.getChunkRef();
            if (chunkRef == null || !chunkRef.isValid()) {
                return false;
            }
            Store<ChunkStore> chunkComponentStore = world.getChunkStore().getStore();
            WorldChunk worldChunkComponent = chunkComponentStore.getComponent(chunkRef, WorldChunk.getComponentType());
            assert (worldChunkComponent != null);
            LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atChunkCoords(world, worldChunkComponent.getX(), worldChunkComponent.getZ(), 1);
            return boundingBox.forEachBlock(position, chunkAccessor, (x, y, z, _chunkAccessor) -> {
                WorldChunk localChunk = _chunkAccessor.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
                if (localChunk == null) {
                    return true;
                }
                return !localChunk.getBlockType(x, y, z).getId().contains("Fluid_Water");
            });
        }
        return true;
    }
}

