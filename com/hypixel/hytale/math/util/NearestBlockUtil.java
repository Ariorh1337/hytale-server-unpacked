/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.util;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.function.BiPredicate;
import java.util.function.DoubleUnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NearestBlockUtil {
    public static final IterationElement[] DEFAULT_ELEMENTS = new IterationElement[]{new IterationElement(-1, 0, 0, x -> 0.0, y -> y, z -> z), new IterationElement(1, 0, 0, x -> 1.0, y -> y, z -> z), new IterationElement(0, -1, 0, x -> x, y -> 0.0, z -> z), new IterationElement(0, 1, 0, x -> x, y -> 1.0, z -> z), new IterationElement(0, 0, -1, x -> x, y -> y, z -> 0.0), new IterationElement(0, 0, 1, x -> x, y -> y, z -> 1.0)};

    private NearestBlockUtil() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public static <T> Vector3i findNearestBlock(@Nonnull Vector3d position, @Nonnull BiPredicate<Vector3i, T> validBlock, T t) {
        return NearestBlockUtil.findNearestBlock(DEFAULT_ELEMENTS, position.getX(), position.getY(), position.getZ(), validBlock, t);
    }

    @Nullable
    public static <T> Vector3i findNearestBlock(@Nonnull IterationElement[] elements, @Nonnull Vector3d position, @Nonnull BiPredicate<Vector3i, T> validBlock, T t) {
        return NearestBlockUtil.findNearestBlock(elements, position.getX(), position.getY(), position.getZ(), validBlock, t);
    }

    @Nullable
    public static <T> Vector3i findNearestBlock(double x, double y, double z, @Nonnull BiPredicate<Vector3i, T> validBlock, T t) {
        return NearestBlockUtil.findNearestBlock(DEFAULT_ELEMENTS, x, y, z, validBlock, t);
    }

    @Nullable
    public static <T> Vector3i findNearestBlock(@Nonnull IterationElement[] elements, double x, double y, double z, @Nonnull BiPredicate<Vector3i, T> validBlock, T t) {
        int blockX = MathUtil.floor(x);
        int blockY = MathUtil.floor(y);
        int blockZ = MathUtil.floor(z);
        double rx = x % 1.0;
        double ry = y % 1.0;
        double rz = z % 1.0;
        Vector3i nearest = null;
        Vector3i tmp = new Vector3i();
        double nearestDist = Double.POSITIVE_INFINITY;
        for (IterationElement element : elements) {
            double dx = rx - element.getX().applyAsDouble(rx);
            double dy = ry - element.getY().applyAsDouble(ry);
            double dz = rz - element.getZ().applyAsDouble(rz);
            double dist = dx * dx + dy * dy + dz * dz;
            tmp.assign(blockX + element.getOffsetX(), blockY + element.getOffsetY(), blockZ + element.getOffsetZ());
            if (!(dist < nearestDist) || !validBlock.test(tmp, (Vector3i)t)) continue;
            nearestDist = dist;
            if (nearest == null) {
                nearest = new Vector3i();
            }
            nearest.assign(tmp);
        }
        return nearest;
    }

    public static class IterationElement {
        private final int ox;
        private final int oy;
        private final int oz;
        private final DoubleUnaryOperator x;
        private final DoubleUnaryOperator y;
        private final DoubleUnaryOperator z;

        public IterationElement(int ox, int oy, int oz, DoubleUnaryOperator x, DoubleUnaryOperator y, DoubleUnaryOperator z) {
            this.ox = ox;
            this.oy = oy;
            this.oz = oz;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getOffsetX() {
            return this.ox;
        }

        public int getOffsetY() {
            return this.oy;
        }

        public int getOffsetZ() {
            return this.oz;
        }

        public DoubleUnaryOperator getX() {
            return this.x;
        }

        public DoubleUnaryOperator getY() {
            return this.y;
        }

        public DoubleUnaryOperator getZ() {
            return this.z;
        }
    }
}

