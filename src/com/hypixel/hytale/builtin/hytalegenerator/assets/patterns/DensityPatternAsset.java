package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.ConstantPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.FieldFunctionPattern;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import javax.annotation.Nonnull;

public class DensityPatternAsset extends PatternAsset {
   @Nonnull
   public static final BuilderCodec<DensityPatternAsset> CODEC = BuilderCodec.builder(
         DensityPatternAsset.class, DensityPatternAsset::new, PatternAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("Delimiters", new ArrayCodec<>(DensityPatternAsset.DelimiterAsset.CODEC, DensityPatternAsset.DelimiterAsset[]::new), true),
         (asset, value) -> asset.delimiterAssets = value,
         asset -> asset.delimiterAssets
      )
      .add()
      .append(new KeyedCodec<>("FieldFunction", DensityAsset.CODEC, true), (asset, value) -> asset.densityAsset = value, asset -> asset.densityAsset)
      .add()
      .build();
   private DensityPatternAsset.DelimiterAsset[] delimiterAssets = new DensityPatternAsset.DelimiterAsset[0];
   private DensityAsset densityAsset = new ConstantDensityAsset();

   @Nonnull
   @Override
   public Pattern build(@Nonnull PatternAsset.Argument argument) {
      if (super.skip()) {
         return ConstantPattern.INSTANCE_FALSE;
      }

      Density field = this.densityAsset.build(DensityAsset.from(argument));
      FieldFunctionPattern out = new FieldFunctionPattern(field);

      for (DensityPatternAsset.DelimiterAsset asset : this.delimiterAssets) {
         out.addDelimiter(asset.min, asset.max);
      }

      return out;
   }

   @Override
   public void cleanUp() {
      this.densityAsset.cleanUp();
   }

   public static class DelimiterAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, DensityPatternAsset.DelimiterAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, DensityPatternAsset.DelimiterAsset> CODEC = AssetBuilderCodec.builder(
            DensityPatternAsset.DelimiterAsset.class,
            DensityPatternAsset.DelimiterAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("Min", Codec.DOUBLE, true), (asset, value) -> asset.min = value, asset -> asset.min)
         .add()
         .append(new KeyedCodec<>("Max", Codec.DOUBLE, true), (asset, value) -> asset.max = value, asset -> asset.max)
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      private double min;
      private double max;

      public String getId() {
         return this.id;
      }
   }
}
