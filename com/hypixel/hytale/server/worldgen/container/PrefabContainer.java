/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.container;

import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabSupplier;
import com.hypixel.hytale.server.worldgen.prefab.PrefabPatternGenerator;
import com.hypixel.hytale.server.worldgen.util.bounds.IChunkBounds;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class PrefabContainer {
    private final PrefabContainerEntry[] entries;
    private final int maxSize;

    public PrefabContainer(PrefabContainerEntry[] entries) {
        this.entries = entries;
        this.maxSize = PrefabContainer.getMaxSize(entries);
    }

    public PrefabContainerEntry[] getEntries() {
        return this.entries;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    @Nonnull
    public String toString() {
        return "PrefabContainer{entries=" + Arrays.toString(this.entries) + "}";
    }

    private static int getMaxSize(PrefabContainerEntry[] entries) {
        int max = 0;
        for (PrefabContainerEntry entry : entries) {
            max = Math.max(max, entry.getPrefabPatternGenerator().getMaxSize());
        }
        return max;
    }

    public static class PrefabContainerEntry {
        protected final IWeightedMap<WorldGenPrefabSupplier> prefabs;
        protected final PrefabPatternGenerator prefabPatternGenerator;
        protected final int environmentId;
        protected int extend = -1;

        public PrefabContainerEntry(IWeightedMap<WorldGenPrefabSupplier> prefabs, PrefabPatternGenerator prefabPatternGenerator, int environmentId) {
            this.prefabs = prefabs;
            this.prefabPatternGenerator = prefabPatternGenerator;
            this.environmentId = environmentId;
        }

        public IWeightedMap<WorldGenPrefabSupplier> getPrefabs() {
            return this.prefabs;
        }

        public int getEnvironmentId() {
            return this.environmentId;
        }

        public int getExtents() {
            if (this.extend == -1) {
                int max = 0;
                for (WorldGenPrefabSupplier supplier : this.prefabs.internalKeys()) {
                    IChunkBounds bounds = supplier.getBounds(supplier.get());
                    int lengthX = bounds.getHighBoundX() - bounds.getLowBoundX();
                    int lengthZ = bounds.getHighBoundZ() - bounds.getLowBoundZ();
                    max = MathUtil.maxValue(max, lengthX, lengthZ);
                }
                this.extend = max;
            }
            return this.extend;
        }

        public PrefabPatternGenerator getPrefabPatternGenerator() {
            return this.prefabPatternGenerator;
        }

        @Nonnull
        public String toString() {
            return "PrefabContainerEntry{prefabs=" + String.valueOf(this.prefabs) + ", prefabPatternGenerator=" + String.valueOf(this.prefabPatternGenerator) + ", extend=" + this.extend + "}";
        }
    }
}

