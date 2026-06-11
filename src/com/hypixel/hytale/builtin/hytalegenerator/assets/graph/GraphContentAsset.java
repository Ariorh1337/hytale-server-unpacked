package com.hypixel.hytale.builtin.hytalegenerator.assets.graph;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public class GraphContentAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, GraphContentAsset>> {
   @Nonnull
   public static final GraphContentAsset INSTANCE = new GraphContentAsset();
   @Nonnull
   private static final Map<String, GraphContentAsset> exportedNodes = new ConcurrentHashMap<>();
   @Nonnull
   public static final AssetBuilderCodec<String, GraphContentAsset> CODEC = AssetBuilderCodec.builder(
         GraphContentAsset.class,
         GraphContentAsset::new,
         Codec.STRING,
         (asset, value) -> asset.id = value,
         asset -> asset.id,
         (asset, value) -> asset.data = value,
         asset -> asset.data
      )
      .append(new KeyedCodec<>("ExportName", Codec.STRING, true), (asset, value) -> asset.exportName = value, asset -> asset.exportName)
      .add()
      .append(new KeyedCodec<>("ImportName", Codec.STRING, true), (asset, value) -> asset.importName = value, asset -> asset.importName)
      .add()
      .append(
         new KeyedCodec<>("DensityContent", new ArrayCodec<>(DensityContentAsset.CODEC, DensityContentAsset[]::new), true),
         (asset, value) -> asset.densityContentAssets = value,
         asset -> asset.densityContentAssets
      )
      .add()
      .append(
         new KeyedCodec<>("MaterialContent", new ArrayCodec<>(MaterialContentAsset.CODEC, MaterialContentAsset[]::new), true),
         (asset, value) -> asset.materialContentAssets = value,
         asset -> asset.materialContentAssets
      )
      .add()
      .append(
         new KeyedCodec<>("PropDistributionContent", new ArrayCodec<>(PropDistributionContentAsset.CODEC, PropDistributionContentAsset[]::new), true),
         (asset, value) -> asset.propDistributionContentAssets = value,
         asset -> asset.propDistributionContentAssets
      )
      .add()
      .append(
         new KeyedCodec<>("PositionsContent", new ArrayCodec<>(PositionsContentAsset.CODEC, PositionsContentAsset[]::new), true),
         (asset, value) -> asset.positionsContentAssets = value,
         asset -> asset.positionsContentAssets
      )
      .add()
      .append(new KeyedCodec<>("ContentTags", new ArrayCodec<>(Codec.STRING, String[]::new), true), (asset, value) -> asset.tags = value, asset -> asset.tags)
      .add()
      .afterDecode(asset -> {
         if (!asset.exportName.isEmpty()) {
            if (exportedNodes.containsKey(asset.exportName)) {
               LoggerUtil.getLogger().warning("Duplicate export name for asset: " + asset.exportName);
            }

            exportedNodes.put(asset.exportName, asset);
            LoggerUtil.getLogger().fine("Registered imported GraphContent asset with name '" + asset.exportName + "' with asset id '" + asset.id);
         }
      })
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   @Nonnull
   private String exportName = "";
   @Nonnull
   private String importName = "";
   @Nonnull
   private DensityContentAsset[] densityContentAssets = new DensityContentAsset[0];
   @Nonnull
   private MaterialContentAsset[] materialContentAssets = new MaterialContentAsset[0];
   @Nonnull
   private PropDistributionContentAsset[] propDistributionContentAssets = new PropDistributionContentAsset[0];
   @Nonnull
   private PositionsContentAsset[] positionsContentAssets = new PositionsContentAsset[0];
   @Nonnull
   private String[] tags = new String[]{""};

   @Nonnull
   public GraphSpace.Content build(@Nonnull GraphGeneratorAsset.Argument argument) {
      if (!this.importName.isEmpty()) {
         GraphContentAsset exportedAsset = exportedNodes.get(this.importName);
         return exportedAsset == null ? GraphSpace.Content.DEFAULT : exportedAsset.build(argument);
      }

      List<GraphSpace.ContentEntry<GraphSpace.DensityContent>> densityContent = new ArrayList<>(this.densityContentAssets.length);
      if (argument.buildDensityContent) {
         for (DensityContentAsset asset : this.densityContentAssets) {
            GraphSpace.DensityContent content = asset.build(argument);
            int contentId = GraphSpace.Content.toIntId(asset.getContentLayerName());
            densityContent.add(new GraphSpace.ContentEntry<>(contentId, content));
         }
      }

      List<GraphSpace.ContentEntry<GraphSpace.MaterialContent>> materialContent = new ArrayList<>(this.materialContentAssets.length);
      if (argument.buildMaterialContent) {
         for (MaterialContentAsset asset : this.materialContentAssets) {
            GraphSpace.MaterialContent content = asset.build(argument);
            int contentId = GraphSpace.Content.toIntId(asset.getContentLayerName());
            materialContent.add(new GraphSpace.ContentEntry<>(contentId, content));
         }
      }

      List<GraphSpace.ContentEntry<GraphSpace.PropDistributionContent>> propContent = new ArrayList<>(this.propDistributionContentAssets.length);
      if (argument.buildPropContent) {
         for (PropDistributionContentAsset asset : this.propDistributionContentAssets) {
            GraphSpace.PropDistributionContent content = asset.build(argument);
            int contentId = GraphSpace.Content.toIntId(asset.getContentLayerName());
            propContent.add(new GraphSpace.ContentEntry<>(contentId, content));
         }
      }

      List<GraphSpace.ContentEntry<GraphSpace.PositionsContent>> positionsContent = new ArrayList<>(this.positionsContentAssets.length);
      if (argument.buildPositionsContent) {
         for (PositionsContentAsset asset : this.positionsContentAssets) {
            GraphSpace.PositionsContent content = asset.build(argument);
            int contentId = GraphSpace.Content.toIntId(asset.getContentLayerName());
            positionsContent.add(new GraphSpace.ContentEntry<>(contentId, content));
         }
      }

      int[] tagsInt = new int[this.tags.length];

      for (int i = 0; i < tagsInt.length; i++) {
         tagsInt[i] = GraphSpace.Content.toIntId(this.tags[i]);
      }

      return new GraphSpace.Content(densityContent, materialContent, propContent, positionsContent, tagsInt);
   }

   @Nonnull
   public String getId() {
      return "";
   }

   @Override
   public void cleanUp() {
      for (DensityContentAsset asset : this.densityContentAssets) {
         asset.cleanUp();
      }

      for (MaterialContentAsset asset : this.materialContentAssets) {
         asset.cleanUp();
      }

      for (PropDistributionContentAsset asset : this.propDistributionContentAssets) {
         asset.cleanUp();
      }

      for (PositionsContentAsset asset : this.positionsContentAssets) {
         asset.cleanUp();
      }
   }
}
