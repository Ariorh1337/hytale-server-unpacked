package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class DensityDelimitedNodeSelector extends NodeSelector {
   @Nonnull
   private final Density density;
   @Nonnull
   private final List<RangeDouble> delimiters;

   public DensityDelimitedNodeSelector(@Nonnull Density density, @Nonnull List<RangeDouble> delimiters) {
      this.density = density;
      this.delimiters = new ArrayList<>(delimiters);
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      Density.Context densityContext = new Density.Context();
      densityContext.position = new Vector3d(node.position());
      densityContext.graphNode = node;
      double densityValue = this.density.process(densityContext);

      for (RangeDouble delimiter : this.delimiters) {
         if (delimiter.contains(densityValue)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }
}
