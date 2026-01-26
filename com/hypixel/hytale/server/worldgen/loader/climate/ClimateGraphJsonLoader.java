/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.climate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.climate.ClimateGraph;
import com.hypixel.hytale.server.worldgen.climate.ClimateType;
import com.hypixel.hytale.server.worldgen.loader.climate.ClimateTypeJsonLoader;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class ClimateGraphJsonLoader<K extends SeedResource>
extends JsonLoader<K, ClimateGraph> {
    public ClimateGraphJsonLoader(SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public ClimateGraph load() {
        ClimateType[] climates = this.loadClimates();
        ClimateGraph.FadeMode fadeMode = this.loadFadeMode();
        double fadeRadius = this.loadFadeRadius();
        double fadeDistance = this.loadFadeDistance();
        return new ClimateGraph(512, climates, fadeMode, fadeRadius, fadeDistance);
    }

    protected ClimateGraph.FadeMode loadFadeMode() {
        String fadeMode = this.mustGetString("FadeMode", Constants.DEFAULT_FADE_MODE);
        return ClimateGraph.FadeMode.valueOf(fadeMode);
    }

    protected double loadFadeRadius() {
        return this.mustGetNumber("FadeRadius", Constants.DEFAULT_FADE_RADIUS).doubleValue();
    }

    protected double loadFadeDistance() {
        return this.mustGetNumber("FadeDistance", Constants.DEFAULT_FADE_DISTANCE).doubleValue();
    }

    @Nonnull
    protected ClimateType[] loadClimates() {
        JsonArray climatesArr = this.mustGetArray("Climates", Constants.DEFAULT_CLIMATES);
        ClimateType[] climates = new ClimateType[climatesArr.size()];
        for (int i = 0; i < climatesArr.size(); ++i) {
            JsonElement climateJson = climatesArr.get(i);
            climates[i] = new ClimateTypeJsonLoader(this.seed, this.dataFolder, climateJson, null).load();
        }
        return climates;
    }

    public static interface Constants {
        public static final String KEY_CLIMATE = "Climate";
        public static final String KEY_FADE_MODE = "FadeMode";
        public static final String KEY_FADE_RADIUS = "FadeRadius";
        public static final String KEY_FADE_DISTANCE = "FadeDistance";
        public static final String KEY_CLIMATES = "Climates";
        public static final JsonArray DEFAULT_CLIMATES = new JsonArray();
        public static final Double DEFAULT_FADE_RADIUS = 50.0;
        public static final Double DEFAULT_FADE_DISTANCE = 100.0;
        public static final String DEFAULT_FADE_MODE = ClimateGraph.FadeMode.CHILDREN.name();
    }
}

