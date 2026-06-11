package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.CrossVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class CrossVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<CrossVectorProviderAsset> CODEC = BuilderCodec.builder(
         CrossVectorProviderAsset.class, CrossVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("VectorA", VectorProviderAsset.CODEC, true),
         (asset, value) -> asset.vectorProviderAssetA = value,
         asset -> asset.vectorProviderAssetA
      )
      .add()
      .append(
         new KeyedCodec<>("VectorB", VectorProviderAsset.CODEC, true),
         (asset, value) -> asset.vectorProviderAssetB = value,
         asset -> asset.vectorProviderAssetB
      )
      .add()
      .build();
   @Nonnull
   private VectorProviderAsset vectorProviderAssetA = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private VectorProviderAsset vectorProviderAssetB = ConstantVectorProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      VectorProvider vectorProviderA = this.vectorProviderAssetA.build(argument);
      VectorProvider vectorProviderB = this.vectorProviderAssetB.build(argument);
      return new CrossVectorProvider(vectorProviderA, vectorProviderB);
   }

   @Override
   public void cleanUp() {
      this.vectorProviderAssetA.cleanUp();
      this.vectorProviderAssetB.cleanUp();
   }
}
