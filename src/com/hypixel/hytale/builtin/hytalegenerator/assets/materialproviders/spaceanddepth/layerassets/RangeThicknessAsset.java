package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.layerassets;

import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.layers.RangedThicknessLayer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class RangeThicknessAsset extends LayerAsset {
   @Nonnull
   public static final BuilderCodec<RangeThicknessAsset> CODEC = BuilderCodec.builder(
         RangeThicknessAsset.class, RangeThicknessAsset::new, LayerAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("RangeMin", Codec.INTEGER, true), (asset, value) -> asset.rangeMin = value, asset -> asset.rangeMin)
      .add()
      .append(new KeyedCodec<>("RangeMax", Codec.INTEGER, true), (asset, value) -> asset.rangeMax = value, asset -> asset.rangeMax)
      .add()
      .append(
         new KeyedCodec<>("Material", MaterialProviderAsset.CODEC, true),
         (asset, value) -> asset.materialProviderAsset = value,
         asset -> asset.materialProviderAsset
      )
      .add()
      .append(new KeyedCodec<>("Seed", Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed)
      .add()
      .afterDecode(asset -> asset.rangeMax = Math.max(asset.rangeMin, asset.rangeMax))
      .build();
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
   private String seed = "";
   private int rangeMin;
   private int rangeMax;

   @Nonnull
   @Override
   public SpaceAndDepthMaterialProvider.Layer<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      MaterialProvider<Material> materialProvider = this.materialProviderAsset.build(argument);
      return new RangedThicknessLayer<>(this.rangeMin, this.rangeMax, argument.parentSeed.child(this.seed), materialProvider);
   }

   @Override
   public void cleanUp() {
      this.materialProviderAsset.cleanUp();
   }
}
