package com.hypixel.hytale.builtin.reactiveblocks.systems;

import com.hypixel.hytale.builtin.reactiveblocks.ReactiveBlocksPlugin;
import com.hypixel.hytale.builtin.reactiveblocks.states.BlockExplosive;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.joml.Vector3d;

public class ExplosiveSystems {
   public static class OnExplosiveAdded extends RefSystem<ChunkStore> {
      @Nonnull
      private final ComponentType<ChunkStore, BlockExplosive> explosiveBlockComponentType = ReactiveBlocksPlugin.get().getExplosiveComponentType();
      @Nonnull
      private final Query<ChunkStore> query;

      public OnExplosiveAdded(@Nonnull ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType) {
         this.query = Query.and(blockStateInfoComponentType, this.explosiveBlockComponentType);
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         BlockExplosive explosiveComponent = commandBuffer.getComponent(ref, this.explosiveBlockComponentType);
         assert explosiveComponent != null;
         if (explosiveComponent.getPrimedOnPlace()) {
            Instant detonationInstant = Instant.now().plus((long)(explosiveComponent.getFuseDuration() * 1000.0F), ChronoUnit.MILLIS);
            explosiveComponent.setDetonationInstant(detonationInstant);
         }
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      @Nonnull
      @Override
      public Query<ChunkStore> getQuery() {
         return this.query;
      }
   }

   public static class Ticking extends EntityTickingSystem<ChunkStore> {
      @Nonnull
      private final ComponentType<ChunkStore, BlockExplosive> explosiveBlockComponentType;
      @Nonnull
      private final ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType = BlockModule.BlockStateInfo.getComponentType();
      @Nonnull
      private final Query<ChunkStore> query;

      public Ticking() {
         this.explosiveBlockComponentType = ReactiveBlocksPlugin.get().getExplosiveComponentType();
         this.query = Query.and(this.explosiveBlockComponentType);
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk,
         @Nonnull Store<ChunkStore> store,
         @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         BlockExplosive explosiveBlockComponent = archetypeChunk.getComponent(index, this.explosiveBlockComponentType);
         BlockModule.BlockStateInfo blockStateInfo = archetypeChunk.getComponent(index, this.blockStateInfoComponentType);
         if (explosiveBlockComponent != null && blockStateInfo != null) {
            World world = commandBuffer.getExternalData().getWorld();
            Instant detonateInstant = explosiveBlockComponent.getDetonationInstant();
            if (detonateInstant != null && !Instant.now().isBefore(detonateInstant)) {
               Ref<ChunkStore> chunkRef = blockStateInfo.getChunkRef();
               if (chunkRef.isValid()) {
                  BlockChunk blockChunk = store.getComponent(chunkRef, BlockChunk.getComponentType());
                  if (blockChunk != null) {
                     int blockIndex = blockStateInfo.getIndex();
                     int localX = ChunkUtil.xFromBlockInColumn(blockIndex);
                     int localY = ChunkUtil.yFromBlockInColumn(blockIndex);
                     int localZ = ChunkUtil.zFromBlockInColumn(blockIndex);
                     int blockX = ChunkUtil.worldCoordFromLocalCoord(blockChunk.getX(), localX);
                     int blockZ = ChunkUtil.worldCoordFromLocalCoord(blockChunk.getZ(), localZ);
                     int blockRotationIndex = blockChunk.getSectionAtBlockY(localY).getRotationIndex(blockX, localY, blockZ);
                     RotationTuple blockRotationTuple = RotationTuple.get(blockRotationIndex);
                     Rotation3f blockRotation = new Rotation3f(
                        (float)blockRotationTuple.pitch().getRadians(),
                        (float)blockRotationTuple.yaw().getRadians(),
                        (float)blockRotationTuple.roll().getRadians()
                     );
                     world.execute(
                        () -> {
                           BlockType blockType = world.getBlockType(blockX, localY, blockZ);
                           if (blockType != null) {
                              world.setBlock(blockX, localY, blockZ, "Empty");
                              explosiveBlockComponent.doExplosion(
                                 blockType,
                                 new Vector3d(blockX + 0.5, localY + 0.5, blockZ + 0.5),
                                 blockRotation,
                                 world.getEntityStore().getStore(),
                                 world.getChunkStore().getStore()
                              );
                           }
                        }
                     );
                  }
               }
            }
         }
      }

      @NullableDecl
      @Override
      public Query<ChunkStore> getQuery() {
         return this.query;
      }
   }
}
