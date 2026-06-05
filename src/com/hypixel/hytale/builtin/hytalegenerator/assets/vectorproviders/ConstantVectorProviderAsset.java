package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class ConstantVectorProviderAsset extends VectorProviderAsset {
   public static final VectorProviderAsset INSTANCE = new ConstantVectorProviderAsset();
   @Nonnull
   public static final BuilderCodec<ConstantVectorProviderAsset> CODEC = BuilderCodec.builder(
         ConstantVectorProviderAsset.class, ConstantVectorProviderAsset::new, ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Vector", Vector3dUtil.CODEC, true), (asset, value) -> asset.value = value, asset -> asset.value)
      .add()
      .build();
   private Vector3d value = new Vector3d();

   public ConstantVectorProviderAsset() {
   }

   public ConstantVectorProviderAsset(@Nonnull Vector3d vector) {
      this.value.set(vector);
   }

   @Nonnull
   @Override
   public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
      return this.isSkipped() ? new ConstantVectorProvider(new Vector3d()) : new ConstantVectorProvider(this.value);
   }
}
