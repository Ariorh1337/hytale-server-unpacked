/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.property;

import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import javax.annotation.Nonnull;

public class OffsetNoiseProperty
implements NoiseProperty {
    protected final NoiseProperty noiseProperty;
    protected final double offsetX;
    protected final double offsetY;
    protected final double offsetZ;

    public OffsetNoiseProperty(NoiseProperty noiseProperty, double offset) {
        this(noiseProperty, offset, offset, offset);
    }

    public OffsetNoiseProperty(NoiseProperty noiseProperty, double offsetX, double offsetY, double offsetZ) {
        this.noiseProperty = noiseProperty;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public NoiseProperty getNoiseProperty() {
        return this.noiseProperty;
    }

    public double getOffsetX() {
        return this.offsetX;
    }

    public double getOffsetY() {
        return this.offsetY;
    }

    public double getOffsetZ() {
        return this.offsetZ;
    }

    @Override
    public double get(int seed, double x, double y) {
        return this.noiseProperty.get(seed, x + this.offsetX, y + this.offsetY);
    }

    @Override
    public double get(int seed, double x, double y, double z) {
        return this.noiseProperty.get(seed, x + this.offsetX, y + this.offsetY, z + this.offsetZ);
    }

    @Nonnull
    public String toString() {
        return "OffsetNoiseProperty{noiseProperty=" + String.valueOf(this.noiseProperty) + ", offsetX=" + this.offsetX + ", offsetY=" + this.offsetY + ", offsetZ=" + this.offsetZ + "}";
    }
}

