package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3dc;

public class RandomNodeSelector extends NodeSelector {
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;
   private final double chance;

   public RandomNodeSelector(double chance, int seed) {
      this.chance = chance;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
   }

   @Override
   public boolean isSelected(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node) {
      Vector3dc nodePosition = node.position();
      int localSeed = this.rngField.get(nodePosition.x(), nodePosition.y(), nodePosition.z());
      this.random.setSeed(localSeed);
      double gauge = this.random.nextDouble();
      return gauge < this.chance;
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }
}
