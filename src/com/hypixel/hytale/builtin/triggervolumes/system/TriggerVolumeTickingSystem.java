package com.hypixel.hytale.builtin.triggervolumes.system;

import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerCondition;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.event.TriggerVolumeEvent;
import com.hypixel.hytale.builtin.triggervolumes.manager.ConditionTiming;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
import com.hypixel.hytale.builtin.triggervolumes.manager.RejectionDelayMode;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.spatial.SpatialData;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.system.PlayerSpatialSystem;
import com.hypixel.hytale.server.core.modules.projectile.component.Projectile;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class TriggerVolumeTickingSystem extends TickingSystem<EntityStore> {
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final double DEFAULT_ENTITY_HEIGHT = 1.8;
   private static final int MAX_PENDING_EVENTS_PER_TICK = 64;
   private static final int[] SINGLE_DEFAULT_ENTRY = new int[]{0};
   @Nonnull
   private static final ThreadLocal<Set<UUID>> THREAD_LOCAL_PREVIOUS = ThreadLocal.withInitial(HashSet::new);
   @Nonnull
   private static final ThreadLocal<Map<UUID, Ref<EntityStore>>> THREAD_LOCAL_PREVIOUS_REFS = ThreadLocal.withInitial(HashMap::new);
   @Nonnull
   private static final ThreadLocal<Vector3d> THREAD_LOCAL_TEST_POINT = ThreadLocal.withInitial(Vector3d::new);
   @Nonnull
   private static final ThreadLocal<List<VolumeEntry>> THREAD_LOCAL_CANDIDATES = ThreadLocal.withInitial(ArrayList::new);
   @Nonnull
   private static final ThreadLocal<List<Ref<EntityStore>>> THREAD_LOCAL_ENTITY_REFS = ThreadLocal.withInitial(ArrayList::new);
   @Nonnull
   private final ResourceType<EntityStore, TriggerVolumeManager> managerResourceType;
   @Nonnull
   private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialResourceType;
   @Nonnull
   private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> entitySpatialResourceType;
   @Nonnull
   private final Set<Dependency<EntityStore>> dependencies;
   @Nonnull
   private final TriggerVolumeTickingSystem.EventDispatcher eventDispatcher;

   public TriggerVolumeTickingSystem(
      @Nonnull ResourceType<EntityStore, TriggerVolumeManager> managerResourceType,
      @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialResourceType,
      @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> entitySpatialResourceType
   ) {
      this(managerResourceType, playerSpatialResourceType, entitySpatialResourceType, TriggerVolumeTickingSystem::dispatchToServerEventBus);
   }

   TriggerVolumeTickingSystem(
      @Nonnull ResourceType<EntityStore, TriggerVolumeManager> managerResourceType,
      @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialResourceType,
      @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> entitySpatialResourceType,
      @Nonnull TriggerVolumeTickingSystem.EventDispatcher eventDispatcher
   ) {
      this.managerResourceType = managerResourceType;
      this.playerSpatialResourceType = playerSpatialResourceType;
      this.entitySpatialResourceType = entitySpatialResourceType;
      this.eventDispatcher = eventDispatcher;
      this.dependencies = Set.of(new SystemDependency<>(Order.AFTER, PlayerSpatialSystem.class, OrderPriority.CLOSEST));
   }

   @Nonnull
   @Override
   public Set<Dependency<EntityStore>> getDependencies() {
      return this.dependencies;
   }

   @Override
   public void tick(float deltaSeconds, int systemIndex, @Nonnull Store<EntityStore> store) {
      TriggerVolumeManager manager = store.getResource(this.managerResourceType);
      if (manager != null) {
         VolumeSpatialIndex spatialIndex = manager.getSpatialIndex();
         spatialIndex.rebuildIfDirty(manager.getVolumes());
         Set<VolumeEntry> toTick = Collections.newSetFromMap(new IdentityHashMap<>());
         List<VolumeEntry> candidates = THREAD_LOCAL_CANDIDATES.get();
         this.collectCandidatesFromEntityPositions(store, spatialIndex, toTick, candidates);
         World world = manager.getWorld();

         for (VolumeEntry entry : manager.getVolumes()) {
            if (!entry.isEnabled()) {
               if (!entry.getTrackedEntities().isEmpty()) {
                  this.processTrackedEntityExits(entry, store, manager);
               }
            } else {
               boolean chunkLoaded = entry.isKeepLoaded() || world == null || isChunkLoaded(world, entry.getPosition());
               if (!chunkLoaded) {
                  toTick.remove(entry);
                  if (!entry.getTrackedEntities().isEmpty()) {
                     this.processTrackedEntityExits(entry, store, manager);
                  }
               } else if (!entry.getTrackedEntities().isEmpty()) {
                  toTick.add(entry);
               }
            }
         }

         for (VolumeEntry entry : toTick) {
            try {
               this.tickVolume(entry, deltaSeconds, store, manager);
            } catch (Exception exception) {
               LOGGER.at(Level.WARNING).withCause(exception).log("Error ticking trigger volume '%s'", entry.getId());
            }
         }

         DelayedEffectScheduler delayedEffectScheduler = manager.getDelayedEffectScheduler();
         if (!delayedEffectScheduler.isEmpty()) {
            delayedEffectScheduler.tick(System.nanoTime(), store);
         }

         this.processPendingEvents(manager, store);
         this.processPendingDestroys(manager);
      }
   }

   private void processPendingEvents(@Nonnull TriggerVolumeManager manager, @Nonnull Store<EntityStore> store) {
      int processed = 0;

      TriggerVolumeManager.PendingTriggerEvent event;
      while ((event = manager.pollPendingEvent()) != null) {
         if (++processed > 64) {
            LOGGER.at(Level.WARNING).log("Stopped trigger volume event cascade after %d events", (int)(processed - 1));
            break;
         }

         this.processPendingEvent(event, manager, store, System.nanoTime());
      }
   }

   private void processPendingEvent(
      @Nonnull TriggerVolumeManager.PendingTriggerEvent event, @Nonnull TriggerVolumeManager manager, @Nonnull Store<EntityStore> store, long nowNanos
   ) {
      if (event.actorRef().isValid()) {
         if (event.volumeId() != null) {
            VolumeEntry entry = manager.getVolume(event.volumeId());
            if (entry != null && entry.isEnabled()) {
               this.firePendingEvent(entry, event, manager, store, nowNanos);
            }
         } else if (event.blockPosition() != null) {
            VolumeSpatialIndex spatialIndex = manager.getSpatialIndex();
            spatialIndex.rebuildIfDirty(manager.getVolumes());
            List<VolumeEntry> candidates = THREAD_LOCAL_CANDIDATES.get();
            candidates.clear();
            spatialIndex.collectCandidates(event.blockPosition(), candidates);

            for (VolumeEntry entry : candidates) {
               if (entry.isEnabled() && entry.getShape().contains(entry.getPosition(), event.blockPosition())) {
                  this.firePendingEvent(entry, event, manager, store, nowNanos);
               }
            }
         }
      }
   }

   private void firePendingEvent(
      @Nonnull VolumeEntry entry,
      @Nonnull TriggerVolumeManager.PendingTriggerEvent event,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull Store<EntityStore> store,
      long nowNanos
   ) {
      this.firePendingVolumeEvent(entry, event, store, nowNanos);
      this.firePendingGroupEvent(entry, event, manager, store, nowNanos);
   }

   private void firePendingVolumeEvent(
      @Nonnull VolumeEntry entry, @Nonnull TriggerVolumeManager.PendingTriggerEvent event, @Nonnull Store<EntityStore> store, long nowNanos
   ) {
      if (!entry.isOnCooldown(event.actorUuid(), nowNanos)) {
         for (int eventEntry : collectEntries(entry.getConditions(), entry.getEffects(), entry.getRejectionEffects())) {
            if (hasMatchingEventEffect(event.eventType(), entry.getEffects(), eventEntry)
               || hasMatchingEventEffect(event.eventType(), entry.getRejectionEffects(), eventEntry)
               || hasMatchingEventCondition(event.eventType(), entry.getConditions(), eventEntry)) {
               this.fireGatedEffects(
                  event.eventType(),
                  event.actorRef(),
                  entry,
                  store,
                  nowNanos,
                  event.actorUuid(),
                  eventEntry,
                  null,
                  entry.getConditions(),
                  entry.getEffects(),
                  entry.getRejectionEffects(),
                  entry.getConditionTiming() == ConditionTiming.BEFORE_VOLUME_DELAY ? entry.getActivationDelay() : 0.0F,
                  rejectionDelay(entry.getRejectionDelayMode(), entry.getActivationDelay()),
                  VolumeEntry.EffectBucket.VOLUME,
                  VolumeEntry.EffectBucket.VOLUME_REJECTION,
                  "volume '" + entry.getId() + "'",
                  event
               );
            }
         }
      }
   }

   private void firePendingGroupEvent(
      @Nonnull VolumeEntry entry,
      @Nonnull TriggerVolumeManager.PendingTriggerEvent event,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull Store<EntityStore> store,
      long nowNanos
   ) {
      if (entry.getGroupId() != null) {
         GroupEntry group = manager.getGroup(entry.getGroupId());
         if (group != null && group.isEnabled()) {
            if (!entry.isOnCooldown(event.actorUuid(), nowNanos)) {
               List<VolumeEntry> spatialVolumes = getGroupSpatialVolumes(entry, manager, group);

               for (int eventEntry : collectEntries(group.getConditions(), group.getEffects(), group.getRejectionEffects())) {
                  if (hasMatchingEventEffect(event.eventType(), group.getEffects(), eventEntry)
                     || hasMatchingEventEffect(event.eventType(), group.getRejectionEffects(), eventEntry)
                     || hasMatchingEventCondition(event.eventType(), group.getConditions(), eventEntry)) {
                     this.fireGatedEffects(
                        event.eventType(),
                        event.actorRef(),
                        entry,
                        store,
                        nowNanos,
                        event.actorUuid(),
                        eventEntry,
                        spatialVolumes,
                        group.getConditions(),
                        group.getEffects(),
                        group.getRejectionEffects(),
                        group.getConditionTiming() == ConditionTiming.BEFORE_VOLUME_DELAY ? entry.getActivationDelay() : 0.0F,
                        rejectionDelay(group.getRejectionDelayMode(), entry.getActivationDelay()),
                        VolumeEntry.EffectBucket.GROUP,
                        VolumeEntry.EffectBucket.GROUP_REJECTION,
                        "group '" + group.getId() + "' via volume '" + entry.getId() + "'",
                        event
                     );
                  }
               }
            }
         }
      }
   }

   private void processPendingDestroys(@Nonnull TriggerVolumeManager manager) {
      for (VolumeEntry entry : manager.getVolumes()) {
         if (entry.isPendingDestroy()) {
            String volumeId = entry.getId();
            manager.getDelayedEffectScheduler().cancelForVolume(entry);
            manager.unregister(volumeId);
            manager.notifyViewersRemove(volumeId);
         }
      }
   }

   private void collectCandidatesFromEntityPositions(
      @Nonnull Store<EntityStore> store, @Nonnull VolumeSpatialIndex spatialIndex, @Nonnull Set<VolumeEntry> toTick, @Nonnull List<VolumeEntry> candidates
   ) {
      SpatialResource<Ref<EntityStore>, EntityStore> playerSpatial = store.getResource(this.playerSpatialResourceType);
      if (playerSpatial != null) {
         SpatialData<Ref<EntityStore>> data = playerSpatial.getSpatialData();

         for (int i = 0; i < data.size(); i++) {
            candidates.clear();
            spatialIndex.collectCandidates(data.getVector(i), candidates);
            toTick.addAll(candidates);
         }
      }

      SpatialResource<Ref<EntityStore>, EntityStore> entitySpatial = store.getResource(this.entitySpatialResourceType);
      if (entitySpatial != null) {
         SpatialData<Ref<EntityStore>> data = entitySpatial.getSpatialData();

         for (int i = 0; i < data.size(); i++) {
            candidates.clear();
            spatialIndex.collectCandidates(data.getVector(i), candidates);
            toTick.addAll(candidates);
         }
      }
   }

   private void tickVolume(@Nonnull VolumeEntry entry, float deltaSeconds, @Nonnull Store<EntityStore> store, @Nonnull TriggerVolumeManager manager) {
      TriggerVolumeShape shape = entry.getShape();
      Vector3d origin = entry.getPosition();
      Set<UUID> previousUuids = THREAD_LOCAL_PREVIOUS.get();
      Map<UUID, Ref<EntityStore>> previousRefs = THREAD_LOCAL_PREVIOUS_REFS.get();
      previousUuids.clear();
      previousRefs.clear();
      previousUuids.addAll(entry.getTrackedEntities().keySet());
      previousRefs.putAll(entry.getTrackedEntities());
      entry.getTrackedEntities().clear();
      List<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
      if (entry.getTargetTypes().contains(EntityTargetType.PLAYER)) {
         SpatialResource<Ref<EntityStore>, EntityStore> playerSpatial = store.getResource(this.playerSpatialResourceType);
         if (playerSpatial != null) {
            playerSpatial.getSpatialStructure().collect(origin, shape.getMaxDistanceFromOrigin(), results);
         }
      }

      if (usesEntitySpatial(entry.getTargetTypes())) {
         SpatialResource<Ref<EntityStore>, EntityStore> entitySpatial = store.getResource(this.entitySpatialResourceType);
         if (entitySpatial != null) {
            entitySpatial.getSpatialStructure().collect(origin, shape.getMaxDistanceFromOrigin(), results);
         }
      }

      List<Ref<EntityStore>> entitiesToProcess = THREAD_LOCAL_ENTITY_REFS.get();
      entitiesToProcess.clear();
      entitiesToProcess.addAll(results);
      long nowNanos = System.nanoTime();
      int[] volumeEntries = collectEntries(entry.getConditions(), entry.getEffects(), entry.getRejectionEffects());
      int[] groupEntries = SINGLE_DEFAULT_ENTRY;
      if (entry.getGroupId() != null) {
         GroupEntry group = manager.getGroup(entry.getGroupId());
         if (group != null) {
            groupEntries = collectEntries(group.getConditions(), group.getEffects(), group.getRejectionEffects());
         }
      }

      for (int i = 0; i < entitiesToProcess.size(); i++) {
         Ref<EntityStore> entityRef = entitiesToProcess.get(i);
         if (entityRef.isValid() && matchesTargetTypes(entry.getTargetTypes(), entityRef, store)) {
            TransformComponent transform = store.getComponent(entityRef, TransformComponent.getComponentType());
            if (transform != null && containsEntity(shape, origin, transform, store.getComponent(entityRef, BoundingBox.getComponentType()))) {
               UUIDComponent uuidComponent = store.getComponent(entityRef, UUIDComponent.getComponentType());
               if (uuidComponent != null) {
                  UUID uuid = uuidComponent.getUuid();
                  entry.getTrackedEntities().put(uuid, entityRef);
                  boolean wasInside = previousUuids.remove(uuid);
                  if (!wasInside) {
                     if (entry.isOnCooldown(uuid, nowNanos)) {
                        continue;
                     }

                     this.clearIntervalTimers(entry, uuid);

                     for (int volumeEntry : volumeEntries) {
                        if (!hasTickActivationGate(entry.getConditions(), volumeEntry)) {
                           this.activateVolumeEntry(entityRef, entry, store, nowNanos, uuid, volumeEntry);
                        }
                     }

                     for (int groupEntry : groupEntries) {
                        if (!hasGroupTickActivationGate(entry, manager, groupEntry)) {
                           this.activateGroupEntry(entityRef, entry, manager, store, nowNanos, uuid, groupEntry);
                        }
                     }
                  }

                  for (int volumeEntry : volumeEntries) {
                     if (entry.isVolumeActivated(volumeEntry, uuid)) {
                        this.fireEffects(TriggerEventType.TICK, entityRef, entry, store, nowNanos, uuid, volumeEntry);
                     } else if (hasTickActivationGate(entry.getConditions(), volumeEntry)) {
                        this.processVolumeTickActivationGate(entityRef, entry, store, nowNanos, uuid, volumeEntry);
                     }
                  }

                  if (entry.getGroupId() != null) {
                     for (int groupEntry : groupEntries) {
                        if (entry.isGroupActivated(entry.getGroupId(), groupEntry, uuid)) {
                           this.fireGroupEffects(TriggerEventType.TICK, entityRef, entry, manager, store, nowNanos, uuid, groupEntry);
                        } else {
                           this.processGroupTickActivationGate(entityRef, entry, manager, store, nowNanos, uuid, groupEntry);
                        }
                     }
                  }
               }
            }
         }
      }

      for (UUID exitedUuid : previousUuids) {
         this.fireVolumeExit(entry, previousRefs.get(exitedUuid), exitedUuid, manager, store, nowNanos, false);
      }
   }

   void fireVolumeExit(
      @Nonnull VolumeEntry entry,
      @Nullable Ref<EntityStore> exitedRef,
      @Nonnull UUID exitedUuid,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      boolean fromRemoval
   ) {
      if (exitedRef != null && exitedRef.isValid()) {
         this.dispatchEvent(TriggerEventType.EXIT, entry, exitedRef, exitedUuid);

         for (int volumeEntry : collectEntries(entry.getConditions(), entry.getEffects(), entry.getRejectionEffects())) {
            this.fireEffects(TriggerEventType.EXIT, exitedRef, entry, store, nowNanos, exitedUuid, volumeEntry);
         }

         if (entry.getGroupId() != null) {
            GroupEntry group = manager.getGroup(entry.getGroupId());
            if (group != null) {
               for (int groupEntry : collectEntries(group.getConditions(), group.getEffects(), group.getRejectionEffects())) {
                  this.fireGroupEffects(TriggerEventType.EXIT, exitedRef, entry, manager, store, nowNanos, exitedUuid, groupEntry);
               }
            }
         }
      }

      notifyVolumeEntityExit(entry, exitedUuid);
      this.fireGroupOnEntityExit(entry, manager, exitedUuid);
      if (!fromRemoval && entry.isCancelDelayedEffectsOnExit()) {
         manager.getDelayedEffectScheduler().cancelNonExitForEntityInVolume(exitedUuid, entry);
      }

      entry.clearEntityRuntimeState(exitedUuid);
   }

   private boolean activateVolumeEntry(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry
   ) {
      TriggerVolumeTickingSystem.ActivationResult result = this.fireEffects(TriggerEventType.ENTER, entityRef, entry, store, nowNanos, entityUuid, eventEntry);
      if (result != TriggerVolumeTickingSystem.ActivationResult.ACCEPTED) {
         return false;
      }

      this.completeVolumeActivation(entityRef, entry, nowNanos, entityUuid, eventEntry);
      return true;
   }

   private void completeVolumeActivation(
      @Nonnull Ref<EntityStore> entityRef, @Nonnull VolumeEntry entry, long nowNanos, @Nonnull UUID entityUuid, int eventEntry
   ) {
      if (entry.getTrackedEntities().containsKey(entityUuid)) {
         entry.markVolumeActivated(eventEntry, entityUuid);
         entry.recordActivation(entityUuid, nowNanos);
         this.dispatchEvent(TriggerEventType.ENTER, entry, entityRef, entityUuid);
      }
   }

   private void processVolumeTickActivationGate(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry
   ) {
      if (entry.isOnCooldown(entityUuid, nowNanos)) {
         entry.markVolumeActivated(eventEntry, entityUuid);
      } else {
         TriggerContext context = new TriggerContext(entityRef, store, TriggerEventType.TICK, entry);
         if (this.conditionsPass(entry.getConditions(), TriggerEventType.TICK, eventEntry, context, "volume '" + entry.getId() + "'")) {
            this.activateVolumeEntry(entityRef, entry, store, nowNanos, entityUuid, eventEntry);
         } else if (entry.markVolumeTickRejectionFired(eventEntry, entityUuid)) {
            this.fireEffectList(
               TriggerEventType.TICK,
               entityRef,
               entry,
               store,
               nowNanos,
               entityUuid,
               null,
               entry.getRejectionEffects(),
               eventEntry,
               0.0F,
               VolumeEntry.EffectBucket.VOLUME_REJECTION,
               "volume '" + entry.getId() + "'"
            );
         }
      }
   }

   private boolean activateGroupEntry(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry
   ) {
      String groupId = entry.getGroupId();
      if (groupId == null) {
         return false;
      }

      TriggerVolumeTickingSystem.ActivationResult result = this.fireGroupEffects(
         TriggerEventType.ENTER, entityRef, entry, manager, store, nowNanos, entityUuid, eventEntry
      );
      if (result != TriggerVolumeTickingSystem.ActivationResult.ACCEPTED) {
         return false;
      }

      this.completeGroupActivation(entry, groupId, entityUuid, eventEntry);
      return true;
   }

   private void completeGroupActivation(@Nonnull VolumeEntry entry, @Nonnull String groupId, @Nonnull UUID entityUuid, int eventEntry) {
      if (entry.getTrackedEntities().containsKey(entityUuid)) {
         entry.markGroupActivated(groupId, eventEntry, entityUuid);
      }
   }

   private void processGroupTickActivationGate(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry
   ) {
      String groupId = entry.getGroupId();
      if (groupId != null) {
         GroupEntry group = manager.getGroup(groupId);
         if (group != null && group.isEnabled() && hasTickActivationGate(group.getConditions(), eventEntry)) {
            if (entry.isOnCooldown(entityUuid, nowNanos)) {
               entry.markGroupActivated(groupId, eventEntry, entityUuid);
            } else {
               List<VolumeEntry> spatialVolumes = getGroupSpatialVolumes(entry, manager, group);
               if (!spatialVolumes.isEmpty()) {
                  TriggerContext context = new TriggerContext(entityRef, store, TriggerEventType.TICK, entry, spatialVolumes);
                  String sourceLabel = "group '" + group.getId() + "' via volume '" + entry.getId() + "'";
                  if (this.conditionsPass(group.getConditions(), TriggerEventType.TICK, eventEntry, context, sourceLabel)) {
                     this.activateGroupEntry(entityRef, entry, manager, store, nowNanos, entityUuid, eventEntry);
                  } else if (entry.markGroupTickRejectionFired(groupId, eventEntry, entityUuid)) {
                     this.fireEffectList(
                        TriggerEventType.TICK,
                        entityRef,
                        entry,
                        store,
                        nowNanos,
                        entityUuid,
                        spatialVolumes,
                        group.getRejectionEffects(),
                        eventEntry,
                        0.0F,
                        VolumeEntry.EffectBucket.GROUP_REJECTION,
                        sourceLabel
                     );
                  }
               }
            }
         }
      }
   }

   @Nonnull
   private static List<VolumeEntry> getGroupSpatialVolumes(@Nonnull VolumeEntry entry, @Nonnull TriggerVolumeManager manager, @Nonnull GroupEntry group) {
      ArrayList<VolumeEntry> spatialVolumes = new ArrayList<>();

      for (String memberId : group.getMemberVolumeIds()) {
         VolumeEntry member = manager.getVolume(memberId);
         if (member != null) {
            spatialVolumes.add(member);
         }
      }

      if (spatialVolumes.isEmpty()) {
         spatialVolumes.add(entry);
      }

      return spatialVolumes;
   }

   private TriggerVolumeTickingSystem.ActivationResult fireEffects(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry
   ) {
      if (!hasMatchingEventEffect(eventType, entry.getEffects(), eventEntry) && !hasMatchingEventEffect(eventType, entry.getRejectionEffects(), eventEntry)) {
         if (!hasMatchingEventCondition(eventType, entry.getConditions(), eventEntry)) {
            return TriggerVolumeTickingSystem.ActivationResult.ACCEPTED;
         }

         if (entry.getConditionTiming() == ConditionTiming.AFTER_VOLUME_DELAY && entry.getActivationDelay() > 0.0F) {
            return this.scheduleDelayedVolumeGate(eventType, entityRef, entry, store, nowNanos, entityUuid, eventEntry);
         }

         TriggerContext context = new TriggerContext(entityRef, store, eventType, entry);
         return this.conditionsPass(entry.getConditions(), eventType, eventEntry, context, "volume '" + entry.getId() + "'")
            ? TriggerVolumeTickingSystem.ActivationResult.ACCEPTED
            : TriggerVolumeTickingSystem.ActivationResult.REJECTED;
      } else {
         return entry.getConditionTiming() == ConditionTiming.AFTER_VOLUME_DELAY && entry.getActivationDelay() > 0.0F
            ? this.scheduleDelayedVolumeGate(eventType, entityRef, entry, store, nowNanos, entityUuid, eventEntry)
            : this.fireGatedEffects(
               eventType,
               entityRef,
               entry,
               store,
               nowNanos,
               entityUuid,
               eventEntry,
               null,
               entry.getConditions(),
               entry.getEffects(),
               entry.getRejectionEffects(),
               entry.getConditionTiming() == ConditionTiming.BEFORE_VOLUME_DELAY ? entry.getActivationDelay() : 0.0F,
               rejectionDelay(entry.getRejectionDelayMode(), entry.getActivationDelay()),
               VolumeEntry.EffectBucket.VOLUME,
               VolumeEntry.EffectBucket.VOLUME_REJECTION,
               "volume '" + entry.getId() + "'"
            );
      }
   }

   @Nonnull
   private TriggerVolumeTickingSystem.ActivationResult scheduleDelayedVolumeGate(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry
   ) {
      if (eventType == TriggerEventType.ENTER && !entry.markVolumeActivationPending(eventEntry, entityUuid)) {
         return TriggerVolumeTickingSystem.ActivationResult.PENDING;
      }

      this.getScheduler(store)
         .scheduleGate(
            (delayedEntityRef, delayedEntityUuid, delayedEventType, delayedVolume, delayedVolumes, delayedStore, delayedNow) -> this.fireDelayedVolumeEffects(
               delayedEntityRef, delayedEntityUuid, delayedEventType, delayedVolume, delayedVolumes, delayedStore, delayedNow, eventEntry
            ),
            entityRef,
            entityUuid,
            eventType,
            entry,
            nowNanos,
            entry.getActivationDelay(),
            null
         );
      return TriggerVolumeTickingSystem.ActivationResult.PENDING;
   }

   private void fireDelayedVolumeEffects(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull UUID entityUuid,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry entry,
      @Nullable List<VolumeEntry> spatialVolumes,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      int eventEntry
   ) {
      try {
         TriggerVolumeTickingSystem.ActivationResult result = this.fireGatedEffects(
            eventType,
            entityRef,
            entry,
            store,
            nowNanos,
            entityUuid,
            eventEntry,
            null,
            entry.getConditions(),
            entry.getEffects(),
            entry.getRejectionEffects(),
            0.0F,
            rejectionDelay(entry.getRejectionDelayMode(), entry.getActivationDelay()),
            VolumeEntry.EffectBucket.VOLUME,
            VolumeEntry.EffectBucket.VOLUME_REJECTION,
            "volume '" + entry.getId() + "'"
         );
         if (eventType == TriggerEventType.ENTER && result == TriggerVolumeTickingSystem.ActivationResult.ACCEPTED) {
            this.completeVolumeActivation(entityRef, entry, nowNanos, entityUuid, eventEntry);
         }
      } finally {
         if (eventType == TriggerEventType.ENTER) {
            entry.clearVolumeActivationPending(eventEntry, entityUuid);
         }
      }
   }

   private TriggerVolumeTickingSystem.ActivationResult fireGatedEffects(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry,
      @Nullable List<VolumeEntry> spatialVolumes,
      @Nonnull List<TriggerCondition> conditions,
      @Nonnull List<TriggerEffect> effects,
      @Nonnull List<TriggerEffect> rejectionEffects,
      float successDelay,
      float rejectionDelay,
      @Nonnull VolumeEntry.EffectBucket effectsBucket,
      @Nonnull VolumeEntry.EffectBucket rejectionBucket,
      @Nonnull String sourceLabel
   ) {
      return this.fireGatedEffects(
         eventType,
         entityRef,
         entry,
         store,
         nowNanos,
         entityUuid,
         eventEntry,
         spatialVolumes,
         conditions,
         effects,
         rejectionEffects,
         successDelay,
         rejectionDelay,
         effectsBucket,
         rejectionBucket,
         sourceLabel,
         null
      );
   }

   private TriggerVolumeTickingSystem.ActivationResult fireGatedEffects(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry,
      @Nullable List<VolumeEntry> spatialVolumes,
      @Nonnull List<TriggerCondition> conditions,
      @Nonnull List<TriggerEffect> effects,
      @Nonnull List<TriggerEffect> rejectionEffects,
      float successDelay,
      float rejectionDelay,
      @Nonnull VolumeEntry.EffectBucket effectsBucket,
      @Nonnull VolumeEntry.EffectBucket rejectionBucket,
      @Nonnull String sourceLabel,
      @Nullable TriggerVolumeManager.PendingTriggerEvent eventData
   ) {
      TriggerContext context = spatialVolumes != null
         ? createContext(entityRef, store, eventType, entry, spatialVolumes, eventData)
         : createContext(entityRef, store, eventType, entry, List.of(entry), eventData);
      boolean accepted = this.conditionsPass(conditions, eventType, eventEntry, context, sourceLabel);
      List<TriggerEffect> effectsToFire = accepted ? effects : rejectionEffects;
      VolumeEntry.EffectBucket bucket = accepted ? effectsBucket : rejectionBucket;
      float activationDelay = accepted ? successDelay : rejectionDelay;
      this.fireEffectList(
         eventType, entityRef, entry, store, nowNanos, entityUuid, spatialVolumes, effectsToFire, eventEntry, activationDelay, bucket, sourceLabel
      );
      return accepted ? TriggerVolumeTickingSystem.ActivationResult.ACCEPTED : TriggerVolumeTickingSystem.ActivationResult.REJECTED;
   }

   @Nonnull
   private static TriggerContext createContext(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull Store<EntityStore> store,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry entry,
      @Nonnull List<VolumeEntry> spatialVolumes,
      @Nullable TriggerVolumeManager.PendingTriggerEvent eventData
   ) {
      return eventData == null
         ? new TriggerContext(entityRef, store, eventType, entry, spatialVolumes)
         : new TriggerContext(
            entityRef,
            store,
            eventType,
            entry,
            spatialVolumes,
            null,
            eventData.tagKey(),
            eventData.tagValue(),
            eventData.blockPosition(),
            eventData.blockId(),
            eventData.interactionType()
         );
   }

   private boolean conditionsPass(
      @Nonnull List<TriggerCondition> conditions,
      @Nonnull TriggerEventType eventType,
      int eventEntry,
      @Nonnull TriggerContext context,
      @Nonnull String sourceLabel
   ) {
      ArrayList<TriggerCondition> acceptedConditions = new ArrayList<>();

      for (TriggerCondition condition : conditions) {
         if (condition.getEntry() == eventEntry && condition.getEventType() == eventType) {
            try {
               if (!condition.test(context)) {
                  return false;
               }

               acceptedConditions.add(condition);
            } catch (Exception exception) {
               LOGGER.at(Level.WARNING).withCause(exception).log("Error evaluating condition %s on %s", condition.getClass().getSimpleName(), sourceLabel);
               return false;
            }
         }
      }

      for (TriggerCondition condition : acceptedConditions) {
         try {
            condition.applyOnAccept(context);
         } catch (Exception exception) {
            LOGGER.at(Level.WARNING).withCause(exception).log("Error applying accepted condition %s on %s", condition.getClass().getSimpleName(), sourceLabel);
            return false;
         }
      }

      return true;
   }

   static boolean hasTickActivationGate(@Nonnull List<TriggerCondition> conditions, int entry) {
      for (TriggerCondition condition : conditions) {
         if (condition.getEntry() == entry && condition.getEventType() == TriggerEventType.TICK) {
            return true;
         }
      }

      return false;
   }

   static boolean hasMatchingEventEffect(@Nonnull TriggerEventType eventType, @Nonnull List<TriggerEffect> effects, int entry) {
      for (TriggerEffect effect : effects) {
         if (effect.getEntry() == entry && effect.getEventType() == eventType) {
            return true;
         }
      }

      return false;
   }

   static boolean hasMatchingEventCondition(@Nonnull TriggerEventType eventType, @Nonnull List<TriggerCondition> conditions, int entry) {
      for (TriggerCondition condition : conditions) {
         if (condition.getEntry() == entry && condition.getEventType() == eventType) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasGroupTickActivationGate(@Nonnull VolumeEntry entry, @Nonnull TriggerVolumeManager manager, int groupEntry) {
      String groupId = entry.getGroupId();
      if (groupId == null) {
         return false;
      }

      GroupEntry group = manager.getGroup(groupId);
      return group != null && group.isEnabled() && hasTickActivationGate(group.getConditions(), groupEntry);
   }

   @Nonnull
   private static int[] collectEntries(
      @Nonnull List<TriggerCondition> conditions, @Nonnull List<TriggerEffect> effects, @Nonnull List<TriggerEffect> rejectionEffects
   ) {
      IntOpenHashSet entries = new IntOpenHashSet();

      for (TriggerCondition condition : conditions) {
         entries.add(condition.getEntry());
      }

      for (TriggerEffect effect : effects) {
         entries.add(effect.getEntry());
      }

      for (TriggerEffect effect : rejectionEffects) {
         entries.add(effect.getEntry());
      }

      if (entries.isEmpty()) {
         return SINGLE_DEFAULT_ENTRY;
      }

      int[] array = entries.toIntArray();
      Arrays.sort(array);
      return array;
   }

   private void fireEffectList(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      @Nullable List<VolumeEntry> spatialVolumes,
      @Nonnull List<TriggerEffect> effects,
      int eventEntry,
      float activationDelay,
      @Nonnull VolumeEntry.EffectBucket bucket,
      @Nonnull String sourceLabel
   ) {
      for (int i = 0; i < effects.size(); i++) {
         TriggerEffect effect = effects.get(i);
         if (effect.getEntry() == eventEntry && effect.getEventType() == eventType) {
            VolumeEntry.EffectEntityKey intervalKey = null;
            boolean hasFiredBefore = false;
            if (eventType == TriggerEventType.TICK && effect.getInterval() > 0.0F) {
               intervalKey = new VolumeEntry.EffectEntityKey(bucket, i, entityUuid);
               long lastFire = entry.getLastFireTimes().getOrDefault(intervalKey, Long.MIN_VALUE);
               hasFiredBefore = lastFire != Long.MIN_VALUE;
               if (hasFiredBefore) {
                  double elapsedSeconds = (nowNanos - lastFire) / 1.0E9;
                  if (elapsedSeconds < effect.getInterval()) {
                     continue;
                  }
               }
            }

            float totalDelay = activationDelay + effectDelay(eventType, effect, hasFiredBefore);
            if (totalDelay > 0.0F) {
               DelayedEffectScheduler scheduler = this.getScheduler(store);
               if (intervalKey != null) {
                  VolumeEntry.EffectEntityKey pendingIntervalKey = intervalKey;
                  if (entry.markDelayedEffectPending(pendingIntervalKey)) {
                     scheduler.schedule(effect, entityRef, entityUuid, eventType, entry, nowNanos, totalDelay, spatialVolumes, (executed, executedAtNanos) -> {
                        entry.clearDelayedEffectPending(pendingIntervalKey);
                        if (executed) {
                           entry.getLastFireTimes().put(pendingIntervalKey, executedAtNanos);
                        }
                     }, store);
                  }
               } else {
                  scheduler.schedule(effect, entityRef, entityUuid, eventType, entry, nowNanos, totalDelay, spatialVolumes, null, store);
               }
            } else {
               try {
                  Ref<EntityStore> actorRef = entry.getProjectileSource().resolveActorRef(entityRef, store);
                  TriggerContext context = spatialVolumes != null
                     ? new TriggerContext(actorRef, store, eventType, entry, spatialVolumes)
                     : new TriggerContext(actorRef, store, eventType, entry);
                  effect.execute(context);
                  if (intervalKey != null) {
                     entry.getLastFireTimes().put(intervalKey, nowNanos);
                  }
               } catch (Exception exception) {
                  LOGGER.at(Level.WARNING).withCause(exception).log("Error executing effect %s on %s", effect.getClass().getSimpleName(), sourceLabel);
               }
            }
         }
      }
   }

   static float effectDelay(@Nonnull TriggerEventType eventType, @Nonnull TriggerEffect effect, boolean hasFiredBefore) {
      return eventType == TriggerEventType.TICK && hasFiredBefore ? 0.0F : effect.getDelay();
   }

   private TriggerVolumeTickingSystem.ActivationResult fireGroupEffects(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry
   ) {
      if (entry.getGroupId() == null) {
         return TriggerVolumeTickingSystem.ActivationResult.REJECTED;
      }

      GroupEntry group = manager.getGroup(entry.getGroupId());
      if (group == null || !group.isEnabled()) {
         return TriggerVolumeTickingSystem.ActivationResult.REJECTED;
      }

      if (group.getConditions().isEmpty() && group.getEffects().isEmpty() && group.getRejectionEffects().isEmpty()) {
         return TriggerVolumeTickingSystem.ActivationResult.ACCEPTED;
      }

      if (hasMatchingEventEffect(eventType, group.getEffects(), eventEntry) || hasMatchingEventEffect(eventType, group.getRejectionEffects(), eventEntry)) {
         List<VolumeEntry> spatialVolumes = getGroupSpatialVolumes(entry, manager, group);
         if (spatialVolumes.isEmpty()) {
            return TriggerVolumeTickingSystem.ActivationResult.REJECTED;
         }

         if (!group.getTargetTypes().isEmpty()) {
            EntityTargetType required = resolveTargetType(entityRef, store);
            if (required == null) {
               return TriggerVolumeTickingSystem.ActivationResult.REJECTED;
            }

            if (!group.getTargetTypes().contains(required)) {
               return TriggerVolumeTickingSystem.ActivationResult.REJECTED;
            }
         }

         return group.getConditionTiming() == ConditionTiming.AFTER_VOLUME_DELAY && entry.getActivationDelay() > 0.0F
            ? this.scheduleDelayedGroupGate(eventType, entityRef, entry, group, store, nowNanos, entityUuid, eventEntry, spatialVolumes)
            : this.fireGroupEffectLists(
               eventType,
               entityRef,
               entry,
               group,
               store,
               nowNanos,
               entityUuid,
               eventEntry,
               spatialVolumes,
               group.getConditionTiming() == ConditionTiming.BEFORE_VOLUME_DELAY ? entry.getActivationDelay() : 0.0F,
               rejectionDelay(group.getRejectionDelayMode(), entry.getActivationDelay())
            );
      } else {
         if (!hasMatchingEventCondition(eventType, group.getConditions(), eventEntry)) {
            return TriggerVolumeTickingSystem.ActivationResult.ACCEPTED;
         }

         if (group.getConditionTiming() == ConditionTiming.AFTER_VOLUME_DELAY && entry.getActivationDelay() > 0.0F) {
            return this.scheduleDelayedGroupGate(
               eventType, entityRef, entry, group, store, nowNanos, entityUuid, eventEntry, getGroupSpatialVolumes(entry, manager, group)
            );
         }

         TriggerContext context = new TriggerContext(entityRef, store, eventType, entry, getGroupSpatialVolumes(entry, manager, group));
         return this.conditionsPass(group.getConditions(), eventType, eventEntry, context, "group '" + group.getId() + "' via volume '" + entry.getId() + "'")
            ? TriggerVolumeTickingSystem.ActivationResult.ACCEPTED
            : TriggerVolumeTickingSystem.ActivationResult.REJECTED;
      }
   }

   @Nonnull
   private TriggerVolumeTickingSystem.ActivationResult scheduleDelayedGroupGate(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull GroupEntry group,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry,
      @Nonnull List<VolumeEntry> spatialVolumes
   ) {
      if (eventType == TriggerEventType.ENTER && !entry.markGroupActivationPending(group.getId(), eventEntry, entityUuid)) {
         return TriggerVolumeTickingSystem.ActivationResult.PENDING;
      }

      this.getScheduler(store)
         .scheduleGate(
            (delayedEntityRef, delayedEntityUuid, delayedEventType, delayedVolume, delayedVolumes, delayedStore, delayedNow) -> this.fireDelayedGroupEffects(
               delayedEventType, delayedEntityRef, delayedVolume, group, delayedStore, delayedNow, delayedEntityUuid, eventEntry, delayedVolumes
            ),
            entityRef,
            entityUuid,
            eventType,
            entry,
            nowNanos,
            entry.getActivationDelay(),
            spatialVolumes
         );
      return TriggerVolumeTickingSystem.ActivationResult.PENDING;
   }

   private void fireDelayedGroupEffects(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull GroupEntry group,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry,
      @Nullable List<VolumeEntry> spatialVolumes
   ) {
      try {
         if (!group.isEnabled()) {
            return;
         }

         TriggerVolumeTickingSystem.ActivationResult result = this.fireGroupEffectLists(
            eventType,
            entityRef,
            entry,
            group,
            store,
            nowNanos,
            entityUuid,
            eventEntry,
            spatialVolumes,
            0.0F,
            rejectionDelay(group.getRejectionDelayMode(), entry.getActivationDelay())
         );
         if (eventType == TriggerEventType.ENTER && result == TriggerVolumeTickingSystem.ActivationResult.ACCEPTED) {
            this.completeGroupActivation(entry, group.getId(), entityUuid, eventEntry);
         }
      } finally {
         if (eventType == TriggerEventType.ENTER) {
            entry.clearGroupActivationPending(group.getId(), eventEntry, entityUuid);
         }
      }
   }

   private TriggerVolumeTickingSystem.ActivationResult fireGroupEffectLists(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull GroupEntry group,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid,
      int eventEntry,
      @Nullable List<VolumeEntry> spatialVolumes,
      float successDelay,
      float rejectionDelay
   ) {
      List<VolumeEntry> volumes = spatialVolumes != null ? spatialVolumes : List.of(entry);
      return this.fireGatedEffects(
         eventType,
         entityRef,
         entry,
         store,
         nowNanos,
         entityUuid,
         eventEntry,
         volumes,
         group.getConditions(),
         group.getEffects(),
         group.getRejectionEffects(),
         successDelay,
         rejectionDelay,
         VolumeEntry.EffectBucket.GROUP,
         VolumeEntry.EffectBucket.GROUP_REJECTION,
         "group '" + group.getId() + "' via volume '" + entry.getId() + "'"
      );
   }

   private static float rejectionDelay(@Nonnull RejectionDelayMode delayMode, float activationDelay) {
      return delayMode == RejectionDelayMode.USE_VOLUME_DELAY ? activationDelay : 0.0F;
   }

   @Nonnull
   private DelayedEffectScheduler getScheduler(@Nonnull Store<EntityStore> store) {
      TriggerVolumeManager manager = store.getResource(this.managerResourceType);
      if (manager == null) {
         throw new IllegalStateException("TriggerVolumeManager missing on store");
      } else {
         return manager.getDelayedEffectScheduler();
      }
   }

   private static boolean usesEntitySpatial(@Nonnull Set<EntityTargetType> targetTypes) {
      return targetTypes.contains(EntityTargetType.NPC)
         || targetTypes.contains(EntityTargetType.ITEM_DROP)
         || targetTypes.contains(EntityTargetType.PROJECTILE);
   }

   private static boolean matchesTargetTypes(@Nonnull Set<EntityTargetType> targetTypes, @Nonnull Ref<EntityStore> entityRef, @Nonnull Store<EntityStore> store) {
      EntityTargetType targetType = resolveTargetType(entityRef, store);
      return targetType != null && targetTypes.contains(targetType);
   }

   @Nullable
   private static EntityTargetType resolveTargetType(@Nonnull Ref<EntityStore> entityRef, @Nonnull Store<EntityStore> store) {
      if (store.getComponent(entityRef, PlayerRef.getComponentType()) != null) {
         return EntityTargetType.PLAYER;
      } else if (store.getComponent(entityRef, ItemComponent.getComponentType()) != null) {
         return EntityTargetType.ITEM_DROP;
      } else {
         return store.getComponent(entityRef, Projectile.getComponentType()) == null
               && store.getComponent(entityRef, ProjectileComponent.getComponentType()) == null
            ? EntityTargetType.NPC
            : EntityTargetType.PROJECTILE;
      }
   }

   private void fireGroupOnEntityExit(@Nonnull VolumeEntry entry, @Nonnull TriggerVolumeManager manager, @Nonnull UUID entityUuid) {
      if (entry.getGroupId() != null) {
         GroupEntry group = manager.getGroup(entry.getGroupId());
         if (group != null) {
            for (TriggerCondition condition : group.getConditions()) {
               condition.onEntityExit(entityUuid);
            }

            for (TriggerEffect effect : group.getEffects()) {
               effect.onEntityExit(entityUuid);
            }

            for (TriggerEffect effect : group.getRejectionEffects()) {
               effect.onEntityExit(entityUuid);
            }
         }
      }
   }

   private static void notifyVolumeEntityExit(@Nonnull VolumeEntry entry, @Nonnull UUID entityUuid) {
      for (TriggerCondition condition : entry.getConditions()) {
         condition.onEntityExit(entityUuid);
      }

      for (TriggerEffect effect : entry.getEffects()) {
         effect.onEntityExit(entityUuid);
      }

      for (TriggerEffect effect : entry.getRejectionEffects()) {
         effect.onEntityExit(entityUuid);
      }
   }

   private void dispatchEvent(@Nonnull TriggerEventType eventType, @Nonnull VolumeEntry entry, @Nonnull Ref<EntityStore> entityRef, @Nonnull UUID entityUuid) {
      this.eventDispatcher.dispatch(eventType, entry, entityRef, entityUuid);
   }

   private static void dispatchToServerEventBus(
      @Nonnull TriggerEventType eventType, @Nonnull VolumeEntry entry, @Nonnull Ref<EntityStore> entityRef, @Nonnull UUID entityUuid
   ) {
      HytaleServer server = HytaleServer.get();
      if (server != null) {
         IEventDispatcher<TriggerVolumeEvent, TriggerVolumeEvent> dispatcher = server.getEventBus().dispatchFor(TriggerVolumeEvent.class, entry.getWorldName());
         if (dispatcher.hasListener()) {
            dispatcher.dispatch(new TriggerVolumeEvent(entry.getWorldName(), eventType, entry, entityRef, entityUuid));
         }
      }
   }

   private void clearIntervalTimers(@Nonnull VolumeEntry entry, @Nonnull UUID entityUuid) {
      entry.getLastFireTimes().object2LongEntrySet().removeIf(lastFireEntry -> lastFireEntry.getKey().entityId().equals(entityUuid));
   }

   private void processTrackedEntityExits(@Nonnull VolumeEntry entry, @Nonnull Store<EntityStore> store, @Nonnull TriggerVolumeManager manager) {
      long nowNanos = System.nanoTime();

      for (Entry<UUID, Ref<EntityStore>> tracked : entry.getTrackedEntities().entrySet()) {
         UUID uuid = tracked.getKey();
         Ref<EntityStore> entityRef = tracked.getValue();
         if (entityRef != null && entityRef.isValid()) {
            this.dispatchEvent(TriggerEventType.EXIT, entry, entityRef, uuid);

            for (int volumeEntry : collectEntries(entry.getConditions(), entry.getEffects(), entry.getRejectionEffects())) {
               this.fireEffects(TriggerEventType.EXIT, entityRef, entry, store, nowNanos, uuid, volumeEntry);
            }

            if (entry.getGroupId() != null) {
               GroupEntry group = manager.getGroup(entry.getGroupId());
               if (group != null) {
                  for (int groupEntry : collectEntries(group.getConditions(), group.getEffects(), group.getRejectionEffects())) {
                     this.fireGroupEffects(TriggerEventType.EXIT, entityRef, entry, manager, store, nowNanos, uuid, groupEntry);
                  }
               }
            }
         }

         notifyVolumeEntityExit(entry, uuid);
         this.fireGroupOnEntityExit(entry, manager, uuid);
         entry.clearEntityRuntimeState(uuid);
      }

      entry.getTrackedEntities().clear();
   }

   private static boolean isChunkLoaded(@Nonnull World world, @Nonnull Vector3d position) {
      long idx = ChunkUtil.indexChunkFromBlock(position.x(), position.z());
      ChunkStore chunkStore = world.getChunkStore();
      Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(idx);
      if (chunkRef != null && chunkRef.isValid()) {
         WorldChunk worldChunkComponent = chunkStore.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
         return worldChunkComponent != null && worldChunkComponent.is(ChunkFlag.TICKING);
      } else {
         return false;
      }
   }

   private static boolean containsEntity(
      @Nonnull TriggerVolumeShape shape, @Nonnull Vector3d origin, @Nonnull TransformComponent transform, @Nullable BoundingBox boundingBox
   ) {
      Vector3d pos = transform.getPosition();
      if (shape.contains(origin, pos)) {
         return true;
      }

      double entityHeight = boundingBox != null ? boundingBox.getBoundingBox().height() : 1.8;
      if (entityHeight <= 0.0) {
         return false;
      }

      Vector3d testPoint = THREAD_LOCAL_TEST_POINT.get();
      testPoint.set(pos.x(), pos.y() + entityHeight * 0.5, pos.z());
      if (shape.contains(origin, testPoint)) {
         return true;
      }

      testPoint.set(pos.x(), pos.y() + entityHeight, pos.z());
      return shape.contains(origin, testPoint);
   }

   private enum ActivationResult {
      ACCEPTED,
      REJECTED,
      PENDING;
   }

   @FunctionalInterface
   interface EventDispatcher {
      void dispatch(@Nonnull TriggerEventType var1, @Nonnull VolumeEntry var2, @Nonnull Ref<EntityStore> var3, @Nonnull UUID var4);
   }
}
