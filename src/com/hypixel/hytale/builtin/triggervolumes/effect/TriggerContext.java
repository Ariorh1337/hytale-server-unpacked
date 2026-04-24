package com.hypixel.hytale.builtin.triggervolumes.effect;

import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import javax.annotation.Nonnull;

public class TriggerContext {
   @Nonnull
   private final Ref<EntityStore> entityRef;
   @Nonnull
   private final Store<EntityStore> store;
   @Nonnull
   private final TriggerEventType eventType;
   @Nonnull
   private final VolumeEntry volume;
   @Nonnull
   private final List<VolumeEntry> spatialVolumes;

   public TriggerContext(
      @Nonnull Ref<EntityStore> entityRef, @Nonnull Store<EntityStore> store, @Nonnull TriggerEventType eventType, @Nonnull VolumeEntry volume
   ) {
      this(entityRef, store, eventType, volume, List.of(volume));
   }

   public TriggerContext(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull Store<EntityStore> store,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry volume,
      @Nonnull List<VolumeEntry> spatialVolumes
   ) {
      this.entityRef = entityRef;
      this.store = store;
      this.eventType = eventType;
      this.volume = volume;
      this.spatialVolumes = spatialVolumes.isEmpty() ? List.of(volume) : List.copyOf(spatialVolumes);
   }

   @Nonnull
   public Ref<EntityStore> getEntityRef() {
      return this.entityRef;
   }

   @Nonnull
   public Store<EntityStore> getStore() {
      return this.store;
   }

   @Nonnull
   public TriggerEventType getEventType() {
      return this.eventType;
   }

   @Nonnull
   public VolumeEntry getVolume() {
      return this.volume;
   }

   @Nonnull
   public List<VolumeEntry> getSpatialVolumes() {
      return this.spatialVolumes;
   }
}
