package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ContentNodeSelector extends NodeSelector {
   @Nonnull
   private final Predicate<IntSet> contentPredicate;

   public ContentNodeSelector(@Nonnull Predicate<IntSet> contentPredicate) {
      this.contentPredicate = contentPredicate;
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      return this.contentPredicate.test(node.content().tagSet);
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }
}
