package com.hypixel.hytale.builtin.hytalegenerator.assets.pointgenerators;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.noise.pointprovider.PointProvider;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public abstract class PointGeneratorAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, PointGeneratorAsset>> {
   @Nonnull
   private static final PointGeneratorAsset[] EMPTY_INPUTS = new PointGeneratorAsset[0];
   @Nonnull
   public static final AssetCodecMapCodec<String, PointGeneratorAsset> CODEC = new AssetCodecMapCodec<>(
      Codec.STRING, (asset, value) -> asset.id = value, asset -> asset.id, (asset, value) -> asset.data = value, asset -> asset.data
   );
   @Nonnull
   private static final Map<String, PointGeneratorAsset> exportedNodes = new HashMap<>();
   @Nonnull
   public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(PointGeneratorAsset.class, CODEC);
   @Nonnull
   public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<>(CHILD_ASSET_CODEC, String[]::new);
   @Nonnull
   public static final BuilderCodec<PointGeneratorAsset> ABSTRACT_CODEC = BuilderCodec.abstractBuilder(PointGeneratorAsset.class)
      .append(new KeyedCodec<>("Skip", Codec.BOOLEAN, false), (asset, value) -> asset.skip = value, asset -> asset.skip)
      .add()
      .append(new KeyedCodec<>("ExportAs", Codec.STRING, false), (asset, value) -> asset.exportName = value, asset -> asset.exportName)
      .add()
      .afterDecode(asset -> {
         if (asset.exportName != null && !asset.exportName.isEmpty()) {
            exportedNodes.put(asset.exportName, asset);
            LoggerUtil.getLogger().fine("Registered imported position provider asset with name '" + asset.exportName + "' with asset id '" + asset.id);
         }
      })
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   @Nonnull
   private PointGeneratorAsset[] inputs = EMPTY_INPUTS;
   private boolean skip;
   private String exportName = "";

   protected PointGeneratorAsset() {
   }

   public abstract PointProvider build(@Nonnull SeedBox var1);

   @Nonnull
   public PointGeneratorAsset[] inputs() {
      return this.inputs;
   }

   public boolean skip() {
      return this.skip;
   }

   public static PointGeneratorAsset getExportedAsset(@Nonnull String name) {
      return exportedNodes.get(name);
   }

   public String getId() {
      return this.id;
   }
}
