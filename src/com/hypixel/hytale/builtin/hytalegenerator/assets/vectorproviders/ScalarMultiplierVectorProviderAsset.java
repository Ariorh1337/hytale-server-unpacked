package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ScalarMultiplierVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class ScalarMultiplierVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<ScalarMultiplierVectorProviderAsset> CODEC = BuilderCodec.builder(
         ScalarMultiplierVectorProviderAsset.class, ScalarMultiplierVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Scalar", DensityAsset.CODEC, true), (asset, value) -> asset.scalarAsset = value, asset -> asset.scalarAsset)
      .add()
      .append(
         new KeyedCodec<>("Vector", VectorProviderAsset.CODEC, true), (asset, value) -> asset.vectorProviderAsset = value, asset -> asset.vectorProviderAsset
      )
      .add()
      .build();
   @Nonnull
   private DensityAsset scalarAsset = new ConstantDensityAsset();
   @Nonnull
   private VectorProviderAsset vectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      Density scalar = this.scalarAsset.build(DensityAsset.from(argument));
      VectorProvider vectorProvider = this.vectorProviderAsset.build(argument);
      return new ScalarMultiplierVectorProvider(scalar, vectorProvider);
   }

   @Override
   public void cleanUp() {
      this.scalarAsset.cleanUp();
      this.vectorProviderAsset.cleanUp();
   }
}
