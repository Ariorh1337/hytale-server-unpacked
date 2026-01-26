/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments.AssignmentsAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments.ConstantAssignmentsAsset;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.Assignments;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.WeightedAssignments;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import javax.annotation.Nonnull;

public class WeightedAssignmentsAsset
extends AssignmentsAsset {
    public static final BuilderCodec<WeightedAssignmentsAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WeightedAssignmentsAsset.class, WeightedAssignmentsAsset::new, AssignmentsAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("SkipChance", Codec.DOUBLE, true), (asset, v) -> {
        asset.skipChance = v;
    }, asset -> asset.skipChance).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, true), (asset, v) -> {
        asset.seed = v;
    }, asset -> asset.seed).add()).append(new KeyedCodec<T[]>("WeightedAssignments", new ArrayCodec(WeightedAssets.CODEC, WeightedAssets[]::new), true), (asset, v) -> {
        asset.weightedAssets = v;
    }, asset -> asset.weightedAssets).add()).build();
    private WeightedAssets[] weightedAssets = new WeightedAssets[0];
    private String seed = "";
    private double skipChance = 0.0;

    @Override
    @Nonnull
    public Assignments build(@Nonnull AssignmentsAsset.Argument argument) {
        if (super.skip()) {
            return Assignments.noPropDistribution(argument.runtime);
        }
        WeightedMap<Assignments> weightMap = new WeightedMap<Assignments>();
        for (WeightedAssets asset : this.weightedAssets) {
            weightMap.add(asset.assignmentsAsset.build(argument), asset.weight);
        }
        SeedBox childSeed = argument.parentSeed.child(this.seed);
        return new WeightedAssignments(weightMap, childSeed.createSupplier().get(), this.skipChance, argument.runtime);
    }

    @Override
    public void cleanUp() {
        for (WeightedAssets weightedAsset : this.weightedAssets) {
            weightedAsset.cleanUp();
        }
    }

    public static class WeightedAssets
    implements Cleanable,
    JsonAssetWithMap<String, DefaultAssetMap<String, WeightedAssets>> {
        public static final AssetBuilderCodec<String, WeightedAssets> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(WeightedAssets.class, WeightedAssets::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Double>("Weight", Codec.DOUBLE, true), (t, v) -> {
            t.weight = v;
        }, t -> t.weight).add()).append(new KeyedCodec("Assignments", AssignmentsAsset.CODEC, true), (t, v) -> {
            t.assignmentsAsset = v;
        }, t -> t.assignmentsAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double weight = 1.0;
        private AssignmentsAsset assignmentsAsset = new ConstantAssignmentsAsset();

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void cleanUp() {
            this.assignmentsAsset.cleanUp();
        }
    }
}

