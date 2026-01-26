/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
import com.hypixel.hytale.server.worldgen.loader.cave.CaveNodeTypeJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class CaveNodeTypeStorage {
    protected final SeedString<SeedStringResource> seed;
    protected final Path dataFolder;
    protected final Path caveFolder;
    protected final ZoneFileContext zoneContext;
    @Nonnull
    protected final Map<String, CaveNodeType> caveNodeTypes;

    public CaveNodeTypeStorage(SeedString<SeedStringResource> seed, Path dataFolder, Path caveFolder, ZoneFileContext zoneContext) {
        this.seed = seed;
        this.dataFolder = dataFolder;
        this.caveFolder = caveFolder;
        this.zoneContext = zoneContext;
        this.caveNodeTypes = new HashMap<String, CaveNodeType>();
    }

    public SeedString<SeedStringResource> getSeed() {
        return this.seed;
    }

    public void add(String name, CaveNodeType caveNodeType) {
        if (this.caveNodeTypes.containsKey(name)) {
            throw new Error(String.format("CaveNodeType (%s) has already been added to CaveNodeTypeStorage!", name));
        }
        this.caveNodeTypes.put(name, caveNodeType);
    }

    @Nonnull
    public CaveNodeType getOrLoadCaveNodeType(@Nonnull String name) {
        CaveNodeType caveNodeType = this.getCaveNodeType(name);
        if (caveNodeType == null) {
            caveNodeType = this.loadCaveNodeType(name);
        }
        return caveNodeType;
    }

    public CaveNodeType getCaveNodeType(String name) {
        return this.caveNodeTypes.get(name);
    }

    @Nonnull
    public CaveNodeType loadCaveNodeType(@Nonnull String name) {
        CaveNodeType caveNodeType;
        Path file = this.caveFolder.resolve(String.format("%s.node.json", name.replace(".", File.separator)));
        JsonReader reader = new JsonReader(Files.newBufferedReader(file));
        try {
            JsonObject caveNodeJson = JsonParser.parseReader(reader).getAsJsonObject();
            caveNodeType = this.loadCaveNodeType(name, caveNodeJson);
        }
        catch (Throwable throwable) {
            try {
                try {
                    reader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Throwable e) {
                throw new Error(String.format("Error while loading CaveNodeType %s for world generator from %s", name, file.toString()), e);
            }
        }
        reader.close();
        return caveNodeType;
    }

    @Nonnull
    public CaveNodeType loadCaveNodeType(@Nonnull String name, @Nonnull JsonObject json) {
        return new CaveNodeTypeJsonLoader(this.seed, this.dataFolder, json, name, this, this.zoneContext).load();
    }

    public static interface Constants {
        public static final String ERROR_ALREADY_ADDED = "CaveNodeType (%s) has already been added to CaveNodeTypeStorage!";
        public static final String ERROR_LOADING_CAVE_NODE_TYPE = "Error while loading CaveNodeType %s for world generator from %s";
        public static final String FILE_SUFFIX = "%s.node.json";
    }
}

