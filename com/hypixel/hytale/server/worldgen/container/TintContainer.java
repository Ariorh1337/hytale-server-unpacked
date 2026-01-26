/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.container;

import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import java.util.List;
import javax.annotation.Nonnull;

public class TintContainer {
    private final DefaultTintContainerEntry defaultEntry;
    private final List<TintContainerEntry> entries;

    public TintContainer(DefaultTintContainerEntry defaultEntry, List<TintContainerEntry> entries) {
        this.defaultEntry = defaultEntry;
        this.entries = entries;
    }

    public int getTintColorAt(int seed, int x, int z) {
        for (int i = 0; i < this.entries.size(); ++i) {
            if (!this.entries.get(i).shouldGenerate(seed, x, z)) continue;
            return this.entries.get(i).getTintColorAt(seed, x, z);
        }
        return this.defaultEntry.getTintColorAt(seed, x, z);
    }

    @Nonnull
    public String toString() {
        return "TintContainer{defaultEntry=" + String.valueOf(this.defaultEntry) + ", entries=" + String.valueOf(this.entries) + "}";
    }

    public static class DefaultTintContainerEntry
    extends TintContainerEntry {
        public DefaultTintContainerEntry(IWeightedMap<Integer> colorMapping, NoiseProperty valueNoise) {
            super(colorMapping, valueNoise, DefaultCoordinateCondition.DEFAULT_TRUE);
        }

        @Override
        @Nonnull
        public String toString() {
            return "DefaultTintContainerEntry{}";
        }
    }

    public static class TintContainerEntry {
        private final IWeightedMap<Integer> colorMapping;
        private final NoiseProperty valueNoise;
        private final ICoordinateCondition mapCondition;

        public TintContainerEntry(IWeightedMap<Integer> colorMapping, NoiseProperty valueNoise, ICoordinateCondition mapCondition) {
            this.colorMapping = colorMapping;
            this.valueNoise = valueNoise;
            this.mapCondition = mapCondition;
        }

        public boolean shouldGenerate(int seed, int x, int z) {
            return this.mapCondition.eval(seed, x, z);
        }

        public int getTintColorAt(int seed, int x, int z) {
            return this.colorMapping.get(seed, x, z, (iSeed, ix, iz, entry) -> entry.valueNoise.get(iSeed, ix, iz), this);
        }

        @Nonnull
        public String toString() {
            return "TintContainerEntry{colorMapping=" + String.valueOf(this.colorMapping) + ", valueNoise=" + String.valueOf(this.valueNoise) + ", mapCondition=" + String.valueOf(this.mapCondition) + "}";
        }
    }
}

