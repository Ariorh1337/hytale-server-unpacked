package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ContentCopyNodeAction extends NodeAction {
   @Nonnull
   private final Predicate<IntSet> contentPredicate;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;

   public ContentCopyNodeAction(@Nonnull Predicate<IntSet> contentTagPredicate, int seed) {
      this.contentPredicate = contentTagPredicate;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      int edgesCount = node.getEdgesCount();
      if (edgesCount != 0) {
         List<GraphSpace.Content> contentPool = new ArrayList<>(edgesCount);
         node.viewConnections((edge, control) -> {
            GraphSpace.Node neighbour = edge.otherNode(node);
            if (this.contentPredicate.test(neighbour.content().tagSet)) {
               contentPool.add(neighbour.content());
            }
         });
         if (!contentPool.isEmpty()) {
            int localSeed = this.rngField.get(node.position());
            this.random.setSeed(localSeed);
            int index = this.random.nextInt(contentPool.size());
            node.setContent(contentPool.get(index));
         }
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return longestConnection;
   }

   @Override
   public double getCausalRangeIncrement() {
      return 0.0;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
   }
}
