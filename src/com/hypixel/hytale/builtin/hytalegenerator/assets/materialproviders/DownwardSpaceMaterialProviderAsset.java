package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.DownwardSpaceMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class DownwardSpaceMaterialProviderAsset extends MaterialProviderAsset {
   @Nonnull
   public static final BuilderCodec<DownwardSpaceMaterialProviderAsset> CODEC = BuilderCodec.builder(
         DownwardSpaceMaterialProviderAsset.class, DownwardSpaceMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Space", Codec.INTEGER, true), (asset, value) -> asset.space = value, asset -> asset.space)
      .add()
      .append(
         new KeyedCodec<>("Material", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.materialProviderAsset = value,
         asset -> asset.materialProviderAsset
      )
      .add()
      .build();
   private int space = 0;
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();

   @Nonnull
   @Override
   public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      return super.skip() ? MaterialProvider.noMaterialProvider() : new DownwardSpaceMaterialProvider<>(this.materialProviderAsset.build(argument), this.space);
   }

   @Override
   public void cleanUp() {
      this.materialProviderAsset.cleanUp();
   }
}
