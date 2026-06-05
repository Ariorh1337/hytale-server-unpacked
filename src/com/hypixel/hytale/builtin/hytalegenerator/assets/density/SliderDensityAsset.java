package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.SliderDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class SliderDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<SliderDensityAsset> CODEC = BuilderCodec.builder(
         SliderDensityAsset.class, SliderDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("SlideX", Codec.DOUBLE, false), (asset, value) -> asset.slideX = value, asset -> asset.slideX)
      .add()
      .append(new KeyedCodec<>("SlideY", Codec.DOUBLE, false), (asset, value) -> asset.slideY = value, asset -> asset.slideY)
      .add()
      .append(new KeyedCodec<>("SlideZ", Codec.DOUBLE, false), (asset, value) -> asset.slideZ = value, asset -> asset.slideZ)
      .add()
      .build();
   private double slideX = 0.0;
   private double slideY = 0.0;
   private double slideZ = 0.0;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return this.isSkipped() ? new ConstantValueDensity(0.0) : new SliderDensity(this.slideX, this.slideY, this.slideZ, this.buildFirstInput(argument));
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
