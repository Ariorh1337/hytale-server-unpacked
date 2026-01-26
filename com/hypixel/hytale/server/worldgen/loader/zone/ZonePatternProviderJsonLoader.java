/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.zone;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.PointGeneratorJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.chunk.MaskProvider;
import com.hypixel.hytale.server.worldgen.climate.ClimateColor;
import com.hypixel.hytale.server.worldgen.climate.ClimateMaskProvider;
import com.hypixel.hytale.server.worldgen.climate.ClimateType;
import com.hypixel.hytale.server.worldgen.loader.zone.UniqueZoneEntryJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.zone.ZoneColorMappingJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.zone.ZoneRequirementJsonLoader;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import com.hypixel.hytale.server.worldgen.zone.ZoneColorMapping;
import com.hypixel.hytale.server.worldgen.zone.ZonePatternProvider;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ZonePatternProviderJsonLoader
extends JsonLoader<SeedStringResource, ZonePatternProvider> {
    protected final MaskProvider maskProvider;
    protected Zone[] zones;
    protected Map<String, Zone> zoneLookup = Map.of();

    public ZonePatternProviderJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, MaskProvider maskProvider) {
        super(seed.append(".ZonePatternGenerator"), dataFolder, json);
        this.maskProvider = maskProvider;
    }

    public void setZones(Zone[] zones) {
        this.zones = zones;
        this.zoneLookup = new HashMap<String, Zone>();
        for (Zone zone : zones) {
            this.zoneLookup.put(zone.name(), zone);
        }
    }

    @Override
    @Nonnull
    public ZonePatternProvider load() {
        return new ZonePatternProvider(this.loadGridGenerator(), this.zones, this.loadUniqueZoneCandidates(), this.maskProvider, this.loadColorMapping());
    }

    @Nullable
    protected IPointGenerator loadGridGenerator() {
        return new PointGeneratorJsonLoader(this.seed, this.dataFolder, this.get("GridGenerator")).load();
    }

    @Nonnull
    protected ZoneColorMapping loadColorMapping() {
        if (!this.has("MaskMapping")) {
            throw new IllegalArgumentException("Could not find mappings for colors in mask file. Keyword: MaskMapping");
        }
        ZoneColorMapping colorMapping = new ZoneColorMappingJsonLoader(this.seed, this.dataFolder, this.get("MaskMapping"), this.zoneLookup).load();
        this.ensureMaskIntegrity(colorMapping);
        return colorMapping;
    }

    @Nonnull
    public Set<String> loadZoneRequirement() {
        return new ZoneRequirementJsonLoader(this.seed, this.dataFolder, this.json).load();
    }

    protected void ensureMaskIntegrity(@Nonnull ZoneColorMapping zoneColorMapping) {
        ClimateType[] climateTypeArray = this.maskProvider;
        if (climateTypeArray instanceof ClimateMaskProvider) {
            ClimateMaskProvider climateMask = (ClimateMaskProvider)climateTypeArray;
            for (ClimateType parent : climateMask.getGraph().getParents()) {
                if (parent.children.length == 0) {
                    ZonePatternProviderJsonLoader.validateMapping(parent, parent, parent.color, zoneColorMapping, "");
                    ZonePatternProviderJsonLoader.validateMapping(parent, parent, parent.island, zoneColorMapping, "Island");
                    continue;
                }
                for (ClimateType child : parent.children) {
                    ZonePatternProviderJsonLoader.validateMapping(parent, child, child.color, zoneColorMapping, "");
                    ZonePatternProviderJsonLoader.validateMapping(parent, child, child.island, zoneColorMapping, "Island.");
                }
            }
        } else {
            this.maskProvider.getFuzzyZoom().getExactZoom().getDistanceProvider().getColors().forEach(rgb -> {
                if (zoneColorMapping.get(rgb) == null) {
                    throw new NullPointerException(Integer.toHexString(rgb));
                }
            });
        }
    }

    protected Zone.UniqueCandidate[] loadUniqueZoneCandidates() {
        MaskProvider maskProvider = this.maskProvider;
        if (maskProvider instanceof ClimateMaskProvider) {
            ClimateMaskProvider climateMask = (ClimateMaskProvider)maskProvider;
            return climateMask.getUniqueZoneCandidates(this.zoneLookup);
        }
        Zone.UniqueEntry[] uniqueZones = new UniqueZoneEntryJsonLoader(this.seed, this.dataFolder, this.get("UniqueZones"), this.zoneLookup).load();
        return this.maskProvider.generateUniqueZoneCandidates(uniqueZones, 100);
    }

    protected static void validateMapping(@Nullable ClimateType parent, @Nonnull ClimateType type, @Nonnull ClimateColor color, ZoneColorMapping mapping, String prefix) {
        if (mapping.get(color.land) == null) {
            throw new Error(prefix + "Color is not mapped in climate type: " + ClimateType.name(parent, type));
        }
        if (mapping.get(color.shore) == null) {
            throw new Error(prefix + "Shore is not mapped in climate type: " + ClimateType.name(parent, type));
        }
        if (mapping.get(color.ocean) == null) {
            throw new Error(prefix + "Ocean is not mapped in climate type: " + ClimateType.name(parent, type));
        }
        if (mapping.get(color.shallowOcean) == null) {
            throw new Error(prefix + "ShallowOcean is not mapped in climate type: " + ClimateType.name(parent, type));
        }
    }

    public static interface Constants {
        public static final String KEY_GRID_GENERATOR = "GridGenerator";
        public static final String KEY_UNIQUE_ZONES = "UniqueZones";
        public static final String KEY_MASK_MAPPING = "MaskMapping";
        public static final String ERROR_UNMAPPED_COLOR = "Mask image contains unmapped color! #%s";
        public static final String ERROR_NO_MAPPING = "Could not find mappings for colors in mask file. Keyword: MaskMapping";
        public static final int UNIQUE_ZONE_CANDIDATE_POS_LIMIT = 100;
    }
}

