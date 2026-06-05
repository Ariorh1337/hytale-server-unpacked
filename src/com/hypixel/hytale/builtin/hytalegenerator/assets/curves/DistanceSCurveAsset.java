package com.hypixel.hytale.builtin.hytalegenerator.assets.curves;

import com.hypixel.hytale.builtin.hytalegenerator.math.InterpolatedCurve;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;

public class DistanceSCurveAsset extends CurveAsset {
   @Nonnull
   public static final BuilderCodec<DistanceSCurveAsset> CODEC = BuilderCodec.builder(
         DistanceSCurveAsset.class, DistanceSCurveAsset::new, CurveAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("ExponentA", Codec.DOUBLE, true), (asset, value) -> asset.exponentA = value, asset -> asset.exponentA)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("ExponentB", Codec.DOUBLE, true), (asset, value) -> asset.exponentB = value, asset -> asset.exponentB)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("Transition", Codec.DOUBLE, false), (asset, value) -> asset.transition = value, asset -> asset.transition)
      .addValidator(Validators.range(0.0, 1.0))
      .add()
      .<Double>append(new KeyedCodec<>("Range", Codec.DOUBLE, true), (asset, value) -> asset.range = value, asset -> asset.range)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .<Double>append(
         new KeyedCodec<>("TransitionSmooth", Codec.DOUBLE, false), (asset, value) -> asset.transitionSmooth = value, asset -> asset.transitionSmooth
      )
      .addValidator(Validators.range(0.0, 1.0))
      .add()
      .build();
   private double exponentA = 1.0;
   private double exponentB = 1.0;
   private double range = 1.0;
   private double transition = 1.0;
   private double transitionSmooth = 1.0;

   @Nonnull
   @Override
   public Double2DoubleFunction build() {
      Double2DoubleFunction functionA = in -> {
         if (in >= this.range) {
            return 0.0;
         }

         in /= this.range;
         in *= -1.0;
         return Math.pow(++in, this.exponentA);
      };
      Double2DoubleFunction functionB = in -> {
         if (in >= this.range) {
            return 0.0;
         }

         in /= this.range;
         in *= -1.0;
         return Math.pow(++in, this.exponentB);
      };
      double transitionDistance = this.transition * this.range;
      double positionA = this.range / 2.0 - transitionDistance / 2.0;
      double positionB = positionA + transitionDistance;
      return new InterpolatedCurve(positionA, positionB, this.transitionSmooth, functionA, functionB);
   }

   @Override
   public void cleanUp() {
   }
}
