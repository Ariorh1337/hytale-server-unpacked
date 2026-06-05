package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WeightedNodeAction extends NodeAction {
   @Nonnull
   private final WeightedMap<NodeAction> weightedNodeActions;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;

   public WeightedNodeAction(@Nonnull WeightedMap<NodeAction> weightedNodeActions, int seed) {
      this.weightedNodeActions = weightedNodeActions;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      if (this.weightedNodeActions.size() != 0) {
         int localSeed = this.rngField.get(node.position());
         this.random.setSeed(localSeed);
         this.weightedNodeActions.pick(this.random).run(graphSpace, node, bounds);
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      double[] maxRange = new double[]{0.0};
      this.weightedNodeActions.forEach((nodeAction, var4) -> maxRange[0] = Math.max(maxRange[0], nodeAction.getReadRange(longestConnection)));
      return maxRange[0];
   }

   @Override
   public double getCausalRangeIncrement() {
      double[] maxRange = new double[]{0.0};
      this.weightedNodeActions.forEach((nodeAction, var2) -> maxRange[0] = Math.max(maxRange[0], nodeAction.getCausalRangeIncrement()));
      return maxRange[0];
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.weightedNodeActions.forEach((nodeAction, var2) -> nodeAction.viewAllPossibleContent(consumer));
   }
}
