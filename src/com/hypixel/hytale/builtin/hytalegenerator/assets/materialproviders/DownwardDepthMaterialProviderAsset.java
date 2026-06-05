package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.DownwardDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class DownwardDepthMaterialProviderAsset extends MaterialProviderAsset {
   @Nonnull
   public static final BuilderCodec<DownwardDepthMaterialProviderAsset> CODEC = BuilderCodec.builder(
         DownwardDepthMaterialProviderAsset.class, DownwardDepthMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Depth", Codec.INTEGER, true), (asset, value) -> asset.depth = value, asset -> asset.depth)
      .add()
      .append(
         new KeyedCodec<>("Material", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.materialProviderAsset = value,
         asset -> asset.materialProviderAsset
      )
      .add()
      .build();
   private int depth;
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();

   @Nonnull
   @Override
   public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      return super.skip() ? MaterialProvider.noMaterialProvider() : new DownwardDepthMaterialProvider<>(this.materialProviderAsset.build(argument), this.depth);
   }

   @Override
   public void cleanUp() {
      this.materialProviderAsset.cleanUp();
   }
}
