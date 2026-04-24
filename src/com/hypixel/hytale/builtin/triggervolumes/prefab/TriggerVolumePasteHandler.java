package com.hypixel.hytale.builtin.triggervolumes.prefab;

import com.hypixel.hytale.builtin.triggervolumes.component.TriggerVolume;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.prefab.event.PrefabPlaceEntityEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class TriggerVolumePasteHandler extends WorldEventSystem<EntityStore, PrefabPlaceEntityEvent> {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final String ID_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
   private static final int ID_LENGTH = 6;
   private final ResourceType<EntityStore, TriggerVolumeManager> managerResourceType;
   private final ComponentType<EntityStore, TriggerVolume> componentType;

   public TriggerVolumePasteHandler(
      @Nonnull ResourceType<EntityStore, TriggerVolumeManager> managerResourceType, @Nonnull ComponentType<EntityStore, TriggerVolume> componentType
   ) {
      super(PrefabPlaceEntityEvent.class);
      this.managerResourceType = managerResourceType;
      this.componentType = componentType;
   }

   public void handle(@Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull PrefabPlaceEntityEvent event) {
      Holder<EntityStore> holder = event.getHolder();
      TriggerVolume tvComponent = holder.getComponent(this.componentType);
      if (tvComponent != null) {
         TransformComponent transform = holder.getComponent(TransformComponent.getComponentType());
         if (transform != null && transform.getPosition() != null) {
            TriggerVolumeManager manager = store.getResource(this.managerResourceType);
            if (manager != null) {
               World world = manager.getWorld();
               if (world != null) {
                  String worldName = world.getName().toLowerCase(Locale.ROOT);
                  Vector3d position = new Vector3d(transform.getPosition());
                  String id = generateId(manager);
                  VolumeEntry entry = tvComponent.toVolumeEntry(id, worldName, position);
                  String linkId = tvComponent.getGroupLinkId();
                  if (linkId != null && !linkId.isBlank()) {
                     String newGroupId = manager.ensureGroupForPrefabLink(linkId, entry, worldName);
                     entry.setGroupId(newGroupId);
                     GroupEntry group = manager.getGroup(newGroupId);
                     if (group != null) {
                        group.addMember(entry.getId());
                     }
                  }

                  manager.register(id, entry);
                  manager.notifyViewersAdd(entry);
                  manager.markSpatialDirty();
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @Nonnull
   private static String generateId(@Nonnull TriggerVolumeManager manager) {
      ThreadLocalRandom rng = ThreadLocalRandom.current();

      String id;
      do {
         StringBuilder builder = new StringBuilder("tv_");

         for (int i = 0; i < 6; i++) {
            builder.append("abcdefghijklmnopqrstuvwxyz0123456789".charAt(rng.nextInt("abcdefghijklmnopqrstuvwxyz0123456789".length())));
         }

         id = builder.toString();
      } while (manager.hasVolume(id));

      return id;
   }
}
