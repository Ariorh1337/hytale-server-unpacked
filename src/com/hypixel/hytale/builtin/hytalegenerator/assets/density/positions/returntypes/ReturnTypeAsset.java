package com.hypixel.hytale.builtin.hytalegenerator.assets.density.positions.returntypes;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.returntypes.ReturnType;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.workerindexer.WorkerIndexer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public abstract class ReturnTypeAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, ReturnTypeAsset>> {
   @Nonnull
   private static final ReturnTypeAsset[] EMPTY_INPUTS = new ReturnTypeAsset[0];
   @Nonnull
   public static final AssetCodecMapCodec<String, ReturnTypeAsset> CODEC = new AssetCodecMapCodec<>(
      Codec.STRING, (asset, value) -> asset.id = value, asset -> asset.id, (asset, value) -> asset.data = value, asset -> asset.data
   );
   @Nonnull
   private static final Map<String, ReturnTypeAsset> exportedNodes = new HashMap<>();
   @Nonnull
   public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(ReturnTypeAsset.class, CODEC);
   @Nonnull
   public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<>(CHILD_ASSET_CODEC, String[]::new);
   @Nonnull
   public static final BuilderCodec<ReturnTypeAsset> ABSTRACT_CODEC = BuilderCodec.abstractBuilder(ReturnTypeAsset.class)
      .append(new KeyedCodec<>("ExportAs", Codec.STRING, false), (asset, value) -> asset.exportName = value, asset -> asset.exportName)
      .add()
      .afterDecode(asset -> {
         if (asset.exportName != null && !asset.exportName.isEmpty()) {
            exportedNodes.put(asset.exportName, asset);
            LoggerUtil.getLogger().fine("Registered imported node asset with name '" + asset.exportName + "' with asset id '" + asset.id);
         }
      })
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private String exportName = "";

   protected ReturnTypeAsset() {
   }

   public abstract ReturnType build(@Nonnull SeedBox var1, @Nonnull ReferenceBundle var2, @Nonnull WorkerIndexer.Id var3);

   public void cleanUp() {
   }

   public static boolean registerExportedNode(@Nonnull String name, @Nonnull ReturnTypeAsset node) {
      exportedNodes.put(name, node);
      return true;
   }

   public static ReturnTypeAsset getExportedAsset(@Nonnull String name) {
      return exportedNodes.get(name);
   }

   public String getId() {
      return this.id;
   }
}
