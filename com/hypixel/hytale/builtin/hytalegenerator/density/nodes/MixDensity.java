/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;

public class MixDensity
extends Density {
    private Density densityA;
    private Density densityB;
    private Density influenceDensity;

    public MixDensity(@Nonnull Density densityA, @Nonnull Density densityB, @Nonnull Density influenceDensity) {
        this.densityA = densityA;
        this.densityB = densityB;
        this.influenceDensity = influenceDensity;
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        double THRESHOLD_INPUT_A = 0.0;
        double THRESHOLD_INPUT_B = 1.0;
        double influence = this.influenceDensity.process(context);
        if (influence <= 0.0) {
            return this.densityA.process(context);
        }
        if (influence >= 1.0) {
            return this.densityB.process(context);
        }
        double valueA = this.densityA.process(context);
        double valueB = this.densityB.process(context);
        double mixedValue = valueA * (1.0 - influence) + valueB * influence;
        return mixedValue;
    }

    @Override
    public void setInputs(@Nonnull Density[] inputs) {
        if (inputs.length != 3) {
            throw new IllegalArgumentException("inputs.length != 3");
        }
        this.densityA = inputs[0];
        this.densityB = inputs[1];
        this.influenceDensity = inputs[2];
    }
}

