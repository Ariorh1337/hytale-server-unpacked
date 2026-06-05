package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SelectorNodeAction extends NodeAction {
   @Nonnull
   private final NodeSelector nodeSelector;
   @Nonnull
   private final NodeAction nodeAction;

   public SelectorNodeAction(@Nonnull NodeSelector nodeSelector, @Nonnull NodeAction nodeAction) {
      this.nodeSelector = nodeSelector;
      this.nodeAction = nodeAction;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      if (this.nodeSelector.isSelected(graphSpace, node)) {
         this.nodeAction.run(graphSpace, node, bounds);
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return Math.max(this.nodeSelector.getReadRange(longestConnection), this.nodeAction.getReadRange(longestConnection));
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
