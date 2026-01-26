/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.material;

import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import java.util.Objects;
import javax.annotation.Nonnull;

public class FluidMaterial {
    private final MaterialCache materialCache;
    public final int fluidId;
    public final byte fluidLevel;

    FluidMaterial(@Nonnull MaterialCache materialCache, int fluidId, byte fluidLevel) {
        this.materialCache = materialCache;
        this.fluidId = fluidId;
        this.fluidLevel = fluidLevel;
    }

    public MaterialCache getVoxelCache() {
        return this.materialCache;
    }

    public final boolean equals(Object o) {
        if (!(o instanceof FluidMaterial)) {
            return false;
        }
        FluidMaterial that = (FluidMaterial)o;
        return this.fluidId == that.fluidId && this.fluidLevel == that.fluidLevel && this.materialCache.equals(that.materialCache);
    }

    public int hashCode() {
        return FluidMaterial.contentHash(this.fluidId, this.fluidLevel);
    }

    public static int contentHash(int blockId, byte fluidLevel) {
        return Objects.hash(blockId, fluidLevel);
    }

    public String toString() {
        return "FluidMaterial{materialCache=" + String.valueOf(this.materialCache) + ", fluidId=" + this.fluidId + ", fluidLevel=" + this.fluidLevel + "}";
    }
}

