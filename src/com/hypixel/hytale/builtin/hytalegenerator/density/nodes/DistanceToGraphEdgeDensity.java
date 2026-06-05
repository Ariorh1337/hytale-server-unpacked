package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import javax.annotation.Nonnull;

public class DistanceToGraphEdgeDensity extends Density {
   @Override
   public double process(@Nonnull Density.Context context) {
      if (context.graphNode == null) {
         return Double.MAX_VALUE;
      }

      double[] smallestDistanceToSegment = new double[]{Double.MAX_VALUE};
      context.graphNode.viewConnections((edge, control) -> {
         GraphSpace.Node otherNode = edge.otherNode(context.graphNode);
         double distanceToSegment = VectorUtil.distanceToSegment3d(context.position, context.graphNode.position(), otherNode.position());
         smallestDistanceToSegment[0] = Math.min(distanceToSegment, smallestDistanceToSegment[0]);
      });
      return smallestDistanceToSegment[0];
   }
}
