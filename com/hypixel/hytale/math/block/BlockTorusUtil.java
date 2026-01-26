/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.block;

import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockTorusUtil {
    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int outerRadius, int minorRadius, @Nullable T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (outerRadius <= 0) {
            throw new IllegalArgumentException(String.valueOf(outerRadius));
        }
        if (minorRadius <= 0) {
            throw new IllegalArgumentException(String.valueOf(minorRadius));
        }
        int majorRadius = Math.max(1, outerRadius - minorRadius);
        int sizeXZ = majorRadius + minorRadius;
        float minorRadiusAdjusted = (float)minorRadius + 0.41f;
        for (int x = -sizeXZ; x <= sizeXZ; ++x) {
            for (int z = -sizeXZ; z <= sizeXZ; ++z) {
                double distFromCenter = Math.sqrt(x * x + z * z);
                double distFromRing = distFromCenter - (double)majorRadius;
                for (int y = -minorRadius; y <= minorRadius; ++y) {
                    double distFromTube = Math.sqrt(distFromRing * distFromRing + (double)(y * y));
                    if (!(distFromTube <= (double)minorRadiusAdjusted) || consumer.test(originX + x, originY + y, originZ + z, t)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int outerRadius, int minorRadius, int thickness, boolean capped, @Nullable T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            return BlockTorusUtil.forEachBlock(originX, originY, originZ, outerRadius, minorRadius, t, consumer);
        }
        if (outerRadius <= 0) {
            throw new IllegalArgumentException(String.valueOf(outerRadius));
        }
        if (minorRadius <= 0) {
            throw new IllegalArgumentException(String.valueOf(minorRadius));
        }
        int majorRadius = Math.max(1, outerRadius - minorRadius);
        int sizeXZ = majorRadius + minorRadius;
        float minorRadiusAdjusted = (float)minorRadius + 0.41f;
        float innerMinorRadius = Math.max(0.0f, minorRadiusAdjusted - (float)thickness);
        for (int x = -sizeXZ; x <= sizeXZ; ++x) {
            for (int z = -sizeXZ; z <= sizeXZ; ++z) {
                double distFromCenter = Math.sqrt(x * x + z * z);
                double distFromRing = distFromCenter - (double)majorRadius;
                for (int y = -minorRadius; y <= minorRadius; ++y) {
                    boolean inInner;
                    boolean inOuter;
                    double distFromTube = Math.sqrt(distFromRing * distFromRing + (double)(y * y));
                    boolean bl = inOuter = distFromTube <= (double)minorRadiusAdjusted;
                    if (!inOuter) continue;
                    boolean bl2 = inInner = distFromTube < (double)innerMinorRadius;
                    if (inInner || consumer.test(originX + x, originY + y, originZ + z, t)) continue;
                    return false;
                }
            }
        }
        return true;
    }
}

