package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class NotNodeSelector extends NodeSelector {
   @Nonnull
   private final NodeSelector nodeSelector;

   public NotNodeSelector(@Nonnull NodeSelector nodeSelector) {
      this.nodeSelector = nodeSelector;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      return !this.nodeSelector.isSelected(graphSpace, node);
   }

   @Override
   public double getReadRange(double longestConnection) {
      return this.nodeSelector.getReadRange(longestConnection);
   }
}
