package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.SmoothCeilingDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SmoothCeilingDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<SmoothCeilingDensityAsset> CODEC = BuilderCodec.builder(
         SmoothCeilingDensityAsset.class, SmoothCeilingDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Limit", Codec.DOUBLE, true), (asset, value) -> asset.limit = value, asset -> asset.limit)
      .add()
      .<Double>append(new KeyedCodec<>("SmoothRange", Codec.DOUBLE, true), (asset, value) -> asset.smoothRange = value, asset -> asset.smoothRange)
      .addValidator(Validators.greaterThanOrEqual(0.0))
      .add()
      .build();
   private double smoothRange = 0.2;
   private double limit;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return this.isSkipped() ? new ConstantValueDensity(0.0) : new SmoothCeilingDensity(this.limit, this.smoothRange, this.buildFirstInput(argument));
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
