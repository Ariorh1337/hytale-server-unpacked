package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngUtil;
import com.hypixel.hytale.math.util.FastRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SomeConnectedEdgesNodeAction extends NodeAction {
   @Nonnull
   private final EdgeAction edgeAction;
   private final double ratio;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;

   public SomeConnectedEdgesNodeAction(@Nonnull EdgeAction edgeAction, double ratio, int seed) {
      assert ratio >= 0.0;
      this.edgeAction = edgeAction;
      this.ratio = Math.max(0.0, ratio);
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      if (!node.edges().isEmpty()) {
         List<GraphSpace.Edge> edges = new ArrayList<>(node.edges());
         int count = (int)(edges.size() * this.ratio);
         int seed = this.rngField.get(node.position());
         this.random.setSeed(seed);
         List<GraphSpace.Edge> pickedEdges = new ArrayList<>(count);
         edges.sort(Comparator.comparingInt(edgex -> edgex.otherNode(node).hashCode()));
         RngUtil.pickElements(this.random, count, edges, pickedEdges);

         for (GraphSpace.Edge edge : pickedEdges) {
            this.edgeAction.run(graphSpace, edge, bounds);
         }
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return Math.max(longestConnection, this.edgeAction.getReadRange(longestConnection));
   }

   @Override
   public double getCausalRangeIncrement() {
      return this.edgeAction.getConnectionRangeIncrement();
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.edgeAction.viewAllPossibleContent(consumer);
   }
}
