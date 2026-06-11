package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.NormalizerVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class NormalizerVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<NormalizerVectorProviderAsset> CODEC = BuilderCodec.builder(
         NormalizerVectorProviderAsset.class, NormalizerVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Magnitude", DensityAsset.CODEC, true), (asset, value) -> asset.magnitudeAsset = value, asset -> asset.magnitudeAsset)
      .add()
      .append(
         new KeyedCodec<>("Vector", VectorProviderAsset.CODEC, true), (asset, value) -> asset.vectorProviderAsset = value, asset -> asset.vectorProviderAsset
      )
      .add()
      .build();
   @Nonnull
   private DensityAsset magnitudeAsset = new ConstantDensityAsset();
   @Nonnull
   private VectorProviderAsset vectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      Density density = this.magnitudeAsset.build(DensityAsset.from(argument));
      VectorProvider vectorProvider = this.vectorProviderAsset.build(argument);
      return new NormalizerVectorProvider(density, vectorProvider);
   }

   @Override
   public void cleanUp() {
      this.magnitudeAsset.cleanUp();
      this.vectorProviderAsset.cleanUp();
   }
}
