/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.property;

import com.hypixel.hytale.procedurallib.logic.GeneralNoise;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class SumNoiseProperty
implements NoiseProperty {
    protected final Entry[] entries;

    public SumNoiseProperty(Entry[] entries) {
        this.entries = entries;
    }

    public Entry[] getEntries() {
        return this.entries;
    }

    @Override
    public double get(int seed, double x, double y) {
        double val = 0.0;
        for (Entry entry : this.entries) {
            val += entry.noiseProperty.get(seed, x, y) * entry.factor;
        }
        return GeneralNoise.limit(val);
    }

    @Override
    public double get(int seed, double x, double y, double z) {
        double val = 0.0;
        for (Entry entry : this.entries) {
            val += entry.noiseProperty.get(seed, x, y, z) * entry.factor;
        }
        return GeneralNoise.limit(val);
    }

    @Nonnull
    public String toString() {
        return "SumNoiseProperty{entries=" + Arrays.toString(this.entries) + "}";
    }

    public static class Entry {
        private NoiseProperty noiseProperty;
        private double factor;

        public Entry(NoiseProperty noiseProperty, double factor) {
            this.noiseProperty = noiseProperty;
            this.factor = factor;
        }

        public NoiseProperty getNoiseProperty() {
            return this.noiseProperty;
        }

        public void setNoiseProperty(NoiseProperty noiseProperty) {
            this.noiseProperty = noiseProperty;
        }

        public double getFactor() {
            return this.factor;
        }

        public void setFactor(double factor) {
            this.factor = factor;
        }

        @Nonnull
        public String toString() {
            return "Entry{noiseProperty=" + String.valueOf(this.noiseProperty) + ", factor=" + this.factor + "}";
        }
    }
}

