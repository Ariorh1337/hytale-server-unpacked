package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ClampDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.SmoothClampDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SmoothClampDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<SmoothClampDensityAsset> CODEC = BuilderCodec.builder(
         SmoothClampDensityAsset.class, SmoothClampDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("WallA", Codec.DOUBLE, true), (asset, value) -> asset.wallA = value, asset -> asset.wallA)
      .add()
      .append(new KeyedCodec<>("WallB", Codec.DOUBLE, true), (asset, value) -> asset.wallB = value, asset -> asset.wallB)
      .add()
      .<Double>append(new KeyedCodec<>("Range", Codec.DOUBLE, true), (asset, value) -> asset.range = value, asset -> asset.range)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .build();
   private double wallA = -1.0;
   private double wallB = 1.0;
   private double range = 0.2;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      }

      if (this.range == 0.0) {
         return new ClampDensity(this.wallA, this.wallB, this.buildSecondInput(argument));
      }

      double min = Math.min(this.wallA, this.wallB);
      double max = Math.max(this.wallA, this.wallB);
      return new SmoothClampDensity(min, max, this.range, this.buildSecondInput(argument));
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
