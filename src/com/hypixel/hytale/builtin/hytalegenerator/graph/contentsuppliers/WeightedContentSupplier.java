package com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers;

import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WeightedContentSupplier extends ContentSupplier {
   @Nonnull
   private final WeightedMap<ContentSupplier> weightedMap;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;

   public WeightedContentSupplier(@Nonnull WeightedMap<ContentSupplier> weightedMap, int seed) {
      this.weightedMap = weightedMap;
      this.random = new FastRandom();
      this.rngField = new RngField(seed);
   }

   @NonNullDecl
   @Override
   public GraphSpace.Content get(@NonNullDecl GraphSpace.Node node) {
      if (this.weightedMap.size() == 0) {
         return GraphSpace.Content.DEFAULT;
      }

      int seed = this.rngField.get(node.position());
      this.random.setSeed(seed);
      return this.weightedMap.pick(this.random).get(node);
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      for (ContentSupplier contentSupplier : this.weightedMap.allElements()) {
         contentSupplier.viewAllPossibleContent(consumer);
      }
   }
}
