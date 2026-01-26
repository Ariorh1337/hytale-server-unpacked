/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.block;

import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockInvertedDomeUtil {
    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int radiusX, int radiusY, int radiusZ, @Nullable T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (radiusX <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusX));
        }
        if (radiusY <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusY));
        }
        if (radiusZ <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusZ));
        }
        float radiusXAdjusted = (float)radiusX + 0.41f;
        float radiusYAdjusted = (float)radiusY + 0.41f;
        float radiusZAdjusted = (float)radiusZ + 0.41f;
        float invRadiusXSqr = 1.0f / (radiusXAdjusted * radiusXAdjusted);
        float invRadiusYSqr = 1.0f / (radiusYAdjusted * radiusYAdjusted);
        for (int x = 0; x <= radiusX; ++x) {
            float qx = 1.0f - (float)(x * x) * invRadiusXSqr;
            double dy = Math.sqrt(qx) * (double)radiusYAdjusted;
            int maxY = (int)dy;
            for (int y = 0; y <= maxY; ++y) {
                double dz = Math.sqrt(qx - (float)(y * y) * invRadiusYSqr) * (double)radiusZAdjusted;
                int maxZ = (int)dz;
                for (int z = 0; z <= maxZ; ++z) {
                    if (BlockInvertedDomeUtil.test(originX, originY, originZ, x, -y, z, t, consumer)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int radiusX, int radiusY, int radiusZ, int thickness, boolean capped, @Nullable T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            return BlockInvertedDomeUtil.forEachBlock(originX, originY, originZ, radiusX, radiusY, radiusZ, t, consumer);
        }
        if (radiusX <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusX));
        }
        if (radiusY <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusY));
        }
        if (radiusZ <= 0) {
            throw new IllegalArgumentException(String.valueOf(radiusZ));
        }
        float radiusXAdjusted = (float)radiusX + 0.41f;
        float radiusYAdjusted = (float)radiusY + 0.41f;
        float radiusZAdjusted = (float)radiusZ + 0.41f;
        float innerRadiusXAdjusted = radiusXAdjusted - (float)thickness;
        float innerRadiusYAdjusted = radiusYAdjusted - (float)thickness;
        float innerRadiusZAdjusted = radiusZAdjusted - (float)thickness;
        float invRadiusX2 = 1.0f / (radiusXAdjusted * radiusXAdjusted);
        float invRadiusY2 = 1.0f / (radiusYAdjusted * radiusYAdjusted);
        float invRadiusZ2 = 1.0f / (radiusZAdjusted * radiusZAdjusted);
        float invInnerRadiusX2 = 1.0f / (innerRadiusXAdjusted * innerRadiusXAdjusted);
        float invInnerRadiusY2 = 1.0f / (innerRadiusYAdjusted * innerRadiusYAdjusted);
        float invInnerRadiusZ2 = 1.0f / (innerRadiusZAdjusted * innerRadiusZAdjusted);
        int y = 0;
        int y1 = 1;
        while (y <= radiusY) {
            float qy = (float)(y * y) * invRadiusY2;
            double dx = Math.sqrt(1.0f - qy) * (double)radiusXAdjusted;
            int maxX = (int)dx;
            float innerQy = (float)(y * y) * invInnerRadiusY2;
            float outerQy = (float)(y1 * y1) * invRadiusY2;
            boolean isAtTop = y == 0 && capped;
            int x = 0;
            int x1 = 1;
            while (x <= maxX) {
                float qx = (float)(x * x) * invRadiusX2;
                double dz = Math.sqrt(1.0f - qx - qy) * (double)radiusZAdjusted;
                int maxZ = (int)dz;
                float innerQx = (float)(x * x) * invInnerRadiusX2;
                float outerQx = (float)(x1 * x1) * invRadiusX2;
                int z = 0;
                int z1 = 1;
                while (z <= maxZ) {
                    float outerQz;
                    float innerQz = (float)(z * z) * invInnerRadiusZ2;
                    if (isAtTop ? !BlockInvertedDomeUtil.test(originX, originY, originZ, x, -y, z, t, consumer) : !(innerQx + innerQy + innerQz < 1.0f && outerQx + outerQy + (outerQz = (float)(z1 * z1) * invRadiusZ2) < 1.0f || BlockInvertedDomeUtil.test(originX, originY, originZ, x, -y, z, t, consumer))) {
                        return false;
                    }
                    ++z;
                    ++z1;
                }
                ++x;
                ++x1;
            }
            ++y;
            ++y1;
        }
        return true;
    }

    private static <T> boolean test(int originX, int originY, int originZ, int x, int y, int z, T context, @Nonnull TriIntObjPredicate<T> consumer) {
        if (!consumer.test(originX + x, originY + y, originZ + z, context)) {
            return false;
        }
        if (x > 0) {
            if (!consumer.test(originX - x, originY + y, originZ + z, context)) {
                return false;
            }
            if (z > 0 && !consumer.test(originX - x, originY + y, originZ - z, context)) {
                return false;
            }
        }
        if (z > 0) {
            return consumer.test(originX + x, originY + y, originZ - z, context);
        }
        return true;
    }
}

