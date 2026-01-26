/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib;

import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.NoiseFunction2d;
import com.hypixel.hytale.procedurallib.NoiseFunction3d;
import javax.annotation.Nonnull;

public class NoiseFunctionPair
implements NoiseFunction {
    protected NoiseFunction2d noiseFunction2d;
    protected NoiseFunction3d noiseFunction3d;

    public NoiseFunctionPair() {
    }

    public NoiseFunctionPair(NoiseFunction2d noiseFunction2d, NoiseFunction3d noiseFunction3d) {
        this.noiseFunction2d = noiseFunction2d;
        this.noiseFunction3d = noiseFunction3d;
    }

    public NoiseFunction2d getNoiseFunction2d() {
        return this.noiseFunction2d;
    }

    public void setNoiseFunction2d(NoiseFunction2d noiseFunction2d) {
        this.noiseFunction2d = noiseFunction2d;
    }

    public NoiseFunction3d getNoiseFunction3d() {
        return this.noiseFunction3d;
    }

    public void setNoiseFunction3d(NoiseFunction3d noiseFunction3d) {
        this.noiseFunction3d = noiseFunction3d;
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y) {
        return this.noiseFunction2d.get(seed, offsetSeed, x, y);
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y, double z) {
        return this.noiseFunction3d.get(seed, offsetSeed, x, y, z);
    }

    @Nonnull
    public String toString() {
        return "NoiseFunctionPair{noiseFunction2d=" + String.valueOf(this.noiseFunction2d) + ", noiseFunction3d=" + String.valueOf(this.noiseFunction3d) + "}";
    }
}

