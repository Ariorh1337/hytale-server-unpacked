package com.hypixel.hytale.builtin.triggervolumes.effect;

import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

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
   @Nullable
   private final Vector3d actorPosition;
   @Nullable
   private final String tagKey;
   @Nullable
   private final String tagValue;
   @Nullable
   private final Vector3d blockPosition;
   @Nullable
   private final String blockId;
   @Nullable
   private final InteractionType interactionType;

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
      this(entityRef, store, eventType, volume, spatialVolumes, null);
   }

   public TriggerContext(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull Store<EntityStore> store,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry volume,
      @Nonnull List<VolumeEntry> spatialVolumes,
      @Nullable Vector3d actorPosition
   ) {
      this(entityRef, store, eventType, volume, spatialVolumes, actorPosition, null, null, null, null, null);
   }

   public TriggerContext(
      @Nonnull Ref<EntityStore> entityRef,
      @Nonnull Store<EntityStore> store,
      @Nonnull TriggerEventType eventType,
      @Nonnull VolumeEntry volume,
      @Nonnull List<VolumeEntry> spatialVolumes,
      @Nullable Vector3d actorPosition,
      @Nullable String tagKey,
      @Nullable String tagValue,
      @Nullable Vector3d blockPosition,
      @Nullable String blockId,
      @Nullable InteractionType interactionType
   ) {
      this.entityRef = entityRef;
      this.store = store;
      this.eventType = eventType;
      this.volume = volume;
      this.spatialVolumes = spatialVolumes.isEmpty() ? List.of(volume) : List.copyOf(spatialVolumes);
      this.actorPosition = actorPosition != null ? new Vector3d(actorPosition) : null;
      this.tagKey = tagKey;
      this.tagValue = tagValue;
      this.blockPosition = blockPosition != null ? new Vector3d(blockPosition) : null;
      this.blockId = blockId;
      this.interactionType = interactionType;
   }

   @Nonnull
   public Ref<EntityStore> getEntityRef() {
      return this.entityRef;
   }

   @Nullable
   public Vector3d getActorPosition() {
      if (this.entityRef != null && this.entityRef.isValid()) {
         TransformComponent transform = this.store.getComponent(this.entityRef, TransformComponent.getComponentType());
         if (transform != null) {
            return new Vector3d(transform.getPosition());
         }
      }

      return this.actorPosition != null ? new Vector3d(this.actorPosition) : null;
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

   @Nullable
   public String getTagKey() {
      return this.tagKey;
   }

   @Nullable
   public String getTagValue() {
      return this.tagValue;
   }

   @Nullable
   public Vector3d getBlockPosition() {
      return this.blockPosition != null ? new Vector3d(this.blockPosition) : null;
   }

   @Nullable
   public String getBlockId() {
      return this.blockId;
   }

   @Nullable
   public InteractionType getInteractionType() {
      return this.interactionType;
   }
}
