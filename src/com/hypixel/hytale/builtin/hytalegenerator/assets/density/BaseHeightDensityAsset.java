package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.framework.DecimalConstantsFrameworkAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.BaseHeightDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class BaseHeightDensityAsset extends DensityAsset {
   @Nonnull
   public static final BuilderCodec<BaseHeightDensityAsset> CODEC = BuilderCodec.builder(
         BaseHeightDensityAsset.class, BaseHeightDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("BaseHeightName", Codec.STRING, false), (asset, value) -> asset.baseHeightName = value, asset -> asset.baseHeightName)
      .add()
      .append(new KeyedCodec<>("Distance", Codec.BOOLEAN, false), (asset, value) -> asset.isDistance = value, asset -> asset.isDistance)
      .add()
      .build();
   private String baseHeightName = "";
   private boolean isDistance = true;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      }

      Double baseHeight = DecimalConstantsFrameworkAsset.Entries.get(this.baseHeightName, argument.referenceBundle);
      if (baseHeight == null) {
         baseHeight = 0.0;
      }

      return new BaseHeightDensity(baseHeight, this.isDistance);
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
