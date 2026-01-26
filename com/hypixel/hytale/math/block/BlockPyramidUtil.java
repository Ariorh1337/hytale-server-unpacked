/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.block;

import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import javax.annotation.Nonnull;

public class BlockPyramidUtil {
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
        for (int y = height - 1; y >= 0; --y) {
            int minX;
            double rf = 1.0 - (double)y / (double)height;
            double dx = (double)radiusX * rf;
            int maxX = (int)dx;
            for (int x = minX = -maxX; x <= maxX; ++x) {
                int minZ;
                double dz = (double)radiusZ * rf;
                int maxZ = (int)dz;
                for (int z = minZ = -maxZ; z <= maxZ; ++z) {
                    if (consumer.test(originX + x, originY + y, originZ + z, t)) continue;
                    return;
                }
            }
        }
    }

    public static <T> void forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        BlockPyramidUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, thickness, false, t, consumer);
    }

    public static <T> void forEachBlock(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, boolean capped, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            BlockPyramidUtil.forEachBlock(originX, originY, originZ, radiusX, height, radiusZ, t, consumer);
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
        double df = 1.0 / (double)height;
        for (int y = height - 1; y >= 0; --y) {
            boolean cap = capped && y < thickness;
            double rf = 1.0 - (double)y * df;
            double dx = rf * (double)radiusX;
            double dz = rf * (double)radiusZ;
            int maxX = (int)dx;
            int minX = -maxX;
            int maxZ = (int)dz;
            int minZ = -maxZ;
            double innerRf = rf - df;
            double innerDx = innerRf * (double)radiusX;
            double innerDz = innerRf * (double)radiusZ;
            int innerMinX = cap ? 1 : -((int)innerDx) + thickness;
            int innerMaxX = cap ? 0 : (int)innerDx - thickness;
            int innerMinZ = cap ? 1 : -((int)innerDz) + thickness;
            int innerMaxZ = cap ? 0 : (int)innerDz - thickness;
            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (x >= innerMinX && x <= innerMaxX && z >= innerMinZ && z <= innerMaxZ || consumer.test(originX + x, originY + y, originZ + z, t)) continue;
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
        for (int y = height - 1; y >= 0; --y) {
            int minX;
            double rf = 1.0 - (double)y / (double)height;
            double dx = (double)radiusX * rf;
            int maxX = (int)dx;
            for (int x = minX = -maxX; x <= maxX; ++x) {
                int minZ;
                double dz = (double)radiusZ * rf;
                int maxZ = (int)dz;
                for (int z = minZ = -maxZ; z <= maxZ; ++z) {
                    if (consumer.test(originX + x, originY + height - 1 - y, originZ + z, t)) continue;
                    return;
                }
            }
        }
    }

    public static <T> void forEachBlockInverted(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        BlockPyramidUtil.forEachBlockInverted(originX, originY, originZ, radiusX, height, radiusZ, thickness, false, t, consumer);
    }

    public static <T> void forEachBlockInverted(int originX, int originY, int originZ, int radiusX, int height, int radiusZ, int thickness, boolean capped, T t, @Nonnull TriIntObjPredicate<T> consumer) {
        if (thickness < 1) {
            BlockPyramidUtil.forEachBlockInverted(originX, originY, originZ, radiusX, height, radiusZ, t, consumer);
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
        double df = 1.0 / (double)height;
        for (int y = height - 1; y >= 0; --y) {
            boolean cap = capped && y < thickness;
            double rf = 1.0 - (double)y * df;
            double dx = rf * (double)radiusX;
            double dz = rf * (double)radiusZ;
            int maxX = (int)dx;
            int minX = -maxX;
            int maxZ = (int)dz;
            int minZ = -maxZ;
            double innerRf = rf - df;
            double innerDx = innerRf * (double)radiusX;
            double innerDz = innerRf * (double)radiusZ;
            int innerMinX = cap ? 1 : -((int)innerDx) + thickness;
            int innerMaxX = cap ? 0 : (int)innerDx - thickness;
            int innerMinZ = cap ? 1 : -((int)innerDz) + thickness;
            int innerMaxZ = cap ? 0 : (int)innerDz - thickness;
            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (x >= innerMinX && x <= innerMaxX && z >= innerMinZ && z <= innerMaxZ || consumer.test(originX + x, originY + height - 1 - y, originZ + z, t)) continue;
                    return;
                }
            }
        }
    }
}

