package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions.EdgeAction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ConnectedEdgesNodeAction extends NodeAction {
   @Nonnull
   private final EdgeAction edgeAction;

   public ConnectedEdgesNodeAction(@Nonnull EdgeAction edgeAction) {
      this.edgeAction = edgeAction;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      node.viewConnections((edge, control) -> this.edgeAction.run(graphSpace, edge, bounds));
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
