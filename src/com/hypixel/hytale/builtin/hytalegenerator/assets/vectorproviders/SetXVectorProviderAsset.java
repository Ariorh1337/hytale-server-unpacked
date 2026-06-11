package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.SetXVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SetXVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<SetXVectorProviderAsset> CODEC = BuilderCodec.builder(
         SetXVectorProviderAsset.class, SetXVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Value", DensityAsset.CODEC, true), (asset, value) -> asset.valueAsset = value, asset -> asset.valueAsset)
      .add()
      .append(
         new KeyedCodec<>("Vector", VectorProviderAsset.CODEC, true), (asset, value) -> asset.vectorProviderAsset = value, asset -> asset.vectorProviderAsset
      )
      .add()
      .build();
   @Nonnull
   private DensityAsset valueAsset = new ConstantDensityAsset();
   @Nonnull
   private VectorProviderAsset vectorProviderAsset = ConstantVectorProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      Density value = this.valueAsset.build(DensityAsset.from(argument));
      VectorProvider vectorProvider = this.vectorProviderAsset.build(argument);
      return new SetXVectorProvider(value, vectorProvider);
   }

   @Override
   public void cleanUp() {
      this.valueAsset.cleanUp();
      this.vectorProviderAsset.cleanUp();
   }
}
