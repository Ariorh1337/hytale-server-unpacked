/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.projectile.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.projectile.component.PredictedProjectile;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PredictedProjectileSystems {

    public static class EntityTrackerUpdate
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType = EntityTrackerSystems.Visible.getComponentType();
        @Nonnull
        private final ComponentType<EntityStore, PredictedProjectile> predictedProjectileComponentType = PredictedProjectile.getComponentType();
        @Nonnull
        private final Query<EntityStore> query = Query.and(this.visibleComponentType, this.predictedProjectileComponentType);

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityTrackerSystems.Visible visibleComponent = archetypeChunk.getComponent(index, this.visibleComponentType);
            assert (visibleComponent != null);
            PredictedProjectile predictedProjectileComponent = archetypeChunk.getComponent(index, this.predictedProjectileComponentType);
            assert (predictedProjectileComponent != null);
            if (!visibleComponent.newlyVisibleTo.isEmpty()) {
                EntityTrackerUpdate.queueUpdatesFor(archetypeChunk.getReferenceTo(index), predictedProjectileComponent, visibleComponent.newlyVisibleTo);
            }
        }

        private static void queueUpdatesFor(@Nonnull Ref<EntityStore> ref, @Nonnull PredictedProjectile predictedProjectile, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.Prediction;
            update.predictionId = predictedProjectile.getUuid();
            for (Map.Entry<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> entry : visibleTo.entrySet()) {
                entry.getValue().queueUpdate(ref, update);
            }
        }
    }
}

