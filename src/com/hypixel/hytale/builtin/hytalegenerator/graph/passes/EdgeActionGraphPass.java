package com.hypixel.hytale.builtin.hytalegenerator.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class EdgeActionGraphPass extends GraphPass {
   @Nonnull
   private final EdgeAction edgeAction;

   public EdgeActionGraphPass(@Nonnull EdgeAction edgeAction, @Nonnull String label) {
      super(label);
      this.edgeAction = edgeAction;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl Bounds3d runBounds) {
      graphSpace.viewEdges((edge, control) -> {
         if (runBounds.contains(edge.nodeA().position()) && runBounds.contains(edge.nodeB().position())) {
            this.edgeAction.run(graphSpace, edge, runBounds);
         }
      });
   }

   @NonNullDecl
   @Override
   public Bounds3d getReadBounds(double longestConnection) {
      double readRange = this.edgeAction.getReadRange(longestConnection);
      return getBounds(readRange);
   }

   @Override
   public double getConnectionRangeIncrement() {
      return this.edgeAction.getConnectionRangeIncrement();
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.edgeAction.viewAllPossibleContent(consumer);
   }

   private static double getRange(@Nonnull Bounds3d bounds) {
      return Math.min(
         Math.min(Math.abs(bounds.min.x), Math.min(Math.abs(bounds.min.y), Math.abs(bounds.min.z))),
         Math.min(Math.abs(bounds.max.x), Math.min(Math.abs(bounds.max.y), Math.abs(bounds.max.z)))
      );
   }

   @Nonnull
   private static Bounds3d getBounds(double range) {
      return new Bounds3d(new Vector3d(-range), new Vector3d(range));
   }
}
