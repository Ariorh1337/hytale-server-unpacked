package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.VectorWarpDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class VectorWarpDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<VectorWarpDensityAsset> CODEC = BuilderCodec.builder(
         VectorWarpDensityAsset.class, VectorWarpDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("WarpFactor", Codec.DOUBLE, true), (asset, value) -> asset.warpFactor = value, asset -> asset.warpFactor)
      .add()
      .append(new KeyedCodec<>("WarpVector", Vector3dUtil.CODEC, true), (asset, value) -> asset.warpVector = value, asset -> asset.warpVector)
      .add()
      .build();
   private double warpFactor = 1.0;
   private Vector3d warpVector = new Vector3d();

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return this.isSkipped()
         ? new ConstantValueDensity(0.0)
         : new VectorWarpDensity(this.buildFirstInput(argument), this.buildSecondInput(argument), this.warpFactor, this.warpVector);
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
