package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GridGraphCache;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

public class GraphDensity extends Density {
   private static final int GRAPH_PROBE_ID = 0;
   private static final int DENSITY_PROBE_ID = 1;
   @Nonnull
   private final GridGraphCache gridGraphCache;
   @Nonnull
   private final GraphGenerator graphGenerator;
   private final int contentLayerId;
   private final double backgroundDensity;
   private final double transitionSteepness;
   private final double transitionPoint;
   private final double contentRadius;
   @Nonnull
   private final List<GraphSpace.Node> rNodeResultList;
   private final boolean printStats;
   @Nonnull
   private final String statsLabel;
   private final int statsSamplesCap;
   private int statsSampleCount;

   public GraphDensity(
      @Nonnull GraphGenerator graphGenerator,
      int contentLayerId,
      double backgroundValue,
      double transitionSteepness,
      double transitionPoint,
      boolean printStats,
      @Nonnull String statsLabel,
      int statsSampleCount,
      @Nonnull Vector3dc cacheCellSize,
      int cacheCapacity
   ) {
      assert statsSampleCount >= 0;
      assert GridGraphCache.isValidCellSize(cacheCellSize);
      assert cacheCapacity >= 0;
      this.contentLayerId = contentLayerId;
      this.gridGraphCache = new GridGraphCache(cacheCellSize, cacheCapacity);
      this.graphGenerator = graphGenerator;
      this.backgroundDensity = backgroundValue;
      this.transitionSteepness = transitionSteepness;
      this.transitionPoint = transitionPoint;
      this.contentRadius = graphGenerator.getDensityContentRadius(contentLayerId);
      this.printStats = printStats;
      this.statsLabel = statsLabel;
      this.statsSamplesCap = statsSampleCount;
      this.statsSampleCount = 0;
      this.rNodeResultList = new ArrayList<>();
   }

   @Override
   public double process(@NonNullDecl Density.Context context) {
      Vector3i cellIndex = new Vector3i();
      this.gridGraphCache.toCellIndex(context.position, cellIndex);
      GridGraphCache.Result result = new GridGraphCache.Result();
      this.gridGraphCache.getCell(cellIndex, result);
      Bounds3d localCellBounds_voxelGrid = new Bounds3d();
      this.gridGraphCache.toCellBounds(cellIndex, localCellBounds_voxelGrid);
      localCellBounds_voxelGrid.expand(this.contentRadius);
      if (result.isNew) {
         this.graphGenerator.generate(result.graph, localCellBounds_voxelGrid);
      }

      List<GraphDensity.DensityDistanceEntry> densityDistanceEntries = new ArrayList<>();
      this.rNodeResultList.clear();
      result.graph.viewNodes(context.position, this.contentRadius, this.rNodeResultList);

      for (GraphSpace.Node node : this.rNodeResultList) {
         GraphSpace.Content content = node.content();
         GraphSpace.DensityContent densityContent = content.getDensityContent().get(this.contentLayerId);
         if (densityContent != null) {
            double distance = node.position().distance(context.position);
            if (!(distance > densityContent.range)) {
               densityDistanceEntries.add(new GraphDensity.DensityDistanceEntry(node, distance, densityContent));
            }
         }
      }

      if (densityDistanceEntries.isEmpty()) {
         this.rNodeResultList.clear();
         return this.backgroundDensity;
      }

      double interpolatedDensity = 0.0;
      double totalFactor = 0.0;

      for (GraphDensity.DensityDistanceEntry entry : densityDistanceEntries) {
         Density.Context nodeContext = new Density.Context(context);
         nodeContext.graphNode = entry.node;
         nodeContext.densityAnchor = new Vector3d(entry.node.position());
         double densityValue = entry.densityContent.density.process(nodeContext);
         double factor = entry.densityContent.range - entry.distance;
         factor /= entry.densityContent.range;
         factor = sigmoid(factor, this.transitionSteepness, this.transitionPoint);
         interpolatedDensity += densityValue * factor;
         totalFactor += factor;
      }

      interpolatedDensity /= totalFactor;
      if (this.printStats) {
         this.statsSampleCount++;
         if (this.statsSampleCount >= this.statsSamplesCap) {
            Thread thread = Thread.currentThread();
            String msg = "[" + thread.getName() + "] GraphDensity " + this.statsLabel + " Call Performance Report";
            GridGraphCache.Stats graphGridStats = this.gridGraphCache.getStats();
            BigDecimal missRatio = BigDecimal.valueOf(graphGridStats.misses)
               .divide(BigDecimal.valueOf(graphGridStats.totalCallCount), new MathContext(5, RoundingMode.HALF_UP))
               .multiply(BigDecimal.valueOf(100L));
            msg = msg + "\n\tGraphGrid Total Call Count : " + graphGridStats.totalCallCount;
            msg = msg + "\n\tGraphGrid Cache Misses : " + graphGridStats.misses;
            msg = msg + "\n\tGraphGrid Miss/Total Ratio : " + missRatio.toString() + "%";
            LoggerUtil.getLogger().info(msg);
            this.statsSampleCount = 0;
         }
      }

      this.rNodeResultList.clear();
      return interpolatedDensity;
   }

   public static double sigmoid(double x, double steepness, double offset) {
      double scaledDelta = (x - offset) * 20.0;
      return 1.0 / (1.0 + Math.exp(-steepness * scaledDelta));
   }

   public static double sigmoid(double x) {
      return 1.0 / (1.0 + Math.exp(10.0 - x * 20.0));
   }

   private static class DensityDistanceEntry {
      @Nonnull
      final GraphSpace.Node node;
      final double distance;
      final GraphSpace.DensityContent densityContent;

      DensityDistanceEntry(@Nonnull GraphSpace.Node node, double distance, @Nonnull GraphSpace.DensityContent densityContent) {
         this.node = node;
         this.distance = distance;
         this.densityContent = densityContent;
      }
   }
}
