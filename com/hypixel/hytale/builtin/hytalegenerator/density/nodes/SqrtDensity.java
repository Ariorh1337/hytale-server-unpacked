/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SqrtDensity
extends Density {
    @Nullable
    private Density input;

    public SqrtDensity(Density input) {
        this.input = input;
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        if (this.input == null) {
            return 0.0;
        }
        double v = this.input.process(context);
        if (v < 0.0) {
            return -Math.sqrt(-v);
        }
        return Math.sqrt(v);
    }

    @Override
    public void setInputs(@Nonnull Density[] inputs) {
        if (inputs.length == 0) {
            this.input = null;
        }
        this.input = inputs[0];
    }
}

