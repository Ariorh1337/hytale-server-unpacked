package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.FastGradientWarpDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class FastGradientWarpDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<FastGradientWarpDensityAsset> CODEC = BuilderCodec.builder(
         FastGradientWarpDensityAsset.class, FastGradientWarpDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("WarpScale", Codec.FLOAT, false), (asset, value) -> asset.warpScale = value, asset -> asset.warpScale)
      .addValidator(Validators.greaterThan(0.0F))
      .add()
      .<Integer>append(new KeyedCodec<>("WarpOctaves", Codec.INTEGER, false), (asset, value) -> asset.warpOctaves = value, asset -> asset.warpOctaves)
      .addValidator(Validators.greaterThan(0))
      .add()
      .<Float>append(new KeyedCodec<>("WarpLacunarity", Codec.FLOAT, false), (asset, value) -> asset.warpLacunarity = value, asset -> asset.warpLacunarity)
      .addValidator(Validators.greaterThanOrEqual(0.0F))
      .add()
      .<Float>append(new KeyedCodec<>("WarpPersistence", Codec.FLOAT, false), (asset, value) -> asset.warpPersistence = value, asset -> asset.warpPersistence)
      .addValidator(Validators.greaterThanOrEqual(0.0F))
      .add()
      .<Float>append(new KeyedCodec<>("WarpFactor", Codec.FLOAT, false), (asset, value) -> asset.warpFactor = value, asset -> asset.warpFactor)
      .addValidator(Validators.greaterThanOrEqual(0.0F))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, false), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   private float warpLacunarity = 2.0F;
   private float warpPersistence = 0.5F;
   private int warpOctaves = 1;
   private float warpScale = 1.0F;
   private float warpFactor = 1.0F;
   private String seed = "A";

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      return this.isSkipped()
         ? new ConstantValueDensity(0.0)
         : new FastGradientWarpDensity(
            this.buildFirstInput(argument),
            this.warpLacunarity,
            this.warpPersistence,
            this.warpOctaves,
            1.0F / this.warpScale,
            this.warpFactor,
            argument.parentSeed.child(this.seed).createSupplier().get()
         );
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
