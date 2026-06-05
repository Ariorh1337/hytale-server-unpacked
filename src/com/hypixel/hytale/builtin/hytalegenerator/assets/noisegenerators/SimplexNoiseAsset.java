package com.hypixel.hytale.builtin.hytalegenerator.assets.noisegenerators;

import com.hypixel.hytale.builtin.hytalegenerator.noise.SimplexNoiseField;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class SimplexNoiseAsset extends NoiseAsset {
   @Nonnull
   public static final BuilderCodec<SimplexNoiseAsset> CODEC = BuilderCodec.builder(SimplexNoiseAsset.class, SimplexNoiseAsset::new, NoiseAsset.ABSTRACT_CODEC)
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
   private double lacunarity = 1.0;
   private double persistence = 1.0;
   private double scale = 1.0;
   private int octaves = 1;
   private String seedKey = "A";

   @Nonnull
   public SimplexNoiseField build(@Nonnull SeedBox parentSeed) {
      SeedBox childSeed = parentSeed.child(this.seedKey);
      return SimplexNoiseField.builder()
         .withAmplitudeMultiplier(this.persistence)
         .withFrequencyMultiplier(this.lacunarity)
         .withScale(this.scale)
         .withSeed(childSeed.createSupplier().get().intValue())
         .withNumberOfOctaves(this.octaves)
         .build();
   }
}
