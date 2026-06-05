package com.hypixel.hytale.builtin.adventure.wilderness.resource;

import com.hypixel.hytale.builtin.adventure.wilderness.WildernessPlugin;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.function.function.TriFunction;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.asset.type.gameplay.WildernessConfig;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class WildernessTracker implements Resource<ChunkStore> {
   protected static final int VECTOR_POOL_CAPACITY = 1000;
   protected final boolean enabled;
   protected final int chunkRadiusX;
   protected final int chunkRadiusY;
   protected final int chunkRadiusZ;
   protected final Map<Vector3i, WildernessTracker.Counter> homeChunks = new ConcurrentHashMap<>();
   protected final Queue<Vector3i> vecPool = new ArrayBlockingQueue<>(1000);

   public WildernessTracker() {
      this.enabled = false;
      this.chunkRadiusX = ChunkUtil.chunkCoordinate(128);
      this.chunkRadiusY = ChunkUtil.chunkCoordinate(48);
      this.chunkRadiusZ = ChunkUtil.chunkCoordinate(128);
   }

   public WildernessTracker(@Nonnull WildernessConfig config) {
      this.enabled = config.isEnabled();
      this.chunkRadiusX = ChunkUtil.chunkCoordinate(config.getHorizontalDistanceBlocks());
      this.chunkRadiusY = ChunkUtil.chunkCoordinate(config.getVerticalDistanceBlocks());
      this.chunkRadiusZ = ChunkUtil.chunkCoordinate(config.getHorizontalDistanceBlocks());
   }

   public WildernessTracker(@Nonnull WildernessTracker tracker) {
      this.enabled = tracker.enabled;
      this.chunkRadiusX = tracker.chunkRadiusX;
      this.chunkRadiusY = tracker.chunkRadiusY;
      this.chunkRadiusZ = tracker.chunkRadiusZ;
      this.homeChunks.putAll(tracker.homeChunks);
   }

   @Nonnull
   public WildernessTracker clone() {
      return new WildernessTracker(this);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isDisabled() {
      return !this.enabled;
   }

   public boolean isHome(@Nonnull Vector3i position) {
      return !this.isWilderness(position);
   }

   public boolean isHome(@Nonnull Vector3d position) {
      return !this.isWilderness(position);
   }

   public boolean isHome(int x, int y, int z) {
      return !this.isWilderness(x, y, z);
   }

   public boolean isWilderness(@Nonnull Vector3i position) {
      int x = position.x;
      int y = position.y;
      int z = position.z;
      return this.isWilderness(x, y, z);
   }

   public boolean isWilderness(@Nonnull Vector3d position) {
      int x = MathUtil.floor(position.x);
      int y = MathUtil.floor(position.y);
      int z = MathUtil.floor(position.z);
      return this.isWilderness(x, y, z);
   }

   public boolean isWilderness(int x, int y, int z) {
      Vector3i coords = this.newVector();

      try {
         coords.x = ChunkUtil.chunkCoordinate(x);
         coords.y = ChunkUtil.chunkCoordinate(y);
         coords.z = ChunkUtil.chunkCoordinate(z);
         return !this.homeChunks.containsKey(coords);
      } finally {
         this.recycle(coords);
      }
   }

   public void collectHomeChunks(@Nonnull Vector3i position, int radiusX, int radiusY, int radiusZ, @Nonnull Collection<Vector3i> collector) {
      int x = position.x;
      int y = position.y;
      int z = position.z;
      this.collectHomeChunks(x, y, z, radiusX, radiusY, radiusZ, collector);
   }

   public void collectHomeChunks(@Nonnull Vector3d position, int radiusX, int radiusY, int radiusZ, @Nonnull Collection<Vector3i> collector) {
      int x = MathUtil.floor(position.x);
      int y = MathUtil.floor(position.y);
      int z = MathUtil.floor(position.z);
      this.collectHomeChunks(x, y, z, radiusX, radiusY, radiusZ, collector);
   }

   public void collectWildernessChunks(@Nonnull Vector3i position, int radiusX, int radiusY, int radiusZ, @Nonnull Collection<Vector3i> collector) {
      int x = position.x;
      int y = position.y;
      int z = position.z;
      this.collectWildernessChunks(x, y, z, radiusX, radiusY, radiusZ, collector);
   }

   public void collectWildernessChunks(@Nonnull Vector3d position, int radiusX, int radiusY, int radiusZ, @Nonnull Collection<Vector3i> collector) {
      int x = MathUtil.floor(position.x);
      int y = MathUtil.floor(position.y);
      int z = MathUtil.floor(position.z);
      this.collectWildernessChunks(x, y, z, radiusX, radiusY, radiusZ, collector);
   }

   public void collectHomeChunks(int x, int y, int z, int radiusX, int radiusY, int radiusZ, @Nonnull Collection<Vector3i> collector) {
      int chunkX = ChunkUtil.chunkCoordinate(x);
      int chunkY = ChunkUtil.chunkCoordinate(y);
      int chunkZ = ChunkUtil.chunkCoordinate(z);
      int chunkRadiusX = ChunkUtil.chunkCoordinate(radiusX);
      int chunkRadiusY = ChunkUtil.chunkCoordinate(radiusY);
      int chunkRadiusZ = ChunkUtil.chunkCoordinate(radiusZ);
      iterate(this, chunkX, chunkY, chunkZ, chunkRadiusX, chunkRadiusY, chunkRadiusZ, collector, (tracker, coords, results) -> {
         if (tracker.homeChunks.containsKey(coords)) {
            results.add(new Vector3i(coords));
         }

         return WildernessTracker.Iterator.CONTINUE;
      });
   }

   public void collectWildernessChunks(int x, int y, int z, int radiusX, int radiusY, int radiusZ, @Nonnull Collection<Vector3i> collector) {
      int chunkX = ChunkUtil.chunkCoordinate(x);
      int chunkY = ChunkUtil.chunkCoordinate(y);
      int chunkZ = ChunkUtil.chunkCoordinate(z);
      int chunkRadiusX = ChunkUtil.chunkCoordinate(radiusX);
      int chunkRadiusY = ChunkUtil.chunkCoordinate(radiusY);
      int chunkRadiusZ = ChunkUtil.chunkCoordinate(radiusZ);
      iterate(this, chunkX, chunkY, chunkZ, chunkRadiusX, chunkRadiusY, chunkRadiusZ, collector, (tracker, coords, results) -> {
         if (!tracker.homeChunks.containsKey(coords)) {
            results.add(new Vector3i(coords));
         }

         return WildernessTracker.Iterator.CONTINUE;
      });
   }

   public void addHomeChunk(int chunkX, int chunkY, int chunkZ) {
      iterate(
         this,
         chunkX,
         chunkY,
         chunkZ,
         this.chunkRadiusX,
         this.chunkRadiusY,
         this.chunkRadiusZ,
         null,
         (tracker, coords, ignored) -> tracker.homeChunks.compute(coords, WildernessTracker.Counter::increment).coords == coords
            ? WildernessTracker.Iterator.CONSUME
            : WildernessTracker.Iterator.CONTINUE
      );
   }

   public void removeHomeChunk(int chunkX, int chunkY, int chunkZ) {
      WildernessTracker.Recycler recycler = WildernessTracker.Recycler.RESOURCE.get();
      iterate(this, chunkX, chunkY, chunkZ, this.chunkRadiusX, this.chunkRadiusY, this.chunkRadiusZ, recycler, (tracker, coords, decrementer) -> {
         tracker.homeChunks.compute(coords, decrementer);
         if (decrementer.removed != null) {
            this.recycle(decrementer.removed);
            decrementer.removed = null;
         }

         return WildernessTracker.Iterator.CONTINUE;
      });
   }

   protected static <T> void iterate(
      @Nonnull WildernessTracker tracker,
      int chunkX,
      int chunkY,
      int chunkZ,
      int radiusX,
      int radiusY,
      int radiusZ,
      @Nullable T ctx,
      @Nonnull TriFunction<WildernessTracker, Vector3i, T, WildernessTracker.Iterator> visitor
   ) {
      Vector3i coords = tracker.newVector();

      try {
         double invRadiusX2 = 1.0 / (radiusX * radiusX);
         double invRadiusY2 = 1.0 / (radiusY * radiusY);
         double invRadiusZ2 = 1.0 / (radiusZ * radiusZ);

         for (int dy = -radiusY; dy <= radiusY; dy++) {
            double fy = dy * dy * invRadiusY2;

            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
               double fz = dz * dz * invRadiusZ2;

               for (int dx = -radiusX; dx <= radiusX; dx++) {
                  double fx = dx * dx * invRadiusX2;
                  if (!(fx + fy + fz > 1.0)) {
                     coords.set(chunkX + dx, chunkY + dy, chunkZ + dz);
                     switch ((WildernessTracker.Iterator)visitor.apply(tracker, coords, ctx)) {
                        case CONTINUE:
                        default:
                           break;
                        case CONSUME:
                           coords = tracker.newVector();
                           break;
                        case EXIT:
                           return;
                     }
                  }
               }
            }
         }
      } finally {
         tracker.recycle(coords);
      }
   }

   @Nonnull
   protected Vector3i newVector() {
      Vector3i vec = this.vecPool.poll();
      return vec != null ? vec : new Vector3i();
   }

   protected void recycle(@Nonnull Vector3i vec) {
      this.vecPool.offer(vec);
   }

   public static ResourceType<ChunkStore, WildernessTracker> getResourceType() {
      return WildernessPlugin.get().getWildernessTrackerResourceType();
   }

   protected static class Counter {
      @Nonnull
      private final Vector3i coords;
      private int count = 0;

      protected Counter(@Nonnull Vector3i coords) {
         this.coords = coords;
      }

      @Nonnull
      protected static WildernessTracker.Counter increment(@Nonnull Vector3i pos, @Nullable WildernessTracker.Counter counter) {
         counter = counter != null ? counter : new WildernessTracker.Counter(pos);
         counter.count++;
         return counter;
      }

      @Nullable
      protected static WildernessTracker.Counter decrement(@Nonnull Vector3i pos, @Nullable WildernessTracker.Counter count) {
         return count != null && --count.count == 0 ? null : count;
      }
   }

   protected enum Iterator {
      CONTINUE,
      CONSUME,
      EXIT;
   }

   protected static class Recycler implements BiFunction<Vector3i, WildernessTracker.Counter, WildernessTracker.Counter> {
      protected static final ThreadLocal<WildernessTracker.Recycler> RESOURCE = ThreadLocal.withInitial(WildernessTracker.Recycler::new);
      @Nullable
      protected Vector3i removed;

      @Nullable
      public WildernessTracker.Counter apply(@Nonnull Vector3i pos, @Nullable WildernessTracker.Counter count) {
         if (WildernessTracker.Counter.decrement(pos, count) == null && count != null) {
            this.removed = count.coords;
            return null;
         } else {
            return count;
         }
      }
   }
}
