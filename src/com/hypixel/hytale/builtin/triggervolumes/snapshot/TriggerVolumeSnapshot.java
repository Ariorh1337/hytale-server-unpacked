package com.hypixel.hytale.builtin.triggervolumes.snapshot;

import com.hypixel.hytale.builtin.buildertools.snapshot.SelectionSnapshot;
import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class TriggerVolumeSnapshot implements SelectionSnapshot<TriggerVolumeSnapshot> {
   private final TriggerVolumeSnapshot.SnapshotType type;
   private final String volumeId;
   private final String worldName;
   @Nullable
   private final Vector3d position;
   @Nullable
   private final TriggerVolumeShape shape;
   @Nullable
   private final List<TriggerEffect> effects;
   @Nullable
   private final Set<EntityTargetType> targetTypes;
   private final boolean enabled;
   private final boolean keepLoaded;
   @Nullable
   private final String effectAssetRef;
   @Nullable
   private final String groupId;
   @Nullable
   private final Vector3f color;

   private TriggerVolumeSnapshot(
      @Nonnull TriggerVolumeSnapshot.SnapshotType type,
      @Nonnull String volumeId,
      @Nonnull String worldName,
      @Nullable Vector3d position,
      @Nullable TriggerVolumeShape shape,
      @Nullable List<TriggerEffect> effects,
      @Nullable Set<EntityTargetType> targetTypes,
      boolean enabled,
      boolean keepLoaded,
      @Nullable String effectAssetRef,
      @Nullable String groupId,
      @Nullable Vector3f color
   ) {
      this.type = type;
      this.volumeId = volumeId;
      this.worldName = worldName;
      this.position = position;
      this.shape = shape;
      this.effects = effects;
      this.targetTypes = targetTypes;
      this.enabled = enabled;
      this.keepLoaded = keepLoaded;
      this.effectAssetRef = effectAssetRef;
      this.groupId = groupId;
      this.color = color;
   }

   public static TriggerVolumeSnapshot ofCreate(@Nonnull VolumeEntry entry) {
      return new TriggerVolumeSnapshot(
         TriggerVolumeSnapshot.SnapshotType.CREATE, entry.getId(), entry.getWorldName(), null, null, null, null, true, false, null, null, null
      );
   }

   public static TriggerVolumeSnapshot ofDelete(@Nonnull VolumeEntry entry) {
      return captureState(TriggerVolumeSnapshot.SnapshotType.DELETE, entry);
   }

   public static TriggerVolumeSnapshot ofMutate(@Nonnull VolumeEntry entry) {
      return captureState(TriggerVolumeSnapshot.SnapshotType.MUTATE, entry);
   }

   private static TriggerVolumeSnapshot captureState(@Nonnull TriggerVolumeSnapshot.SnapshotType type, @Nonnull VolumeEntry entry) {
      return new TriggerVolumeSnapshot(
         type,
         entry.getId(),
         entry.getWorldName(),
         new Vector3d(entry.getPosition()),
         entry.getShape().copy(),
         new ArrayList<>(entry.getEffects()),
         EnumSet.copyOf(entry.getTargetTypes()),
         entry.isEnabled(),
         entry.isKeepLoaded(),
         entry.getEffectAssetRef(),
         entry.getGroupId(),
         entry.getColor() != null ? new Vector3f(entry.getColor()) : null
      );
   }

   @Nullable
   public TriggerVolumeSnapshot restore(@Nonnull Ref<EntityStore> ref, PlayerRef playerRef, World world, ComponentAccessor<EntityStore> componentAccessor) {
      TriggerVolumesPlugin plugin = TriggerVolumesPlugin.get();
      TriggerVolumeManager manager = world.getEntityStore().getStore().getResource(plugin.getManagerResourceType());
      if (manager == null) {
         return null;
      }

      return switch (this.type) {
         case CREATE -> {
            VolumeEntry entry = manager.getVolume(this.volumeId);
            if (entry == null) {
               yield null;
            } else {
               TriggerVolumeSnapshot inverse = ofDelete(entry);
               manager.unregister(this.volumeId);
               manager.notifyViewersRemove(this.volumeId);
               yield inverse;
            }
         }
         case DELETE -> {
            if (this.position != null && this.shape != null && this.effects != null && this.targetTypes != null) {
               VolumeEntry entry = new VolumeEntry(
                  this.volumeId,
                  this.worldName,
                  new Vector3d(this.position),
                  this.shape,
                  new ArrayList<>(this.effects),
                  EnumSet.copyOf(this.targetTypes),
                  this.enabled
               );
               entry.setKeepLoaded(this.keepLoaded);
               entry.setEffectAssetRef(this.effectAssetRef);
               entry.setGroupId(this.groupId);
               entry.setColor(this.color != null ? new Vector3f(this.color) : null);
               manager.register(this.volumeId, entry);
               manager.notifyViewersAdd(entry);
               yield ofCreate(entry);
            } else {
               yield null;
            }
         }
         case MUTATE -> {
            VolumeEntry entry = manager.getVolume(this.volumeId);
            if (entry == null) {
               yield null;
            } else {
               TriggerVolumeSnapshot inverse = ofMutate(entry);
               if (this.position != null) {
                  entry.setPosition(new Vector3d(this.position));
               }

               if (this.shape != null) {
                  entry.setShape(this.shape);
               }

               if (this.effects != null) {
                  entry.getEffects().clear();
                  entry.getEffects().addAll(this.effects);
               }

               if (this.targetTypes != null) {
                  entry.getTargetTypes().clear();
                  entry.getTargetTypes().addAll(this.targetTypes);
               }

               entry.setEnabled(this.enabled);
               entry.setKeepLoaded(this.keepLoaded);
               entry.setEffectAssetRef(this.effectAssetRef);
               entry.setGroupId(this.groupId);
               entry.setColor(this.color != null ? new Vector3f(this.color) : null);
               manager.markSpatialDirty();
               manager.notifyViewersAdd(entry);
               yield inverse;
            }
         }
      };
   }

   public enum SnapshotType {
      CREATE,
      DELETE,
      MUTATE;
   }
}
