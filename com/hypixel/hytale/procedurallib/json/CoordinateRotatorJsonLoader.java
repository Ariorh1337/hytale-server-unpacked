/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.random.CoordinateOriginRotator;
import com.hypixel.hytale.procedurallib.random.CoordinateRotator;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class CoordinateRotatorJsonLoader<K extends SeedResource>
extends JsonLoader<K, CoordinateRotator> {
    public CoordinateRotatorJsonLoader(SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public CoordinateRotator load() {
        double originZ;
        double yaw;
        double pitch = this.has("Pitch") ? this.get("Pitch").getAsDouble() * 0.01745329238474369 : 0.0;
        double d = yaw = this.has("Yaw") ? this.get("Yaw").getAsDouble() * 0.01745329238474369 : 0.0;
        if (pitch == 0.0 && yaw == 0.0) {
            return CoordinateRotator.NONE;
        }
        double originX = this.has("OriginX") ? this.get("OriginX").getAsDouble() : 0.0;
        double originY = this.has("OriginY") ? this.get("OriginY").getAsDouble() : 0.0;
        double d2 = originZ = this.has("OriginZ") ? this.get("OriginZ").getAsDouble() : 0.0;
        if (originX == 0.0 && originY == 0.0 && originZ == 0.0) {
            return new CoordinateRotator(pitch, yaw);
        }
        return new CoordinateOriginRotator(pitch, yaw, originX, originY, originZ);
    }

    public static interface Constants {
        public static final String KEY_ROTATE = "Rotate";
        public static final String KEY_PITCH = "Pitch";
        public static final String KEY_YAW = "Yaw";
        public static final String KEY_ORIGIN_X = "OriginX";
        public static final String KEY_ORIGIN_Y = "OriginY";
        public static final String KEY_ORIGIN_Z = "OriginZ";
    }
}

