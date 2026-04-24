package com.hypixel.hytale.builtin.triggervolumes.system;

import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.event.TriggerVolumeEvent;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
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
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.system.PlayerSpatialSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
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
   private final DelayedEffectScheduler delayedEffectScheduler = new DelayedEffectScheduler();

   public TriggerVolumeTickingSystem(
      @Nonnull ResourceType<EntityStore, TriggerVolumeManager> managerResourceType,
      @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialResourceType,
      @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> entitySpatialResourceType
   ) {
      this.managerResourceType = managerResourceType;
      this.playerSpatialResourceType = playerSpatialResourceType;
      this.entitySpatialResourceType = entitySpatialResourceType;
      this.dependencies = Set.of(new SystemDependency<>(Order.AFTER, PlayerSpatialSystem.class, OrderPriority.CLOSEST));
   }

   @Nonnull
   @Override
   public Set<Dependency<EntityStore>> getDependencies() {
      return this.dependencies;
   }

   @Override
   public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
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
               this.tickVolume(entry, dt, store, manager);
            } catch (Exception e) {
               LOGGER.at(Level.WARNING).withCause(e).log("Error ticking trigger volume '%s'", entry.getId());
            }
         }

         if (!this.delayedEffectScheduler.isEmpty()) {
            this.delayedEffectScheduler.tick(System.nanoTime(), store);
         }

         this.processPendingDestroys(manager);
      }
   }

   private void processPendingDestroys(@Nonnull TriggerVolumeManager manager) {
      for (VolumeEntry entry : manager.getVolumes()) {
         if (entry.isPendingDestroy()) {
            String id = entry.getId();
            this.delayedEffectScheduler.cancelForVolume(entry);
            manager.unregister(id);
            manager.notifyViewersRemove(id);
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

   private void tickVolume(@Nonnull VolumeEntry entry, float dt, @Nonnull Store<EntityStore> store, @Nonnull TriggerVolumeManager manager) {
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

      if (entry.getTargetTypes().contains(EntityTargetType.NPC)) {
         SpatialResource<Ref<EntityStore>, EntityStore> entitySpatial = store.getResource(this.entitySpatialResourceType);
         if (entitySpatial != null) {
            entitySpatial.getSpatialStructure().collect(origin, shape.getMaxDistanceFromOrigin(), results);
         }
      }

      List<Ref<EntityStore>> entitiesToProcess = THREAD_LOCAL_ENTITY_REFS.get();
      entitiesToProcess.clear();
      entitiesToProcess.addAll(results);
      long nowNanos = System.nanoTime();

      for (int i = 0; i < entitiesToProcess.size(); i++) {
         Ref<EntityStore> entityRef = entitiesToProcess.get(i);
         if (entityRef.isValid()) {
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

                     entry.recordActivation(uuid, nowNanos);
                     this.dispatchEvent(TriggerEventType.ENTER, entry, entityRef, uuid);
                     this.fireEffects(TriggerEventType.ENTER, entityRef, entry, store, nowNanos, uuid);
                     this.fireGroupEffects(TriggerEventType.ENTER, entityRef, entry, manager, store, nowNanos, uuid);
                     this.clearIntervalTimers(entry, uuid);
                  }

                  this.fireEffects(TriggerEventType.TICK, entityRef, entry, store, nowNanos, uuid);
                  this.fireGroupEffects(TriggerEventType.TICK, entityRef, entry, manager, store, nowNanos, uuid);
               }
            }
         }
      }

      for (UUID exitedUuid : previousUuids) {
         Ref<EntityStore> exitedRef = previousRefs.get(exitedUuid);
         if (exitedRef != null && exitedRef.isValid()) {
            this.dispatchEvent(TriggerEventType.EXIT, entry, exitedRef, exitedUuid);
            this.fireEffects(TriggerEventType.EXIT, exitedRef, entry, store, nowNanos, exitedUuid);
            this.fireGroupEffects(TriggerEventType.EXIT, exitedRef, entry, manager, store, nowNanos, exitedUuid);
         }

         for (TriggerEffect effect : entry.getEffects()) {
            effect.onEntityExit(exitedUuid);
         }

         this.fireGroupOnEntityExit(entry, manager, exitedUuid);
         this.delayedEffectScheduler.cancelNonExitForEntity(exitedUuid);
         this.clearIntervalTimers(entry, exitedUuid);
      }
   }

   private void fireEffects(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid
   ) {
      List<TriggerEffect> effects = entry.getEffects();

      for (int i = 0; i < effects.size(); i++) {
         TriggerEffect effect = effects.get(i);
         if (effect.getEventType() == eventType) {
            VolumeEntry.EffectEntityKey intervalKey = null;
            if (eventType == TriggerEventType.TICK && effect.getInterval() > 0.0F) {
               intervalKey = new VolumeEntry.EffectEntityKey(i, entityUuid);
               Long lastFire = entry.getLastFireTimes().get(intervalKey);
               if (lastFire != null) {
                  double elapsedSeconds = (nowNanos - lastFire) / 1.0E9;
                  if (elapsedSeconds < effect.getInterval()) {
                     continue;
                  }
               }
            }

            float totalDelay = entry.getActivationDelay() + effect.getDelay();
            if (totalDelay > 0.0F) {
               this.delayedEffectScheduler.schedule(effect, entityRef, entityUuid, eventType, entry, nowNanos, totalDelay);
               if (intervalKey != null) {
                  entry.getLastFireTimes().put(intervalKey, nowNanos);
               }
            } else {
               try {
                  TriggerContext context = new TriggerContext(entityRef, store, eventType, entry);
                  effect.execute(context);
                  if (intervalKey != null) {
                     entry.getLastFireTimes().put(intervalKey, nowNanos);
                  }
               } catch (Exception e) {
                  LOGGER.at(Level.WARNING).withCause(e).log("Error executing effect %s on volume '%s'", effect.getClass().getSimpleName(), entry.getId());
               }
            }
         }
      }
   }

   private void fireGroupEffects(
      @Nonnull TriggerEventType eventType,
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull VolumeEntry entry,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull Store<EntityStore> store,
      long nowNanos,
      @Nonnull UUID entityUuid
   ) {
      if (entry.getGroupId() != null) {
         GroupEntry group = manager.getGroup(entry.getGroupId());
         if (group != null && group.isEnabled() && !group.getEffects().isEmpty()) {
            ArrayList<VolumeEntry> spatialVolumes = new ArrayList<>();

            for (String memberId : group.getMemberVolumeIds()) {
               VolumeEntry member = manager.getVolume(memberId);
               if (member != null) {
                  spatialVolumes.add(member);
               }
            }

            if (!spatialVolumes.isEmpty()) {
               if (!group.getTargetTypes().isEmpty()) {
                  boolean isPlayer = store.getComponent(entityRef, PlayerRef.getComponentType()) != null;
                  EntityTargetType required = isPlayer ? EntityTargetType.PLAYER : EntityTargetType.NPC;
                  if (!group.getTargetTypes().contains(required)) {
                     return;
                  }
               }

               List<TriggerEffect> effects = group.getEffects();

               for (int i = 0; i < effects.size(); i++) {
                  TriggerEffect effect = effects.get(i);
                  if (effect.getEventType() == eventType) {
                     VolumeEntry.EffectEntityKey intervalKey = null;
                     if (eventType == TriggerEventType.TICK && effect.getInterval() > 0.0F) {
                        intervalKey = new VolumeEntry.EffectEntityKey(-(i + 1), entityUuid);
                        Long lastFire = entry.getLastFireTimes().get(intervalKey);
                        if (lastFire != null) {
                           double elapsedSeconds = (nowNanos - lastFire) / 1.0E9;
                           if (elapsedSeconds < effect.getInterval()) {
                              continue;
                           }
                        }
                     }

                     float totalDelay = entry.getActivationDelay() + effect.getDelay();
                     if (totalDelay > 0.0F) {
                        this.delayedEffectScheduler.schedule(effect, entityRef, entityUuid, eventType, entry, nowNanos, totalDelay, spatialVolumes);
                        if (intervalKey != null) {
                           entry.getLastFireTimes().put(intervalKey, nowNanos);
                        }
                     } else {
                        try {
                           TriggerContext context = new TriggerContext(entityRef, store, eventType, entry, spatialVolumes);
                           effect.execute(context);
                           if (intervalKey != null) {
                              entry.getLastFireTimes().put(intervalKey, nowNanos);
                           }
                        } catch (Exception e) {
                           LOGGER.at(Level.WARNING)
                              .withCause(e)
                              .log(
                                 "Error executing group effect %s on group '%s' via volume '%s'",
                                 effect.getClass().getSimpleName(),
                                 group.getId(),
                                 entry.getId()
                              );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void fireGroupOnEntityExit(@Nonnull VolumeEntry entry, @Nonnull TriggerVolumeManager manager, @Nonnull UUID entityUuid) {
      if (entry.getGroupId() != null) {
         GroupEntry group = manager.getGroup(entry.getGroupId());
         if (group != null && !group.getEffects().isEmpty()) {
            for (TriggerEffect effect : group.getEffects()) {
               effect.onEntityExit(entityUuid);
            }
         }
      }
   }

   private void dispatchEvent(@Nonnull TriggerEventType eventType, @Nonnull VolumeEntry entry, @Nonnull Ref<EntityStore> entityRef, @Nonnull UUID entityUuid) {
      IEventDispatcher<TriggerVolumeEvent, TriggerVolumeEvent> dispatcher = HytaleServer.get()
         .getEventBus()
         .dispatchFor(TriggerVolumeEvent.class, entry.getWorldName());
      if (dispatcher.hasListener()) {
         dispatcher.dispatch(new TriggerVolumeEvent(entry.getWorldName(), eventType, entry, entityRef, entityUuid));
      }
   }

   private void clearIntervalTimers(@Nonnull VolumeEntry entry, @Nonnull UUID entityUuid) {
      entry.getLastFireTimes().entrySet().removeIf(e -> e.getKey().entityId().equals(entityUuid));
   }

   private void processTrackedEntityExits(@Nonnull VolumeEntry entry, @Nonnull Store<EntityStore> store, @Nonnull TriggerVolumeManager manager) {
      long nowNanos = System.nanoTime();

      for (Entry<UUID, Ref<EntityStore>> tracked : entry.getTrackedEntities().entrySet()) {
         UUID uuid = tracked.getKey();
         Ref<EntityStore> entityRef = tracked.getValue();
         if (entityRef != null && entityRef.isValid()) {
            this.dispatchEvent(TriggerEventType.EXIT, entry, entityRef, uuid);
            this.fireEffects(TriggerEventType.EXIT, entityRef, entry, store, nowNanos, uuid);
            this.fireGroupEffects(TriggerEventType.EXIT, entityRef, entry, manager, store, nowNanos, uuid);
         }

         for (TriggerEffect effect : entry.getEffects()) {
            effect.onEntityExit(uuid);
         }

         this.fireGroupOnEntityExit(entry, manager, uuid);
      }

      entry.getTrackedEntities().clear();
      entry.getLastFireTimes().clear();
   }

   private static boolean isChunkLoaded(@Nonnull World world, @Nonnull Vector3d position) {
      long idx = ChunkUtil.indexChunkFromBlock(position.x(), position.z());
      return world.getChunkIfLoaded(idx) != null;
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

      Vector3d tp = THREAD_LOCAL_TEST_POINT.get();
      tp.set(pos.x(), pos.y() + entityHeight * 0.5, pos.z());
      if (shape.contains(origin, tp)) {
         return true;
      }

      tp.set(pos.x(), pos.y() + entityHeight, pos.z());
      return shape.contains(origin, tp);
   }
}
