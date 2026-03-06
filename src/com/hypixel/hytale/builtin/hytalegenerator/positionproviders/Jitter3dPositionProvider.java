package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class Jitter3dPositionProvider extends PositionProvider {
   private static final float PI = (float) Math.PI;
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

   public Jitter3dPositionProvider(double magnitude, int seed, @Nonnull PositionProvider positionProvider) {
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
      this.rBounds.min.add(-this.magnitude);
      this.rBounds.max.add(this.magnitude);
      this.rChildContext.assign(context);
      this.rChildContext.bounds = this.rBounds;
      this.rChildContext.pipe = (position, control) -> {
         this.random.setSeed(this.rngField.get(position.x, position.y, position.z));
         double radius = this.magnitude * Math.sqrt(this.random.nextDouble());
         float rotationX = this.random.nextFloat() * 2.0F * (float) Math.PI;
         float rotationY = this.random.nextFloat() * 2.0F * (float) Math.PI;
         float rotationZ = this.random.nextFloat() * 2.0F * (float) Math.PI;
         this.rVector.assign(radius, 0.0, 0.0);
         this.rVector.rotateX(rotationX);
         this.rVector.rotateY(rotationY);
         this.rVector.rotateZ(rotationZ);
         position.add(this.rVector);
         if (context.bounds.contains(position)) {
            context.pipe.accept(position, control);
         }
      };
      this.positionProvider.generate(this.rChildContext);
   }
}
