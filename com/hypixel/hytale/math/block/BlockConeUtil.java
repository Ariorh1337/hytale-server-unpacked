/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.block;

import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import javax.annotation.Nonnull;

public class BlockConeUtil {
    public static <T> void forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, T t, @Nonnull TriIntObjPredicate<T> consumer) {
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
        for (int y = height - 1; y >= 0; --y) {
            int minX;
            double rf = 1.0 - (double)y / (double)height;
            double dx = (double)radiusXAdjusted * rf;
            int maxX = (int)dx;
            for (int x = minX = -maxX; x <= maxX; ++x) {
                int minZ;
                double qx = 1.0 - (double)(x * x) / (dx * dx);
                double dz = Math.sqrt(qx) * (double)radiusZAdjusted * rf;
                int maxZ = (int)dz;
                for (int z = minZ = -maxZ; z <= maxZ; ++z) {
                    if (consumer.test(originX + x, originY + y, originZ + z, t)) continue;
                    return;
                }
            }
        }
    }

    public static <T> void forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        BlockConeUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, thickness, false, t, consumer);
    }

    public static <T> void forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, boolean capped, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            BlockConeUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, t, consumer);
            return;
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
        for (int y = height - 1; y >= 0; --y) {
            int minX;
            boolean cap = capped && y < thickness;
            double rf = 1.0 - (double)y / (double)height;
            double dx = (double)radiusXAdjusted * rf;
            double dxInvSqr = 1.0 / (dx * dx);
            double innerDx = dx > (double)thickness ? dx - (double)thickness : 0.0;
            double innerDxInvSqr = innerDx > 0.0 ? 1.0 / (innerDx * innerDx) : 0.0;
            int maxX = (int)dx;
            for (int x = minX = -maxX; x <= maxX; ++x) {
                double dz = Math.sqrt(1.0 - (double)(x * x) * dxInvSqr) * dx;
                int maxZ = (int)dz;
                int minZ = -maxZ;
                double innerMaxZ = cap ? 0.0 : Math.sqrt(1.0 - (double)(x * x) * innerDxInvSqr) * innerDx;
                double innerMinZ = cap ? 0.0 : -innerMaxZ;
                for (int z = minZ; z <= maxZ; ++z) {
                    if ((double)z > innerMinZ && (double)z < innerMaxZ || consumer.test(originX + x, originY + y, originZ + z, t)) continue;
                    return;
                }
            }
        }
    }

    public static <T> void forEachBlockInverted(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, T t, @Nonnull TriIntObjPredicate<T> consumer) {
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
        for (int y = height - 1; y >= 0; --y) {
            int minX;
            double rf = 1.0 - (double)y / (double)height;
            double dx = (double)radiusXAdjusted * rf;
            int maxX = (int)dx;
            for (int x = minX = -maxX; x <= maxX; ++x) {
                int minZ;
                double qx = 1.0 - (double)(x * x) / (dx * dx);
                double dz = Math.sqrt(qx) * (double)radiusZAdjusted * rf;
                int maxZ = (int)dz;
                for (int z = minZ = -maxZ; z <= maxZ; ++z) {
                    if (consumer.test(originX + x, originY + height - 1 - y, originZ + z, t)) continue;
                    return;
                }
            }
        }
    }

    public static <T> void forEachBlockInverted(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        BlockConeUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, thickness, false, t, consumer);
    }

    public static <T> void forEachBlockInverted(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, boolean capped, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            BlockConeUtil.forEachBlockInverted(originX, originY, originZ, radiusX, height, radiusZ, t, consumer);
            return;
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
        for (int y = height - 1; y >= 0; --y) {
            int minX;
            boolean cap = capped && y < thickness;
            double rf = 1.0 - (double)y / (double)height;
            double dx = (double)radiusXAdjusted * rf;
            double dxInvSqr = 1.0 / (dx * dx);
            double innerDx = dx > (double)thickness ? dx - (double)thickness : 0.0;
            double innerDxInvSqr = innerDx > 0.0 ? 1.0 / (innerDx * innerDx) : 0.0;
            int maxX = (int)dx;
            for (int x = minX = -maxX; x <= maxX; ++x) {
                double dz = Math.sqrt(1.0 - (double)(x * x) * dxInvSqr) * dx;
                int maxZ = (int)dz;
                int minZ = -maxZ;
                double innerMaxZ = cap ? 0.0 : Math.sqrt(1.0 - (double)(x * x) * innerDxInvSqr) * innerDx;
                double innerMinZ = cap ? 0.0 : -innerMaxZ;
                for (int z = minZ; z <= maxZ; ++z) {
                    if ((double)z > innerMinZ && (double)z < innerMaxZ || consumer.test(originX + x, originY + height - 1 - y, originZ + z, t)) continue;
                    return;
                }
            }
        }
    }
}

