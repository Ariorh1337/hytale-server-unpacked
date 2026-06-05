package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiCacheDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.Noise2dDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.YOverrideDensity;
import com.hypixel.hytale.builtin.hytalegenerator.noise.SimplexNoiseField;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SimplexNoise2dDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<SimplexNoise2dDensityAsset> CODEC = BuilderCodec.builder(
         SimplexNoise2dDensityAsset.class, SimplexNoise2dDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Lacunarity", Codec.DOUBLE, true), (asset, value) -> asset.lacunarity = value, asset -> asset.lacunarity)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("Persistence", Codec.DOUBLE, true), (asset, value) -> asset.persistence = value, asset -> asset.persistence)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("Scale", Codec.DOUBLE, true), (asset, value) -> asset.scale = value, asset -> asset.scale)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .<Integer>append(new KeyedCodec<>("Octaves", Codec.INTEGER, true), (asset, value) -> asset.octaves = value, asset -> asset.octaves)
      .addValidator(Validators.greaterThan(0))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seedKey = value, asset -> asset.seedKey)
      .add()
      .build();
   private double lacunarity = 2.0;
   private double persistence = 0.5;
   private double scale = 50.0;
   private int octaves = 1;
   private String seedKey = "";

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      }

      SeedBox childSeed = argument.parentSeed.child(this.seedKey);
      SimplexNoiseField noise = SimplexNoiseField.builder()
         .withAmplitudeMultiplier(this.persistence)
         .withFrequencyMultiplier(this.lacunarity)
         .withScale(this.scale)
         .withSeed(childSeed.createSupplier().get().intValue())
         .withNumberOfOctaves(this.octaves)
         .build();
      Noise2dDensity noiseDensity = new Noise2dDensity(noise);
      Density cacheDensity = new MultiCacheDensity(noiseDensity, CacheDensityAsset.DEFAULT_CAPACITY);
      return new YOverrideDensity(cacheDensity, 0.0);
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
