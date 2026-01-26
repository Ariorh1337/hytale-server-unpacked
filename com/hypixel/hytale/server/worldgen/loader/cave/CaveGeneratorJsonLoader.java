/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.CaveGenerator;
import com.hypixel.hytale.server.worldgen.cave.CaveType;
import com.hypixel.hytale.server.worldgen.loader.cave.CaveTypesJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CaveGeneratorJsonLoader
extends JsonLoader<SeedStringResource, CaveGenerator> {
    protected final Path caveFolder;
    protected final ZoneFileContext zoneContext;

    public CaveGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, Path caveFolder, ZoneFileContext zoneContext) {
        super(seed.append(".CaveGenerator"), dataFolder, json);
        this.caveFolder = caveFolder;
        this.zoneContext = zoneContext;
    }

    @Override
    @Nullable
    public CaveGenerator load() {
        CaveGenerator caveGenerator = null;
        if (this.caveFolder != null && Files.exists(this.caveFolder, new LinkOption[0])) {
            Path file = this.caveFolder.resolve("Caves.json");
            try {
                JsonObject cavesJson;
                try (JsonReader reader = new JsonReader(Files.newBufferedReader(file));){
                    cavesJson = JsonParser.parseReader(reader).getAsJsonObject();
                }
                caveGenerator = new CaveGenerator(this.loadCaveTypes(cavesJson));
            }
            catch (Throwable e) {
                throw new Error(String.format("Error while loading caves for world generator from %s", file.toString()), e);
            }
        }
        return caveGenerator;
    }

    @Nonnull
    protected CaveType[] loadCaveTypes(@Nonnull JsonObject jsonObject) {
        return new CaveTypesJsonLoader(this.seed, this.dataFolder, jsonObject.get("Types"), this.caveFolder, this.zoneContext).load();
    }

    public static interface Constants {
        public static final String FILE_CAVES_JSON = "Caves.json";
        public static final String KEY_TYPES = "Types";
        public static final String ERROR_LOADING_CAVES = "Error while loading caves for world generator from %s";
    }
}

