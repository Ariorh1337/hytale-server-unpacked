/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.SwitchDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SwitchDensityAsset
extends DensityAsset {
    public static final BuilderCodec<SwitchDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(SwitchDensityAsset.class, SwitchDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("SwitchCases", new ArrayCodec(SwitchCaseAsset.CODEC, SwitchCaseAsset[]::new), false), (t, k) -> {
        t.switchCaseAssets = k;
    }, t -> t.switchCaseAssets).add()).build();
    public static final String DEFAULT_STATE = "Default";
    public static final int DEFAULT_STATE_HASH = 0;
    private SwitchCaseAsset[] switchCaseAssets = new SwitchCaseAsset[0];

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        ArrayList<Integer> switchStates = new ArrayList<Integer>();
        ArrayList<Density> densityNodes = new ArrayList<Density>();
        for (int i = 0; i < this.switchCaseAssets.length; ++i) {
            if (this.switchCaseAssets[i] == null || this.switchCaseAssets[i].densityAsset == null) continue;
            String stringState = this.switchCaseAssets[i].caseState;
            int stateHash = SwitchDensityAsset.getHashFromState(stringState);
            Density densityNode = this.switchCaseAssets[i].densityAsset.build(argument);
            switchStates.add(stateHash);
            densityNodes.add(densityNode);
        }
        return new SwitchDensity(densityNodes, switchStates);
    }

    public static int getHashFromState(String state) {
        if (DEFAULT_STATE.equals(state)) {
            return 0;
        }
        return Objects.hash(state);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        for (SwitchCaseAsset switchCaseAsset : this.switchCaseAssets) {
            switchCaseAsset.cleanUp();
        }
    }

    public static class SwitchCaseAsset
    implements Cleanable,
    JsonAssetWithMap<String, DefaultAssetMap<String, SwitchCaseAsset>> {
        public static final AssetBuilderCodec<String, SwitchCaseAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(SwitchCaseAsset.class, SwitchCaseAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<String>("CaseState", Codec.STRING, true), (t, y) -> {
            t.caseState = y;
        }, t -> t.caseState).add()).append(new KeyedCodec("Density", DensityAsset.CODEC, true), (t, out) -> {
            t.densityAsset = out;
        }, t -> t.densityAsset).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private String caseState = "";
        private DensityAsset densityAsset;

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void cleanUp() {
            this.densityAsset.cleanUp();
        }
    }
}

