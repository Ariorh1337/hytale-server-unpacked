package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class LengthEdgeSelector extends EdgeSelector {
   @Nonnull
   private final List<RangeDouble> delimitersLengthSqr;

   public LengthEdgeSelector(@Nonnull List<RangeDouble> delimiters) {
      this.delimitersLengthSqr = new ArrayList<>(delimiters.size());

      for (RangeDouble delimiter : delimiters) {
         RangeDouble delimiterSqr = new RangeDouble(delimiter.min() * delimiter.min(), delimiter.max() * delimiter.max());
         this.delimitersLengthSqr.add(delimiterSqr);
      }
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge) {
      Vector3d edgeVector = new Vector3d(edge.nodeA().position());
      edgeVector.sub(edge.nodeB().position());
      double lengthSqr = edgeVector.lengthSquared();

      for (RangeDouble delimiter : this.delimitersLengthSqr) {
         if (delimiter.contains(lengthSqr)) {
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
