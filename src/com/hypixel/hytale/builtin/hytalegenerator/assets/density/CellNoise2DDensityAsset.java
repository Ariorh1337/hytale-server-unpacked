package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiCacheDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.Noise2dDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.YOverrideDensity;
import com.hypixel.hytale.builtin.hytalegenerator.noise.CellNoiseField;
import com.hypixel.hytale.builtin.hytalegenerator.noise.FastNoiseLite;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class CellNoise2DDensityAsset extends DensityAsset {
   private static Set<String> validCellTypes = new HashSet<>();
   @Nonnull
   public static final BuilderCodec<CellNoise2DDensityAsset> CODEC;
   private double scaleX = 50.0;
   private double scaleZ = 50.0;
   private double jitter = 0.3;
   private int octaves = 1;
   private String seedKey = "";
   private FastNoiseLite.CellularReturnType cellType = FastNoiseLite.CellularReturnType.CellValue;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      }

      SeedBox childSeed = argument.parentSeed.child(this.seedKey);
      CellNoiseField noise = new CellNoiseField(childSeed.createSupplier().get(), this.scaleX, 1.0, this.scaleZ, this.jitter, this.octaves, this.cellType);
      Noise2dDensity noiseDensity = new Noise2dDensity(noise);
      Density cacheDensity = new MultiCacheDensity(noiseDensity, CacheDensityAsset.DEFAULT_CAPACITY);
      return new YOverrideDensity(cacheDensity, 0.0);
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }

   static {
      for (FastNoiseLite.CellularReturnType e : FastNoiseLite.CellularReturnType.values()) {
         validCellTypes.add(e.toString());
      }

      CODEC = BuilderCodec.builder(CellNoise2DDensityAsset.class, CellNoise2DDensityAsset::new, DensityAsset.ABSTRACT_CODEC)
         .append(new KeyedCodec<>("ScaleX", Codec.DOUBLE, true), (asset, value) -> asset.scaleX = value, asset -> asset.scaleX)
         .addValidator(Validators.greaterThan(0.0))
         .add()
         .<Double>append(new KeyedCodec<>("ScaleZ", Codec.DOUBLE, true), (asset, value) -> asset.scaleZ = value, asset -> asset.scaleZ)
         .addValidator(Validators.greaterThan(0.0))
         .add()
         .append(new KeyedCodec<>("Jitter", Codec.DOUBLE, true), (asset, value) -> asset.jitter = value, asset -> asset.scaleZ)
         .add()
         .<Integer>append(new KeyedCodec<>("Octaves", Codec.INTEGER, true), (asset, value) -> asset.octaves = value, asset -> asset.octaves)
         .addValidator(Validators.greaterThan(0))
         .add()
         .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seedKey = value, asset -> asset.seedKey)
         .add()
         .append(new KeyedCodec<>("CellType", FastNoiseLite.CellularReturnType.CODEC, true), (asset, value) -> asset.cellType = value, asset -> asset.cellType)
         .add()
         .build();
   }
}
