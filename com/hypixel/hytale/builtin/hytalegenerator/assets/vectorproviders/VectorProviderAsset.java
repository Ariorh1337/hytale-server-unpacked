/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.ExportedVectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public abstract class VectorProviderAsset
implements Cleanable,
JsonAssetWithMap<String, DefaultAssetMap<String, VectorProviderAsset>> {
    @Nonnull
    public static final AssetCodecMapCodec<String, VectorProviderAsset> CODEC = new AssetCodecMapCodec<String, VectorProviderAsset>(Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (t, data) -> {
        t.data = data;
    }, t -> t.data);
    @Nonnull
    private static final Map<String, Exported> exportedNodes = new ConcurrentHashMap<String, Exported>();
    @Nonnull
    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec(VectorProviderAsset.class, CODEC);
    @Nonnull
    public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<String>(CHILD_ASSET_CODEC, String[]::new);
    @Nonnull
    public static final BuilderCodec<VectorProviderAsset> ABSTRACT_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(VectorProviderAsset.class).append(new KeyedCodec<Boolean>("Skip", Codec.BOOLEAN, false), (t, k) -> {
        t.skip = k;
    }, t -> t.skip).add()).append(new KeyedCodec<String>("ExportAs", Codec.STRING, false), (t, k) -> {
        t.exportName = k;
    }, t -> t.exportName).add()).afterDecode(asset -> {
        if (asset.exportName != null && !asset.exportName.isEmpty()) {
            boolean isSingleInstance;
            if (exportedNodes.containsKey(asset.exportName)) {
                LoggerUtil.getLogger().warning("Duplicate export name for asset: " + asset.exportName);
            }
            if (asset instanceof ExportedVectorProviderAsset) {
                ExportedVectorProviderAsset exportedAsset = (ExportedVectorProviderAsset)asset;
                isSingleInstance = exportedAsset.isSingleInstance();
            } else {
                isSingleInstance = false;
            }
            Exported exported = new Exported(isSingleInstance, (VectorProviderAsset)asset);
            exportedNodes.put(asset.exportName, exported);
            LoggerUtil.getLogger().fine("Registered imported node asset with name '" + asset.exportName + "' with asset id '" + asset.id);
        }
    })).build();
    private String id;
    private AssetExtraInfo.Data data;
    protected boolean skip = false;
    protected String exportName = "";

    protected VectorProviderAsset() {
    }

    public abstract VectorProvider build(@Nonnull Argument var1);

    public boolean isSkipped() {
        return this.skip;
    }

    public static Exported getExportedAsset(@Nonnull String name) {
        return exportedNodes.get(name);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void cleanUp() {
    }

    public static class Exported {
        public boolean isSingleInstance;
        @Nonnull
        public VectorProviderAsset asset;
        @Nonnull
        public Map<WorkerIndexer.Id, VectorProvider> threadInstances;

        public Exported(boolean isSingleInstance, @Nonnull VectorProviderAsset asset) {
            this.isSingleInstance = isSingleInstance;
            this.asset = asset;
            this.threadInstances = new ConcurrentHashMap<WorkerIndexer.Id, VectorProvider>();
        }
    }

    public static class Argument {
        public SeedBox parentSeed;
        public ReferenceBundle referenceBundle;
        public WorkerIndexer.Id workerId;

        public Argument(@Nonnull SeedBox parentSeed, @Nonnull ReferenceBundle referenceBundle, @Nonnull WorkerIndexer.Id workerId) {
            this.parentSeed = parentSeed;
            this.referenceBundle = referenceBundle;
            this.workerId = workerId;
        }

        public Argument(@Nonnull Argument argument) {
            this.parentSeed = argument.parentSeed;
            this.referenceBundle = argument.referenceBundle;
            this.workerId = argument.workerId;
        }

        public Argument(@Nonnull DensityAsset.Argument argument) {
            this.parentSeed = argument.parentSeed;
            this.referenceBundle = argument.referenceBundle;
            this.workerId = argument.workerId;
        }
    }
}

