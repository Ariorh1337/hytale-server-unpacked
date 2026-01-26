/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.container;

import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class EnvironmentContainer {
    protected final DefaultEnvironmentContainerEntry defaultEntry;
    protected final EnvironmentContainerEntry[] entries;

    public EnvironmentContainer(DefaultEnvironmentContainerEntry defaultEntry, EnvironmentContainerEntry[] entries) {
        this.defaultEntry = defaultEntry;
        this.entries = entries;
    }

    public int getEnvironmentAt(int seed, int x, int z) {
        for (EnvironmentContainerEntry entry : this.entries) {
            if (!entry.shouldGenerate(seed, x, z)) continue;
            return entry.getEnvironmentAt(seed, x, z);
        }
        return this.defaultEntry.getEnvironmentAt(seed, x, z);
    }

    @Nonnull
    public String toString() {
        return "EnvironmentContainer{defaultEntry=" + String.valueOf(this.defaultEntry) + ", entries=" + Arrays.toString(this.entries) + "}";
    }

    public static class DefaultEnvironmentContainerEntry
    extends EnvironmentContainerEntry {
        public DefaultEnvironmentContainerEntry(IWeightedMap<Integer> environmentMapping, NoiseProperty valueNoise) {
            super(environmentMapping, valueNoise, DefaultCoordinateCondition.DEFAULT_TRUE);
        }

        @Override
        @Nonnull
        public String toString() {
            return "DefaultEnvironmentContainerEntry{environmentMapping=" + String.valueOf(this.environmentMapping) + ", valueNoise=" + String.valueOf(this.valueNoise) + ", mapCondition=" + String.valueOf(this.mapCondition) + "}";
        }
    }

    public static class EnvironmentContainerEntry {
        public static final EnvironmentContainerEntry[] EMPTY_ARRAY = new EnvironmentContainerEntry[0];
        protected final IWeightedMap<Integer> environmentMapping;
        protected final NoiseProperty valueNoise;
        protected final ICoordinateCondition mapCondition;

        public EnvironmentContainerEntry(IWeightedMap<Integer> environmentMapping, NoiseProperty valueNoise, ICoordinateCondition mapCondition) {
            this.environmentMapping = environmentMapping;
            this.valueNoise = valueNoise;
            this.mapCondition = mapCondition;
        }

        public boolean shouldGenerate(int seed, int x, int z) {
            return this.mapCondition.eval(seed, x, z);
        }

        public int getEnvironmentAt(int seed, int x, int z) {
            return this.environmentMapping.get(seed, x, z, (iSeed, ix, iz, entry) -> entry.valueNoise.get(iSeed, ix, iz), this);
        }

        @Nonnull
        public String toString() {
            return "EnvironmentContainerEntry{environmentMapping=" + String.valueOf(this.environmentMapping) + ", valueNoise=" + String.valueOf(this.valueNoise) + ", mapCondition=" + String.valueOf(this.mapCondition) + "}";
        }
    }
}

