/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.climate;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.climate.ClimatePoint;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClimatePointJsonLoader<K extends SeedResource>
extends JsonLoader<K, ClimatePoint> {
    public ClimatePointJsonLoader(SeedString<K> seed, Path dataFolder, @Nullable JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public ClimatePoint load() {
        return new ClimatePoint(this.loadTemperature(), this.loadIntensity(), this.loadModifier());
    }

    protected double loadTemperature() {
        return this.mustGetNumber("Temperature", Constants.DEFAULT_TEMPERATURE).doubleValue();
    }

    protected double loadIntensity() {
        return this.mustGetNumber("Intensity", Constants.DEFAULT_INTENSITY).doubleValue();
    }

    protected double loadModifier() {
        return this.mustGetNumber("Modifier", Constants.DEFAULT_MODIFIER).doubleValue();
    }

    public static interface Constants {
        public static final String KEY_TEMPERATURE = "Temperature";
        public static final String KEY_INTENSITY = "Intensity";
        public static final String KEY_MODIFIER = "Modifier";
        public static final Double DEFAULT_TEMPERATURE = 0.5;
        public static final Double DEFAULT_INTENSITY = 0.5;
        public static final Double DEFAULT_MODIFIER = 1.0;
    }
}

