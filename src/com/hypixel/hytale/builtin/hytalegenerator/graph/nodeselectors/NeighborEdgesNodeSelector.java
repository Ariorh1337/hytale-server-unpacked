package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors.EdgeSelector;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class NeighborEdgesNodeSelector extends NodeSelector {
   public static final int ALL_NEIGHBORS_THRESHOLD = -1;
   @Nonnull
   private final EdgeSelector edgeSelector;
   private final int selectedNeighborsThreshold;

   public NeighborEdgesNodeSelector(@Nonnull EdgeSelector edgeSelector, int selectedNeighborsThreshold) {
      this.edgeSelector = edgeSelector;
      this.selectedNeighborsThreshold = selectedNeighborsThreshold;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      int[] selectedNeighborsCount = new int[1];
      node.viewConnections((edge, control) -> {
         if (this.edgeSelector.isSelected(graphSpace, edge)) {
            selectedNeighborsCount[0]++;
         }
      });
      return this.selectedNeighborsThreshold == -1
         ? selectedNeighborsCount[0] == node.getEdgesCount()
         : selectedNeighborsCount[0] >= this.selectedNeighborsThreshold;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return longestConnection + this.edgeSelector.getReadRange(longestConnection);
   }
}
