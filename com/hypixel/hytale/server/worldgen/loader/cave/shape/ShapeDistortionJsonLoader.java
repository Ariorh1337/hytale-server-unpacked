/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave.shape;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.server.worldgen.cave.shape.distorted.ShapeDistortion;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShapeDistortionJsonLoader<K extends SeedResource>
extends JsonLoader<K, ShapeDistortion> {
    public ShapeDistortionJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".ShapeDistortion"), dataFolder, json);
    }

    @Override
    public ShapeDistortion load() {
        return ShapeDistortion.of(this.loadWidth(), this.loadFloor(), this.loadCeiling());
    }

    @Nullable
    private NoiseProperty loadWidth() {
        if (this.has("Width")) {
            return new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("Width")).load();
        }
        return null;
    }

    @Nullable
    private NoiseProperty loadFloor() {
        if (this.has("Floor")) {
            return new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("Floor")).load();
        }
        return null;
    }

    @Nullable
    private NoiseProperty loadCeiling() {
        if (this.has("Ceiling")) {
            return new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("Ceiling")).load();
        }
        return null;
    }

    public static interface Constants {
        public static final String KEY_WIDTH = "Width";
        public static final String KEY_FLOOR = "Floor";
        public static final String KEY_CEILING = "Ceiling";
    }
}

