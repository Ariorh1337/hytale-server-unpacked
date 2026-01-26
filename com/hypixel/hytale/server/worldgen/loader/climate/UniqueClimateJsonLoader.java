/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.climate;

import com.google.gson.JsonElement;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.climate.ClimateSearch;
import com.hypixel.hytale.server.worldgen.climate.UniqueClimateGenerator;
import com.hypixel.hytale.server.worldgen.loader.climate.ClimateRuleJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.util.ColorUtil;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UniqueClimateJsonLoader<K extends SeedResource>
extends JsonLoader<K, UniqueClimateGenerator.Entry> {
    public UniqueClimateJsonLoader(SeedString<K> seed, Path dataFolder, @Nullable JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public UniqueClimateGenerator.Entry load() {
        return new UniqueClimateGenerator.Entry(this.loadName(), this.loadParent(), this.loadColor(), this.loadRadius(), this.loadOrigin(), this.loadMinDistance(), this.loadDistance(), this.loadRule());
    }

    protected String loadName() {
        return this.mustGetString("Name", null);
    }

    protected String loadParent() {
        return this.mustGetString("Parent", "");
    }

    protected int loadColor() {
        return ColorUtil.hexString(this.mustGetString("Color", null));
    }

    protected int loadRadius() {
        return this.mustGetNumber("Radius", Constants.DEFAULT_RADIUS).intValue();
    }

    @Nonnull
    protected Vector2i loadOrigin() {
        int x = this.mustGetNumber("OriginX", Constants.DEFAULT_OFFSET).intValue();
        int y = this.mustGetNumber("OriginY", Constants.DEFAULT_OFFSET).intValue();
        return new Vector2i(x, y);
    }

    protected int loadDistance() {
        return this.mustGetNumber("Distance", Constants.DEFAULT_SEARCH_RADIUS).intValue();
    }

    protected int loadMinDistance() {
        return this.mustGetNumber("MinDistance", Constants.DEFAULT_SEARCH_MIN_RADIUS).intValue();
    }

    protected ClimateSearch.Rule loadRule() {
        return new ClimateRuleJsonLoader(this.seed, this.dataFolder, this.mustGetObject("Rule", null)).load();
    }

    protected static interface Constants {
        public static final String KEY_ZONE = "Name";
        public static final String KEY_PARENT = "Parent";
        public static final String KEY_COLOR = "Color";
        public static final String KEY_RADIUS = "Radius";
        public static final String KEY_ORIGIN_X = "OriginX";
        public static final String KEY_ORIGIN_Y = "OriginY";
        public static final String KEY_DISTANCE = "Distance";
        public static final String KEY_MIN_DISTANCE = "MinDistance";
        public static final String KEY_RULE = "Rule";
        public static final Integer DEFAULT_RADIUS = 8;
        public static final Integer DEFAULT_OFFSET = 8;
        public static final Integer DEFAULT_SEARCH_RADIUS = 5000;
        public static final Integer DEFAULT_SEARCH_MIN_RADIUS = 100;
    }
}

