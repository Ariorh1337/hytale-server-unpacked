package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class NodesEdgeAction extends EdgeAction {
   @Nonnull
   private final NodeAction nodeAction;

   public NodesEdgeAction(@Nonnull NodeAction nodeAction) {
      this.nodeAction = nodeAction;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge, @NonNullDecl Bounds3d bounds) {
      this.nodeAction.run(graphSpace, edge.nodeA(), bounds);
      this.nodeAction.run(graphSpace, edge.nodeB(), bounds);
   }

   @Override
   public double getReadRange(double longestConnection) {
      return this.nodeAction.getReadRange(longestConnection);
   }

   @Override
   public double getConnectionRangeIncrement() {
      return this.nodeAction.getCausalRangeIncrement();
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.nodeAction.viewAllPossibleContent(consumer);
   }
}
