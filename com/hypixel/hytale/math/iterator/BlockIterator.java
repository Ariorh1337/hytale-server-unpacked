/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.iterator;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public final class BlockIterator {
    private BlockIterator() {
        throw new UnsupportedOperationException("This is a utilitiy class. Do not instantiate.");
    }

    public static boolean iterateFromTo(@Nonnull Vector3d origin, @Nonnull Vector3d target, @Nonnull BlockIteratorProcedure procedure) {
        return BlockIterator.iterateFromTo(origin.x, origin.y, origin.z, target.x, target.y, target.z, procedure);
    }

    public static boolean iterateFromTo(@Nonnull Vector3i origin, @Nonnull Vector3i target, @Nonnull BlockIteratorProcedure procedure) {
        return BlockIterator.iterateFromTo(origin.x, origin.y, origin.z, target.x, target.y, target.z, procedure);
    }

    public static boolean iterateFromTo(double sx, double sy, double sz, double tx, double ty, double tz, @Nonnull BlockIteratorProcedure procedure) {
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double maxDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return BlockIterator.iterate(sx, sy, sz, dx, dy, dz, maxDistance, procedure);
    }

    public static <T> boolean iterateFromTo(double sx, double sy, double sz, double tx, double ty, double tz, @Nonnull BlockIteratorProcedurePlus1<T> procedure, T t) {
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double maxDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return BlockIterator.iterate(sx, sy, sz, dx, dy, dz, maxDistance, procedure, t);
    }

    public static boolean iterate(@Nonnull Vector3d origin, @Nonnull Vector3d direction, double maxDistance, @Nonnull BlockIteratorProcedure procedure) {
        return BlockIterator.iterate(origin.x, origin.y, origin.z, direction.x, direction.y, direction.z, maxDistance, procedure);
    }

    public static boolean iterate(double sx, double sy, double sz, double dx, double dy, double dz, double maxDistance, @Nonnull BlockIteratorProcedure procedure) {
        BlockIterator.checkParameters(sx, sy, sz, dx, dy, dz);
        return BlockIterator.iterate0(sx, sy, sz, dx, dy, dz, maxDistance, procedure);
    }

    private static boolean iterate0(double sx, double sy, double sz, double dx, double dy, double dz, double maxDistance, @Nonnull BlockIteratorProcedure procedure) {
        double t;
        maxDistance /= Math.sqrt(dx * dx + dy * dy + dz * dz);
        int bx = (int)FastMath.fastFloor(sx);
        int by = (int)FastMath.fastFloor(sy);
        int bz = (int)FastMath.fastFloor(sz);
        double px = sx - (double)bx;
        double py = sy - (double)by;
        double pz = sz - (double)bz;
        for (double pt = 0.0; pt <= maxDistance; pt += t) {
            double qz;
            double qy;
            t = BlockIterator.intersection(px, py, pz, dx, dy, dz);
            double qx = px + t * dx;
            if (!procedure.accept(bx, by, bz, px, py, pz, qx, qy = py + t * dy, qz = pz + t * dz)) {
                return false;
            }
            if (dx < 0.0 && FastMath.sEq(qx, 0.0)) {
                qx += 1.0;
                --bx;
            } else if (dx > 0.0 && FastMath.gEq(qx, 1.0)) {
                qx -= 1.0;
                ++bx;
            }
            if (dy < 0.0 && FastMath.sEq(qy, 0.0)) {
                qy += 1.0;
                --by;
            } else if (dy > 0.0 && FastMath.gEq(qy, 1.0)) {
                qy -= 1.0;
                ++by;
            }
            if (dz < 0.0 && FastMath.sEq(qz, 0.0)) {
                qz += 1.0;
                --bz;
            } else if (dz > 0.0 && FastMath.gEq(qz, 1.0)) {
                qz -= 1.0;
                ++bz;
            }
            px = qx;
            py = qy;
            pz = qz;
        }
        return true;
    }

    public static <T> boolean iterate(double sx, double sy, double sz, double dx, double dy, double dz, double maxDistance, @Nonnull BlockIteratorProcedurePlus1<T> procedure, T obj1) {
        BlockIterator.checkParameters(sx, sy, sz, dx, dy, dz);
        return BlockIterator.iterate0(sx, sy, sz, dx, dy, dz, maxDistance, procedure, obj1);
    }

    private static <T> boolean iterate0(double sx, double sy, double sz, double dx, double dy, double dz, double maxDistance, @Nonnull BlockIteratorProcedurePlus1<T> procedure, T obj1) {
        double t;
        maxDistance /= Math.sqrt(dx * dx + dy * dy + dz * dz);
        int bx = (int)FastMath.fastFloor(sx);
        int by = (int)FastMath.fastFloor(sy);
        int bz = (int)FastMath.fastFloor(sz);
        double px = sx - (double)bx;
        double py = sy - (double)by;
        double pz = sz - (double)bz;
        for (double pt = 0.0; pt <= maxDistance; pt += t) {
            double qz;
            double qy;
            t = BlockIterator.intersection(px, py, pz, dx, dy, dz);
            double qx = px + t * dx;
            if (!procedure.accept(bx, by, bz, px, py, pz, qx, qy = py + t * dy, qz = pz + t * dz, obj1)) {
                return false;
            }
            if (dx < 0.0 && FastMath.sEq(qx, 0.0)) {
                qx += 1.0;
                --bx;
            } else if (dx > 0.0 && FastMath.gEq(qx, 1.0)) {
                qx -= 1.0;
                ++bx;
            }
            if (dy < 0.0 && FastMath.sEq(qy, 0.0)) {
                qy += 1.0;
                --by;
            } else if (dy > 0.0 && FastMath.gEq(qy, 1.0)) {
                qy -= 1.0;
                ++by;
            }
            if (dz < 0.0 && FastMath.sEq(qz, 0.0)) {
                qz += 1.0;
                --bz;
            } else if (dz > 0.0 && FastMath.gEq(qz, 1.0)) {
                qz -= 1.0;
                ++bz;
            }
            px = qx;
            py = qy;
            pz = qz;
        }
        return true;
    }

    private static void checkParameters(double sx, double sy, double sz, double dx, double dy, double dz) {
        if (BlockIterator.isNonValidNumber(sx)) {
            throw new IllegalArgumentException("sx is a non-valid number! Given: " + sx);
        }
        if (BlockIterator.isNonValidNumber(sy)) {
            throw new IllegalArgumentException("sy is a non-valid number! Given: " + sy);
        }
        if (BlockIterator.isNonValidNumber(sz)) {
            throw new IllegalArgumentException("sz is a non-valid number! Given: " + sz);
        }
        if (BlockIterator.isNonValidNumber(dx)) {
            throw new IllegalArgumentException("dx is a non-valid number! Given: " + dx);
        }
        if (BlockIterator.isNonValidNumber(dy)) {
            throw new IllegalArgumentException("dy is a non-valid number! Given: " + dy);
        }
        if (BlockIterator.isNonValidNumber(dz)) {
            throw new IllegalArgumentException("dz is a non-valid number! Given: " + dz);
        }
        if (BlockIterator.isZeroDirection(dx, dy, dz)) {
            throw new IllegalArgumentException("Direction is ZERO! Given: (" + dx + ", " + dy + ", " + dz + ")");
        }
    }

    public static boolean isNonValidNumber(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }

    public static boolean isZeroDirection(double dx, double dy, double dz) {
        return FastMath.eq(dx, 0.0) && FastMath.eq(dy, 0.0) && FastMath.eq(dz, 0.0);
    }

    private static double intersection(double px, double py, double pz, double dx, double dy, double dz) {
        double v;
        double u;
        double t;
        double tFar = 0.0;
        if (dx < 0.0) {
            t = -px / dx;
            u = pz + dz * t;
            v = py + dy * t;
            if (t > tFar && FastMath.gEq(u, 0.0) && FastMath.sEq(u, 1.0) && FastMath.gEq(v, 0.0) && FastMath.sEq(v, 1.0)) {
                tFar = t;
            }
        } else if (dx > 0.0) {
            t = (1.0 - px) / dx;
            u = pz + dz * t;
            v = py + dy * t;
            if (t > tFar && FastMath.gEq(u, 0.0) && FastMath.sEq(u, 1.0) && FastMath.gEq(v, 0.0) && FastMath.sEq(v, 1.0)) {
                tFar = t;
            }
        }
        if (dy < 0.0) {
            t = -py / dy;
            u = px + dx * t;
            v = pz + dz * t;
            if (t > tFar && FastMath.gEq(u, 0.0) && FastMath.sEq(u, 1.0) && FastMath.gEq(v, 0.0) && FastMath.sEq(v, 1.0)) {
                tFar = t;
            }
        } else if (dy > 0.0) {
            t = (1.0 - py) / dy;
            u = px + dx * t;
            v = pz + dz * t;
            if (t > tFar && FastMath.gEq(u, 0.0) && FastMath.sEq(u, 1.0) && FastMath.gEq(v, 0.0) && FastMath.sEq(v, 1.0)) {
                tFar = t;
            }
        }
        if (dz < 0.0) {
            t = -pz / dz;
            u = px + dx * t;
            v = py + dy * t;
            if (t > tFar && FastMath.gEq(u, 0.0) && FastMath.sEq(u, 1.0) && FastMath.gEq(v, 0.0) && FastMath.sEq(v, 1.0)) {
                tFar = t;
            }
        } else if (dz > 0.0) {
            t = (1.0 - pz) / dz;
            u = px + dx * t;
            v = py + dy * t;
            if (t > tFar && FastMath.gEq(u, 0.0) && FastMath.sEq(u, 1.0) && FastMath.gEq(v, 0.0) && FastMath.sEq(v, 1.0)) {
                tFar = t;
            }
        }
        return tFar;
    }

    @FunctionalInterface
    public static interface BlockIteratorProcedure {
        public boolean accept(int var1, int var2, int var3, double var4, double var6, double var8, double var10, double var12, double var14);
    }

    @FunctionalInterface
    public static interface BlockIteratorProcedurePlus1<T> {
        public boolean accept(int var1, int var2, int var3, double var4, double var6, double var8, double var10, double var12, double var14, T var16);
    }

    static class FastMath {
        static final double TWO_POWER_52 = 4.503599627370496E15;
        static final double ROUNDING_ERROR = 1.0E-15;

        FastMath() {
        }

        static boolean eq(double a, double b) {
            return FastMath.abs(a - b) < 1.0E-15;
        }

        static boolean sEq(double a, double b) {
            return a <= b + 1.0E-15;
        }

        static boolean gEq(double a, double b) {
            return a >= b - 1.0E-15;
        }

        static double abs(double x) {
            return x < 0.0 ? -x : x;
        }

        static long fastFloor(double x) {
            if (x >= 4.503599627370496E15 || x <= -4.503599627370496E15) {
                return (long)x;
            }
            long y = (long)x;
            if (x < 0.0 && (double)y != x) {
                --y;
            }
            if (y == 0L) {
                return (long)(x * (double)y);
            }
            return y;
        }
    }
}

