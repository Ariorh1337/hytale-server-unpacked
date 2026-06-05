package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class AllEdgeSelector extends EdgeSelector {
   public static final AllEdgeSelector INSTANCE = new AllEdgeSelector();

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge) {
      return true;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }
}
