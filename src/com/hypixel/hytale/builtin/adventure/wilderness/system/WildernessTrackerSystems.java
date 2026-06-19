package com.hypixel.hytale.builtin.adventure.wilderness.system;

import com.hypixel.hytale.builtin.adventure.wilderness.WildernessConfig;
import com.hypixel.hytale.builtin.adventure.wilderness.resource.WildernessTracker;
import com.hypixel.hytale.builtin.adventure.wilderness.resource.WildernessVisitor;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.state.RespawnBlock;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WildernessTrackerSystems {
   protected static final ComponentType<ChunkStore, WorldChunk> CHUNK_COMPONENT_TYPE = WorldChunk.getComponentType();
   protected static final ComponentType<ChunkStore, RespawnBlock> SPAWN_BLOCK_COMPONENT_TYPE = RespawnBlock.getComponentType();
   protected static final ComponentType<ChunkStore, BlockModule.BlockStateInfo> BLOCK_STATE_COMPONENT_TYPE = BlockModule.BlockStateInfo.getComponentType();
   protected static final Query<ChunkStore> SPAWN_BLOCKSTATE_QUERY = Query.and(BLOCK_STATE_COMPONENT_TYPE, SPAWN_BLOCK_COMPONENT_TYPE);

   public static WildernessTracker reload(@Nonnull World world) {
      Store<ChunkStore> store = world.getChunkStore().getStore();
      store.assertThread();
      WildernessConfig config = WildernessConfig.getOrDefault(world);
      WildernessTracker tracker = new WildernessTracker(config);
      if (tracker.isEnabled()) {
         try (WildernessVisitor.ChunkVisitor visitor = store.getResource(WildernessVisitor.getChunkResourceType())) {
            store.forEachEntityParallel(SPAWN_BLOCKSTATE_QUERY, visitor.setup(tracker, (i, table, buffer, t) -> {
               BlockModule.BlockStateInfo state = table.getComponent(i, BLOCK_STATE_COMPONENT_TYPE);
               assert state != null;
               RespawnBlock spawn = table.getComponent(i, SPAWN_BLOCK_COMPONENT_TYPE);
               assert spawn != null;
               WorldChunk chunk = buffer.getComponent(state.getChunkRef(), CHUNK_COMPONENT_TYPE);
               assert chunk != null;
               int chunkX = chunk.getX();
               int chunkZ = chunk.getZ();
               int chunkY = ChunkUtil.chunkCoordinate(ChunkUtil.yFromBlockInColumn(state.getIndex()));
               boolean owned = spawn.getOwnerUUID() != null;
               t.addHomeChunk(chunkX, chunkY, chunkZ, owned);
            }));
         }
      }

      store.replaceResource(WildernessTracker.getResourceType(), tracker);
      return tracker;
   }

   public static class ComponentAddRemove extends RefChangeSystem<ChunkStore, RespawnBlock> {
      @Nonnull
      @Override
      public Query<ChunkStore> getQuery() {
         return WildernessTrackerSystems.SPAWN_BLOCKSTATE_QUERY;
      }

      @Nonnull
      @Override
      public ComponentType<ChunkStore, RespawnBlock> componentType() {
         return WildernessTrackerSystems.SPAWN_BLOCK_COMPONENT_TYPE;
      }

      public void onComponentAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RespawnBlock spawn, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> buffer
      ) {
         onAdd(ref, spawn, buffer);
      }

      public void onComponentRemoved(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RespawnBlock spawn, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> buffer
      ) {
         onRemove(ref, spawn, buffer);
      }

      public void onComponentSet(
         @Nonnull Ref<ChunkStore> ref,
         @Nullable RespawnBlock oldSpawn,
         @Nonnull RespawnBlock newSpawn,
         @Nonnull Store<ChunkStore> store,
         @Nonnull CommandBuffer<ChunkStore> buffer
      ) {
         WildernessTracker tracker = buffer.getResource(WildernessTracker.getResourceType());
         if (!tracker.isDisabled()) {
            if (oldSpawn == null) {
               onAdd(ref, newSpawn, buffer);
            } else {
               boolean isOwned = newSpawn.getOwnerUUID() != null;
               boolean wasOwned = oldSpawn.getOwnerUUID() != null;
               if (isOwned != wasOwned) {
                  BlockModule.BlockStateInfo state = buffer.getComponent(ref, WildernessTrackerSystems.BLOCK_STATE_COMPONENT_TYPE);
                  assert state != null : "System query matched an entity that does not have component: BlockModule.BlockStateInfo";
                  WorldChunk chunk = buffer.getComponent(state.getChunkRef(), WildernessTrackerSystems.CHUNK_COMPONENT_TYPE);
                  if (chunk != null) {
                     int chunkX = chunk.getX();
                     int chunkZ = chunk.getZ();
                     int chunkY = ChunkUtil.chunkCoordinate(ChunkUtil.yFromBlockInColumn(state.getIndex()));
                     tracker.removeHomeChunk(chunkX, chunkY, chunkZ, wasOwned);
                     tracker.addHomeChunk(chunkX, chunkY, chunkZ, isOwned);
                  }
               }
            }
         }
      }

      protected static void onAdd(@Nonnull Ref<ChunkStore> ref, @Nonnull RespawnBlock spawn, @Nonnull CommandBuffer<ChunkStore> buffer) {
         WildernessTracker tracker = buffer.getResource(WildernessTracker.getResourceType());
         if (!tracker.isDisabled()) {
            BlockModule.BlockStateInfo state = buffer.getComponent(ref, WildernessTrackerSystems.BLOCK_STATE_COMPONENT_TYPE);
            assert state != null : "System query matched an entity that does not have component: BlockModule.BlockStateInfo";
            WorldChunk chunk = buffer.getComponent(state.getChunkRef(), WildernessTrackerSystems.CHUNK_COMPONENT_TYPE);
            if (chunk != null) {
               int chunkX = chunk.getX();
               int chunkZ = chunk.getZ();
               int chunkY = ChunkUtil.chunkCoordinate(ChunkUtil.yFromBlockInColumn(state.getIndex()));
               boolean owned = spawn.getOwnerUUID() != null;
               tracker.addHomeChunk(chunkX, chunkY, chunkZ, owned);
            }
         }
      }

      protected static void onRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RespawnBlock spawn, @Nonnull CommandBuffer<ChunkStore> buffer) {
         WildernessTracker tracker = buffer.getResource(WildernessTracker.getResourceType());
         if (!tracker.isDisabled()) {
            BlockModule.BlockStateInfo state = buffer.getComponent(ref, WildernessTrackerSystems.BLOCK_STATE_COMPONENT_TYPE);
            assert state != null : "System query matched an entity that does not have component: BlockModule.BlockStateInfo";
            WorldChunk chunk = buffer.getComponent(state.getChunkRef(), WildernessTrackerSystems.CHUNK_COMPONENT_TYPE);
            if (chunk != null) {
               int chunkX = chunk.getX();
               int chunkZ = chunk.getZ();
               int chunkY = ChunkUtil.chunkCoordinate(ChunkUtil.yFromBlockInColumn(state.getIndex()));
               boolean owned = spawn.getOwnerUUID() != null;
               tracker.removeHomeChunk(chunkX, chunkY, chunkZ, owned);
            }
         }
      }
   }

   public static class EntityAddRemove extends RefSystem<ChunkStore> {
      @Nonnull
      @Override
      public Query<ChunkStore> getQuery() {
         return WildernessTrackerSystems.SPAWN_BLOCKSTATE_QUERY;
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> buffer
      ) {
         RespawnBlock spawn = buffer.getComponent(ref, WildernessTrackerSystems.SPAWN_BLOCK_COMPONENT_TYPE);
         assert spawn != null : "System query matched an entity that does not have component: RespawnBlock";
         WildernessTrackerSystems.ComponentAddRemove.onAdd(ref, spawn, buffer);
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> buffer
      ) {
         RespawnBlock spawn = buffer.getComponent(ref, WildernessTrackerSystems.SPAWN_BLOCK_COMPONENT_TYPE);
         assert spawn != null : "System query matched an entity that does not have component: RespawnBlock";
         WildernessTrackerSystems.ComponentAddRemove.onRemove(ref, spawn, buffer);
      }
   }
}
