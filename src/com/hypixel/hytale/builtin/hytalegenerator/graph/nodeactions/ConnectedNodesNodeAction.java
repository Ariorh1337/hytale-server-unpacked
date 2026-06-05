package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ConnectedNodesNodeAction extends NodeAction {
   @Nonnull
   private final NodeAction nodeAction;

   public ConnectedNodesNodeAction(@Nonnull NodeAction nodeAction) {
      this.nodeAction = nodeAction;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      node.viewConnections((edge, control) -> this.nodeAction.run(graphSpace, edge.otherNode(node), bounds));
   }

   @Override
   public double getReadRange(double longestConnection) {
      return longestConnection + this.nodeAction.getReadRange(longestConnection);
   }

   @Override
   public double getCausalRangeIncrement() {
      return this.nodeAction.getCausalRangeIncrement();
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.nodeAction.viewAllPossibleContent(consumer);
   }
}
