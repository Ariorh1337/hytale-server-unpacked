package com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.DelimiterDouble;
import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.DensityDelimitedTintProvider;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class DensityDelimitedTintProviderAsset extends TintProviderAsset {
   @Nonnull
   public static final BuilderCodec<DensityDelimitedTintProviderAsset> CODEC = BuilderCodec.builder(
         DensityDelimitedTintProviderAsset.class, DensityDelimitedTintProviderAsset::new, TintProviderAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>(
            "Delimiters",
            new ArrayCodec<>(DensityDelimitedTintProviderAsset.DelimiterAsset.CODEC, DensityDelimitedTintProviderAsset.DelimiterAsset[]::new),
            true
         ),
         (asset, value) -> asset.delimiterAssets = value,
         asset -> asset.delimiterAssets
      )
      .add()
      .append(new KeyedCodec<>("Density", DensityAsset.CODEC, true), (asset, value) -> asset.densityAsset = value, asset -> asset.densityAsset)
      .add()
      .build();
   private DensityDelimitedTintProviderAsset.DelimiterAsset[] delimiterAssets = new DensityDelimitedTintProviderAsset.DelimiterAsset[0];
   private DensityAsset densityAsset = DensityAsset.getFallbackAsset();

   @Nonnull
   @Override
   public TintProvider build(@Nonnull TintProviderAsset.Argument argument) {
      if (super.isSkipped()) {
         return TintProvider.noTintProvider();
      }

      List<DelimiterDouble<TintProvider>> delimiters = new ArrayList<>(this.delimiterAssets.length);

      for (DensityDelimitedTintProviderAsset.DelimiterAsset delimiterAsset : this.delimiterAssets) {
         delimiters.add(delimiterAsset.build(argument));
      }

      Density density = this.densityAsset.build(DensityAsset.from(argument));
      return new DensityDelimitedTintProvider(delimiters, density);
   }

   @Override
   public void cleanUp() {
      this.densityAsset.cleanUp();

      for (DensityDelimitedTintProviderAsset.DelimiterAsset delimiterAsset : this.delimiterAssets) {
         delimiterAsset.cleanUp();
      }
   }

   public static class DelimiterAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, DensityDelimitedTintProviderAsset.DelimiterAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, DensityDelimitedTintProviderAsset.DelimiterAsset> CODEC = AssetBuilderCodec.builder(
            DensityDelimitedTintProviderAsset.DelimiterAsset.class,
            DensityDelimitedTintProviderAsset.DelimiterAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("Range", RangeDoubleAsset.CODEC, true), (asset, value) -> asset.rangeAsset = value, asset -> asset.rangeAsset)
         .add()
         .append(new KeyedCodec<>("Tint", TintProviderAsset.CODEC, true), (asset, value) -> asset.tintProviderAsset = value, asset -> asset.tintProviderAsset)
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      private RangeDoubleAsset rangeAsset = new RangeDoubleAsset();
      private TintProviderAsset tintProviderAsset = TintProviderAsset.getFallbackAsset();

      @Nonnull
      public DelimiterDouble<TintProvider> build(@Nonnull TintProviderAsset.Argument argument) {
         RangeDouble range = this.rangeAsset.build();
         TintProvider tintProvider = this.tintProviderAsset.build(argument);
         return new DelimiterDouble<>(range, tintProvider);
      }

      public String getId() {
         return this.id;
      }

      @Override
      public void cleanUp() {
         this.tintProviderAsset.cleanUp();
      }
   }
}
