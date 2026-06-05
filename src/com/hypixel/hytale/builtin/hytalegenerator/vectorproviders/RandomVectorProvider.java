package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class RandomVectorProvider extends VectorProvider {
   private static final double FULL_CIRCLE = Math.PI * 2;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;

   public RandomVectorProvider(int seed) {
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
   }

   @Override
   public void process(@NonNullDecl VectorProvider.Context context, @NonNullDecl Vector3d vector_out) {
      int localSeed = this.rngField.get(context.position);
      this.random.setSeed(localSeed);
      vector_out.set(0.0, 1.0, 0.0);
      double angle = this.random.nextDouble();
      angle *= Math.PI * 2;
      vector_out.rotateX(angle);
      angle = this.random.nextDouble();
      angle *= Math.PI * 2;
      vector_out.rotateY(angle);
      angle = this.random.nextDouble();
      angle *= Math.PI * 2;
      vector_out.rotateZ(angle);
   }
}
