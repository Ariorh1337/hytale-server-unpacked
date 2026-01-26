/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MultiMixDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class MultiMixDensityAsset
extends DensityAsset {
    public static final BuilderCodec<MultiMixDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(MultiMixDensityAsset.class, MultiMixDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Keys", new ArrayCodec(KeyAsset.CODEC, KeyAsset[]::new), true), (asset, v) -> {
        asset.keyAssets = v;
    }, asset -> asset.keyAssets).add()).build();
    private KeyAsset[] keyAssets = new KeyAsset[0];

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        List<Density> densityInputs = this.buildInputs(argument, true);
        if (densityInputs.isEmpty()) {
            return new ConstantValueDensity(0.0);
        }
        ArrayList<MultiMixDensity.Key> keys = new ArrayList<MultiMixDensity.Key>(this.keyAssets.length);
        for (KeyAsset keyAsset : this.keyAssets) {
            if (keyAsset.densityIndex <= 0) {
                keys.add(new MultiMixDensity.Key(keyAsset.value, null));
                continue;
            }
            if (keyAsset.densityIndex >= densityInputs.size() - 1) {
                LoggerUtil.getLogger().warning("Density Index out of bounds in MultiMix node " + keyAsset.densityIndex + ", valid range is [0, " + (densityInputs.size() - 1) + "]");
                keys.add(new MultiMixDensity.Key(keyAsset.value, null));
                continue;
            }
            Density density = densityInputs.get(keyAsset.densityIndex);
            keys.add(new MultiMixDensity.Key(keyAsset.value, density));
        }
        int i = 1;
        while (i < keys.size()) {
            MultiMixDensity.Key previousKey = (MultiMixDensity.Key)keys.get(i - 1);
            MultiMixDensity.Key currentKey = (MultiMixDensity.Key)keys.get(i);
            if (previousKey.value() == currentKey.value()) {
                keys.remove(i);
                continue;
            }
            ++i;
        }
        i = 0;
        while (i < keys.size()) {
            if (((MultiMixDensity.Key)keys.get(i)).density() == null) {
                keys.remove(i);
                continue;
            }
            ++i;
        }
        for (i = keys.size() - 1; i >= 0 && ((MultiMixDensity.Key)keys.get(i)).density() == null; --i) {
            keys.remove(i);
        }
        for (i = keys.size() - 2; i >= 0; --i) {
            if (((MultiMixDensity.Key)keys.get(i)).density() != null || ((MultiMixDensity.Key)keys.get(i + 1)).density() != null) continue;
            keys.remove(i);
        }
        if (keys.isEmpty()) {
            return new ConstantValueDensity(0.0);
        }
        if (keys.size() == 1) {
            return ((MultiMixDensity.Key)keys.getFirst()).density();
        }
        keys.trimToSize();
        Density influenceDensity = densityInputs.getLast();
        return new MultiMixDensity(keys, influenceDensity);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }

    public static class KeyAsset
    implements JsonAssetWithMap<String, DefaultAssetMap<String, KeyAsset>> {
        public static final int NO_DENSITY_INDEX = 0;
        public static final AssetBuilderCodec<String, KeyAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(KeyAsset.class, KeyAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Double>("Value", Codec.DOUBLE, true), (t, value) -> {
            t.value = value;
        }, t -> t.value).add()).append(new KeyedCodec<Integer>("DensityIndex", Codec.INTEGER, true), (t, value) -> {
            t.densityIndex = value;
        }, t -> t.densityIndex).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double value = 0.0;
        private int densityIndex = 0;

        @Override
        public String getId() {
            return this.id;
        }
    }
}

