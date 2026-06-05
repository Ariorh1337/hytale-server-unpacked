package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProjectorVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class VectorProjectorVectorProviderAsset extends VectorProviderAsset {
   @Nonnull
   public static final BuilderCodec<VectorProjectorVectorProviderAsset> CODEC = BuilderCodec.builder(
         VectorProjectorVectorProviderAsset.class, VectorProjectorVectorProviderAsset::new, VectorProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Source", VectorProviderAsset.CODEC, true), (asset, value) -> asset.sourceAsset = value, asset -> asset.sourceAsset)
      .add()
      .append(new KeyedCodec<>("Target", VectorProviderAsset.CODEC, true), (asset, value) -> asset.targetAsset = value, asset -> asset.targetAsset)
      .add()
      .build();
   @Nonnull
   private VectorProviderAsset sourceAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private VectorProviderAsset targetAsset = ConstantVectorProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      VectorProvider source = this.sourceAsset.build(argument);
      VectorProvider target = this.targetAsset.build(argument);
      return new VectorProjectorVectorProvider(source, target);
   }

   @Override
   public void cleanUp() {
      this.sourceAsset.cleanUp();
      this.targetAsset.cleanUp();
   }
}
