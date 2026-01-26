/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OffsetDensity
extends Density {
    @Nonnull
    private final Double2DoubleFunction offsetFunc;
    @Nullable
    private Density input;

    public OffsetDensity(@Nonnull Double2DoubleFunction offsetFunction, Density input) {
        this.offsetFunc = offsetFunction;
        this.input = input;
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        if (this.input == null) {
            return this.offsetFunc.get(context.position.y);
        }
        return this.input.process(context) + this.offsetFunc.get(context.position.y);
    }

    @Override
    public void setInputs(@Nonnull Density[] inputs) {
        if (inputs.length == 0) {
            this.input = null;
        }
        this.input = inputs[0];
    }
}

