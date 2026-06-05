package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.PlaneProjectorVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class PlaneProjectorVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<PlaneProjectorVectorProviderAsset> CODEC = BuilderCodec.builder(
         PlaneProjectorVectorProviderAsset.class, PlaneProjectorVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("VectorProvider", VectorProviderAsset.CODEC, true),
         (asset, value) -> asset.vectorProviderAsset = value,
         asset -> asset.vectorProviderAsset
      )
      .add()
      .append(new KeyedCodec<>("PlaneA", VectorProviderAsset.CODEC, true), (asset, value) -> asset.planeAAsset = value, asset -> asset.planeAAsset)
      .add()
      .append(new KeyedCodec<>("PlaneB", VectorProviderAsset.CODEC, true), (asset, value) -> asset.planeBAsset = value, asset -> asset.planeBAsset)
      .add()
      .build();
   @Nonnull
   private VectorProviderAsset vectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private VectorProviderAsset planeAAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private VectorProviderAsset planeBAsset = ConstantVectorProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      VectorProvider vectorProvider = this.vectorProviderAsset.build(argument);
      VectorProvider planeA = this.planeAAsset.build(argument);
      VectorProvider planeB = this.planeBAsset.build(argument);
      return new PlaneProjectorVectorProvider(vectorProvider, planeA, planeB);
   }

   @Override
   public void cleanUp() {
      this.vectorProviderAsset.cleanUp();
      this.planeAAsset.cleanUp();
      this.planeBAsset.cleanUp();
   }
}
