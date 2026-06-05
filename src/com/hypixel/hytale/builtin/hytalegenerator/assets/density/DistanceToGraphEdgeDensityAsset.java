package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.DistanceToGraphEdgeDensity;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class DistanceToGraphEdgeDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<DistanceToGraphEdgeDensityAsset> CODEC = BuilderCodec.builder(
         DistanceToGraphEdgeDensityAsset.class, DistanceToGraphEdgeDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return this.isSkipped() ? new ConstantValueDensity(0.0) : new DistanceToGraphEdgeDensity();
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
