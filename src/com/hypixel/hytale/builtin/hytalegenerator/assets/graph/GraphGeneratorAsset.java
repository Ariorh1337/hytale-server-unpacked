package com.hypixel.hytale.builtin.hytalegenerator.assets.graph;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes.GraphPassAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propdistribution.PropDistributionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.workerindexer.WorkerIndexer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GraphGeneratorAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, GraphGeneratorAsset>> {
   public static final GraphGeneratorAsset EMPTY = new GraphGeneratorAsset();
   @Nonnull
   private static final Map<String, GraphGeneratorAsset> exportedNodes = new ConcurrentHashMap<>();
   @Nonnull
   public static final AssetBuilderCodec<String, GraphGeneratorAsset> CODEC = AssetBuilderCodec.builder(
         GraphGeneratorAsset.class,
         GraphGeneratorAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(
         new KeyedCodec<>("Passes", new ArrayCodec<>(GraphPassAsset.CODEC, GraphPassAsset[]::new), true),
         (asset, value) -> asset.graphPassAssets = value,
         asset -> asset.graphPassAssets
      )
      .add()
      .append(new KeyedCodec<>("ExportName", Codec.STRING, true), (asset, value) -> asset.exportName = value, asset -> asset.exportName)
      .add()
      .append(new KeyedCodec<>("ImportName", Codec.STRING, true), (asset, value) -> asset.importName = value, asset -> asset.importName)
      .add()
      .append(new KeyedCodec<>("PrintStats", Codec.BOOLEAN, true), (asset, value) -> asset.printStats = value, asset -> asset.printStats)
      .add()
      .append(new KeyedCodec<>("StatsLabel", Codec.STRING, true), (asset, value) -> asset.statsLabel = value, asset -> asset.statsLabel)
      .add()
      .<Integer>append(
         new KeyedCodec<>("StatsSampleCount", Codec.INTEGER, true), (asset, value) -> asset.statsSampleCount = value, asset -> asset.statsSampleCount
      )
      .addValidator(Validators.greaterThanOrEqual(0))
      .add()
      .afterDecode(asset -> {
         if (!asset.exportName.isEmpty()) {
            if (exportedNodes.containsKey(asset.exportName)) {
               LoggerUtil.getLogger().warning("Duplicate export name for asset: " + asset.exportName);
            }

            exportedNodes.put(asset.exportName, asset);
            LoggerUtil.getLogger().fine("Registered imported GraphGenerator asset with name '" + asset.exportName + "' with asset id '" + asset.id);
         }
      })
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   @Nonnull
   private GraphPassAsset[] graphPassAssets = new GraphPassAsset[0];
   @Nonnull
   private String exportName = "";
   @Nonnull
   private String importName = "";
   @Nonnull
   private String statsLabel = "UNNAMED";
   private boolean printStats = false;
   private int statsSampleCount = 50;

   @Nonnull
   public GraphGenerator build(@Nonnull GraphGeneratorAsset.Argument argument) {
      List<GraphPass> graphPasses = new ArrayList<>(this.graphPassAssets.length);
      if (!this.importName.isEmpty()) {
         GraphGeneratorAsset exportedAsset = exportedNodes.get(this.importName);
         return exportedAsset == null ? GraphGenerator.EMPTY_INSTANCE : exportedAsset.build(argument);
      }

      for (GraphPassAsset graphPassAsset : this.graphPassAssets) {
         GraphPass graphPass = graphPassAsset.build(argument);
         graphPasses.add(graphPass);
      }

      return new GraphGenerator(graphPasses, this.printStats, this.statsSampleCount, this.statsLabel);
   }

   @Nonnull
   public String getId() {
      return "";
   }

   @Override
   public void cleanUp() {
      for (GraphPassAsset graphPassAsset : this.graphPassAssets) {
         graphPassAsset.cleanUp();
      }
   }

   public static class Argument {
      @Nonnull
      public SeedBox parentSeed;
      @Nonnull
      public ReferenceBundle referenceBundle;
      @Nullable
      public MaterialCache materialCache;
      @Nonnull
      public WorkerIndexer.Id workerId;
      public boolean buildDensityContent;
      public boolean buildMaterialContent;
      public boolean buildPropContent;
      public boolean buildPositionsContent;

      public Argument(
         @Nonnull SeedBox parentSeed,
         @Nonnull ReferenceBundle referenceBundle,
         @Nullable MaterialCache materialCache,
         @Nonnull WorkerIndexer.Id workerId,
         boolean buildDensityContent,
         boolean buildMaterialContent,
         boolean buildPropContent,
         boolean buildPositionsContent
      ) {
         this.parentSeed = parentSeed;
         this.referenceBundle = referenceBundle;
         this.materialCache = materialCache;
         this.workerId = workerId;
         this.buildDensityContent = buildDensityContent;
         this.buildMaterialContent = buildMaterialContent;
         this.buildPropContent = buildPropContent;
         this.buildPositionsContent = buildPositionsContent;
      }

      public Argument(@Nonnull GraphGeneratorAsset.Argument argument) {
         this.parentSeed = argument.parentSeed;
         this.referenceBundle = argument.referenceBundle;
         this.materialCache = argument.materialCache;
         this.workerId = argument.workerId;
         this.buildDensityContent = argument.buildDensityContent;
         this.buildMaterialContent = argument.buildMaterialContent;
         this.buildPropContent = argument.buildPropContent;
         this.buildPositionsContent = argument.buildPositionsContent;
      }

      public Argument(
         @Nonnull DensityAsset.Argument argument,
         boolean buildDensityContent,
         boolean buildMaterialContent,
         boolean buildPropContent,
         boolean buildPositionsContent
      ) {
         this.parentSeed = argument.parentSeed;
         this.referenceBundle = argument.referenceBundle;
         this.materialCache = null;
         this.workerId = argument.workerId;
         this.buildDensityContent = buildDensityContent;
         this.buildMaterialContent = buildMaterialContent;
         this.buildPropContent = buildPropContent;
         this.buildPositionsContent = buildPositionsContent;
      }

      public Argument(
         @Nonnull PropDistributionAsset.Argument argument,
         boolean buildDensityContent,
         boolean buildMaterialContent,
         boolean buildPropContent,
         boolean buildPositionsContent
      ) {
         this.parentSeed = argument.parentSeed;
         this.referenceBundle = argument.referenceBundle;
         this.materialCache = argument.materialCache;
         this.workerId = argument.workerId;
         this.buildDensityContent = buildDensityContent;
         this.buildMaterialContent = buildMaterialContent;
         this.buildPropContent = buildPropContent;
         this.buildPositionsContent = buildPositionsContent;
      }

      public Argument(
         @Nonnull MaterialProviderAsset.Argument argument,
         boolean buildDensityContent,
         boolean buildMaterialContent,
         boolean buildPropContent,
         boolean buildPositionsContent
      ) {
         this.parentSeed = argument.parentSeed;
         this.referenceBundle = argument.referenceBundle;
         this.materialCache = argument.materialCache;
         this.workerId = argument.workerId;
         this.buildDensityContent = buildDensityContent;
         this.buildMaterialContent = buildMaterialContent;
         this.buildPropContent = buildPropContent;
         this.buildPositionsContent = buildPositionsContent;
      }

      public Argument(
         @Nonnull PositionProviderAsset.Argument argument,
         boolean buildDensityContent,
         boolean buildMaterialContent,
         boolean buildPropContent,
         boolean buildPositionsContent
      ) {
         this.parentSeed = argument.parentSeed;
         this.referenceBundle = argument.referenceBundle;
         this.materialCache = null;
         this.workerId = argument.workerId;
         this.buildDensityContent = buildDensityContent;
         this.buildMaterialContent = buildMaterialContent;
         this.buildPropContent = buildPropContent;
         this.buildPositionsContent = buildPositionsContent;
      }
   }
}
