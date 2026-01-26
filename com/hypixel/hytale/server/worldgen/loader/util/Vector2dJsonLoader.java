/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class Vector2dJsonLoader
extends JsonLoader<SeedStringResource, Vector2d> {
    public Vector2dJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public Vector2d load() {
        if (this.json == null || this.json.isJsonNull()) {
            return new Vector2d();
        }
        if (this.json.isJsonArray()) {
            JsonArray array = this.json.getAsJsonArray();
            if (array.isEmpty()) {
                return new Vector2d();
            }
            if (array.size() == 1) {
                double value = array.get(0).getAsDouble();
                return new Vector2d(value, value);
            }
            double x = array.get(0).getAsDouble();
            double y = array.get(1).getAsDouble();
            return new Vector2d(x, y);
        }
        if (this.json.isJsonObject()) {
            JsonObject object = this.json.getAsJsonObject();
            double x = object.get("X").getAsDouble();
            double y = object.get("Y").getAsDouble();
            return new Vector2d(x, y);
        }
        return new Vector2d();
    }

    public static interface Constants {
        public static final String KEY_X = "X";
        public static final String KEY_Y = "Y";
    }
}

