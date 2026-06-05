package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class NeighborNodesNodeSelector extends NodeSelector {
   public static final int ALL_NEIGHBORS_THRESHOLD = -1;
   @Nonnull
   private final NodeSelector nodeSelector;
   private final int selectedNeighborsThreshold;

   public NeighborNodesNodeSelector(@Nonnull NodeSelector nodeSelector, int selectedNeighborsThreshold) {
      this.nodeSelector = nodeSelector;
      this.selectedNeighborsThreshold = selectedNeighborsThreshold;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      int[] selectedNeighborsCount = new int[1];
      node.viewConnections((edge, control) -> {
         if (this.nodeSelector.isSelected(graphSpace, edge.otherNode(node))) {
            selectedNeighborsCount[0]++;
         }
      });
      return this.selectedNeighborsThreshold == -1
         ? selectedNeighborsCount[0] == node.getEdgesCount()
         : selectedNeighborsCount[0] >= this.selectedNeighborsThreshold;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return longestConnection + this.nodeSelector.getReadRange(longestConnection);
   }
}
