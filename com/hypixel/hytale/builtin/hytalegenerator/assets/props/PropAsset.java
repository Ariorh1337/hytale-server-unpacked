/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.props;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public abstract class PropAsset
implements Cleanable,
JsonAssetWithMap<String, DefaultAssetMap<String, PropAsset>> {
    public static final AssetCodecMapCodec<String, PropAsset> CODEC = new AssetCodecMapCodec<String, PropAsset>(Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (t, data) -> {
        t.data = data;
    }, t -> t.data);
    private static final Map<String, PropAsset> exportedNodes = new ConcurrentHashMap<String, PropAsset>();
    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec(PropAsset.class, CODEC);
    public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<String>(CHILD_ASSET_CODEC, String[]::new);
    public static final BuilderCodec<PropAsset> ABSTRACT_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(PropAsset.class).append(new KeyedCodec<Boolean>("Skip", Codec.BOOLEAN, false), (t, k) -> {
        t.skip = k;
    }, t -> t.skip).add()).append(new KeyedCodec<String>("ExportAs", Codec.STRING, false), (t, k) -> {
        t.exportName = k;
    }, t -> t.exportName).add()).afterDecode(asset -> {
        if (asset.exportName != null && !asset.exportName.isEmpty()) {
            if (exportedNodes.containsKey(asset.exportName)) {
                LoggerUtil.getLogger().warning("Duplicate export name for asset: " + asset.exportName);
            }
            exportedNodes.put(asset.exportName, (PropAsset)asset);
            LoggerUtil.getLogger().fine("Registered imported position provider asset with name '" + asset.exportName + "' with asset id '" + asset.id);
        }
    })).build();
    private String id;
    private AssetExtraInfo.Data data;
    private boolean skip = false;
    private String exportName = "";

    protected PropAsset() {
    }

    public abstract Prop build(@Nonnull Argument var1);

    public boolean skip() {
        return this.skip;
    }

    public static PropAsset getExportedAsset(@Nonnull String name) {
        return exportedNodes.get(name);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void cleanUp() {
    }

    public static class Argument {
        public SeedBox parentSeed;
        public MaterialCache materialCache;
        public ReferenceBundle referenceBundle;
        public WorkerIndexer workerIndexer;

        public Argument(@Nonnull SeedBox parentSeed, @Nonnull MaterialCache materialCache, @Nonnull ReferenceBundle referenceBundle, @Nonnull WorkerIndexer workerIndexer) {
            this.parentSeed = parentSeed;
            this.materialCache = materialCache;
            this.referenceBundle = referenceBundle;
            this.workerIndexer = workerIndexer;
        }

        public Argument(@Nonnull Argument argument) {
            this.parentSeed = argument.parentSeed;
            this.materialCache = argument.materialCache;
            this.referenceBundle = argument.referenceBundle;
        }
    }
}

