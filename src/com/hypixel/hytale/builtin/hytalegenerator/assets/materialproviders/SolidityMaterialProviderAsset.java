package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.SolidityMaterialProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class SolidityMaterialProviderAsset extends MaterialProviderAsset {
   @Nonnull
   public static final BuilderCodec<SolidityMaterialProviderAsset> CODEC = BuilderCodec.builder(
         SolidityMaterialProviderAsset.class, SolidityMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Solid", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.solidMaterialProvider = value,
         asset -> asset.solidMaterialProvider
      )
      .add()
      .append(
         new KeyedCodec<>("Empty", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.emptyMaterialProvider = value,
         asset -> asset.emptyMaterialProvider
      )
      .add()
      .build();
   private MaterialProviderAsset solidMaterialProvider = new ConstantMaterialProviderAsset();
   private MaterialProviderAsset emptyMaterialProvider = new ConstantMaterialProviderAsset();

   @Nonnull
   @Override
   public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      return super.skip()
         ? MaterialProvider.noMaterialProvider()
         : new SolidityMaterialProvider<>(this.solidMaterialProvider.build(argument), this.emptyMaterialProvider.build(argument));
   }

   @Override
   public void cleanUp() {
      this.solidMaterialProvider.cleanUp();
      this.emptyMaterialProvider.cleanUp();
   }
}
