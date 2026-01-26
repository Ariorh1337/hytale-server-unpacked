/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.GridNoise;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class GridNoiseJsonLoader<K extends SeedResource>
extends JsonLoader<K, NoiseFunction> {
    public GridNoiseJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".GridNoise"), dataFolder, json);
    }

    @Override
    @Nonnull
    public NoiseFunction load() {
        double defaultThickness = this.loadDefaultThickness();
        return new GridNoise(this.loadThicknessX(defaultThickness), this.loadThicknessY(defaultThickness), this.loadThicknessZ(defaultThickness));
    }

    protected double loadDefaultThickness() {
        if (!this.has("Thickness")) {
            return Double.NaN;
        }
        return this.get("Thickness").getAsDouble();
    }

    protected double loadThicknessX(double defaultThickness) {
        return this.loadThickness("ThicknessX", defaultThickness);
    }

    protected double loadThicknessY(double defaultThickness) {
        return this.loadThickness("ThicknessY", defaultThickness);
    }

    protected double loadThicknessZ(double defaultThickness) {
        if (Double.isNaN(defaultThickness)) {
            defaultThickness = 0.0;
        }
        return this.loadThickness("ThicknessZ", defaultThickness);
    }

    protected double loadThickness(String key, double defaultThickness) {
        double value = defaultThickness;
        if (this.has(key)) {
            value = this.get(key).getAsDouble();
        }
        if (Double.isNaN(value)) {
            throw new Error(String.format("Could not find thickness '%s' and no default 'Thickness' value defined!", key));
        }
        return value;
    }

    public static interface Constants {
        public static final double DEFAULT_NO_THICKNESS = Double.NaN;
        public static final double DEFAULT_THICKNESS_Z = 0.0;
        public static final String KEY_THICKNESS = "Thickness";
        public static final String KEY_THICKNESS_X = "ThicknessX";
        public static final String KEY_THICKNESS_Y = "ThicknessY";
        public static final String KEY_THICKNESS_Z = "ThicknessZ";
        public static final String ERROR_NO_THICKNESS = "Could not find thickness '%s' and no default 'Thickness' value defined!";
    }
}

