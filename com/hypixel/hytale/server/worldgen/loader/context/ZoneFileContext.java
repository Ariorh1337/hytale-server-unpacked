/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.server.worldgen.loader.context.BiomeFileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ZoneFileContext
extends FileContext<FileLoadingContext> {
    private final FileContext.Registry<BiomeFileContext> tileBiomes = new FileContext.Registry("TileBiome");
    private final FileContext.Registry<BiomeFileContext> customBiomes = new FileContext.Registry("CustomBiome");

    public ZoneFileContext(int id, String name, Path filepath, FileLoadingContext context) {
        super(id, name, filepath, context);
    }

    @Nonnull
    public FileContext.Registry<BiomeFileContext> getTileBiomes() {
        return this.tileBiomes;
    }

    @Nonnull
    public FileContext.Registry<BiomeFileContext> getCustomBiomes() {
        return this.customBiomes;
    }

    @Nonnull
    public FileContext.Registry<BiomeFileContext> getBiomes(@Nonnull BiomeFileContext.Type type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case BiomeFileContext.Type.Tile -> this.getTileBiomes();
            case BiomeFileContext.Type.Custom -> this.getCustomBiomes();
        };
    }

    @Nonnull
    public ZoneFileContext matchContext(@Nullable JsonElement json, String key) {
        if (json == null || !json.isJsonObject()) {
            return this;
        }
        JsonElement element = json.getAsJsonObject().get(key);
        if (element == null || !element.isJsonObject()) {
            return this;
        }
        JsonObject object = element.getAsJsonObject();
        if (!object.has("File")) {
            return this;
        }
        String filePath = object.get("File").getAsString();
        return this.matchContext(filePath);
    }

    @Nonnull
    public ZoneFileContext matchContext(@Nonnull String filePath) {
        if (!filePath.startsWith("Zones.")) {
            return this;
        }
        int nameStart = "Zones.".length();
        int nameEnd = filePath.indexOf(46, nameStart);
        if (nameEnd < nameStart) {
            return this;
        }
        if (filePath.regionMatches(nameStart, this.getName(), 0, nameEnd - nameStart)) {
            return this;
        }
        String zoneName = filePath.substring(nameStart, nameEnd);
        FileContext.Registry<ZoneFileContext> zoneRegistry = ((FileLoadingContext)this.getParentContext()).getZones();
        if (!zoneRegistry.contains(zoneName)) {
            return this;
        }
        return zoneRegistry.get(zoneName);
    }

    @Nonnull
    protected BiomeFileContext createBiome(String name, Path path, BiomeFileContext.Type type) {
        return this.createBiome(((FileLoadingContext)this.getParentContext()).nextBiomeId(), name, path, type);
    }

    @Nonnull
    protected BiomeFileContext createBiome(int id, String name, Path path, BiomeFileContext.Type type) {
        return new BiomeFileContext(((FileLoadingContext)this.getParentContext()).updateBiomeId(id), name, path, type, this);
    }

    public static interface Constants {
        public static final String ZONE_PREFIX = "Zones.";
    }
}

