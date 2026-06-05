package com.hypixel.hytale.builtin.hytalegenerator.assets.density.positions.returntypes;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.CacheDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.FieldFunctionMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiCacheDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.returntypes.DensityReturnType;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.returntypes.ReturnType;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.workerindexer.WorkerIndexer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.Range;
import java.util.HashMap;
import javax.annotation.Nonnull;

public class DensityReturnTypeAsset extends ReturnTypeAsset {
   @Nonnull
   public static final BuilderCodec<DensityReturnTypeAsset> CODEC = BuilderCodec.builder(
         DensityReturnTypeAsset.class, DensityReturnTypeAsset::new, ReturnTypeAsset.ABSTRACT_CODEC
      )
      .append(
         new KeyedCodec<>("ChoiceDensity", DensityAsset.CODEC, true), (asset, value) -> asset.choiceDensityAsset = value, asset -> asset.choiceDensityAsset
      )
      .add()
      .append(
         new KeyedCodec<>("Delimiters", new ArrayCodec<>(DensityReturnTypeAsset.DelimiterAsset.CODEC, DensityReturnTypeAsset.DelimiterAsset[]::new), true),
         (asset, value) -> asset.delimiterAssets = value,
         asset -> asset.delimiterAssets
      )
      .add()
      .append(new KeyedCodec<>("DefaultValue", Codec.DOUBLE, false), (asset, value) -> asset.defaultValue = value, asset -> asset.defaultValue)
      .add()
      .build();
   private DensityAsset choiceDensityAsset = new ConstantDensityAsset();
   private DensityReturnTypeAsset.DelimiterAsset[] delimiterAssets = new DensityReturnTypeAsset.DelimiterAsset[0];
   private double defaultValue = 0.0;

   @Nonnull
   @Override
   public ReturnType build(@Nonnull SeedBox parentSeed, @Nonnull ReferenceBundle referenceBundle, @Nonnull WorkerIndexer.Id workerId) {
      DensityAsset.Argument densityArgument = new DensityAsset.Argument(parentSeed, referenceBundle, workerId);
      Density choiceDensity = this.choiceDensityAsset.build(densityArgument);
      HashMap<Range, Density> delimiterMap = new HashMap<>(this.delimiterAssets.length);

      for (DensityReturnTypeAsset.DelimiterAsset delimiter : this.delimiterAssets) {
         delimiterMap.put(new Range((float)delimiter.from, (float)delimiter.to), delimiter.densityAsset.build(densityArgument));
      }

      Density cache = new MultiCacheDensity(choiceDensity, CacheDensityAsset.DEFAULT_CAPACITY);
      return new DensityReturnType(cache, delimiterMap, true, this.defaultValue);
   }

   public static class DelimiterAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, FieldFunctionMaterialProviderAsset.DelimiterAsset>> {
      @Nonnull
      public static final AssetBuilderCodec<String, DensityReturnTypeAsset.DelimiterAsset> CODEC = AssetBuilderCodec.builder(
            DensityReturnTypeAsset.DelimiterAsset.class,
            DensityReturnTypeAsset.DelimiterAsset::new,
            Codec.STRING,
            (asset, value) -> asset.id = value,
            asset -> asset.id,
            (asset, value) -> asset.data = value,
            asset -> asset.data
         )
         .append(new KeyedCodec<>("From", Codec.DOUBLE, true), (asset, value) -> asset.from = value, asset -> asset.from)
         .add()
         .append(new KeyedCodec<>("To", Codec.DOUBLE, true), (asset, value) -> asset.to = value, asset -> asset.to)
         .add()
         .append(new KeyedCodec<>("Density", DensityAsset.CODEC, true), (asset, value) -> asset.densityAsset = value, asset -> asset.densityAsset)
         .add()
         .build();
      private String id;
      private AssetExtraInfo.Data data;
      private double from = 0.0;
      private double to = 0.0;
      private DensityAsset densityAsset = new ConstantDensityAsset();

      public String getId() {
         return this.id;
      }
   }
}
