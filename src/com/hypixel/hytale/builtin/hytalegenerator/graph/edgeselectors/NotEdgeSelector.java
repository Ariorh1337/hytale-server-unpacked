package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class NotEdgeSelector extends EdgeSelector {
   @Nonnull
   private final EdgeSelector edgeSelector;

   public NotEdgeSelector(@Nonnull EdgeSelector edgeSelector) {
      this.edgeSelector = edgeSelector;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge) {
      return !this.edgeSelector.isSelected(graphSpace, edge);
   }

   @Override
   public double getReadRange(double longestConnection) {
      return this.edgeSelector.getReadRange(longestConnection);
   }
}
