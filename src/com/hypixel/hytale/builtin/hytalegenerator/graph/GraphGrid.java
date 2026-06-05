package com.hypixel.hytale.builtin.hytalegenerator.graph;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class GraphGrid {
   @Nonnull
   private static final Vector3d CELL_SIZE = new Vector3d(32.0, 320.0, 32.0);
   @Nonnull
   private static final Bounds3d CELL_BOUNDS = new Bounds3d(Vector3dUtil.ZERO, CELL_SIZE);
   @Nonnull
   private final GraphGenerator graphGenerator;
   private final double cellPadding;
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
   private final GraphGrid.Stats stats;

   public GraphGrid(@Nonnull GraphGenerator graphGenerator, double cellPadding, int capacity) {
      assert capacity >= 0;
      this.graphGenerator = graphGenerator;
      this.cellPadding = cellPadding;
      this.positionCellMap = new HashMap<>();
      this.hotCell = null;
      this.hotKey = new Vector3i();
      this.capacity = capacity;
      this.history = new ArrayDeque<>(capacity);
      this.stats = new GraphGrid.Stats();
   }

   public GraphSpace get(@Nonnull Vector3i position_cellGrid) {
      if (this.stats.totalCallCount == Long.MAX_VALUE) {
         this.stats.reset();
      }

      this.stats.totalCallCount++;
      if (this.hotCell != null && this.hotKey.equals(position_cellGrid)) {
         return this.hotCell;
      }

      GraphSpace cell = this.positionCellMap.get(position_cellGrid);
      this.hotKey.set(position_cellGrid);
      if (cell != null) {
         this.hotCell = cell;
         return cell;
      }

      this.stats.misses++;
      if (this.history.size() >= this.capacity) {
         Vector3i oldestKey = this.history.removeFirst();
         this.positionCellMap.remove(oldestKey);
      }

      Bounds3d localCellBounds_voxelGrid = this.getCellBounds_voxelGrid(position_cellGrid);
      cell = new GraphSpace();
      this.graphGenerator.generate(cell, localCellBounds_voxelGrid);
      this.hotCell = cell;
      this.positionCellMap.put(position_cellGrid, cell);
      this.history.addLast(position_cellGrid);
      return cell;
   }

   @Nonnull
   public Bounds3d getCellBounds_voxelGrid(@Nonnull Vector3i cellPosition_cellGrid) {
      Vector3d min = new Vector3d(cellPosition_cellGrid);
      toVoxelGrid_fromCellGrid(min);
      Vector3d max = new Vector3d(min).add(CELL_SIZE);
      Bounds3d cellBounds = new Bounds3d(min, max);
      cellBounds.expand(this.cellPadding);
      return cellBounds;
   }

   @Nonnull
   public GraphGenerator getGraphGenerator() {
      return this.graphGenerator;
   }

   @Nonnull
   public GraphGrid.Stats getStats() {
      GraphGrid.Stats stats = new GraphGrid.Stats();
      stats.totalCallCount = this.stats.totalCallCount;
      stats.misses = this.stats.misses;
      return stats;
   }

   private static void toVoxelGrid_fromCellGrid(@Nonnull Vector3d position_cellGrid) {
      position_cellGrid.mul(CELL_SIZE);
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
