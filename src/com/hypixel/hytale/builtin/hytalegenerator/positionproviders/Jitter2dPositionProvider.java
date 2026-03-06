package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class Jitter2dPositionProvider extends PositionProvider {
   private static final double SEED_GENERATOR_RESOLUTION = 10.0;
   private final double magnitude;
   @Nonnull
   private final PositionProvider positionProvider;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;
   @Nonnull
   private final Vector3d rVector;
   @Nonnull
   private final Bounds3d rBounds;
   @Nonnull
   private final PositionProvider.Context rChildContext;

   public Jitter2dPositionProvider(double magnitude, int seed, @Nonnull PositionProvider positionProvider) {
      this.magnitude = Math.abs(magnitude);
      this.positionProvider = positionProvider;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
      this.rVector = new Vector3d();
      this.rBounds = new Bounds3d();
      this.rChildContext = new PositionProvider.Context();
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      this.rBounds.assign(context.bounds);
      this.rBounds.min.add(-this.magnitude, 0.0, -this.magnitude);
      this.rBounds.max.add(this.magnitude, 0.0, this.magnitude);
      this.rChildContext.assign(context);
      this.rChildContext.bounds = this.rBounds;
      this.rChildContext.pipe = (position, control) -> {
         int localSeed = this.rngField.get(position.x, position.y, position.z);
         this.random.setSeed(localSeed);
         double radius = this.magnitude * Math.sqrt(this.random.nextDouble());
         double theta = this.random.nextDouble() * 2.0 * Math.PI;
         this.rVector.assign(radius * Math.cos(theta), 0.0, radius * Math.sin(theta));
         position.add(this.rVector);
         if (context.bounds.contains(position)) {
            context.pipe.accept(position, control);
         }
      };
      this.positionProvider.generate(this.rChildContext);
   }
}
