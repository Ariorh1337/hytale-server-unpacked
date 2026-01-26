/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.patterns;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.directionality.DirectionalityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
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

public abstract class PatternAsset
implements Cleanable,
JsonAssetWithMap<String, DefaultAssetMap<String, PatternAsset>> {
    public static final AssetCodecMapCodec<String, PatternAsset> CODEC = new AssetCodecMapCodec<String, PatternAsset>(Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (t, data) -> {
        t.data = data;
    }, t -> t.data);
    private static final Map<String, PatternAsset> exportedNodes = new ConcurrentHashMap<String, PatternAsset>();
    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec(PatternAsset.class, CODEC);
    public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<String>(CHILD_ASSET_CODEC, String[]::new);
    public static final BuilderCodec<PatternAsset> ABSTRACT_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(PatternAsset.class).append(new KeyedCodec<Boolean>("Skip", Codec.BOOLEAN, false), (t, k) -> {
        t.skip = k;
    }, t -> t.skip).add()).append(new KeyedCodec<String>("ExportAs", Codec.STRING, false), (t, k) -> {
        t.exportName = k;
    }, t -> t.exportName).add()).afterDecode(asset -> {
        if (asset.exportName != null && !asset.exportName.isEmpty()) {
            if (exportedNodes.containsKey(asset.exportName)) {
                LoggerUtil.getLogger().warning("Duplicate export name for asset: " + asset.exportName);
            }
            exportedNodes.put(asset.exportName, (PatternAsset)asset);
            LoggerUtil.getLogger().fine("Registered imported node asset with name '" + asset.exportName + "' with asset id '" + asset.id);
        }
    })).build();
    private String id;
    private AssetExtraInfo.Data data;
    private boolean skip = false;
    private String exportName = "";

    protected PatternAsset() {
    }

    public abstract Pattern build(@Nonnull Argument var1);

    public boolean isSkipped() {
        return this.skip;
    }

    public static PatternAsset getExportedAsset(@Nonnull String name) {
        return exportedNodes.get(name);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void cleanUp() {
    }

    @Nonnull
    public static Argument argumentFrom(@Nonnull DirectionalityAsset.Argument argument) {
        return new Argument(argument.parentSeed, argument.materialCache, argument.referenceBundle, argument.workerIndexer);
    }

    @Nonnull
    public static Argument argumentFrom(@Nonnull PropAsset.Argument argument) {
        return new Argument(argument.parentSeed, argument.materialCache, argument.referenceBundle, argument.workerIndexer);
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
            this.workerIndexer = argument.workerIndexer;
        }
    }
}

