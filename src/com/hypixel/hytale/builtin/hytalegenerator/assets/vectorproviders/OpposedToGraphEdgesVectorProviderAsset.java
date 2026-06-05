package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.OpposedToGraphEdgesVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class OpposedToGraphEdgesVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<OpposedToGraphEdgesVectorProviderAsset> CODEC = BuilderCodec.builder(
         OpposedToGraphEdgesVectorProviderAsset.class, OpposedToGraphEdgesVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      return this.isSkipped() ? new ConstantVectorProvider(new Vector3d()) : new OpposedToGraphEdgesVectorProvider();
   }
}
