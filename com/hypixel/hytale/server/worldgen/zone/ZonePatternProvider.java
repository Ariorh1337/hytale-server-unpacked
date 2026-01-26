/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.zone;

import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.chunk.MaskProvider;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import com.hypixel.hytale.server.worldgen.zone.ZoneColorMapping;
import com.hypixel.hytale.server.worldgen.zone.ZonePatternGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class ZonePatternProvider {
    protected final IPointGenerator pointGenerator;
    protected final Zone[] zones;
    protected final Zone.UniqueCandidate[] uniqueZones;
    protected final MaskProvider maskProvider;
    protected final ZoneColorMapping zoneColorMapping;
    protected final int maxExtent;

    public ZonePatternProvider(IPointGenerator pointGenerator, Zone[] zones, Zone.UniqueCandidate[] uniqueZones, MaskProvider maskProvider, ZoneColorMapping zoneColorMapping) {
        this.pointGenerator = pointGenerator;
        this.zones = zones;
        this.uniqueZones = uniqueZones;
        this.maskProvider = maskProvider;
        this.zoneColorMapping = zoneColorMapping;
        this.maxExtent = ZonePatternProvider.getMaxExtent(zones);
        for (Zone.UniqueCandidate uniqueZone : uniqueZones) {
            zoneColorMapping.add(uniqueZone.zone().color(), uniqueZone.zone().zone());
        }
    }

    public int getMaxExtent() {
        return this.maxExtent;
    }

    public Zone[] getZones() {
        return this.zones;
    }

    public MaskProvider getMaskProvider() {
        return this.maskProvider;
    }

    public ZonePatternGenerator createGenerator(int seed) {
        FastRandom random = new FastRandom(seed);
        ArrayList<Zone.Unique> uniqueZones = new ArrayList<Zone.Unique>(this.uniqueZones.length);
        MaskProvider maskProvider = this.maskProvider.generateUniqueZones(seed, this.uniqueZones, random, uniqueZones);
        return new ZonePatternGenerator(this.pointGenerator, this.zones, (Zone.Unique[])uniqueZones.toArray(Zone.Unique[]::new), maskProvider, this.zoneColorMapping);
    }

    public String toString() {
        return "ZonePatternProvider{pointGenerator=" + String.valueOf(this.pointGenerator) + ", zones=" + Arrays.toString(this.zones) + ", uniqueZones=" + Arrays.toString(this.uniqueZones) + ", maskProvider=" + String.valueOf(this.maskProvider) + ", zoneColorMapping=" + String.valueOf(this.zoneColorMapping) + "}";
    }

    private static int getMaxExtent(@Nonnull Zone[] zones) {
        int max = 0;
        for (Zone zone : zones) {
            for (Biome biome : zone.biomePatternGenerator().getBiomes()) {
                if (biome.getPrefabContainer() == null) continue;
                max = Math.max(max, biome.getPrefabContainer().getMaxSize());
            }
        }
        return max;
    }
}

