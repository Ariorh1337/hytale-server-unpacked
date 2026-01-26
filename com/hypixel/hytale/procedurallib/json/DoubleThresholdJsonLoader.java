/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.condition.DefaultDoubleThresholdCondition;
import com.hypixel.hytale.procedurallib.condition.DoubleThreshold;
import com.hypixel.hytale.procedurallib.condition.IDoubleThreshold;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class DoubleThresholdJsonLoader<K extends SeedResource>
extends JsonLoader<K, IDoubleThreshold> {
    protected final boolean defaultValue;

    public DoubleThresholdJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        this(seed, dataFolder, json, true);
    }

    public DoubleThresholdJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, boolean defaultValue) {
        super(seed.append(".DoubleThreshold"), dataFolder, json);
        this.defaultValue = defaultValue;
    }

    @Override
    @Nonnull
    public IDoubleThreshold load() {
        if (this.json == null || this.json.isJsonNull()) {
            return this.defaultValue ? DefaultDoubleThresholdCondition.DEFAULT_TRUE : DefaultDoubleThresholdCondition.DEFAULT_FALSE;
        }
        if (this.json.isJsonPrimitive()) {
            double value = this.json.getAsDouble();
            return new DoubleThreshold.Single(0.0, value);
        }
        JsonArray jsonArray = this.json.getAsJsonArray();
        if (jsonArray.size() <= 0) {
            throw new IllegalArgumentException("Threshold array must contain at least one entry!");
        }
        if (jsonArray.get(0).isJsonArray()) {
            DoubleThreshold.Single[] entries = new DoubleThreshold.Single[jsonArray.size()];
            for (int i = 0; i < entries.length; ++i) {
                JsonArray jsonArrayEntry = jsonArray.get(i).getAsJsonArray();
                if (jsonArrayEntry.size() != 2) {
                    throw new IllegalArgumentException("Threshold array entries must have 2 numbers for lower/upper limit!");
                }
                entries[i] = new DoubleThreshold.Single(jsonArrayEntry.get(0).getAsDouble(), jsonArrayEntry.get(1).getAsDouble());
            }
            return new DoubleThreshold.Multiple(entries);
        }
        if (jsonArray.size() != 2) {
            throw new IllegalArgumentException("Threshold array entries must have 2 numbers for lower/upper limit!");
        }
        return new DoubleThreshold.Single(jsonArray.get(0).getAsDouble(), jsonArray.get(1).getAsDouble());
    }

    public static interface Constants {
        public static final String ERROR_NO_ENTRY = "Threshold array must contain at least one entry!";
        public static final String ERROR_THRESHOLD_SIZE = "Threshold array entries must have 2 numbers for lower/upper limit!";
    }
}

