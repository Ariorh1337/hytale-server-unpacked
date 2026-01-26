/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.collision;

import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class CollisionMath {
    public static final ThreadLocal<Vector2d> MIN_MAX = ThreadLocal.withInitial(Vector2d::new);
    public static final int DISJOINT = 0;
    public static final int TOUCH_X = 1;
    public static final int TOUCH_Y = 2;
    public static final int TOUCH_Z = 4;
    public static final int TOUCH_ANY = 7;
    public static final int OVERLAP_X = 8;
    public static final int OVERLAP_Y = 16;
    public static final int OVERLAP_Z = 32;
    public static final int OVERLAP_ANY = 56;
    public static final int OVERLAP_ALL = 56;

    public CollisionMath() {
        throw new IllegalStateException("CollisionMath can't be instantiated");
    }

    public static boolean intersectVectorAABB(@Nonnull Vector3d pos, @Nonnull Vector3d vec, double x, double y, double z, @Nonnull Box box, @Nonnull Vector2d minMax) {
        return CollisionMath.intersectRayAABB(pos, vec, x, y, z, box, minMax) && minMax.x <= 1.0;
    }

    public static boolean intersectRayAABB(@Nonnull Vector3d pos, @Nonnull Vector3d ray, double x, double y, double z, @Nonnull Box box, @Nonnull Vector2d minMax) {
        minMax.x = 0.0;
        minMax.y = Double.MAX_VALUE;
        Vector3d min = box.getMin();
        Vector3d max = box.getMax();
        return CollisionMath.intersect1D(pos.x, ray.getX(), x + min.x, x + max.x, minMax) && CollisionMath.intersect1D(pos.y, ray.getY(), y + min.y, y + max.y, minMax) && CollisionMath.intersect1D(pos.z, ray.getZ(), z + min.z, z + max.z, minMax) && minMax.x >= 0.0;
    }

    public static double intersectRayAABB(@Nonnull Vector3d pos, @Nonnull Vector3d ray, double x, double y, double z, @Nonnull Box box) {
        Vector2d minMax = MIN_MAX.get();
        return CollisionMath.intersectRayAABB(pos, ray, x, y, z, box, minMax) ? minMax.x : -1.7976931348623157E308;
    }

    public static boolean intersectVectorAABB(@Nonnull Vector3d pos, @Nonnull Vector3d vec, double x, double y, double z, double radius, double height, @Nonnull Vector2d minMax) {
        return CollisionMath.intersectRayAABB(pos, vec, x, y, z, radius, height, minMax) && minMax.x <= 1.0;
    }

    public static boolean intersectRayAABB(@Nonnull Vector3d pos, @Nonnull Vector3d ray, double x, double y, double z, double radius, double height, @Nonnull Vector2d minMax) {
        minMax.x = 0.0;
        minMax.y = Double.MAX_VALUE;
        return CollisionMath.intersect1D(pos.x, ray.getX(), x - radius, x + radius, minMax) && CollisionMath.intersect1D(pos.y, ray.getY(), y, y + height, minMax) && CollisionMath.intersect1D(pos.z, ray.getZ(), z - radius, z + radius, minMax) && minMax.x >= 0.0;
    }

    public static boolean intersectSweptAABBs(@Nonnull Vector3d posP, @Nonnull Vector3d vP, @Nonnull Box p, @Nonnull Vector3d posQ, @Nonnull Box q, @Nonnull Vector2d minMax, @Nonnull Box temp) {
        return CollisionMath.intersectSweptAABBs(posP, vP, p, posQ.x, posQ.y, posQ.z, q, minMax, temp);
    }

    public static boolean intersectSweptAABBs(@Nonnull Vector3d posP, @Nonnull Vector3d vP, @Nonnull Box p, double qx, double qy, double qz, @Nonnull Box q, @Nonnull Vector2d minMax, @Nonnull Box temp) {
        temp.assign(q).minkowskiSum(p);
        return CollisionMath.intersectVectorAABB(posP, vP, qx, qy, qz, temp, minMax);
    }

    public static boolean intersect1D(double p, double s, double min, double max, @Nonnull Vector2d minMax) {
        if (Math.abs(s) < 1.0E-5) {
            return !(p < min) && !(p > max);
        }
        double t2 = (max - p) / s;
        double t1 = (min - p) / s;
        if (t2 >= t1) {
            if (t1 > minMax.x) {
                minMax.x = t1;
            }
            if (t2 < minMax.y) {
                minMax.y = t2;
            }
        } else {
            if (t2 > minMax.x) {
                minMax.x = t2;
            }
            if (t1 < minMax.y) {
                minMax.y = t1;
            }
        }
        return minMax.x <= minMax.y;
    }

    public static boolean isDisjoint(int code) {
        return code == 0;
    }

    public static boolean isOverlapping(int code) {
        return code == 56;
    }

    public static boolean isTouching(int code) {
        return (code & 7) != 0;
    }

    public static int intersectAABBs(@Nonnull Vector3d p, @Nonnull Box bbP, @Nonnull Vector3d q, @Nonnull Box bbQ) {
        return CollisionMath.intersectAABBs(p.x, p.y, p.z, bbP, q.x, q.y, q.z, bbQ);
    }

    public static int intersectAABBs(double px, double py, double pz, @Nonnull Box bbP, double qx, double qy, double qz, @Nonnull Box bbQ) {
        int x = CollisionMath.intersect1D(px, bbP.min.x, bbP.max.x, qx, bbQ.min.x, bbQ.max.x);
        if (x == 0) {
            return 0;
        }
        x &= 9;
        int y = CollisionMath.intersect1D(py, bbP.min.y, bbP.max.y, qy, bbQ.min.y, bbQ.max.y);
        if (y == 0) {
            return 0;
        }
        y &= 0x12;
        int z = CollisionMath.intersect1D(pz, bbP.min.z, bbP.max.z, qz, bbQ.min.z, bbQ.max.z);
        if (z == 0) {
            return 0;
        }
        return x | y | (z &= 0x24);
    }

    public static int intersect1D(double p, double pMin, double pMax, double q, double qMin, double qMax) {
        return CollisionMath.intersect1D(p, pMin, pMax, q, qMin, qMax, 1.0E-5);
    }

    public static int intersectAABBs(double px, double py, double pz, @Nonnull Box bbP, double qx, double qy, double qz, @Nonnull Box bbQ, double thickness) {
        int x = CollisionMath.intersect1D(px, bbP.min.x, bbP.max.x, qx, bbQ.min.x, bbQ.max.x, thickness);
        if (x == 0) {
            return 0;
        }
        x &= 9;
        int y = CollisionMath.intersect1D(py, bbP.min.y, bbP.max.y, qy, bbQ.min.y, bbQ.max.y, thickness);
        if (y == 0) {
            return 0;
        }
        y &= 0x12;
        int z = CollisionMath.intersect1D(pz, bbP.min.z, bbP.max.z, qz, bbQ.min.z, bbQ.max.z, thickness);
        if (z == 0) {
            return 0;
        }
        return x | y | (z &= 0x24);
    }

    public static int intersect1D(double p, double pMin, double pMax, double q, double qMin, double qMax, double thickness) {
        double offset = q - p;
        double dist = pMin - qMax - offset;
        if (dist > thickness) {
            return 0;
        }
        if (dist > -thickness) {
            return 7;
        }
        dist = qMin - pMax + offset;
        if (dist > thickness) {
            return 0;
        }
        if (dist > -thickness) {
            return 7;
        }
        return 56;
    }
}

