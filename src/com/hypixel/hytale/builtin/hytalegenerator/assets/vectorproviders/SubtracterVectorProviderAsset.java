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
      .append(new KeyedCodec<>("Minuend", VectorProviderAsset.CODEC, true), (asset, value) -> asset.minuendAsset = value, asset -> asset.minuendAsset)
      .add()
      .append(new KeyedCodec<>("Subtrahend", VectorProviderAsset.CODEC, true), (asset, value) -> asset.subtrahendAsset = value, asset -> asset.subtrahendAsset)
      .add()
      .build();
   @Nonnull
   private VectorProviderAsset minuendAsset = ConstantVectorProviderAsset.INSTANCE;
   @Nonnull
   private VectorProviderAsset subtrahendAsset = ConstantVectorProviderAsset.INSTANCE;

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantVectorProvider(new Vector3d());
      }

      VectorProvider minuend = this.minuendAsset.build(argument);
      VectorProvider subtrahend = this.subtrahendAsset.build(argument);
      return new SubtracterVectorProvider(minuend, subtrahend);
   }

   @Override
   public void cleanUp() {
      this.minuendAsset.cleanUp();
      this.subtrahendAsset.cleanUp();
   }
}
