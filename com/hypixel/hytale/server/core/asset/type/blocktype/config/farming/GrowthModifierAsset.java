/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.farming;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public abstract class GrowthModifierAsset
implements JsonAssetWithMap<String, DefaultAssetMap<String, GrowthModifierAsset>> {
    public static final AssetCodecMapCodec<String, GrowthModifierAsset> CODEC = new AssetCodecMapCodec<String, GrowthModifierAsset>(Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (t, data) -> {
        t.data = data;
    }, t -> t.data);
    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec(GrowthModifierAsset.class, CODEC);
    public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<String>(CHILD_ASSET_CODEC, String[]::new);
    public static final BuilderCodec<GrowthModifierAsset> ABSTRACT_CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(GrowthModifierAsset.class).appendInherited(new KeyedCodec<Double>("Modifier", Codec.DOUBLE), (asset, modifier) -> {
        asset.modifier = modifier;
    }, asset -> asset.modifier, (asset, parent) -> {
        asset.modifier = parent.modifier;
    }).add()).build();
    private static AssetStore<String, GrowthModifierAsset, DefaultAssetMap<String, GrowthModifierAsset>> ASSET_STORE;
    private AssetExtraInfo.Data data;
    protected String id;
    protected double modifier;

    public static AssetStore<String, GrowthModifierAsset, DefaultAssetMap<String, GrowthModifierAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(GrowthModifierAsset.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, GrowthModifierAsset> getAssetMap() {
        return GrowthModifierAsset.getAssetStore().getAssetMap();
    }

    public GrowthModifierAsset() {
    }

    public GrowthModifierAsset(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public double getModifier() {
        return this.modifier;
    }

    public double getCurrentGrowthMultiplier(CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, int x, int y, int z, boolean initialTick) {
        return this.modifier;
    }

    @Nonnull
    public String toString() {
        return "GrowthModifierAsset{id='" + this.id + "', modifier=" + this.modifier + "}";
    }
}

