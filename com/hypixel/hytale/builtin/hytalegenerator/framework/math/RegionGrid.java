/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

public class RegionGrid {
    private int regionSizeX;
    private int regionSizeZ;

    public RegionGrid(int regionSizeX, int regionSizeZ) {
        this.regionSizeX = regionSizeX;
        this.regionSizeZ = regionSizeZ;
    }

    public int regionMinX(int chunkX) {
        if (chunkX >= 0) {
            return chunkX / this.regionSizeX * this.regionSizeX;
        }
        return (chunkX - (this.regionSizeZ - 1)) / this.regionSizeX * this.regionSizeX;
    }

    public int regionMinZ(int chunkZ) {
        if (chunkZ >= 0) {
            return chunkZ / this.regionSizeZ * this.regionSizeZ;
        }
        return (chunkZ - (this.regionSizeX - 1)) / this.regionSizeZ * this.regionSizeZ;
    }

    public int regionMaxX(int chunkX) {
        return this.regionMinX(chunkX) + this.regionSizeX;
    }

    public int regionMaxZ(int chunkZ) {
        return this.regionMinZ(chunkZ) + this.regionSizeZ;
    }
}

