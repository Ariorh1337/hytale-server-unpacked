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
    public static final SystemGroup<EntityStore> FIND_VISIBLE_ENTITIES_GROUP = EntityStore.REGISTRY.registerSystemGroup();
    public static final SystemGroup<EntityStore> QUEUE_UPDATE_GROUP = EntityStore.REGISTRY.registerSystemGroup();

    public static boolean despawnAll(@Nonnull Ref<EntityStore> viewerRef, @Nonnull Store<EntityStore> store) {
        EntityViewer viewer = store.getComponent(viewerRef, EntityViewer.getComponentType());
        if (viewer == null) {
            return false;
        }
        int networkId = viewer.sent.removeInt(viewerRef);
        EntityUpdates packet = new EntityUpdates();
        packet.removed = viewer.sent.values().toIntArray();
        viewer.packetReceiver.writeNoCache(packet);
        EntityTrackerSystems.clear(viewerRef, store);
        viewer.sent.put(viewerRef, networkId);
        return true;
    }

    public static boolean clear(@Nonnull Ref<EntityStore> viewerRef, @Nonnull Store<EntityStore> store) {
        EntityViewer viewer = store.getComponent(viewerRef, EntityViewer.getComponentType());
        if (viewer == null) {
            return false;
        }
        for (Ref ref : viewer.sent.keySet()) {
            Visible visible = store.getComponent(ref, Visible.getComponentType());
            if (visible == null) continue;
            visible.visibleTo.remove(viewerRef);
        }
        viewer.sent.clear();
        return true;
    }

    public static class EntityViewer
    implements Component<EntityStore> {
        public int viewRadiusBlocks;
        public IPacketReceiver packetReceiver;
        public Set<Ref<EntityStore>> visible;
        public Map<Ref<EntityStore>, EntityUpdate> updates;
        public Object2IntMap<Ref<EntityStore>> sent;
        public int lodExcludedCount;
        public int hiddenCount;

        public static ComponentType<EntityStore, EntityViewer> getComponentType() {
            return EntityModule.get().getEntityViewerComponentType();
        }

        public EntityViewer(int viewRadiusBlocks, IPacketReceiver packetReceiver) {
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

        public void queueRemove(Ref<EntityStore> ref, ComponentUpdateType type) {
            if (!this.visible.contains(ref)) {
                throw new IllegalArgumentException("Entity is not visible!");
            }
            this.updates.computeIfAbsent(ref, k -> new EntityUpdate()).queueRemove(type);
        }

        public void queueUpdate(Ref<EntityStore> ref, ComponentUpdate update) {
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
        public void addViewerParallel(Ref<EntityStore> ref, EntityViewer entityViewer) {
            long stamp = this.lock.writeLock();
            try {
                this.visibleTo.put(ref, entityViewer);
                if (!this.previousVisibleTo.containsKey(ref)) {
                    this.newlyVisibleTo.put(ref, entityViewer);
                }
            }
            finally {
                this.lock.unlockWrite(stamp);
            }
        }
    }

    public static class SendPackets
    extends EntityTickingSystem<EntityStore> {
        public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
        public static final ThreadLocal<IntList> INT_LIST_THREAD_LOCAL = ThreadLocal.withInitial(IntArrayList::new);
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemGroupDependency<EntityStore>(Order.AFTER, QUEUE_UPDATE_GROUP));
        private final ComponentType<EntityStore, EntityViewer> componentType;

        public SendPackets(ComponentType<EntityStore, EntityViewer> componentType) {
            this.componentType = componentType;
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
            return this.componentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityViewer viewer = archetypeChunk.getComponent(index, this.componentType);
            IntList removedEntities = INT_LIST_THREAD_LOCAL.get();
            removedEntities.clear();
            int before = viewer.updates.size();
            viewer.updates.entrySet().removeIf(v -> !((Ref)v.getKey()).isValid());
            if (before != viewer.updates.size()) {
                ((HytaleLogger.Api)LOGGER.atWarning()).log("Removed %d invalid updates for removed entities.", before - viewer.updates.size());
            }
            Iterator<Ref<EntityStore>> iterator = viewer.sent.object2IntEntrySet().iterator();
            while (iterator.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)iterator.next();
                Ref ref = (Ref)entry.getKey();
                if (ref.isValid() && viewer.visible.contains(ref)) continue;
                removedEntities.add(entry.getIntValue());
                iterator.remove();
                if (viewer.updates.remove(ref) == null) continue;
                ((HytaleLogger.Api)LOGGER.atSevere()).log("Entity can't be removed and also receive an update! " + String.valueOf(ref));
            }
            if (!removedEntities.isEmpty() || !viewer.updates.isEmpty()) {
                iterator = viewer.updates.keySet().iterator();
                while (iterator.hasNext()) {
                    Ref<EntityStore> ref = iterator.next();
                    if (!ref.isValid() || ref.getStore() != store) {
                        iterator.remove();
                        continue;
                    }
                    if (viewer.sent.containsKey(ref)) continue;
                    int networkId = commandBuffer.getComponent(ref, NetworkId.getComponentType()).getId();
                    if (networkId == -1) {
                        throw new IllegalArgumentException("Invalid entity network id: " + String.valueOf(ref));
                    }
                    viewer.sent.put(ref, networkId);
                }
                EntityUpdates packet = new EntityUpdates();
                packet.removed = !removedEntities.isEmpty() ? removedEntities.toIntArray() : null;
                packet.updates = new com.hypixel.hytale.protocol.EntityUpdate[viewer.updates.size()];
                int i = 0;
                for (Map.Entry<Ref<EntityStore>, EntityUpdate> entry : viewer.updates.entrySet()) {
                    int n = i++;
                    com.hypixel.hytale.protocol.EntityUpdate entityUpdate = new com.hypixel.hytale.protocol.EntityUpdate();
                    packet.updates[n] = entityUpdate;
                    com.hypixel.hytale.protocol.EntityUpdate entityUpdate2 = entityUpdate;
                    entityUpdate2.networkId = viewer.sent.getInt(entry.getKey());
                    EntityUpdate update = entry.getValue();
                    entityUpdate2.removed = update.toRemovedArray();
                    entityUpdate2.updates = update.toUpdatesArray();
                }
                viewer.updates.clear();
                viewer.packetReceiver.writeNoCache(packet);
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
            Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
            EffectControllerComponent effectControllerComponent = archetypeChunk.getComponent(index, this.effectControllerComponentType);
            assert (effectControllerComponent != null);
            if (!visibleComponent.newlyVisibleTo.isEmpty()) {
                EffectControllerSystem.queueFullUpdate(entityRef, effectControllerComponent, visibleComponent.newlyVisibleTo);
            }
            if (effectControllerComponent.consumeNetworkOutdated()) {
                EffectControllerSystem.queueUpdatesFor(entityRef, effectControllerComponent, visibleComponent.visibleTo, visibleComponent.newlyVisibleTo);
            }
        }

        private static void queueFullUpdate(@Nonnull Ref<EntityStore> ref, @Nonnull EffectControllerComponent effectControllerComponent, @Nonnull Map<Ref<EntityStore>, EntityViewer> visibleTo) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.EntityEffects;
            update.entityEffectUpdates = effectControllerComponent.createInitUpdates();
            for (EntityViewer viewer : visibleTo.values()) {
                viewer.queueUpdate(ref, update);
            }
        }

        private static void queueUpdatesFor(@Nonnull Ref<EntityStore> ref, @Nonnull EffectControllerComponent effectControllerComponent, @Nonnull Map<Ref<EntityStore>, EntityViewer> visibleTo, @Nonnull Map<Ref<EntityStore>, EntityViewer> exclude) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.EntityEffects;
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
        private final ComponentType<EntityStore, Visible> componentType;

        public RemoveVisibleComponent(ComponentType<EntityStore, Visible> componentType) {
            this.componentType = componentType;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.componentType;
        }

        @Override
        public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
            holder.removeComponent(this.componentType);
        }
    }

    public static class RemoveEmptyVisibleComponent
    extends EntityTickingSystem<EntityStore> {
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, AddToVisible.class), new SystemGroupDependency<EntityStore>(Order.BEFORE, QUEUE_UPDATE_GROUP));
        private final ComponentType<EntityStore, Visible> componentType;

        public RemoveEmptyVisibleComponent(ComponentType<EntityStore, Visible> componentType) {
            this.componentType = componentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.componentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            if (archetypeChunk.getComponent((int)index, this.componentType).visibleTo.isEmpty()) {
                commandBuffer.removeComponent(archetypeChunk.getReferenceTo(index), this.componentType);
            }
        }
    }

    public static class AddToVisible
    extends EntityTickingSystem<EntityStore> {
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Collections.singleton(new SystemDependency(Order.AFTER, EnsureVisibleComponent.class));
        private final ComponentType<EntityStore, EntityViewer> entityViewerComponentType;
        private final ComponentType<EntityStore, Visible> visibleComponentType;

        public AddToVisible(ComponentType<EntityStore, EntityViewer> entityViewerComponentType, ComponentType<EntityStore, Visible> visibleComponentType) {
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
            Ref<EntityStore> viewerRef = archetypeChunk.getReferenceTo(index);
            EntityViewer viewer = archetypeChunk.getComponent(index, this.entityViewerComponentType);
            for (Ref<EntityStore> ref : viewer.visible) {
                commandBuffer.getComponent(ref, this.visibleComponentType).addViewerParallel(viewerRef, viewer);
            }
        }
    }

    public static class EnsureVisibleComponent
    extends EntityTickingSystem<EntityStore> {
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Collections.singleton(new SystemDependency(Order.AFTER, ClearPreviouslyVisible.class));
        private final ComponentType<EntityStore, EntityViewer> entityViewerComponentType;
        private final ComponentType<EntityStore, Visible> visibleComponentType;

        public EnsureVisibleComponent(ComponentType<EntityStore, EntityViewer> entityViewerComponentType, ComponentType<EntityStore, Visible> visibleComponentType) {
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
            for (Ref<EntityStore> ref : archetypeChunk.getComponent((int)index, this.entityViewerComponentType).visible) {
                if (commandBuffer.getArchetype(ref).contains(this.visibleComponentType)) continue;
                commandBuffer.ensureComponent(ref, this.visibleComponentType);
            }
        }
    }

    public static class ClearPreviouslyVisible
    extends EntityTickingSystem<EntityStore> {
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(new SystemDependency(Order.AFTER, ClearEntityViewers.class), new SystemGroupDependency<EntityStore>(Order.AFTER, FIND_VISIBLE_ENTITIES_GROUP));
        private final ComponentType<EntityStore, Visible> componentType;

        public ClearPreviouslyVisible(ComponentType<EntityStore, Visible> componentType) {
            this.componentType = componentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.componentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            Visible visible = archetypeChunk.getComponent(index, this.componentType);
            Map<Ref<EntityStore>, EntityViewer> oldVisibleTo = visible.previousVisibleTo;
            visible.previousVisibleTo = visible.visibleTo;
            visible.visibleTo = oldVisibleTo;
            visible.visibleTo.clear();
            visible.newlyVisibleTo.clear();
        }
    }

    public static class CollectVisible
    extends EntityTickingSystem<EntityStore> {
        private final ComponentType<EntityStore, EntityViewer> componentType;
        private final Query<EntityStore> query;
        @Nonnull
        private final Set<Dependency<EntityStore>> dependencies;

        public CollectVisible(ComponentType<EntityStore, EntityViewer> componentType) {
            this.componentType = componentType;
            this.query = Archetype.of(componentType, TransformComponent.getComponentType());
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
            TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
            Vector3d position = transform.getPosition();
            EntityViewer entityViewer = archetypeChunk.getComponent(index, this.componentType);
            SpatialStructure<Ref<EntityStore>> spatialStructure = store.getResource(EntityModule.get().getNetworkSendableSpatialResourceType()).getSpatialStructure();
            ObjectList results = SpatialResource.getThreadLocalReferenceList();
            spatialStructure.collect(position, entityViewer.viewRadiusBlocks, results);
            entityViewer.visible.addAll(results);
        }
    }

    public static class ClearEntityViewers
    extends EntityTickingSystem<EntityStore> {
        public static final Set<Dependency<EntityStore>> DEPENDENCIES = Collections.singleton(new SystemGroupDependency<EntityStore>(Order.BEFORE, FIND_VISIBLE_ENTITIES_GROUP));
        private final ComponentType<EntityStore, EntityViewer> componentType;

        public ClearEntityViewers(ComponentType<EntityStore, EntityViewer> componentType) {
            this.componentType = componentType;
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return DEPENDENCIES;
        }

        @Override
        public Query<EntityStore> getQuery() {
            return this.componentType;
        }

        @Override
        public boolean isParallel(int archetypeChunkSize, int taskCount) {
            return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            EntityViewer viewer = archetypeChunk.getComponent(index, this.componentType);
            viewer.visible.clear();
            viewer.lodExcludedCount = 0;
            viewer.hiddenCount = 0;
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

