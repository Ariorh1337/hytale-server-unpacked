package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.SubtracterVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SubtracterVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<SubtracterVectorProviderAsset> CODEC = BuilderCodec.builder(
         SubtracterVectorProviderAsset.class, SubtracterVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("VectorProviderA", VectorProviderAsset.CODEC, true),
         (asset, value) -> asset.vectorProviderAssetA = value,
         asset -> asset.vectorProviderAssetA
      )
      .add()
      .append(
         new KeyedCodec<>("VectorProviderB", VectorProviderAsset.CODEC, true),
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
      return new SubtracterVectorProvider(vectorProviderA, vectorProviderB);
   }

   @Override
   public void cleanUp() {
      this.vectorProviderAssetA.cleanUp();
      this.vectorProviderAssetB.cleanUp();
   }
}
