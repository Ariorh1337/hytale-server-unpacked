/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace;

import javax.annotation.Nonnull;

public class VoxelCoordinate {
    int x;
    int y;
    int z;

    public VoxelCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object other) {
        if (!(other instanceof VoxelCoordinate)) {
            return false;
        }
        VoxelCoordinate otherVoxelCoordinate = (VoxelCoordinate)other;
        return this == otherVoxelCoordinate || this.x == otherVoxelCoordinate.x && this.y == otherVoxelCoordinate.y && this.z == otherVoxelCoordinate.z;
    }

    @Nonnull
    public VoxelCoordinate clone() {
        return new VoxelCoordinate(this.x, this.y, this.z);
    }

    @Nonnull
    public String toString() {
        return "VoxelCoordinate{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }
}

