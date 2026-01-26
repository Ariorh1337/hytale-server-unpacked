/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.cell.jitter.CellJitter;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public abstract class AbstractCellJitterJsonLoader<K extends SeedResource, T>
extends JsonLoader<K, T> {
    public AbstractCellJitterJsonLoader(SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Nonnull
    protected CellJitter loadJitter() {
        double defaultJitter = this.loadDefaultJitter();
        double x = this.loadJitterX(defaultJitter);
        double y = this.loadJitterY(defaultJitter);
        double z = this.loadJitterZ(defaultJitter);
        return CellJitter.of(x, y, z);
    }

    protected double loadDefaultJitter() {
        return AbstractCellJitterJsonLoader.loadJitter(this, "Jitter", 1.0);
    }

    protected double loadJitterX(double defaultJitter) {
        return AbstractCellJitterJsonLoader.loadJitter(this, "JitterX", defaultJitter);
    }

    protected double loadJitterY(double defaultJitter) {
        return AbstractCellJitterJsonLoader.loadJitter(this, "JitterY", defaultJitter);
    }

    protected double loadJitterZ(double defaultJitter) {
        return AbstractCellJitterJsonLoader.loadJitter(this, "JitterZ", defaultJitter);
    }

    protected static double loadJitter(@Nonnull JsonLoader<?, ?> loader, String key, double defaultJitter) {
        if (!loader.has(key)) {
            return defaultJitter;
        }
        return loader.get(key).getAsDouble();
    }
}

