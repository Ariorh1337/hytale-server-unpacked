package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class NotNodeSelector extends NodeSelector {
   @Nonnull
   private final NodeSelector edgeSelector;

   public NotNodeSelector(@Nonnull NodeSelector edgeSelector) {
      this.edgeSelector = edgeSelector;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      return !this.edgeSelector.isSelected(graphSpace, node);
   }

   @Override
   public double getReadRange(double longestConnection) {
      return this.edgeSelector.getReadRange(longestConnection);
   }
}
