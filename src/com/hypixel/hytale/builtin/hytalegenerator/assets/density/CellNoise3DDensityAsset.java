package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.Noise3dDensity;
import com.hypixel.hytale.builtin.hytalegenerator.noise.CellNoiseField;
import com.hypixel.hytale.builtin.hytalegenerator.noise.FastNoiseLite;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class CellNoise3DDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<CellNoise3DDensityAsset> CODEC = BuilderCodec.builder(
         CellNoise3DDensityAsset.class, CellNoise3DDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("ScaleX", Codec.DOUBLE, true), (asset, value) -> asset.scaleX = value, asset -> asset.scaleX)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("ScaleY", Codec.DOUBLE, true), (asset, value) -> asset.scaleY = value, asset -> asset.scaleY)
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
   private double scaleX = 50.0;
   private double scaleY = 50.0;
   private double scaleZ = 50.0;
   private double jitter = 0.3;
   private int octaves = 1;
   private String seedKey = "";
   @Nonnull
   private FastNoiseLite.CellularReturnType cellType = FastNoiseLite.CellularReturnType.CellValue;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      }

      SeedBox childSeed = argument.parentSeed.child(this.seedKey);
      CellNoiseField noise = new CellNoiseField(
         childSeed.createSupplier().get(), this.scaleX, this.scaleY, this.scaleZ, this.jitter, this.octaves, this.cellType
      );
      return new Noise3dDensity(noise);
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
