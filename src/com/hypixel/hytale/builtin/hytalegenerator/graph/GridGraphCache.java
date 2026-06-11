package com.hypixel.hytale.builtin.hytalegenerator.graph;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class GridGraphCache {
   @Nonnull
   private final Vector3d cellSize;
   private final int capacity;
   @Nonnull
   private final ArrayDeque<Vector3i> history;
   @Nonnull
   private final Map<Vector3i, GraphSpace> positionCellMap;
   @Nullable
   private GraphSpace hotCell;
   @Nonnull
   private final Vector3i hotKey;
   @Nonnull
   private final GridGraphCache.Stats stats;
   @Nonnull
   private final Vector3i rCellIndex;

   public GridGraphCache(@Nonnull Vector3dc cellSize, int capacity) {
      assert capacity >= 0;
      assert isValidCellSize(cellSize);
      this.cellSize = new Vector3d(cellSize);
      this.positionCellMap = new HashMap<>();
      this.hotCell = null;
      this.hotKey = new Vector3i();
      this.capacity = capacity;
      this.history = new ArrayDeque<>(capacity);
      this.stats = new GridGraphCache.Stats();
      this.rCellIndex = new Vector3i();
   }

   public void getCellContaining(@Nonnull Vector3dc position_voxelGrid, @Nonnull GridGraphCache.Result result) {
      toCellIndex(position_voxelGrid, this.cellSize, this.rCellIndex);
      this.getCell(this.rCellIndex, result);
   }

   public void getCell(@Nonnull Vector3i cellIndex, @Nonnull GridGraphCache.Result result) {
      if (this.stats.totalCallCount == Long.MAX_VALUE) {
         this.stats.reset();
      }

      this.stats.totalCallCount++;
      if (this.hotCell != null && this.hotKey.equals(cellIndex)) {
         result.graph = this.hotCell;
         result.isNew = false;
      } else {
         GraphSpace cell = this.positionCellMap.get(cellIndex);
         this.hotKey.set(cellIndex);
         if (cell != null) {
            this.hotCell = cell;
            result.graph = cell;
            result.isNew = false;
         } else {
            this.stats.misses++;
            if (this.history.size() >= this.capacity) {
               Vector3i oldestKey = this.history.removeFirst();
               this.positionCellMap.remove(oldestKey);
            }

            cell = new GraphSpace();
            this.hotCell = cell;
            this.positionCellMap.put(cellIndex, cell);
            this.history.addLast(cellIndex);
            result.graph = cell;
            result.isNew = true;
         }
      }
   }

   public void toCellIndex(@Nonnull Vector3dc position, @Nonnull Vector3i cellIndex_out) {
      toCellIndex(position, this.cellSize, cellIndex_out);
   }

   public static void toCellIndex(@Nonnull Vector3dc position, @Nonnull Vector3dc gridSize, @Nonnull Vector3i cellIndex_out) {
      cellIndex_out.x = toCellIndex(position.x(), gridSize.x());
      cellIndex_out.y = toCellIndex(position.y(), gridSize.y());
      cellIndex_out.z = toCellIndex(position.z(), gridSize.z());
   }

   public void toCellBounds(@Nonnull Vector3ic cellIndex, @Nonnull Bounds3d cellBounds_out) {
      toCellBounds(cellIndex, this.cellSize, cellBounds_out);
   }

   public static void toCellBounds(@Nonnull Vector3ic cellIndex, @Nonnull Vector3dc gridSize, @Nonnull Bounds3d cellBounds_out) {
      cellBounds_out.min.x = toCellMin(cellIndex.x(), gridSize.x());
      cellBounds_out.min.y = toCellMin(cellIndex.y(), gridSize.y());
      cellBounds_out.min.z = toCellMin(cellIndex.z(), gridSize.z());
      cellBounds_out.max.x = toCellMax(cellIndex.x(), gridSize.x());
      cellBounds_out.max.y = toCellMax(cellIndex.y(), gridSize.y());
      cellBounds_out.max.z = toCellMax(cellIndex.z(), gridSize.z());
   }

   public static int toCellIndex(double position, double gridSize) {
      assert gridSize > 0.0;
      return (int)Math.floor(position / gridSize);
   }

   public static double toCellMin(int cellIndex, double gridSize) {
      return cellIndex * gridSize;
   }

   public static double toCellMax(int cellIndex, double gridSize) {
      return (cellIndex + 1) * gridSize;
   }

   @Nonnull
   public GridGraphCache.Stats getStats() {
      GridGraphCache.Stats stats = new GridGraphCache.Stats();
      stats.totalCallCount = this.stats.totalCallCount;
      stats.misses = this.stats.misses;
      return stats;
   }

   public static boolean isValidCellSize(@Nonnull Vector3dc value) {
      return value.x() > 0.0 && value.y() > 0.0 && value.z() > 0.0;
   }

   public static class Result {
      @Nullable
      public GraphSpace graph;
      public boolean isNew;
   }

   public static class Stats {
      public long totalCallCount = 0L;
      public long misses = 0L;

      private void reset() {
         this.totalCallCount = 0L;
         this.misses = 0L;
      }
   }
}
