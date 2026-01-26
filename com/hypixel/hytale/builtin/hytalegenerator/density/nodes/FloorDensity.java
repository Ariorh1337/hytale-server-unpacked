/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloorDensity
extends Density {
    private double limit;
    @Nullable
    private Density input;

    public FloorDensity(double limit, Density input) {
        this.limit = limit;
        this.input = input;
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        if (this.input == null) {
            return 0.0;
        }
        return Math.max(this.input.process(context), this.limit);
    }

    @Override
    public void setInputs(@Nonnull Density[] inputs) {
        if (inputs.length == 0) {
            this.input = null;
        }
        this.input = inputs[0];
    }
}

