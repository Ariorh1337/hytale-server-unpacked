package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class AngleEdgeSelector extends EdgeSelector {
   @Nonnull
   private final VectorProvider vectorProvider;
   @Nonnull
   private final List<RangeDouble> delimiters;

   public AngleEdgeSelector(@Nonnull VectorProvider vectorProvider, @Nonnull List<RangeDouble> delimiters) {
      this.vectorProvider = vectorProvider;
      this.delimiters = delimiters;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge) {
      Vector3d edgeVector = new Vector3d(edge.nodeA().position());
      edgeVector.sub(edge.nodeB().position());
      Vector3d edgeMiddlePosition = new Vector3d(edge.nodeA().position());
      edgeMiddlePosition.add(edge.nodeB().position());
      edgeMiddlePosition.mul(0.5);
      Vector3d referenceVector = new Vector3d();
      this.vectorProvider.process(new VectorProvider.Context(edgeMiddlePosition, null, null), referenceVector);
      double angle = Math.toDegrees(referenceVector.angle(edgeVector));
      if (angle > 90.0) {
         angle = 180.0 - angle;
      }

      for (RangeDouble delimiter : this.delimiters) {
         if (delimiter.contains(angle)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return longestConnection;
   }
}
