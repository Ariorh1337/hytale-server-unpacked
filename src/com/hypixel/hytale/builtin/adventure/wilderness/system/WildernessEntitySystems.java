package com.hypixel.hytale.builtin.adventure.wilderness.system;

import com.hypixel.hytale.builtin.adventure.wilderness.WildernessConfig;
import com.hypixel.hytale.builtin.adventure.wilderness.component.Wilderness;
import com.hypixel.hytale.builtin.adventure.wilderness.resource.WildernessTracker;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.ArchetypeTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class WildernessEntitySystems {
   public static class AddSystem extends RefSystem<EntityStore> {
      @Nonnull
      protected final ComponentType<EntityStore, Wilderness> wildernessComponentType;
      @Nonnull
      protected final Query<EntityStore> query = Query.and(PlayerRef.getComponentType(), TransformComponent.getComponentType());

      public AddSystem(@Nonnull ComponentType<EntityStore, Wilderness> wildernessComponentType) {
         this.wildernessComponentType = wildernessComponentType;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> buffer
      ) {
         World world = store.getExternalData().getWorld();
         WildernessConfig config = WildernessConfig.getOrDefault(world);
         int radius = config.getPlayerTrackerChunkRadius();
         int radiusY = config.getPlayerTrackerChunkRadiusY();
         buffer.putComponent(ref, this.wildernessComponentType, new Wilderness(radius, radiusY));
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> buffer
      ) {
      }
   }

   public static class TickSystem extends ArchetypeTickingSystem<EntityStore> {
      @Nonnull
      protected final ComponentType<EntityStore, Wilderness> wildernessComponentType;
      @Nonnull
      protected final ComponentType<EntityStore, TransformComponent> transformComponentType;
      @Nonnull
      protected final ResourceType<ChunkStore, WildernessTracker> wildernessTrackerResourceType;
      protected final Query<EntityStore> query;

      public TickSystem(
         @Nonnull ComponentType<EntityStore, Wilderness> wildernessComponentType,
         @Nonnull ComponentType<EntityStore, TransformComponent> transformComponentType,
         @Nonnull ResourceType<ChunkStore, WildernessTracker> wildernessTrackerResourceType
      ) {
         this.wildernessComponentType = wildernessComponentType;
         this.transformComponentType = transformComponentType;
         this.wildernessTrackerResourceType = wildernessTrackerResourceType;
         this.query = Query.and(wildernessComponentType, transformComponentType);
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public void tick(float dt, @Nonnull ArchetypeChunk<EntityStore> table, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> buffer) {
         WildernessTracker tracker = WildernessTracker.getTracker(store.getExternalData().getWorld());
         if (!tracker.isDisabled()) {
            for (int i = 0; i < table.size(); i++) {
               Wilderness wilderness = table.getComponent(i, this.wildernessComponentType);
               assert wilderness != null;
               TransformComponent transform = table.getComponent(i, this.transformComponentType);
               assert transform != null;
               int x = MathUtil.floor(transform.getPosition().x);
               int y = MathUtil.floor(transform.getPosition().y);
               int z = MathUtil.floor(transform.getPosition().z);
               wilderness.move(x, y, z, tracker);
            }
         }
      }
   }
}
