/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.property;

import com.hypixel.hytale.procedurallib.property.NoiseProperty;

public class InvertNoiseProperty
implements NoiseProperty {
    protected final NoiseProperty noiseProperty;

    public InvertNoiseProperty(NoiseProperty noiseProperty) {
        this.noiseProperty = noiseProperty;
    }

    @Override
    public double get(int seed, double x, double y) {
        return 1.0 - this.noiseProperty.get(seed, x, y);
    }

    @Override
    public double get(int seed, double x, double y, double z) {
        return 1.0 - this.noiseProperty.get(seed, x, y, z);
    }
}

