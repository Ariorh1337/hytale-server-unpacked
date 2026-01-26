/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments.AssignmentsAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propassignments.ConstantAssignmentsAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.Assignments;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.FieldFunctionAssignments;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class FieldFunctionAssignmentsAsset
extends AssignmentsAsset {
    public static final BuilderCodec<FieldFunctionAssignmentsAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FieldFunctionAssignmentsAsset.class, FieldFunctionAssignmentsAsset::new, AssignmentsAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Delimiters", new ArrayCodec(DelimiterAsset.CODEC, DelimiterAsset[]::new), true), (asset, v) -> {
        asset.delimiterAssets = v;
    }, asset -> asset.delimiterAssets).add()).append(new KeyedCodec("FieldFunction", DensityAsset.CODEC, true), (asset, v) -> {
        asset.densityAsset = v;
    }, asset -> asset.densityAsset).add()).build();
    private DelimiterAsset[] delimiterAssets = new DelimiterAsset[0];
    private DensityAsset densityAsset = new ConstantDensityAsset();

    @Override
    @Nonnull
    public Assignments build(@Nonnull AssignmentsAsset.Argument argument) {
        if (super.skip()) {
            return Assignments.noPropDistribution(argument.runtime);
        }
        Density functionTree = this.densityAsset.build(DensityAsset.from(argument));
        ArrayList<FieldFunctionAssignments.FieldDelimiter> delimiterList = new ArrayList<FieldFunctionAssignments.FieldDelimiter>();
        for (DelimiterAsset asset : this.delimiterAssets) {
            Assignments propDistribution = asset.assignmentsAsset.build(argument);
            FieldFunctionAssignments.FieldDelimiter delimiter = new FieldFunctionAssignments.FieldDelimiter(propDistribution, asset.min, asset.max);
            delimiterList.add(delimiter);
        }
        return new FieldFunctionAssignments(functionTree, delimiterList, argument.runtime);
    }

    @Override
    public void cleanUp() {
        this.densityAsset.cleanUp();
    }

    public static class DelimiterAsset
    implements Cleanable,
    JsonAssetWithMap<String, DefaultAssetMap<String, DelimiterAsset>> {
        public static final AssetBuilderCodec<String, DelimiterAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(DelimiterAsset.class, DelimiterAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec("Assignments", AssignmentsAsset.CODEC, true), (t, v) -> {
            t.assignmentsAsset = v;
        }, t -> t.assignmentsAsset).add()).append(new KeyedCodec<Double>("Min", Codec.DOUBLE, true), (t, v) -> {
            t.min = v;
        }, t -> t.min).add()).append(new KeyedCodec<Double>("Max", Codec.DOUBLE, true), (t, v) -> {
            t.max = v;
        }, t -> t.max).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private double min;
        private double max;
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

