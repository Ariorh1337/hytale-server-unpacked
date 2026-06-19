package com.hypixel.hytale.builtin.triggervolumes.system;

import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class TriggerVolumeEntityRemoveSystem extends RefSystem<EntityStore> {
   @Nonnull
   private static final Query<EntityStore> QUERY = Query.and(UUIDComponent.getComponentType(), TransformComponent.getComponentType());
   @Nonnull
   private final TriggerVolumeTickingSystem tickingSystem;
   @Nonnull
   private final ResourceType<EntityStore, TriggerVolumeManager> managerResourceType;

   public TriggerVolumeEntityRemoveSystem(
      @Nonnull TriggerVolumeTickingSystem tickingSystem, @Nonnull ResourceType<EntityStore, TriggerVolumeManager> managerResourceType
   ) {
      this.tickingSystem = tickingSystem;
      this.managerResourceType = managerResourceType;
   }

   @Nonnull
   @Override
   public Query<EntityStore> getQuery() {
      return QUERY;
   }

   @Override
   public void onEntityAdded(
      @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
   }

   @Override
   public void onEntityRemove(
      @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
      TriggerVolumeManager manager = store.getResource(this.managerResourceType);
      if (manager != null) {
         UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
         if (uuidComponent != null) {
            UUID uuid = uuidComponent.getUuid();
            long nowNanos = System.nanoTime();

            for (VolumeEntry entry : manager.getVolumes()) {
               if (entry.getTrackedEntities().remove(uuid) != null) {
                  this.tickingSystem.fireVolumeExit(entry, ref, uuid, manager, store, nowNanos, true);
               }
            }
         }
      }
   }
}
