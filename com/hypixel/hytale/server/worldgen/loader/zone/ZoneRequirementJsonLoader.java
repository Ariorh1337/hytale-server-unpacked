/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.zone;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.loader.zone.UniqueZoneEntryJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.zone.ZoneColorMappingJsonLoader;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class ZoneRequirementJsonLoader
extends JsonLoader<SeedStringResource, Set<String>> {
    public ZoneRequirementJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public Set<String> load() {
        if (!this.has("MaskMapping")) {
            throw new IllegalArgumentException("Could not find mappings for colors in mask file. Keyword: MaskMapping");
        }
        HashSet<String> zoneSet = new HashSet<String>();
        ZoneColorMappingJsonLoader.collectZones(zoneSet, this.get("MaskMapping"));
        UniqueZoneEntryJsonLoader.collectZones(zoneSet, this.get("UniqueZones"));
        return zoneSet;
    }
}

