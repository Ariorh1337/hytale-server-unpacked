package com.hypixel.hytale.builtin.triggervolumes.effect;

import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public final class TriggerVolumeEntityQuery {
   private static final double DEFAULT_ENTITY_HEIGHT = 1.8;
   private static final ThreadLocal<Vector3d> THREAD_LOCAL_TEST_POINT = ThreadLocal.withInitial(Vector3d::new);

   private TriggerVolumeEntityQuery() {
   }

   @Nonnull
   public static List<Ref<EntityStore>> collectLivingNpcs(
      @Nonnull Store<EntityStore> store, @Nonnull List<VolumeEntry> volumes, @Nonnull Collection<String> roleFilters
   ) {
      return collectTargets(store, volumes, true, false, roleFilters);
   }

   @Nonnull
   public static List<Ref<EntityStore>> collectTargets(
      @Nonnull Store<EntityStore> store, @Nonnull List<VolumeEntry> volumes, boolean includeNpcs, boolean includePlayers, @Nonnull String roleFilter
   ) {
      return collectTargets(store, volumes, includeNpcs, includePlayers, roleFilter.isBlank() ? List.of() : List.of(roleFilter));
   }

   @Nonnull
   public static List<Ref<EntityStore>> collectTargets(
      @Nonnull Store<EntityStore> store,
      @Nonnull List<VolumeEntry> volumes,
      boolean includeNpcs,
      boolean includePlayers,
      @Nonnull Collection<String> roleFilters
   ) {
      EntityModule entityModule = EntityModule.get();
      SpatialResource<Ref<EntityStore>, EntityStore> entitySpatial = includeNpcs ? store.getResource(entityModule.getEntitySpatialResourceType()) : null;
      SpatialResource<Ref<EntityStore>, EntityStore> playerSpatial = includePlayers ? store.getResource(entityModule.getPlayerSpatialResourceType()) : null;
      if (entitySpatial == null && playerSpatial == null) {
         return List.of();
      }

      Set<String> filter = normalizeRoleFilters(roleFilters);
      HashSet<UUID> seen = new HashSet<>();
      ArrayList<Ref<EntityStore>> collected = new ArrayList<>();
      List<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();

      for (VolumeEntry volume : volumes) {
         TriggerVolumeShape shape = volume.getShape();
         Vector3d origin = volume.getPosition();
         if (entitySpatial != null) {
            collectFromSpatial(store, entitySpatial, shape, origin, false, filter, seen, collected, results);
         }

         if (playerSpatial != null) {
            collectFromSpatial(store, playerSpatial, shape, origin, true, filter, seen, collected, results);
         }
      }

      return collected;
   }

   private static void collectFromSpatial(
      @Nonnull Store<EntityStore> store,
      @Nonnull SpatialResource<Ref<EntityStore>, EntityStore> spatial,
      @Nonnull TriggerVolumeShape shape,
      @Nonnull Vector3d origin,
      boolean players,
      @Nonnull Set<String> roleFilters,
      @Nonnull HashSet<UUID> seen,
      @Nonnull List<Ref<EntityStore>> collected,
      @Nonnull List<Ref<EntityStore>> results
   ) {
      results.clear();
      spatial.getSpatialStructure().collect(origin, shape.getMaxDistanceFromOrigin(), results);

      for (int i = 0; i < results.size(); i++) {
         Ref<EntityStore> ref = results.get(i);
         if (players ? isLivingPlayer(store, ref) : isLivingNpc(store, ref, roleFilters)) {
            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform != null && containsEntity(shape, origin, transform, store.getComponent(ref, BoundingBox.getComponentType()))) {
               UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
               if (uuidComponent != null && seen.add(uuidComponent.getUuid())) {
                  collected.add(ref);
               }
            }
         }
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

   private static boolean isLivingNpc(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Set<String> roleFilters) {
      if (!ref.isValid()) {
         return false;
      }

      NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
      if (npc == null) {
         return false;
      }

      if (store.getComponent(ref, DeathComponent.getComponentType()) != null) {
         return false;
      }

      if (roleFilters.isEmpty()) {
         return true;
      }

      String roleName = npc.getRoleName();
      return roleName != null && roleFilters.contains(roleName.toLowerCase(Locale.ROOT));
   }

   @Nonnull
   private static Set<String> normalizeRoleFilters(@Nonnull Collection<String> roleFilters) {
      if (roleFilters.isEmpty()) {
         return Set.of();
      }

      LinkedHashSet<String> normalized = new LinkedHashSet<>(roleFilters.size());

      for (String roleFilter : roleFilters) {
         if (roleFilter != null) {
            String trimmed = roleFilter.trim();
            if (!trimmed.isEmpty()) {
               normalized.add(trimmed.toLowerCase(Locale.ROOT));
            }
         }
      }

      return normalized;
   }

   private static boolean isLivingPlayer(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref) {
      if (!ref.isValid()) {
         return false;
      } else {
         return store.getComponent(ref, PlayerRef.getComponentType()) == null ? false : store.getComponent(ref, DeathComponent.getComponentType()) == null;
      }
   }
}
