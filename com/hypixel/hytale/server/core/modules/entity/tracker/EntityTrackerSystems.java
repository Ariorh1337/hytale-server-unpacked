/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.tracker;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.spatial.SpatialStructure;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.EntityEffectsUpdate;
import com.hypixel.hytale.protocol.packets.entities.EntityUpdates;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.system.NetworkSendableSpatialSystem;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.receiver.IPacketReceiver;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityTrackerSystems {
    @Nonnull
    public static final SystemGroup<EntityStore> FIND_VISIBLE_ENTITIES_GROUP = EntityStore.REGISTRY.registerSystemGroup();
    @Nonnull
    public static final SystemGroup<EntityStore> QUEUE_UPDATE_GROUP = EntityStore.REGISTRY.registerSystemGroup();

    public static boolean despawnAll(@Nonnull Ref<EntityStore> viewerRef, @Nonnull Store<EntityStore> store) {
        if (!viewerRef.isValid()) {
            return false;
        }
        EntityViewer entityViewerComponent = store.getComponent(viewerRef, EntityViewer.getComponentType());
        if (entityViewerComponent == null) {
            return false;
        }
        int networkId = entityViewerComponent.sent.removeInt(viewerRef);
        EntityUpdates packet = new EntityUpdates();
        packet.removed = entityViewerComponent.sent.values().toIntArray();
        entityViewerComponent.packetReceiver.writeNoCache(packet);
        EntityTrackerSystems.clear(viewerRef, store);
        entityViewerComponent.sent.put(viewerRef, networkId);
        return true;
    }

    public static boolean clear(@Nonnull Ref<EntityStore> viewerRef, @Nonnull Store<EntityStore> store) {
        if (!viewerRef.isValid()) {
            return false;
        }
        EntityViewer entityViewerComponent = store.getComponent(viewerRef, EntityViewer.getComponentType());
        if (entityViewerComponent == null) {
            return false;
        }
        for (Ref ref : entityViewerComponent.sent.keySet()) {
            Visible visibleComponent;
            if (ref == null || !ref.isValid() || (visibleComponent = store.getComponent(ref, Visible.getComponentType())) == null) continue;
            visibleComponent.visibleTo.remove(viewerRef);
        }
        entityViewerComponent.sent.clear();
        return true;
    }

    public static class EntityViewer
    implements Component<EntityStore> {
        public int viewRadiusBlocks;
        @Nonnull
        public IPacketReceiver packetReceiver;
        @Nonnull
        public Set<Ref<EntityStore>> visible;
        @Nonnull
        public Map<Ref<EntityStore>, EntityUpdate> updates;
        @Nonnull
        public Object2IntMap<Ref<EntityStore>> sent;
        public int lodExcludedCount;
        public int hiddenCount;

        public static ComponentType<EntityStore, EntityViewer> getComponentType() {
            return EntityModule.get().getEntityViewerComponentType();
        }

        public EntityViewer(int viewRadiusBlocks, @Nonnull IPacketReceiver packetReceiver) {
            this.viewRadiusBlocks = viewRadiusBlocks;
            this.packetReceiver = packetReceiver;
            this.visible = new ObjectOpenHashSet<Ref<EntityStore>>();
            this.updates = new ConcurrentHashMap<Ref<EntityStore>, EntityUpdate>();
            this.sent = new Object2IntOpenHashMap<Ref<EntityStore>>();
            this.sent.defaultReturnValue(-1);
        }

        public EntityViewer(@Nonnull EntityViewer other) {
            this.viewRadiusBlocks = other.viewRadiusBlocks;
            this.packetReceiver = other.packetReceiver;
            this.visible = new HashSet<Ref<EntityStore>>(other.visible);
            this.updates = new ConcurrentHashMap<Ref<EntityStore>, EntityUpdate>(other.updates.size());
            for (Map.Entry<Ref<EntityStore>, EntityUpdate> entry : other.updates.entrySet()) {
                this.updates.put(entry.getKey(), entry.getValue().clone());
            }
            this.sent = new Object2IntOpenHashMap<Ref<EntityStore>>(other.sent);
            this.sent.defaultReturnValue(-1);
        }

        @Override
        @Nonnull
        public Component<EntityStore> clone() {
            return new EntityViewer(this);
        }

        public void queueRemove(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentUpdateType type) {
            if (!this.visible.contains(ref)) {
                throw new IllegalArgumentException("Entity is not visible!");
            }
            this.updates.computeIfAbsent(ref, k -> new EntityUpdate()).queueRemove(type);
        }

        public void queueUpdate(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentUpdate update) {
            if (!this.visible.contains(ref)) {
                throw new IllegalArgumentException("Entity is not visible!");
            }
            this.updates.computeIfAbsent(ref, k -> new EntityUpdate()).queueUpdate(update);
        }
    }

    public static class Visible
    implements Component<EntityStore> {
        @Nonnull
        private final StampedLock lock = new StampedLock();
        @Nonnull
        public Map<Ref<EntityStore>, EntityViewer> previousVisibleTo = new Object2ObjectOpenHashMap<Ref<EntityStore>, EntityViewer>();
        @Nonnull
        public Map<Ref<EntityStore>, EntityViewer> visibleTo = new Object2ObjectOpenHashMap<Ref<EntityStore>, EntityViewer>();
        @Nonnull
        public Map<Ref<EntityStore>, EntityViewer> newlyVisibleTo = new Object2ObjectOpenHashMap<Ref<EntityStore>, EntityViewer>();

        @Nonnull
        public static ComponentType<EntityStore, Visible> getComponentType() {
            return EntityModule.get().getVisibleComponentType();
        }

        @Override
        @Nonnull
        public Component<EntityStore> clone() {
            return new Visible();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addViewerParallel(@Nonnull Ref<EntityStore> ref, @Nonnull EntityViewer entityViewerComponent) {
            long stamp = this.lock.writeLock();
            try {
                this.visibleTo.put(ref, entityViewerComponent);
                if (!this.previousVisibleTo.containsKey(ref)) {
                    this.newlyVisibleTo.put(ref, entityViewerComponent);
                }
            }
            finally {
                this.lock.unlockWrite(stamp);
            }
        }
    }

    public static class SendPackets
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
        @Nonnull
        public static final ThreadLocal<IntList> INT_LIST_THREAD_LOCAL = ThreadLocal.withInitial(IntArrayList::new);
        @Nonnull
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemGroupDependency<EntityStore>(Order.AFTER, QUEUE_UPDATE_GROUP));
        @Nonnull
        private final ComponentType<EntityStore, EntityViewer> entityViewerComponentType;

        public SendPackets(@Nonnull ComponentType<EntityStore, EntityViewer> entityViewerComponentType) {
            this.entityViewerComponentType = entityViewerComponentType;
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return EntityStore.SEND_PACKET_GROUP;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.entityViewerComponentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);
            assert (entityViewerComponent != null);
            IntList removedEntities = INT_LIST_THREAD_LOCAL.get();
            removedEntities.clear();
            int before = entityViewerComponent.updates.size();
            entityViewerComponent.updates.entrySet().removeIf(v -> !((Ref)v.getKey()).isValid());
            if (before != entityViewerComponent.updates.size()) {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("Removed %d invalid updates for removed entities.", before - entityViewerComponent.updates.size());
            }
            Iterator<Ref<EntityStore>> iterator = entityViewerComponent.sent.object2IntEntrySet().iterator();
            while (iterator.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)iterator.next();
                Ref ref = (Ref)entry.getKey();
                if (ref != null && ref.isValid() && entityViewerComponent.visible.contains(ref)) continue;
                removedEntities.add(entry.getIntValue());
                iterator.remove();
                if (entityViewerComponent.updates.remove(ref) == null) continue;
                ((HytaleLogger.Api)LOGGER.atSevere()).log("Entity can't be removed and also receive an update! " + String.valueOf(ref));
            }
            if (!removedEntities.isEmpty() || !entityViewerComponent.updates.isEmpty()) {
                iterator = entityViewerComponent.updates.keySet().iterator();
                while (iterator.hasNext()) {
                    Ref<EntityStore> ref = iterator.next();
                    if (ref == null || !ref.isValid() || ref.getStore() != store) {
                        iterator.remove();
                        continue;
                    }
                    if (entityViewerComponent.sent.containsKey(ref)) continue;
                    NetworkId networkIdComponent = commandBuffer.getComponent(ref, NetworkId.getComponentType());
                    assert (networkIdComponent != null);
                    int networkId = networkIdComponent.getId();
                    if (networkId == -1) {
                        throw new IllegalArgumentException("Invalid entity network id: " + String.valueOf(ref));
                    }
                    entityViewerComponent.sent.put(ref, networkId);
                }
                EntityUpdates packet = new EntityUpdates();
                packet.removed = !removedEntities.isEmpty() ? removedEntities.toIntArray() : null;
                packet.updates = new com.hypixel.hytale.protocol.EntityUpdate[entityViewerComponent.updates.size()];
                int i = 0;
                for (Map.Entry<Ref<EntityStore>, EntityUpdate> entry : entityViewerComponent.updates.entrySet()) {
                    int n = i++;
                    com.hypixel.hytale.protocol.EntityUpdate entityUpdate = new com.hypixel.hytale.protocol.EntityUpdate();
                    packet.updates[n] = entityUpdate;
                    com.hypixel.hytale.protocol.EntityUpdate entityUpdate2 = entityUpdate;
                    entityUpdate2.networkId = entityViewerComponent.sent.getInt(entry.getKey());
                    EntityUpdate update = entry.getValue();
                    entityUpdate2.removed = update.toRemovedArray();
                    entityUpdate2.updates = update.toUpdatesArray();
                }
                entityViewerComponent.updates.clear();
                entityViewerComponent.packetReceiver.writeNoCache(packet);
            }
        }
    }

    public static class EffectControllerSystem
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, Visible> visibleComponentType;
        @Nonnull
        private final ComponentType<EntityStore, EffectControllerComponent> effectControllerComponentType;
        @Nonnull
        private final Query<EntityStore> query;

        public EffectControllerSystem(@Nonnull ComponentType<EntityStore, Visible> visibleComponentType, @Nonnull ComponentType<EntityStore, EffectControllerComponent> effectControllerComponentType) {
            this.visibleComponentType = visibleComponentType;
            this.effectControllerComponentType = effectControllerComponentType;
            this.query = Query.and(visibleComponentType, effectControllerComponentType);
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return QUEUE_UPDATE_GROUP;
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
            Visible visibleComponent = archetypeChunk.getComponent(index, this.visibleComponentType);
            assert (visibleComponent != null);
            EffectControllerComponent effectControllerComponent = archetypeChunk.getComponent(index, this.effectControllerComponentType);
            assert (effectControllerComponent != null);
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            if (!visibleComponent.newlyVisibleTo.isEmpty()) {
                EffectControllerSystem.queueFullUpdate(ref, effectControllerComponent, visibleComponent.newlyVisibleTo);
            }
            if (effectControllerComponent.consumeNetworkOutdated()) {
                EffectControllerSystem.queueUpdatesFor(ref, effectControllerComponent, visibleComponent.visibleTo, visibleComponent.newlyVisibleTo);
            }
        }

        private static void queueFullUpdate(@Nonnull Ref<EntityStore> ref, @Nonnull EffectControllerComponent effectControllerComponent, @Nonnull Map<Ref<EntityStore>, EntityViewer> visibleTo) {
            EntityEffectsUpdate update = new EntityEffectsUpdate();
            update.entityEffectUpdates = effectControllerComponent.createInitUpdates();
            for (EntityViewer viewer : visibleTo.values()) {
                viewer.queueUpdate(ref, update);
            }
        }

        private static void queueUpdatesFor(@Nonnull Ref<EntityStore> ref, @Nonnull EffectControllerComponent effectControllerComponent, @Nonnull Map<Ref<EntityStore>, EntityViewer> visibleTo, @Nonnull Map<Ref<EntityStore>, EntityViewer> exclude) {
            EntityEffectsUpdate update = new EntityEffectsUpdate();
            update.entityEffectUpdates = effectControllerComponent.consumeChanges();
            if (exclude.isEmpty()) {
                for (EntityViewer viewer : visibleTo.values()) {
                    viewer.queueUpdate(ref, update);
                }
                return;
            }
            for (Map.Entry<Ref<EntityStore>, EntityViewer> entry : visibleTo.entrySet()) {
                if (exclude.containsKey(entry.getKey())) continue;
                entry.getValue().queueUpdate(ref, update);
            }
        }
    }

    public static class RemoveVisibleComponent
    extends HolderSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, Visible> visibleComponentType;

        public RemoveVisibleComponent(@Nonnull ComponentType<EntityStore, Visible> visibleComponentType) {
            this.visibleComponentType = visibleComponentType;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.visibleComponentType;
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
            holder.removeComponent(this.visibleComponentType);
        }
    }

    public static class RemoveEmptyVisibleComponent
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, AddToVisible.class), new SystemGroupDependency<EntityStore>(Order.BEFORE, QUEUE_UPDATE_GROUP));
        @Nonnull
        private final ComponentType<EntityStore, Visible> visibleComponentType;

        public RemoveEmptyVisibleComponent(@Nonnull ComponentType<EntityStore, Visible> visibleComponentType) {
            this.visibleComponentType = visibleComponentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.visibleComponentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Visible visibleComponent = archetypeChunk.getComponent(index, this.visibleComponentType);
            assert (visibleComponent != null);
            if (visibleComponent.visibleTo.isEmpty()) {
                commandBuffer.removeComponent(archetypeChunk.getReferenceTo(index), this.visibleComponentType);
            }
        }
    }

    public static class AddToVisible
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Collections.singleton(new SystemDependency(Order.AFTER, EnsureVisibleComponent.class));
        @Nonnull
        private final ComponentType<EntityStore, EntityViewer> entityViewerComponentType;
        @Nonnull
        private final ComponentType<EntityStore, Visible> visibleComponentType;

        public AddToVisible(@Nonnull ComponentType<EntityStore, EntityViewer> entityViewerComponentType, @Nonnull ComponentType<EntityStore, Visible> visibleComponentType) {
            this.entityViewerComponentType = entityViewerComponentType;
            this.visibleComponentType = visibleComponentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.entityViewerComponentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);
            assert (entityViewerComponent != null);
            for (Ref<EntityStore> vislbleRef : entityViewerComponent.visible) {
                Visible visibleComponent;
                if (vislbleRef == null || !vislbleRef.isValid() || (visibleComponent = commandBuffer.getComponent(vislbleRef, this.visibleComponentType)) == null) continue;
                visibleComponent.addViewerParallel(ref, entityViewerComponent);
            }
        }
    }

    public static class EnsureVisibleComponent
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Collections.singleton(new SystemDependency(Order.AFTER, ClearPreviouslyVisible.class));
        @Nonnull
        private final ComponentType<EntityStore, EntityViewer> entityViewerComponentType;
        @Nonnull
        private final ComponentType<EntityStore, Visible> visibleComponentType;

        public EnsureVisibleComponent(@Nonnull ComponentType<EntityStore, EntityViewer> entityViewerComponentType, @Nonnull ComponentType<EntityStore, Visible> visibleComponentType) {
            this.entityViewerComponentType = entityViewerComponentType;
            this.visibleComponentType = visibleComponentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.entityViewerComponentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);
            assert (entityViewerComponent != null);
            for (Ref<EntityStore> visibleRef : entityViewerComponent.visible) {
                if (visibleRef == null || !visibleRef.isValid() || commandBuffer.getArchetype(visibleRef).contains(this.visibleComponentType)) continue;
                commandBuffer.ensureComponent(visibleRef, this.visibleComponentType);
            }
        }
    }

    public static class ClearPreviouslyVisible
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, ClearEntityViewers.class), new SystemGroupDependency<EntityStore>(Order.AFTER, FIND_VISIBLE_ENTITIES_GROUP));
        @Nonnull
        private final ComponentType<EntityStore, Visible> visibleComponentType;

        public ClearPreviouslyVisible(@Nonnull ComponentType<EntityStore, Visible> visibleComponentType) {
            this.visibleComponentType = visibleComponentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.visibleComponentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Visible visibleComponent = archetypeChunk.getComponent(index, this.visibleComponentType);
            assert (visibleComponent != null);
            Map<Ref<EntityStore>, EntityViewer> oldVisibleTo = visibleComponent.previousVisibleTo;
            visibleComponent.previousVisibleTo = visibleComponent.visibleTo;
            visibleComponent.visibleTo = oldVisibleTo;
            visibleComponent.visibleTo.clear();
            visibleComponent.newlyVisibleTo.clear();
        }
    }

    public static class CollectVisible
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, EntityViewer> entityViewerComponentType;
        @Nonnull
        private final Query<EntityStore> query;
        @Nonnull
        private final Set<Dependency<EntityStore>> dependencies;

        public CollectVisible(@Nonnull ComponentType<EntityStore, EntityViewer> entityViewerComponentType) {
            this.entityViewerComponentType = entityViewerComponentType;
            this.query = Archetype.of(entityViewerComponentType, TransformComponent.getComponentType());
            this.dependencies = Collections.singleton(new SystemDependency(Order.AFTER, NetworkSendableSpatialSystem.class));
        }

        @Override
        @Nullable
        public SystemGroup<EntityStore> getGroup() {
            return FIND_VISIBLE_ENTITIES_GROUP;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return this.dependencies;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.query;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);
            assert (entityViewerComponent != null);
            SpatialStructure<Ref<EntityStore>> spatialStructure = store.getResource(EntityModule.get().getNetworkSendableSpatialResourceType()).getSpatialStructure();
            ObjectList results = SpatialResource.getThreadLocalReferenceList();
            spatialStructure.collect(position, entityViewerComponent.viewRadiusBlocks, results);
            entityViewerComponent.visible.addAll(results);
        }
    }

    public static class ClearEntityViewers
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Collections.singleton(new SystemGroupDependency<EntityStore>(Order.BEFORE, FIND_VISIBLE_ENTITIES_GROUP));
        @Nonnull
        private final ComponentType<EntityStore, EntityViewer> entityViewerComponentType;

        public ClearEntityViewers(@Nonnull ComponentType<EntityStore, EntityViewer> entityViewerComponentType) {
            this.entityViewerComponentType = entityViewerComponentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.entityViewerComponentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);
            assert (entityViewerComponent != null);
            entityViewerComponent.visible.clear();
            entityViewerComponent.lodExcludedCount = 0;
            entityViewerComponent.hiddenCount = 0;
        }
    }

    public static class EntityUpdate {
        @Nonnull
        private final StampedLock removeLock = new StampedLock();
        @Nonnull
        private final EnumSet<ComponentUpdateType> removed;
        @Nonnull
        private final StampedLock updatesLock = new StampedLock();
        @Nonnull
        private final List<ComponentUpdate> updates;

        public EntityUpdate() {
            this.removed = EnumSet.noneOf(ComponentUpdateType.class);
            this.updates = new ObjectArrayList<ComponentUpdate>();
        }

        public EntityUpdate(@Nonnull EntityUpdate other) {
            this.removed = EnumSet.copyOf(other.removed);
            this.updates = new ObjectArrayList<ComponentUpdate>(other.updates);
        }

        @Nonnull
        public EntityUpdate clone() {
            return new EntityUpdate(this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void queueRemove(@Nonnull ComponentUpdateType type) {
            long stamp = this.removeLock.writeLock();
            try {
                this.removed.add(type);
            }
            finally {
                this.removeLock.unlockWrite(stamp);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void queueUpdate(@Nonnull ComponentUpdate update) {
            long stamp = this.updatesLock.writeLock();
            try {
                this.updates.add(update);
            }
            finally {
                this.updatesLock.unlockWrite(stamp);
            }
        }

        @Nullable
        public ComponentUpdateType[] toRemovedArray() {
            return this.removed.isEmpty() ? null : (ComponentUpdateType[])this.removed.toArray(ComponentUpdateType[]::new);
        }

        @Nullable
        public ComponentUpdate[] toUpdatesArray() {
            return this.updates.isEmpty() ? null : (ComponentUpdate[])this.updates.toArray(ComponentUpdate[]::new);
        }
    }
}

