/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.pointgenerators;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.fields.points.PointProvider;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public abstract class PointGeneratorAsset
implements JsonAssetWithMap<String, DefaultAssetMap<String, PointGeneratorAsset>> {
    private static final PointGeneratorAsset[] EMPTY_INPUTS = new PointGeneratorAsset[0];
    public static final AssetCodecMapCodec<String, PointGeneratorAsset> CODEC = new AssetCodecMapCodec<String, PointGeneratorAsset>(Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (t, data) -> {
        t.data = data;
    }, t -> t.data);
    private static final Map<String, PointGeneratorAsset> exportedNodes = new HashMap<String, PointGeneratorAsset>();
    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec(PointGeneratorAsset.class, CODEC);
    public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<String>(CHILD_ASSET_CODEC, String[]::new);
    public static final BuilderCodec<PointGeneratorAsset> ABSTRACT_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(PointGeneratorAsset.class).append(new KeyedCodec<Boolean>("Skip", Codec.BOOLEAN, false), (t, k) -> {
        t.skip = k;
    }, t -> t.skip).add()).append(new KeyedCodec<String>("ExportAs", Codec.STRING, false), (t, k) -> {
        t.exportName = k;
    }, t -> t.exportName).add()).afterDecode(asset -> {
        if (asset.exportName != null && !asset.exportName.isEmpty()) {
            exportedNodes.put(asset.exportName, (PointGeneratorAsset)asset);
            LoggerUtil.getLogger().fine("Registered imported position provider asset with name '" + asset.exportName + "' with asset id '" + asset.id);
        }
    })).build();
    private String id;
    private AssetExtraInfo.Data data;
    private PointGeneratorAsset[] inputs = EMPTY_INPUTS;
    private boolean skip = false;
    private String exportName = "";

    protected PointGeneratorAsset() {
    }

    public abstract PointProvider build(@Nonnull SeedBox var1);

    public PointGeneratorAsset[] inputs() {
        return this.inputs;
    }

    public boolean skip() {
        return this.skip;
    }

    public static PointGeneratorAsset getExportedAsset(@Nonnull String name) {
        return exportedNodes.get(name);
    }

    @Override
    public String getId() {
        return this.id;
    }
}

