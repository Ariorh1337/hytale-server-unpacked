package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGrid;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.PreciseTimeInstrument;
import com.hypixel.hytale.math.util.ChunkUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class GraphDensity extends Density {
   private static final int GRAPH_PROBE_ID = 0;
   private static final int DENSITY_PROBE_ID = 1;
   @Nonnull
   private final GraphGrid graphGrid;
   private final int contentLayerId;
   private final double backgroundDensity;
   private final double transitionSteepness;
   private final double transitionPoint;
   private final double contentRadius;
   @Nonnull
   private final List<GraphSpace.Node> rNodeResultList;
   private final boolean measureStats;
   @Nonnull
   private final String statsLabel;
   private final int statsSamplesCap;
   @Nonnull
   private final PreciseTimeInstrument timeInstrument;

   public GraphDensity(
      @Nonnull GraphGenerator graphGenerator,
      int contentLayerId,
      double backgroundValue,
      double transitionSteepness,
      double transitionPoint,
      boolean measureStats,
      @Nonnull String statsLabel,
      int statsSampleCount
   ) {
      assert statsSampleCount >= 0;
      int CACHE_CAPACITY = 1;
      this.contentLayerId = contentLayerId;
      this.graphGrid = new GraphGrid(graphGenerator, graphGenerator.getDensityContentRadius(contentLayerId), 1);
      this.backgroundDensity = backgroundValue;
      this.transitionSteepness = transitionSteepness;
      this.transitionPoint = transitionPoint;
      this.contentRadius = graphGenerator.getDensityContentRadius(contentLayerId);
      this.measureStats = measureStats;
      this.statsLabel = statsLabel;
      this.statsSamplesCap = statsSampleCount;
      this.timeInstrument = new PreciseTimeInstrument(2);
      this.timeInstrument.setProbeLabel(0, "Graph Retrieval");
      this.timeInstrument.setProbeLabel(1, "Density Generation");
      this.rNodeResultList = new ArrayList<>();
   }

   @Override
   public double process(@NonNullDecl Density.Context context) {
      if (this.measureStats) {
         this.timeInstrument.start(0);
      }

      Vector3i cellPosition = new Vector3i(ChunkUtil.chunkCoordinate(context.position.x), 0, ChunkUtil.chunkCoordinate(context.position.z));
      GraphSpace graph = this.graphGrid.get(cellPosition);
      if (this.measureStats) {
         this.timeInstrument.stop(0);
         this.timeInstrument.start(1);
      }

      List<GraphDensity.DensityDistanceEntry> densityDistanceEntries = new ArrayList<>();
      this.rNodeResultList.clear();
      graph.viewNodes(context.position, this.contentRadius, this.rNodeResultList);

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
      if (this.measureStats) {
         this.timeInstrument.stop(1);
         this.timeInstrument.save();
         if (this.timeInstrument.getSampleCount() >= this.statsSamplesCap) {
            Thread thread = Thread.currentThread();
            String label = "[" + thread.getName() + "] GraphDensity " + this.statsLabel + " Call Performance Report";
            String msg = this.timeInstrument.toString(label);
            this.timeInstrument.clear();
            GraphGrid.Stats graphGridStats = this.graphGrid.getStats();
            BigDecimal missRatio = BigDecimal.valueOf(graphGridStats.misses)
               .divide(BigDecimal.valueOf(graphGridStats.totalCallCount), new MathContext(6, RoundingMode.HALF_UP))
               .multiply(BigDecimal.valueOf(100L));
            msg = msg + "\nGraphGrid Total Call Count : " + graphGridStats.totalCallCount;
            msg = msg + "\nGraphGrid Cache Misses : " + graphGridStats.misses;
            msg = msg + "\nGraphGrid Miss/Total Ratio : " + missRatio.toString() + "%";
            LoggerUtil.getLogger().info(msg);
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
