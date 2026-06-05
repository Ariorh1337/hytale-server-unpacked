package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.GradientWarpDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class GradientWarpDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<GradientWarpDensityAsset> CODEC = BuilderCodec.builder(
         GradientWarpDensityAsset.class, GradientWarpDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("SampleRange", Codec.DOUBLE, false), (asset, value) -> asset.sampleRange = value, asset -> asset.sampleRange)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .append(new KeyedCodec<>("WarpFactor", Codec.DOUBLE, false), (asset, value) -> asset.warpFactor = value, asset -> asset.warpFactor)
      .add()
      .append(new KeyedCodec<>("2D", Codec.BOOLEAN, false), (asset, value) -> asset.is2d = value, asset -> asset.is2d)
      .add()
      .append(new KeyedCodec<>("YFor2D", Codec.DOUBLE, false), (asset, value) -> asset.y2d = value, asset -> asset.y2d)
      .add()
      .build();
   private double sampleRange = 1.0;
   private double warpFactor = 1.0;
   private boolean is2d;
   private double y2d;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return this.isSkipped()
         ? new ConstantValueDensity(0.0)
         : new GradientWarpDensity(this.buildFirstInput(argument), this.buildSecondInput(argument), this.sampleRange, this.warpFactor);
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
