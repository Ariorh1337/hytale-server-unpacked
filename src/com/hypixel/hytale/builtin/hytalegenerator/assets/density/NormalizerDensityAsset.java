package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.NormalizerDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NormalizerDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<NormalizerDensityAsset> CODEC = BuilderCodec.builder(
         NormalizerDensityAsset.class, NormalizerDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("FromMin", Codec.DOUBLE, true), (asset, value) -> asset.fromMin = value, asset -> asset.fromMin)
      .add()
      .append(new KeyedCodec<>("FromMax", Codec.DOUBLE, true), (asset, value) -> asset.fromMax = value, asset -> asset.fromMax)
      .add()
      .append(new KeyedCodec<>("ToMin", Codec.DOUBLE, true), (asset, value) -> asset.toMin = value, asset -> asset.toMin)
      .add()
      .append(new KeyedCodec<>("ToMax", Codec.DOUBLE, true), (asset, value) -> asset.toMax = value, asset -> asset.toMax)
      .add()
      .build();
   private double fromMin = -1.0;
   private double fromMax = 1.0;
   private double toMin = -1.0;
   private double toMax = 1.0;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return this.isSkipped()
         ? new ConstantValueDensity(0.0)
         : new NormalizerDensity(this.fromMin, this.fromMax, this.toMin, this.toMax, this.buildFirstInput(argument));
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
