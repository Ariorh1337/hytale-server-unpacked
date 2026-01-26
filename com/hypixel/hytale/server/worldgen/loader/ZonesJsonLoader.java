/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.procedurallib.json.Loader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.loader.context.FileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import com.hypixel.hytale.server.worldgen.loader.zone.ZoneJsonLoader;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nonnull;

public class ZonesJsonLoader
extends Loader<SeedStringResource, Zone[]> {
    protected final FileLoadingContext loadingContext;

    public ZonesJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, FileLoadingContext loadingContext) {
        super(seed.append(".Zones"), dataFolder);
        this.loadingContext = loadingContext;
    }

    @Override
    @Nonnull
    public Zone[] load() {
        FileContext.Registry<ZoneFileContext> zoneRegistry = this.loadingContext.getZones();
        int index = 0;
        Zone[] zones = new Zone[zoneRegistry.size()];
        for (Map.Entry<String, ZoneFileContext> entry : zoneRegistry) {
            ZoneFileContext zoneContext = entry.getValue();
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(zoneContext.getPath().resolve("Zone.json")));){
                JsonElement zoneJson = JsonParser.parseReader(reader);
                Zone zone = new ZoneJsonLoader(this.seed, this.dataFolder, zoneJson, zoneContext).load();
                zones[index++] = zone;
            }
            catch (Throwable e) {
                throw new Error(String.format("Error while loading zone \"%s\" for world generator from file.", zoneContext.getPath().toString()), e);
            }
        }
        return zones;
    }

    public static interface Constants {
        public static final String PATH_ZONES = "Zones";
        public static final String FILE_ZONE_MAIN_FILE = "Zone.json";
        public static final String ERROR_LOADING_ZONE = "Error while loading zone \"%s\" for world generator from file.";
    }
}

