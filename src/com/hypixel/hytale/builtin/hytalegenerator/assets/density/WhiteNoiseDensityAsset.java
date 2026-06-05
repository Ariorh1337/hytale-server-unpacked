package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.WhiteNoiseDensity;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class WhiteNoiseDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<WhiteNoiseDensityAsset> CODEC = BuilderCodec.builder(
         WhiteNoiseDensityAsset.class, WhiteNoiseDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      }

      SeedBox childSeed = argument.parentSeed.child(this.seed);
      return new WhiteNoiseDensity(childSeed.createSupplier().get());
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
