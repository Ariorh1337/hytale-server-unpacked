package com.hypixel.hytale.server.core.modules.physics;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class RestingSupport {
   protected int supportMinX;
   protected int supportMaxX;
   protected int supportMinZ;
   protected int supportMaxZ;
   protected int supportMinY;
   protected int supportMaxY;
   @Nullable
   protected int[] supportBlocks;

   public boolean hasChanged(@Nonnull ChunkStore chunkStore) {
      if (this.supportBlocks == null) {
         return false;
      }

      Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
      int index = 0;

      for (int z = this.supportMinZ; z <= this.supportMaxZ; z++) {
         for (int x = this.supportMinX; x <= this.supportMaxX; x++) {
            Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(ChunkUtil.indexChunkFromBlock(x, z));
            WorldChunk worldChunkComponent = chunkRef != null && chunkRef.isValid()
               ? chunkComponentStore.getComponent(chunkRef, WorldChunk.getComponentType())
               : null;
            if (worldChunkComponent != null) {
               for (int y = this.supportMinY; y <= this.supportMaxY; y++) {
                  if (this.supportBlocks[index++] != worldChunkComponent.getBlock(x, y, z)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public void rest(@Nonnull ChunkStore chunkStore, @Nonnull Box boundingBox, @Nonnull Vector3d position) {
      if (this.supportBlocks == null) {
         int maxSize = (int)(Math.ceil(boundingBox.width() + 1.0) * Math.ceil(boundingBox.depth() + 1.0) * Math.ceil(boundingBox.height() + 1.0));
         this.supportBlocks = new int[maxSize];
      }

      this.supportMinX = MathUtil.floor(position.x + boundingBox.min.x);
      this.supportMaxX = MathUtil.floor(position.x + boundingBox.max.x);
      this.supportMinZ = MathUtil.floor(position.z + boundingBox.min.z);
      this.supportMaxZ = MathUtil.floor(position.z + boundingBox.max.z);
      this.supportMinY = MathUtil.floor(position.y + boundingBox.min.y);
      this.supportMaxY = MathUtil.floor(position.y + boundingBox.max.y);
      Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
      int index = 0;

      for (int z = this.supportMinZ; z <= this.supportMaxZ; z++) {
         for (int x = this.supportMinX; x <= this.supportMaxX; x++) {
            long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
            Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
            WorldChunk worldChunkComponent = chunkRef != null && chunkRef.isValid()
               ? chunkComponentStore.getComponent(chunkRef, WorldChunk.getComponentType())
               : null;
            if (worldChunkComponent != null) {
               for (int y = this.supportMinY; y <= this.supportMaxY; y++) {
                  this.supportBlocks[index++] = worldChunkComponent.getBlock(x, y, z);
               }
            } else {
               for (int y = this.supportMinY; y <= this.supportMaxY; y++) {
                  this.supportBlocks[index++] = 1;
               }
            }
         }
      }
   }

   public void clear() {
      this.supportBlocks = null;
   }
}
