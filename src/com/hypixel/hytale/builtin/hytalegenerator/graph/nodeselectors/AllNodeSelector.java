package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class AllNodeSelector extends NodeSelector {
   public static final AllNodeSelector INSTANCE = new AllNodeSelector();

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      return true;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }
}
