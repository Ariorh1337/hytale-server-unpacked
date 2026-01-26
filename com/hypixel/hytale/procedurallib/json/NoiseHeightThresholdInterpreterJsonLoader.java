/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import com.hypixel.hytale.procedurallib.condition.NoiseHeightThresholdInterpreter;
import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoiseHeightThresholdInterpreterJsonLoader<K extends SeedResource>
extends JsonLoader<K, NoiseHeightThresholdInterpreter> {
    protected final int length;

    public NoiseHeightThresholdInterpreterJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, int length) {
        super(seed.append(".NoiseHeightThresholdInterpreter"), dataFolder, json);
        this.length = length;
    }

    @Override
    @Nonnull
    public NoiseHeightThresholdInterpreter load() {
        IHeightThresholdInterpreter[] interpreters = this.loadInterpreters();
        float[] keys = this.loadKeys();
        if (keys.length != interpreters.length) {
            throw new IllegalArgumentException("Keys and Thresholds array do not have the same length!");
        }
        return new NoiseHeightThresholdInterpreter(this.loadNoise(), keys, interpreters);
    }

    @Nullable
    protected NoiseProperty loadNoise() {
        if (!this.has("Noise")) {
            throw new IllegalStateException("Could not find noise map data in NoiseHeightThresholdInterpreter. Keyword: Noise");
        }
        return new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("Noise")).load();
    }

    @Nonnull
    protected IHeightThresholdInterpreter[] loadInterpreters() {
        if (!this.has("Thresholds")) {
            throw new IllegalStateException("Could not find threshold data in NoiseHeightThresholdInterpreter. Keyword: Thresholds");
        }
        JsonArray array = this.get("Thresholds").getAsJsonArray();
        IHeightThresholdInterpreter[] interpreters = new IHeightThresholdInterpreter[array.size()];
        for (int i = 0; i < interpreters.length; ++i) {
            interpreters[i] = new HeightThresholdInterpreterJsonLoader(this.seed.append("-" + i), this.dataFolder, array.get(i), this.length).load();
        }
        return interpreters;
    }

    protected float[] loadKeys() {
        if (!this.has("Keys")) {
            throw new IllegalStateException("Could not find key data in NoiseHeightThresholdInterpreter. Keyword: Keys");
        }
        JsonArray array = this.get("Keys").getAsJsonArray();
        float[] keys = new float[array.size()];
        for (int i = 0; i < keys.length; ++i) {
            keys[i] = array.get(i).getAsFloat();
        }
        return keys;
    }

    public static boolean shouldHandle(@Nonnull JsonObject jsonObject) {
        return jsonObject.has("Thresholds");
    }

    public static interface Constants {
        public static final String KEY_NOISE = "Noise";
        public static final String KEY_THRESHOLDS = "Thresholds";
        public static final String KEY_KEYS = "Keys";
        public static final String ERROR_NO_NOISE = "Could not find noise map data in NoiseHeightThresholdInterpreter. Keyword: Noise";
        public static final String ERROR_NO_THRESHOLDS = "Could not find threshold data in NoiseHeightThresholdInterpreter. Keyword: Thresholds";
        public static final String ERROR_NO_KEYS = "Could not find key data in NoiseHeightThresholdInterpreter. Keyword: Keys";
    }
}

