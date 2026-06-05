package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class AndNodeSelector extends NodeSelector {
   @Nonnull
   private final List<NodeSelector> edgeSelectors;

   public AndNodeSelector(@Nonnull List<NodeSelector> edgeSelector) {
      this.edgeSelectors = new ArrayList<>(edgeSelector);
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      for (NodeSelector edgeSelector : this.edgeSelectors) {
         if (!edgeSelector.isSelected(graphSpace, node)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public double getReadRange(double longestConnection) {
      double maxReadRange = 0.0;

      for (NodeSelector edgeSelector : this.edgeSelectors) {
         maxReadRange = Math.max(maxReadRange, edgeSelector.getReadRange(longestConnection));
      }

      return maxReadRange;
   }
}
