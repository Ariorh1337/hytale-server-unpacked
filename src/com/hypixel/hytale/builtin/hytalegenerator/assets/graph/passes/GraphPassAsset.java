package com.hypixel.hytale.builtin.hytalegenerator.assets.graph.passes;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.passes.GraphPass;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public abstract class GraphPassAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, GraphPassAsset>> {
   @Nonnull
   public static final AssetCodecMapCodec<String, GraphPassAsset> CODEC = new AssetCodecMapCodec<>(
      Codec.STRING, (asset, value) -> asset.id = value, asset -> asset.id, (asset, value) -> asset.data = value, asset -> asset.data
   );
   @Nonnull
   private static final Map<String, GraphPassAsset> exportedNodes = new ConcurrentHashMap<>();
   @Nonnull
   public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(GraphPassAsset.class, CODEC);
   @Nonnull
   public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<>(CHILD_ASSET_CODEC, String[]::new);
   @Nonnull
   public static final BuilderCodec<GraphPassAsset> ABSTRACT_CODEC = BuilderCodec.abstractBuilder(GraphPassAsset.class)
      .append(new KeyedCodec<>("Skip", Codec.BOOLEAN, false), (asset, value) -> asset.skip = value, asset -> asset.skip)
      .add()
      .append(new KeyedCodec<>("ExportAs", Codec.STRING, false), (asset, value) -> asset.exportName = value, asset -> asset.exportName)
      .add()
      .afterDecode(asset -> {
         if (asset.exportName != null && !asset.exportName.isEmpty()) {
            if (exportedNodes.containsKey(asset.exportName)) {
               LoggerUtil.getLogger().warning("Duplicate export name for asset: " + asset.exportName);
            }

            exportedNodes.put(asset.exportName, asset);
            LoggerUtil.getLogger().fine("Registered imported GraphPass asset with name '" + asset.exportName + "' with asset id '" + asset.id);
         }
      })
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private boolean skip = false;
   private String exportName = "";

   protected GraphPassAsset() {
   }

   public abstract GraphPass build(@Nonnull GraphGeneratorAsset.Argument var1);

   public boolean skip() {
      return this.skip;
   }

   public static GraphPassAsset getExportedAsset(@Nonnull String name) {
      return exportedNodes.get(name);
   }

   public String getId() {
      return this.id;
   }

   @Override
   public void cleanUp() {
   }
}
