/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.ListPositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class ListPositionProviderAsset
extends PositionProviderAsset {
    public static final BuilderCodec<ListPositionProviderAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ListPositionProviderAsset.class, ListPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Positions", new ArrayCodec(PositionAsset.CODEC, PositionAsset[]::new), true), (asset, v) -> {
        asset.positions = v;
    }, asset -> asset.positions).add()).build();
    private PositionAsset[] positions = new PositionAsset[0];

    @Override
    @Nonnull
    public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
        if (super.skip()) {
            return PositionProvider.noPositionProvider();
        }
        ArrayList<Vector3i> list = new ArrayList<Vector3i>();
        for (PositionAsset asset : this.positions) {
            Vector3i position = new Vector3i(asset.x, asset.y, asset.z);
            list.add(position);
        }
        return ListPositionProvider.from3i(list);
    }

    public static class PositionAsset
    implements JsonAssetWithMap<String, DefaultAssetMap<String, PositionAsset>> {
        public static final AssetBuilderCodec<String, PositionAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(PositionAsset.class, PositionAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Integer>("X", Codec.INTEGER, true), (t, x) -> {
            t.x = x;
        }, t -> t.x).add()).append(new KeyedCodec<Integer>("Y", Codec.INTEGER, true), (t, y) -> {
            t.y = y;
        }, t -> t.y).add()).append(new KeyedCodec<Integer>("Z", Codec.INTEGER, true), (t, z) -> {
            t.z = z;
        }, t -> t.z).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private int x;
        private int y;
        private int z;

        @Override
        public String getId() {
            return this.id;
        }
    }
}

