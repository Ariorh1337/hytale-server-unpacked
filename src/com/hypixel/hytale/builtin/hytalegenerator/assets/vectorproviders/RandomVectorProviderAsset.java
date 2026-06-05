package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.RandomVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class RandomVectorProviderAsset extends VectorProviderAsset {
   public static final VectorProviderAsset INSTANCE = new RandomVectorProviderAsset();
   @Nonnull
   public static final BuilderCodec<RandomVectorProviderAsset> CODEC = BuilderCodec.builder(
         RandomVectorProviderAsset.class, RandomVectorProviderAsset::new, ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .build();
   @Nonnull
   private String seed = "";

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      int seedInt = argument.parentSeed.child(this.seed).createSupplier().get();
      return new RandomVectorProvider(seedInt);
   }
}
