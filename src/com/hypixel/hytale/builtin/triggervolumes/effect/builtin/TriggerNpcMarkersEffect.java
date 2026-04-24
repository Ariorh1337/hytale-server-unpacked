package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.spawnmarkers.SpawnMarkerEntity;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class TriggerNpcMarkersEffect extends TriggerEffect {
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   @Nonnull
   public static final BuilderCodec<TriggerNpcMarkersEffect> CODEC = BuilderCodec.builder(
         TriggerNpcMarkersEffect.class, TriggerNpcMarkersEffect::new, BASE_CODEC
      )
      .append(new KeyedCodec<>("MarkerType", Codec.STRING, false), (e, v) -> e.markerType = v, e -> e.markerType)
      .add()
      .build();
   @Nullable
   private String markerType;

   @Override
   public void execute(@Nonnull TriggerContext context) {
      Store<EntityStore> store = context.getStore();
      SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(SpawningPlugin.get().getSpawnMarkerSpatialResource());
      if (spatialResource != null) {
         ReferenceArrayList<Ref<EntityStore>> candidates = new ReferenceArrayList<>();
         ReferenceOpenHashSet<Ref<EntityStore>> seenMarkers = new ReferenceOpenHashSet<>();
         int triggered = 0;

         for (VolumeEntry vol : context.getSpatialVolumes()) {
            Vector3d origin = vol.getPosition();
            TriggerVolumeShape shape = vol.getShape();
            candidates.clear();
            spatialResource.getSpatialStructure().collect(origin, (int)shape.getBoundingRadius() + 1, candidates);

            for (int i = 0; i < candidates.size(); i++) {
               Ref<EntityStore> markerRef = candidates.get(i);
               if (markerRef.isValid() && seenMarkers.add(markerRef)) {
                  SpawnMarkerEntity markerComponent = store.getComponent(markerRef, SpawnMarkerEntity.getComponentType());
                  if (markerComponent != null
                     && markerComponent.isManualTrigger()
                     && (this.markerType == null || this.markerType.equals(markerComponent.getSpawnMarkerId()))) {
                     TransformComponent transform = store.getComponent(markerRef, TransformComponent.getComponentType());
                     if (transform != null && shape.contains(origin, transform.getPosition()) && markerComponent.trigger(markerRef, store)) {
                        triggered++;
                     }
                  }
               }
            }
         }

         if (triggered == 0) {
            LOGGER.at(Level.FINE)
               .log(
                  "TriggerNpcMarkers: no manual spawn markers matched in volume '%s'%s",
                  context.getVolume().getId(),
                  this.markerType != null ? " (MarkerType filter: " + this.markerType + ")" : ""
               );
         }
      }
   }
}
