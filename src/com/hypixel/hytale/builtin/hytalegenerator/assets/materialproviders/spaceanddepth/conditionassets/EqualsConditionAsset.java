package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.conditionassets;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.conditions.ConditionParameter;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.conditions.EqualsCondition;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class EqualsConditionAsset extends ConditionAsset {
   @Nonnull
   public static final BuilderCodec<EqualsConditionAsset> CODEC = BuilderCodec.builder(
         EqualsConditionAsset.class, EqualsConditionAsset::new, ConditionAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("ContextToCheck", ConditionParameter.CODEC, true), (asset, value) -> asset.parameter = value, asset -> asset.parameter)
      .add()
      .append(new KeyedCodec<>("Value", Codec.INTEGER, true), (asset, value) -> asset.value = value, asset -> asset.value)
      .add()
      .build();
   private ConditionParameter parameter = ConditionParameter.SPACE_ABOVE_FLOOR;
   private int value = 0;

   @Nonnull
   @Override
   public SpaceAndDepthMaterialProvider.Condition build() {
      return new EqualsCondition(this.value, this.parameter);
   }
}
