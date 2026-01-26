/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.block;

import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import com.hypixel.hytale.math.util.MathUtil;
import javax.annotation.Nonnull;

public class BlockCylinderUtil {
    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (radiusX <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusX));
        }
        if (height <= 0) {
            throw new IllegalArgumentException(String.valueOf(height));
        }
        if (radiusZ <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusZ));
        }
        float radiusXAdjusted = (float)radiusX + 0.41f;
        float radiusZAdjusted = (float)radiusZ + 0.41f;
        double invRadiusXSqr = 1.0 / (double)(radiusXAdjusted * radiusXAdjusted);
        for (int x = -radiusX; x <= radiusX; ++x) {
            int minZ;
            double qx = 1.0 - (double)(x * x) * invRadiusXSqr;
            double dz = Math.sqrt(qx) * (double)radiusZAdjusted;
            int maxZ = (int)dz;
            for (int z = minZ = -maxZ; z <= maxZ; ++z) {
                for (int y = height - 1; y >= 0; --y) {
                    if (consumer.test(originX + x, originY + y, originZ + z, t)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        return BlockCylinderUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, thickness, false, t, consumer);
    }

    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, boolean capped, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            return BlockCylinderUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, t, consumer);
        }
        if (radiusX <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusX));
        }
        if (height <= 0) {
            throw new IllegalArgumentException(String.valueOf(height));
        }
        if (radiusZ <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusZ));
        }
        float radiusXAdjusted = (float)radiusX + 0.41f;
        float radiusZAdjusted = (float)radiusZ + 0.41f;
        float innerRadiusXAdjusted = radiusXAdjusted - (float)thickness;
        float innerRadiusZAdjusted = radiusZAdjusted - (float)thickness;
        if (innerRadiusXAdjusted <= 0.0f || innerRadiusZAdjusted <= 0.0f) {
            return BlockCylinderUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, t, consumer);
        }
        double invRadiusXSqr = 1.0 / (double)(radiusXAdjusted * radiusXAdjusted);
        double invInnerRadiusXSqr = 1.0 / (double)(innerRadiusXAdjusted * innerRadiusXAdjusted);
        int innerMinY = thickness;
        int innerMaxY = height - thickness;
        for (int y = height - 1; y >= 0; --y) {
            boolean cap = capped && (y < innerMinY || y > innerMaxY);
            for (int x = -radiusX; x <= radiusX; ++x) {
                double qx = 1.0 - (double)(x * x) * invRadiusXSqr;
                double dz = Math.sqrt(qx) * (double)radiusZAdjusted;
                int maxZ = (int)dz;
                double innerQx = (float)x < innerRadiusXAdjusted ? 1.0 - (double)(x * x) * invInnerRadiusXSqr : 0.0;
                double innerDZ = innerQx > 0.0 ? Math.sqrt(innerQx) * (double)innerRadiusZAdjusted : 0.0;
                int minZ = cap ? 0 : MathUtil.ceil(innerDZ);
                int z = minZ;
                if (z == 0) {
                    if (!consumer.test(originX + x, originY + y, originZ, t)) {
                        return false;
                    }
                    ++z;
                }
                while (z <= maxZ) {
                    if (!consumer.test(originX + x, originY + y, originZ + z, t)) {
                        return false;
                    }
                    if (!consumer.test(originX + x, originY + y, originZ - z, t)) {
                        return false;
                    }
                    ++z;
                }
            }
        }
        return true;
    }
}

