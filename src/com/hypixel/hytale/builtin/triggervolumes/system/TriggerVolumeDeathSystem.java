package com.hypixel.hytale.builtin.triggervolumes.system;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public final class TriggerVolumeDeathSystem extends DeathSystems.OnDeathSystem {
   @Nonnull
   private static final Query<EntityStore> QUERY = Query.and(TransformComponent.getComponentType(), UUIDComponent.getComponentType());
   @Nonnull
   private final ResourceType<EntityStore, TriggerVolumeManager> managerResourceType;

   public TriggerVolumeDeathSystem(@Nonnull ResourceType<EntityStore, TriggerVolumeManager> managerResourceType) {
      this.managerResourceType = managerResourceType;
   }

   @Nonnull
   @Override
   public Query<EntityStore> getQuery() {
      return QUERY;
   }

   public void onComponentAdded(
      @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
      TriggerVolumeManager manager = store.getResource(this.managerResourceType);
      if (manager != null) {
         TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
         if (transform != null) {
            UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
            if (uuidComponent != null) {
               manager.enqueuePositionalEvent(TriggerEventType.ENTITY_DIED, ref, uuidComponent.getUuid(), new Vector3d(transform.getPosition()));
            }
         }
      }
   }
}
