package com.hypixel.hytale.builtin.hytalegenerator.graph;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.math.Calculator;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class GraphGenerator {
   public static final GraphGenerator EMPTY_INSTANCE = new GraphGenerator();
   private static final int TOTAL_PROBE_ID = 0;
   @Nonnull
   private final List<GraphPass> passes;
   @Nonnull
   private final Bounds3d[] passWorkBoundsFrame;
   @Nonnull
   private final Int2DoubleMap densityRadius;
   @Nonnull
   private final Int2DoubleMap materialRadius;
   @Nonnull
   private final Int2DoubleMap propDistributionRadius;
   @Nonnull
   private final Int2DoubleMap positionsRadius;
   @Nonnull
   private final PreciseTimeInstrument timeInstrument;
   @Nonnull
   private final String statsLabel;
   private final boolean printStats;
   private final int sampleCountTarget;

   private GraphGenerator() {
      this(List.of(), false, 0, "");
   }

   public GraphGenerator(@Nonnull List<GraphPass> passes, boolean printStats, int sampleCountTarget, @Nonnull String statsLabel) {
      assert sampleCountTarget >= 0;
      this.densityRadius = new Int2DoubleOpenHashMap();
      this.materialRadius = new Int2DoubleOpenHashMap();
      this.propDistributionRadius = new Int2DoubleOpenHashMap();
      this.positionsRadius = new Int2DoubleOpenHashMap();
      this.printStats = printStats;
      this.sampleCountTarget = sampleCountTarget;
      this.statsLabel = statsLabel;
      this.timeInstrument = new PreciseTimeInstrument(passes.size() + 1);
      this.timeInstrument.setProbeLabel(0, "Total");

      for (int i = 0; i < passes.size(); i++) {
         int probeIndex = i + 1;
         GraphPass pass = passes.get(i);
         String label = pass.getLabel();
         if (label.isEmpty()) {
            label = "Pass " + i;
         } else {
            label = "Pass " + label;
         }

         this.timeInstrument.setProbeLabel(probeIndex, label);
      }

      if (passes.isEmpty()) {
         this.passes = List.of();
         this.passWorkBoundsFrame = null;
      } else {
         this.passes = new ArrayList<>(passes);
         int finalPassIndex = this.passes.size() - 1;
         double[] longestConnection = new double[this.passes.size()];
         longestConnection[0] = this.passes.getFirst().getConnectionRangeIncrement();

         for (int i = 1; i < this.passes.size(); i++) {
            GraphPass pass = this.passes.get(i);
            longestConnection[i] = longestConnection[i - 1] + pass.getConnectionRangeIncrement();
         }

         this.passWorkBoundsFrame = new Bounds3d[passes.size()];
         this.passWorkBoundsFrame[finalPassIndex] = this.passes.getLast().getReadBounds(longestConnection[finalPassIndex]);

         for (int i = finalPassIndex - 1; i >= 0; i--) {
            GraphPass pass = this.passes.get(i);
            this.passWorkBoundsFrame[i] = pass.getReadBounds(longestConnection[i]).clone().stackOrAssign(this.passWorkBoundsFrame[i + 1]);
         }

         for (GraphPass pass : this.passes) {
            pass.viewAllPossibleContent(content -> {
               for (GraphSpace.ContentEntry<GraphSpace.DensityContent> entry : content.getDensityContent().getAll()) {
                  double currentRadius = this.densityRadius.getOrDefault(entry.id(), 0.0);
                  this.densityRadius.put(entry.id(), Calculator.max(entry.content().range, currentRadius, longestConnection[finalPassIndex]));
               }

               for (GraphSpace.ContentEntry<GraphSpace.MaterialContent> entry : content.getMaterialContent().getAll()) {
                  double currentRadius = this.materialRadius.getOrDefault(entry.id(), 0.0);
                  this.materialRadius.put(entry.id(), Calculator.max(entry.content().range, currentRadius, longestConnection[finalPassIndex]));
               }

               for (GraphSpace.ContentEntry<GraphSpace.PropDistributionContent> entry : content.getPropDistributionContent().getAll()) {
                  double currentRadius = this.propDistributionRadius.getOrDefault(entry.id(), 0.0);
                  this.propDistributionRadius.put(entry.id(), Calculator.max(entry.content().range, currentRadius, longestConnection[finalPassIndex]));
               }

               for (GraphSpace.ContentEntry<GraphSpace.PositionsContent> entry : content.getPositionsContent().getAll()) {
                  double currentRadius = this.positionsRadius.getOrDefault(entry.id(), 0.0);
                  this.positionsRadius.put(entry.id(), Calculator.max(entry.content().range, currentRadius, longestConnection[finalPassIndex]));
               }
            });
         }
      }
   }

   public void generate(@Nonnull GraphSpace graphSpace, @Nonnull Bounds3d resultBounds) {
      if (!this.passes.isEmpty()) {
         if (this.printStats) {
            this.timeInstrument.start(0);
         }

         Bounds3d workBounds = new Bounds3d();

         for (int i = 0; i < this.passes.size(); i++) {
            if (this.printStats) {
               this.timeInstrument.start(i + 1);
            }

            workBounds.assign(resultBounds);
            workBounds.stack(this.passWorkBoundsFrame[i]);
            GraphPass pass = this.passes.get(i);
            pass.run(graphSpace, workBounds);
            graphSpace.processTaskQueue();
            if (this.printStats) {
               this.timeInstrument.stop(i + 1);
            }
         }

         if (this.printStats) {
            this.timeInstrument.stop(0);
            this.timeInstrument.save();
            if (this.timeInstrument.getSampleCount() >= this.sampleCountTarget) {
               Thread thread = Thread.currentThread();
               String label = "[" + thread.getName() + "] GraphGenerator " + this.statsLabel + " Performance Report";
               String msg = this.timeInstrument.toString(label);
               this.timeInstrument.clear();
               msg = msg + "\nPass Work Bounds Frame (Blocks) :";

               for (int i = 0; i < this.passWorkBoundsFrame.length; i++) {
                  Vector3d size = this.passWorkBoundsFrame[i].getSize().div(2.0);
                  String passLabel = this.passes.get(i).getLabel();
                  if (passLabel.isEmpty()) {
                     passLabel = Integer.toString(i);
                  }

                  msg = msg + "\n\tPass " + passLabel + " : {x=" + size.x + ", y=" + size.y + ", z=" + size.z + "}";
               }

               LoggerUtil.getLogger().info(msg);
            }
         }
      }
   }

   public double getDensityContentRadius(int contentPayer) {
      return this.densityRadius.getOrDefault(contentPayer, 0.0);
   }

   public double getMaterialContentRadius(int contentLayer) {
      return this.materialRadius.getOrDefault(contentLayer, 0.0);
   }

   public double getPropDistributionRadius(int contentLayer) {
      return this.propDistributionRadius.getOrDefault(contentLayer, 0.0);
   }

   public double getPositionsRadius(int contentLayer) {
      return this.positionsRadius.getOrDefault(contentLayer, 0.0);
   }

   public void viewAllPossibleContent(@Nonnull Consumer<GraphSpace.Content> consumer) {
      for (GraphPass pass : this.passes) {
         pass.viewAllPossibleContent(consumer);
      }
   }
}
