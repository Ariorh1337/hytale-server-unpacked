/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.PointNoise;
import java.nio.file.Path;
import javax.annotation.Nullable;

public class PointNoiseJsonLoader<K extends SeedResource>
extends JsonLoader<K, PointNoise> {
    public PointNoiseJsonLoader(SeedString<K> seed, Path dataFolder, @Nullable JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nullable
    public PointNoise load() {
        return new PointNoise(this.mustGetNumber("X", Constants.DEFAULT_COORD).doubleValue(), this.mustGetNumber("Y", Constants.DEFAULT_COORD).doubleValue(), this.mustGetNumber("Z", Constants.DEFAULT_COORD).doubleValue(), this.mustGetNumber("InnerRadius", Constants.DEFAULT_RADIUS).doubleValue(), this.mustGetNumber("OuterRadius", Constants.DEFAULT_RADIUS).doubleValue());
    }

    public static interface Constants {
        public static final String KEY_X = "X";
        public static final String KEY_Y = "Y";
        public static final String KEY_Z = "Z";
        public static final String KEY_INNER_RADIUS = "InnerRadius";
        public static final String KEY_OUTER_RADIUS = "OuterRadius";
        public static final Double DEFAULT_COORD = 0.0;
        public static final Double DEFAULT_RADIUS = 0.0;
    }
}

