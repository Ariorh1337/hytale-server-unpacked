package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ConnectionCountNodeSelector extends NodeSelector {
   @Nonnull
   private final IntSet countsSet;

   public ConnectionCountNodeSelector(@Nonnull List<Integer> counts) {
      this.countsSet = new IntOpenHashSet(counts);
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      return this.countsSet.contains(node.getEdgesCount());
   }

   @Override
   public double getReadRange(double longestConnection) {
      return longestConnection;
   }
}
