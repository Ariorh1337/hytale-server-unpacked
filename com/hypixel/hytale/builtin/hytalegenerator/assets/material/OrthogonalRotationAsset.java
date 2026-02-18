/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.material;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import javax.annotation.Nonnull;

public class OrthogonalRotationAsset
implements JsonAssetWithMap<String, DefaultAssetMap<String, OrthogonalRotationAsset>> {
    @Nonnull
    public static final AssetBuilderCodec<String, OrthogonalRotationAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(OrthogonalRotationAsset.class, OrthogonalRotationAsset::new, Codec.STRING, (asset, id) -> {
        asset.id = id;
    }, config -> config.id, (config, data) -> {
        config.data = data;
    }, config -> config.data).append(new KeyedCodec<Rotation>("Yaw", Rotation.CODEC, false), (asset, value) -> {
        asset.yaw = value;
    }, asset -> asset.yaw).add()).append(new KeyedCodec<Rotation>("Pitch", Rotation.CODEC, false), (asset, value) -> {
        asset.pitch = value;
    }, asset -> asset.pitch).add()).append(new KeyedCodec<Rotation>("Roll", Rotation.CODEC, false), (asset, value) -> {
        asset.roll = value;
    }, asset -> asset.roll).add()).build();
    private String id;
    private AssetExtraInfo.Data data;
    @Nonnull
    private Rotation yaw = Rotation.None;
    @Nonnull
    private Rotation pitch = Rotation.None;
    @Nonnull
    private Rotation roll = Rotation.None;

    @Nonnull
    public RotationTuple build() {
        return RotationTuple.of(this.yaw, this.pitch, this.roll);
    }

    public boolean isNone() {
        return this.yaw == Rotation.None && this.pitch == Rotation.None && this.roll == Rotation.None;
    }

    @Override
    public String getId() {
        return this.id;
    }
}

