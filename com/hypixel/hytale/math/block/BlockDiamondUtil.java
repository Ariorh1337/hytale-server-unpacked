/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.block;

import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockDiamondUtil {
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
        float radiusZAdjusted = (float)radiusZ + 0.41f;
        for (int y = 0; y <= radiusY; ++y) {
            float normalizedY = (float)y / (float)radiusY;
            float currentRadiusX = radiusXAdjusted * (1.0f - normalizedY);
            float currentRadiusZ = radiusZAdjusted * (1.0f - normalizedY);
            int maxX = (int)currentRadiusX;
            int maxZ = (int)currentRadiusZ;
            for (int x = 0; x <= maxX; ++x) {
                for (int z = 0; z <= maxZ; ++z) {
                    if (!((float)Math.abs(x) <= currentRadiusX) || !((float)Math.abs(z) <= currentRadiusZ) || BlockDiamondUtil.test(originX, originY, originZ, x, y, z, t, consumer)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    public static <T> boolean forEachBlock(int originX, int originY, int originZ, int radiusX, int radiusY, int radiusZ, int thickness, boolean capped, @Nullable T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            return BlockDiamondUtil.forEachBlock(originX, originY, originZ, radiusX, radiusY, radiusZ, t, consumer);
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
        float radiusZAdjusted = (float)radiusZ + 0.41f;
        for (int y = 0; y <= radiusY; ++y) {
            float normalizedY = (float)y / (float)radiusY;
            float currentRadiusX = radiusXAdjusted * (1.0f - normalizedY);
            float currentRadiusZ = radiusZAdjusted * (1.0f - normalizedY);
            float innerRadiusX = Math.max(0.0f, currentRadiusX - (float)thickness);
            float innerRadiusZ = Math.max(0.0f, currentRadiusZ - (float)thickness);
            int maxX = (int)currentRadiusX;
            int maxZ = (int)currentRadiusZ;
            for (int x = 0; x <= maxX; ++x) {
                for (int z = 0; z <= maxZ; ++z) {
                    boolean inInner;
                    boolean inOuter;
                    boolean bl = inOuter = (float)Math.abs(x) <= currentRadiusX && (float)Math.abs(z) <= currentRadiusZ;
                    if (!inOuter) continue;
                    boolean bl2 = inInner = (float)Math.abs(x) < innerRadiusX && (float)Math.abs(z) < innerRadiusZ;
                    if (inInner || BlockDiamondUtil.test(originX, originY, originZ, x, y, z, t, consumer)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    private static <T> boolean test(int originX, int originY, int originZ, int x, int y, int z, T context, @Nonnull TriIntObjPredicate<T> consumer) {
        if (!consumer.test(originX + x, originY + y, originZ + z, context)) {
            return false;
        }
        if (y > 0 && !consumer.test(originX + x, originY - y, originZ + z, context)) {
            return false;
        }
        if (x > 0) {
            if (!consumer.test(originX - x, originY + y, originZ + z, context)) {
                return false;
            }
            if (y > 0 && !consumer.test(originX - x, originY - y, originZ + z, context)) {
                return false;
            }
            if (z > 0 && !consumer.test(originX - x, originY + y, originZ - z, context)) {
                return false;
            }
            if (y > 0 && z > 0 && !consumer.test(originX - x, originY - y, originZ - z, context)) {
                return false;
            }
        }
        if (z > 0) {
            if (!consumer.test(originX + x, originY + y, originZ - z, context)) {
                return false;
            }
            if (y > 0 && !consumer.test(originX + x, originY - y, originZ - z, context)) {
                return false;
            }
        }
        return true;
    }
}

