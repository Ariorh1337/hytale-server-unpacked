package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.TransparentMaterialProvider;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class TransparentMaterialProviderAsset extends MaterialProviderAsset {
   @Nonnull
   public static final BuilderCodec<TransparentMaterialProviderAsset> CODEC = BuilderCodec.builder(
         TransparentMaterialProviderAsset.class, TransparentMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC
      )
      .build();

   @Nonnull
   @Override
   public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      return super.skip() ? MaterialProvider.noMaterialProvider() : TransparentMaterialProvider.instance();
   }
}
