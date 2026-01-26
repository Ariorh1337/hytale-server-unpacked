/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.MixDensity;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import java.util.List;
import javax.annotation.Nonnull;

public class MixDensityAsset
extends DensityAsset {
    public static final BuilderCodec<MixDensityAsset> CODEC = BuilderCodec.builder(MixDensityAsset.class, MixDensityAsset::new, DensityAsset.ABSTRACT_CODEC).build();

    @Override
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity(0.0);
        }
        List<Density> builtInputs = this.buildInputs(argument, true);
        if (builtInputs.size() != 3) {
            return new ConstantValueDensity(0.0);
        }
        return new MixDensity(builtInputs.get(0), builtInputs.get(1), builtInputs.get(2));
    }

    @Override
    public void cleanUp() {
        this.cleanUpInputs();
    }
}

