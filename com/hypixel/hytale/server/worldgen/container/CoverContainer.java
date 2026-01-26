/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.container;

import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoverContainer {
    protected final CoverContainerEntry[] entries;

    public CoverContainer(CoverContainerEntry[] entries) {
        this.entries = entries;
    }

    public CoverContainerEntry[] getEntries() {
        return this.entries;
    }

    public static class CoverContainerEntry {
        protected final IWeightedMap<CoverContainerEntryPart> entries;
        protected final ICoordinateCondition mapCondition;
        protected final ICoordinateRndCondition heightCondition;
        protected final IBlockFluidCondition parentCondition;
        protected final double coverDensity;
        protected final boolean onWater;

        public CoverContainerEntry(IWeightedMap<CoverContainerEntryPart> entries, ICoordinateCondition mapCondition, ICoordinateRndCondition heightCondition, IBlockFluidCondition parentCondition, double coverDensity, boolean onWater) {
            this.entries = entries;
            this.mapCondition = mapCondition;
            this.heightCondition = heightCondition;
            this.parentCondition = parentCondition;
            this.coverDensity = coverDensity;
            this.onWater = onWater;
        }

        @Nullable
        public CoverContainerEntryPart get(Random random) {
            return this.entries.get(random);
        }

        public IBlockFluidCondition getParentCondition() {
            return this.parentCondition;
        }

        public ICoordinateCondition getMapCondition() {
            return this.mapCondition;
        }

        public double getCoverDensity() {
            return this.coverDensity;
        }

        public ICoordinateRndCondition getHeightCondition() {
            return this.heightCondition;
        }

        public boolean isOnWater() {
            return this.onWater;
        }

        @Nonnull
        public String toString() {
            return "CoverContainerEntry{entries=" + String.valueOf(this.entries) + ", mapCondition=" + String.valueOf(this.mapCondition) + ", heightCondition=" + String.valueOf(this.heightCondition) + ", parentCondition=" + String.valueOf(this.parentCondition) + ", coverDensity=" + this.coverDensity + ", onWater=" + this.onWater + "}";
        }

        public static class CoverContainerEntryPart {
            public static final CoverContainerEntryPart[] EMPTY_ARRAY = new CoverContainerEntryPart[0];
            protected final BlockFluidEntry entry;
            protected final int offset;

            public CoverContainerEntryPart(BlockFluidEntry entry, int offset) {
                this.entry = entry;
                this.offset = offset;
            }

            public BlockFluidEntry getEntry() {
                return this.entry;
            }

            public int getOffset() {
                return this.offset;
            }

            @Nonnull
            public String toString() {
                return "CoverContainerEntryPart{entry=" + String.valueOf(this.entry) + ", offset=" + this.offset + "}";
            }
        }
    }
}

