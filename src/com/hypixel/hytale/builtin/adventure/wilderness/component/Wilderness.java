package com.hypixel.hytale.builtin.adventure.wilderness.component;

import com.hypixel.hytale.builtin.adventure.wilderness.WildernessPlugin;
import com.hypixel.hytale.builtin.adventure.wilderness.resource.WildernessTracker;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.BitSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3i;

public class Wilderness implements Component<EntityStore> {
   protected static final int INVALID_COORD = Integer.MIN_VALUE;
   protected long generation = Long.MIN_VALUE;
   protected int chunkX = Integer.MIN_VALUE;
   protected int chunkY = Integer.MIN_VALUE;
   protected int chunkZ = Integer.MIN_VALUE;
   protected final int sizeY;
   protected final int sizeXZ;
   protected final int sizeXZ2;
   protected final int radiusY;
   protected final int radiusXZ;
   protected final BitSet chunks = new BitSet();
   protected final Vector3i vector = new Vector3i();

   public Wilderness() {
      this(0, 0);
   }

   public Wilderness(int horizontalRadius, int verticalRadius) {
      this.radiusY = verticalRadius;
      this.radiusXZ = horizontalRadius;
      this.sizeY = 1 + 2 * this.radiusY;
      this.sizeXZ = 1 + 2 * this.radiusXZ;
      this.sizeXZ2 = this.sizeY * this.sizeXZ;
   }

   public Wilderness(@Nonnull Wilderness other) {
      this(other.radiusXZ, other.radiusY);
      this.generation = other.generation;
      this.chunkX = other.chunkX;
      this.chunkY = other.chunkY;
      this.chunkZ = other.chunkZ;
      this.chunks.or(other.chunks);
      this.vector.set(other.vector);
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkY() {
      return this.chunkY;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public int begin() {
      return this.chunks.nextSetBit(0);
   }

   public int next(int index) {
      return this.chunks.nextSetBit(index + 1);
   }

   public boolean empty() {
      return this.chunks.isEmpty();
   }

   public int xFromIndex(int index) {
      return index / this.sizeY % this.sizeXZ + this.chunkX - this.radiusXZ;
   }

   public int yFromIndex(int index) {
      return index % this.sizeY + this.chunkY - this.radiusY;
   }

   public int zFromIndex(int index) {
      return index / this.sizeXZ2 + this.chunkZ - this.radiusXZ;
   }

   public boolean test(int x, int y, int z) {
      int chunkX = ChunkUtil.chunkCoordinate(x);
      int chunkY = ChunkUtil.chunkCoordinate(y);
      int chunkZ = ChunkUtil.chunkCoordinate(z);
      int index = this.index(chunkX, chunkY, chunkZ);
      return index != -1 && this.chunks.get(index);
   }

   public void move(int x, int y, int z, @Nonnull WildernessTracker tracker) {
      long generation = tracker.generation();
      int chunkX = ChunkUtil.chunkCoordinate(x);
      int chunkY = ChunkUtil.chunkCoordinate(y);
      int chunkZ = ChunkUtil.chunkCoordinate(z);
      if (generation != this.generation || chunkX != this.chunkX || chunkY != this.chunkY || chunkZ != this.chunkZ) {
         this.chunkX = chunkX;
         this.chunkY = chunkY;
         this.chunkZ = chunkZ;
         this.generation = generation;
         int minX = chunkX - this.radiusXZ;
         int minY = chunkY - this.radiusY;
         int minZ = chunkZ - this.radiusXZ;
         int maxX = chunkX + this.radiusXZ;
         int maxY = chunkY + this.radiusY;
         int maxZ = chunkZ + this.radiusXZ;
         this.chunks.clear();
         int cz = minZ;
         int i = 0;

         while (cz <= maxZ) {
            this.vector.z = cz;

            for (int cx = minX; cx <= maxX; cx++) {
               this.vector.x = cx;

               for (int cy = minY; cy <= maxY; i++) {
                  this.vector.y = cy;
                  this.chunks.set(i, tracker.isWildernessChunk(this.vector));
                  cy++;
               }
            }

            cz++;
         }
      }
   }

   @Nullable
   public Wilderness clone() {
      return new Wilderness(this);
   }

   protected int index(int x, int y, int z) {
      int dx = x - this.chunkX + this.radiusXZ;
      int dy = y - this.chunkY + this.radiusY;
      int dz = z - this.chunkZ + this.radiusXZ;
      return dx >= 0 && dx < this.sizeXZ && dy >= 0 && dy < this.sizeY && dz >= 0 && dz < this.sizeXZ ? dy + dx * this.sizeY + dz * this.sizeXZ2 : -1;
   }

   public static ComponentType<EntityStore, Wilderness> getComponentType() {
      return WildernessPlugin.get().getWildernessComponentType();
   }
}
