/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.spatial;

public class MortonCode {
    private static final int BITS_PER_AXIS = 21;
    private static final long MAX_COORD = 0x1FFFFFL;

    public static long encode(double x, double y, double z, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double nx = (x - minX) / (maxX - minX);
        double ny = (y - minY) / (maxY - minY);
        double nz = (z - minZ) / (maxZ - minZ);
        long ix = Math.min(Math.max((long)(nx * 2097151.0), 0L), 0x1FFFFFL);
        long iy = Math.min(Math.max((long)(ny * 2097151.0), 0L), 0x1FFFFFL);
        long iz = Math.min(Math.max((long)(nz * 2097151.0), 0L), 0x1FFFFFL);
        return MortonCode.interleaveBits(ix, iy, iz);
    }

    private static long interleaveBits(long x, long y, long z) {
        x = MortonCode.expandBits(x);
        y = MortonCode.expandBits(y);
        z = MortonCode.expandBits(z);
        return x | y << 1 | z << 2;
    }

    private static long expandBits(long value) {
        value &= 0x1FFFFFL;
        value = (value | value << 32) & 0x1F00000000FFFFL;
        value = (value | value << 16) & 0x1F0000FF0000FFL;
        value = (value | value << 8) & 0x100F00F00F00F00FL;
        value = (value | value << 4) & 0x10C30C30C30C30CL;
        value = (value | value << 2) & 0x1249249249249249L;
        return value;
    }
}

