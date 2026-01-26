/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.SwitchStateDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SwitchStateDensityAsset
extends DensityAsset {
    public static final BuilderCodec<SwitchStateDensityAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(SwitchStateDensityAsset.class, SwitchStateDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("SwitchState", Codec.STRING, true), (t, k) -> {
        t.switchState = k;
    }, t -> t.switchState).add()).build();
    private String switchState = "";

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        int stateHash = Objects.hash(this.switchState);
        return new SwitchStateDensity(this.buildFirstInput(argument), stateHash);
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

