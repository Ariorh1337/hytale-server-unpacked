/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.legacy.NodeFunctionYOutAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.AmplitudeDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class AmplitudeDensityAsset
extends DensityAsset {
    public static final BuilderCodec<AmplitudeDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(AmplitudeDensityAsset.class, AmplitudeDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec("FunctionForY", NodeFunctionYOutAsset.CODEC, true), (t, k) -> {
        t.nodeFunctionYOutAsset = k;
    }, k -> k.nodeFunctionYOutAsset).add()).build();
    private NodeFunctionYOutAsset nodeFunctionYOutAsset = new NodeFunctionYOutAsset();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        return new AmplitudeDensity(this.nodeFunctionYOutAsset.build(), this.buildFirstInput(argument));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
        this.nodeFunctionYOutAsset.cleanUp();
    }
}

