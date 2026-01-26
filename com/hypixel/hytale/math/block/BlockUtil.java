/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.block;

import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class BlockUtil {
    public static final float RADIUS_ADJUST = 0.41f;
    public static final long BITS_Y = 9L;
    public static final long MAX_Y = 512L;
    public static final long MIN_Y = -513L;
    public static final long Y_INVERT = -512L;
    public static final long Y_MASK = 511L;
    public static final long BITS_PER_DIRECTION = 26L;
    public static final long MAX = 0x4000000L;
    public static final long MIN = -67108865L;
    public static final long DIRECTION_INVERT = -67108864L;
    public static final long DIRECTION_MASK = 0x3FFFFFFL;

    public static long pack(@Nonnull Vector3i val) {
        return BlockUtil.pack(val.x, val.y, val.z);
    }

    public static long pack(int x, int y, int z) {
        if ((long)y <= -513L || (long)y >= 512L) {
            throw new IllegalArgumentException(String.valueOf(y));
        }
        if ((long)x <= -67108865L || (long)x >= 0x4000000L) {
            throw new IllegalArgumentException(String.valueOf(x));
        }
        if ((long)z <= -67108865L || (long)z >= 0x4000000L) {
            throw new IllegalArgumentException(String.valueOf(z));
        }
        long l = ((long)y & 0x1FFL) << 54 | ((long)z & 0x3FFFFFFL) << 27 | (long)x & 0x3FFFFFFL;
        if (y < 0) {
            l |= Long.MIN_VALUE;
        }
        if (z < 0) {
            l |= 0x20000000000000L;
        }
        if (x < 0) {
            l |= 0x4000000L;
        }
        return l;
    }

    public static long packUnchecked(int x, int y, int z) {
        long l = ((long)y & 0x1FFL) << 54 | ((long)z & 0x3FFFFFFL) << 27 | (long)x & 0x3FFFFFFL;
        if (y < 0) {
            l |= Long.MIN_VALUE;
        }
        if (z < 0) {
            l |= 0x20000000000000L;
        }
        if (x < 0) {
            l |= 0x4000000L;
        }
        return l;
    }

    public static int unpackX(long packed) {
        int i = (int)(packed & 0x3FFFFFFL);
        if ((packed & 0x4000000L) != 0L) {
            i = (int)((long)i | 0xFFFFFFFFFC000000L);
        }
        return i;
    }

    public static int unpackY(long packed) {
        int i = (int)(packed >> 54 & 0x1FFL);
        if ((packed & Long.MIN_VALUE) != 0L) {
            i = (int)((long)i | 0xFFFFFFFFFFFFFE00L);
        }
        return i;
    }

    public static int unpackZ(long packed) {
        int i = (int)(packed >> 27 & 0x3FFFFFFL);
        if ((packed & 0x20000000000000L) != 0L) {
            i = (int)((long)i | 0xFFFFFFFFFC000000L);
        }
        return i;
    }

    @Nonnull
    public static Vector3i unpack(long packed) {
        return new Vector3i(BlockUtil.unpackX(packed), BlockUtil.unpackY(packed), BlockUtil.unpackZ(packed));
    }
}

