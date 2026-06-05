package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class DirectionalJitterPositionProvider extends PositionProvider {
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
   private final Vector3d direction;
   private final boolean isBidirectional;
   @Nonnull
   private final Vector3d rVector;
   @Nonnull
   private final Bounds3d rBounds;
   @Nonnull
   private final PositionProvider.Context rChildContext;
   @Nonnull
   private PositionProvider.Context rContext;
   @Nonnull
   private final Pipe.One<Vector3d> rChildPipe = new Pipe.One<Vector3d>() {
      public void accept(@NonNullDecl Vector3d position, @NonNullDecl Control control) {
         int localSeed = DirectionalJitterPositionProvider.this.rngField.get(position.x, position.y, position.z);
         DirectionalJitterPositionProvider.this.random.setSeed(localSeed);
         double randomValue = DirectionalJitterPositionProvider.this.isBidirectional
            ? DirectionalJitterPositionProvider.this.random.nextDouble() * 2.0 - 1.0
            : DirectionalJitterPositionProvider.this.random.nextDouble();
         double radius = DirectionalJitterPositionProvider.this.magnitude * randomValue;
         DirectionalJitterPositionProvider.this.rVector.set(DirectionalJitterPositionProvider.this.direction).mul(radius);
         position.add(DirectionalJitterPositionProvider.this.rVector);
         if (DirectionalJitterPositionProvider.this.rContext.bounds.contains(position)) {
            DirectionalJitterPositionProvider.this.rContext.pipe.accept(position, control);
         }
      }
   };

   public DirectionalJitterPositionProvider(
      double magnitude, boolean isBidirectional, @Nonnull Vector3dc direction, int seed, @Nonnull PositionProvider positionProvider
   ) {
      this.magnitude = Math.abs(magnitude);
      this.isBidirectional = isBidirectional;
      this.positionProvider = positionProvider;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
      this.direction = new Vector3d(direction).normalize();
      this.rVector = new Vector3d();
      this.rBounds = new Bounds3d();
      this.rChildContext = new PositionProvider.Context();
      this.rContext = new PositionProvider.Context();
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      this.rContext = context;
      this.rBounds.assign(context.bounds);
      this.rBounds.min.add(-this.magnitude, 0.0, -this.magnitude);
      this.rBounds.max.add(this.magnitude, 0.0, this.magnitude);
      this.rChildContext.assign(context);
      this.rChildContext.bounds = this.rBounds;
      this.rChildContext.pipe = this.rChildPipe;
      this.positionProvider.generate(this.rChildContext);
   }
}
