/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.raycast;

import javax.annotation.Nonnull;

public class RaycastAABB {
    public static final double EPSILON = -1.0E-8;

    public static double intersect(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double ox, double oy, double oz, double dx, double dy, double dz) {
        double v;
        double u;
        double t = (minX - ox) / dx;
        double tNear = Double.POSITIVE_INFINITY;
        if (t < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
            }
        }
        if ((t = (maxX - ox) / dx) < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
            }
        }
        if ((t = (minY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
            }
        }
        if ((t = (maxY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
            }
        }
        if ((t = (minZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
            }
        }
        if ((t = (maxZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
            }
        }
        return tNear;
    }

    public static void intersect(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double ox, double oy, double oz, double dx, double dy, double dz, @Nonnull RaycastConsumer consumer) {
        double v;
        double u;
        double tNear = Double.POSITIVE_INFINITY;
        double nx = 0.0;
        double ny = 0.0;
        double nz = 0.0;
        double t = (minX - ox) / dx;
        if (t < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = -1.0;
            }
        }
        if ((t = (maxX - ox) / dx) < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = 1.0;
            }
        }
        if ((t = (minY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = -1.0;
            }
        }
        if ((t = (maxY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = 1.0;
            }
        }
        if ((t = (minZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = -1.0;
            }
        }
        if ((t = (maxZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = 1.0;
            }
        }
        consumer.accept(tNear != Double.POSITIVE_INFINITY, ox, oy, oz, dx, dy, dz, tNear, nx, ny, nz);
    }

    public static <T> void intersect(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double ox, double oy, double oz, double dx, double dy, double dz, @Nonnull RaycastConsumerPlus1<T> consumer, T obj1) {
        double v;
        double u;
        double tNear = Double.POSITIVE_INFINITY;
        double nx = 0.0;
        double ny = 0.0;
        double nz = 0.0;
        double t = (minX - ox) / dx;
        if (t < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = -1.0;
            }
        }
        if ((t = (maxX - ox) / dx) < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = 1.0;
            }
        }
        if ((t = (minY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = -1.0;
            }
        }
        if ((t = (maxY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = 1.0;
            }
        }
        if ((t = (minZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = -1.0;
            }
        }
        if ((t = (maxZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = 1.0;
            }
        }
        consumer.accept(tNear != Double.POSITIVE_INFINITY, ox, oy, oz, dx, dy, dz, tNear, nx, ny, nz, obj1);
    }

    public static <T, K> void intersect(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double ox, double oy, double oz, double dx, double dy, double dz, @Nonnull RaycastConsumerPlus2<T, K> consumer, T obj1, K obj2) {
        double v;
        double u;
        double tNear = Double.POSITIVE_INFINITY;
        double nx = 0.0;
        double ny = 0.0;
        double nz = 0.0;
        double t = (minX - ox) / dx;
        if (t < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = -1.0;
            }
        }
        if ((t = (maxX - ox) / dx) < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = 1.0;
            }
        }
        if ((t = (minY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = -1.0;
            }
        }
        if ((t = (maxY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = 1.0;
            }
        }
        if ((t = (minZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = -1.0;
            }
        }
        if ((t = (maxZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = 1.0;
            }
        }
        consumer.accept(tNear != Double.POSITIVE_INFINITY, ox, oy, oz, dx, dy, dz, tNear, nx, ny, nz, obj1, obj2);
    }

    public static <T, K, L> void intersect(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double ox, double oy, double oz, double dx, double dy, double dz, @Nonnull RaycastConsumerPlus3<T, K, L> consumer, T obj1, K obj2, L obj3) {
        double v;
        double u;
        double tNear = Double.POSITIVE_INFINITY;
        double nx = 0.0;
        double ny = 0.0;
        double nz = 0.0;
        double t = (minX - ox) / dx;
        if (t < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = -1.0;
            }
        }
        if ((t = (maxX - ox) / dx) < tNear && t > -1.0E-8) {
            u = oz + dz * t;
            v = oy + dy * t;
            if (u >= minZ && u <= maxZ && v >= minY && v <= maxY) {
                tNear = t;
                nx = 1.0;
            }
        }
        if ((t = (minY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = -1.0;
            }
        }
        if ((t = (maxY - oy) / dy) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oz + dz * t;
            if (u >= minX && u <= maxX && v >= minZ && v <= maxZ) {
                tNear = t;
                ny = 1.0;
            }
        }
        if ((t = (minZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = -1.0;
            }
        }
        if ((t = (maxZ - oz) / dz) < tNear && t > -1.0E-8) {
            u = ox + dx * t;
            v = oy + dy * t;
            if (u >= minX && u <= maxX && v >= minY && v <= maxY) {
                tNear = t;
                nz = 1.0;
            }
        }
        consumer.accept(tNear != Double.POSITIVE_INFINITY, ox, oy, oz, dx, dy, dz, tNear, nx, ny, nz, obj1, obj2, obj3);
    }

    @FunctionalInterface
    public static interface RaycastConsumer {
        public void accept(boolean var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20);
    }

    @FunctionalInterface
    public static interface RaycastConsumerPlus1<T> {
        public void accept(boolean var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, T var22);
    }

    @FunctionalInterface
    public static interface RaycastConsumerPlus2<T, K> {
        public void accept(boolean var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, T var22, K var23);
    }

    @FunctionalInterface
    public static interface RaycastConsumerPlus3<T, K, L> {
        public void accept(boolean var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, T var22, K var23, L var24);
    }
}

