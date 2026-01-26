/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.GradientNoiseProperty;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class GradientNoisePropertyJsonLoader<K extends SeedResource>
extends JsonLoader<K, GradientNoiseProperty> {
    protected final NoiseProperty noise;

    public GradientNoisePropertyJsonLoader(SeedString<K> seed, Path dataFolder, JsonElement json, NoiseProperty noise) {
        super(seed, dataFolder, json);
        this.noise = noise;
    }

    @Override
    @Nonnull
    public GradientNoiseProperty load() {
        return new GradientNoiseProperty(this.noise, this.loadMode(), this.loadDistance(), this.loadNormalization());
    }

    @Nonnull
    protected GradientNoiseProperty.GradientMode loadMode() {
        GradientNoiseProperty.GradientMode mode = Constants.DEFAULT_MODE;
        if (this.has("Mode")) {
            mode = GradientNoiseProperty.GradientMode.valueOf(this.get("Mode").getAsString());
        }
        return mode;
    }

    protected double loadDistance() {
        double distance = 5.0;
        if (this.has("Distance")) {
            distance = this.get("Distance").getAsDouble();
        }
        return distance;
    }

    protected double loadNormalization() {
        double distance = 0.1;
        if (this.has("Normalize")) {
            distance = this.get("Normalize").getAsDouble();
        }
        return distance;
    }

    public static interface Constants {
        public static final String KEY_MODE = "Mode";
        public static final String KEY_DISTANCE = "Distance";
        public static final String KEY_NORMALIZE = "Normalize";
        public static final GradientNoiseProperty.GradientMode DEFAULT_MODE = GradientNoiseProperty.GradientMode.MAGNITUDE;
        public static final double DEFAULT_DISTANCE = 5.0;
        public static final double DEFAULT_NORMALIZATION = 0.1;
    }
}

