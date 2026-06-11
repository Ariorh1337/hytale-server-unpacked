package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.AdderVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class AdderVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<AdderVectorProviderAsset> CODEC = BuilderCodec.builder(
         AdderVectorProviderAsset.class, AdderVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Vectors", new ArrayCodec<>(VectorProviderAsset.CODEC, VectorProviderAsset[]::new), true),
         (asset, value) -> asset.vectorProviderAssets = value,
         asset -> asset.vectorProviderAssets
      )
      .add()
      .build();
   private VectorProviderAsset[] vectorProviderAssets = new VectorProviderAsset[0];

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      ArrayList<VectorProvider> vectorProviders = new ArrayList<>(this.vectorProviderAssets.length);

      for (VectorProviderAsset asset : this.vectorProviderAssets) {
         vectorProviders.add(asset.build(argument));
      }

      return new AdderVectorProvider(vectorProviders);
   }

   @Override
   public void cleanUp() {
      for (VectorProviderAsset vectorProviderAsset : this.vectorProviderAssets) {
         vectorProviderAsset.cleanUp();
      }
   }
}
