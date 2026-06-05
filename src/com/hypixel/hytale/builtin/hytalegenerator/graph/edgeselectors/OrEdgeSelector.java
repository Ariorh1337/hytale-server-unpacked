package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OrEdgeSelector extends EdgeSelector {
   @Nonnull
   private final List<EdgeSelector> edgeSelectors;

   public OrEdgeSelector(@Nonnull List<EdgeSelector> edgeSelector) {
      this.edgeSelectors = new ArrayList<>(edgeSelector);
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge) {
      for (EdgeSelector edgeSelector : this.edgeSelectors) {
         if (edgeSelector.isSelected(graphSpace, edge)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public double getReadRange(double longestConnection) {
      double maxReadRange = 0.0;

      for (EdgeSelector edgeSelector : this.edgeSelectors) {
         maxReadRange = Math.max(maxReadRange, edgeSelector.getReadRange(longestConnection));
      }

      return maxReadRange;
   }
}
