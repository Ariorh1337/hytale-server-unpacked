package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SelectorEdgeAction extends EdgeAction {
   @Nonnull
   private final EdgeSelector edgeSelector;
   @Nonnull
   private final EdgeAction edgeAction;

   public SelectorEdgeAction(@Nonnull EdgeSelector edgeSelector, @Nonnull EdgeAction edgeAction) {
      this.edgeSelector = edgeSelector;
      this.edgeAction = edgeAction;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge, @NonNullDecl Bounds3d bounds) {
      if (this.edgeSelector.isSelected(graphSpace, edge)) {
         this.edgeAction.run(graphSpace, edge, bounds);
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return Math.max(this.edgeSelector.getReadRange(longestConnection), this.edgeAction.getReadRange(longestConnection));
   }

   @Override
   public double getConnectionRangeIncrement() {
      return this.edgeAction.getConnectionRangeIncrement();
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.edgeAction.viewAllPossibleContent(consumer);
   }
}
