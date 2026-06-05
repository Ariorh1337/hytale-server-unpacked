package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WhiteNoiseDensity extends Density {
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;

   public WhiteNoiseDensity(int seed) {
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
   }

   @Override
   public double process(@NonNullDecl Density.Context context) {
      int localSeed = this.rngField.get(context.position);
      this.random.setSeed(localSeed);
      return this.random.nextDouble() * 2.0 - 1.0;
   }
}
