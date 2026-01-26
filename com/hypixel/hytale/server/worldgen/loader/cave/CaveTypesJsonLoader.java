/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.CaveType;
import com.hypixel.hytale.server.worldgen.loader.cave.CaveTypeJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class CaveTypesJsonLoader
extends JsonLoader<SeedStringResource, CaveType[]> {
    protected final Path caveFolder;
    protected final ZoneFileContext zoneContext;

    public CaveTypesJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, Path caveFolder, ZoneFileContext zoneContext) {
        super(seed, dataFolder, json);
        this.caveFolder = caveFolder;
        this.zoneContext = zoneContext;
    }

    @Override
    @Nonnull
    public CaveType[] load() {
        if (this.json == null || !this.json.isJsonArray()) {
            throw new IllegalArgumentException("CaveTypes must be a JSON array.");
        }
        JsonArray typesArray = this.json.getAsJsonArray();
        CaveType[] caveTypes = new CaveType[typesArray.size()];
        for (int i = 0; i < typesArray.size(); ++i) {
            JsonElement entry = this.getOrLoad(typesArray.get(i));
            if (!entry.isJsonObject()) {
                throw CaveTypesJsonLoader.error("Expected CaveType entry to be a JsonObject at index: %d", i);
            }
            JsonObject caveTypeJson = entry.getAsJsonObject();
            String name = this.loadName(caveTypeJson);
            caveTypes[i] = this.loadCaveType(name, caveTypeJson);
        }
        return caveTypes;
    }

    @Nonnull
    protected CaveType loadCaveType(String name, JsonElement json) {
        return new CaveTypeJsonLoader(this.seed.append(String.format("-%s", name)), this.dataFolder, json, this.caveFolder, name, this.zoneContext).load();
    }

    protected String loadName(@Nonnull JsonObject jsonObject) {
        return jsonObject.get("Name").getAsString();
    }

    public static interface Constants {
        public static final String KEY_NAME = "Name";
        public static final String SEED_CAVE_TYPE_SUFFIX = "-%s";
        public static final String ERROR_NOT_AN_ARRAY = "CaveTypes must be a JSON array.";
    }
}

