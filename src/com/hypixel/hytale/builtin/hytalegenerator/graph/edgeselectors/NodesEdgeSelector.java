package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors.NodeSelector;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class NodesEdgeSelector extends EdgeSelector {
   @Nonnull
   private final NodeSelector nodeSelector;
   @Nonnull
   private final NodesEdgeSelector.Operator operator;

   public NodesEdgeSelector(@Nonnull NodeSelector nodeSelector, @Nonnull NodesEdgeSelector.Operator operator) {
      this.nodeSelector = nodeSelector;
      this.operator = operator;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge) {
      Vector3d edgePosition = new Vector3d(edge.nodeA().position()).add(edge.nodeB().position());
      return this.operator == NodesEdgeSelector.Operator.OR
         ? this.nodeSelector.isSelected(graphSpace, edge.nodeA()) || this.nodeSelector.isSelected(graphSpace, edge.nodeB())
         : this.nodeSelector.isSelected(graphSpace, edge.nodeA()) && this.nodeSelector.isSelected(graphSpace, edge.nodeB());
   }

   @Override
   public double getReadRange(double longestConnection) {
      return this.nodeSelector.getReadRange(longestConnection);
   }

   public enum Operator {
      AND,
      OR;
   }
}
