package com.hypixel.hytale.builtin.hytalegenerator.assets.pointgenerators;

import com.hypixel.hytale.builtin.hytalegenerator.noise.pointprovider.JitterPointField;
import com.hypixel.hytale.builtin.hytalegenerator.noise.pointprovider.PointProvider;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class MeshPointGeneratorAsset extends PointGeneratorAsset {
   @Nonnull
   public static final BuilderCodec<MeshPointGeneratorAsset> CODEC = BuilderCodec.builder(
         MeshPointGeneratorAsset.class, MeshPointGeneratorAsset::new, PointGeneratorAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Jitter", Codec.DOUBLE, true), (asset, value) -> asset.jitter = value, asset -> asset.jitter)
      .addValidator(Validators.range(0.0, 0.5))
      .add()
      .<Double>append(new KeyedCodec<>("ScaleX", Codec.DOUBLE, true), (asset, value) -> asset.scaleX = value, asset -> asset.scaleX)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("ScaleY", Codec.DOUBLE, true), (asset, value) -> asset.scaleY = value, asset -> asset.scaleY)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .<Double>append(new KeyedCodec<>("ScaleZ", Codec.DOUBLE, true), (asset, value) -> asset.scaleZ = value, asset -> asset.scaleZ)
      .addValidator(Validators.greaterThan(0.0))
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seedKey = value, asset -> asset.seedKey)
      .add()
      .build();
   private double jitter = 0.35;
   private double scaleX = 40.0;
   private double scaleY = 40.0;
   private double scaleZ = 40.0;
   private String seedKey = "A";

   @Override
   public PointProvider build(@Nonnull SeedBox parentSeed) {
      SeedBox childSeed = parentSeed.child(this.seedKey);
      return new JitterPointField(childSeed.createSupplier().get(), this.jitter).setScale(this.scaleX, this.scaleY, this.scaleZ, 1.0);
   }
}
