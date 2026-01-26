/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave.shape.distorted;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.procedurallib.logic.GeneralNoise;
import com.hypixel.hytale.server.worldgen.cave.CaveType;
import com.hypixel.hytale.server.worldgen.cave.shape.distorted.ShapeDistortion;
import com.hypixel.hytale.server.worldgen.util.bounds.IWorldBounds;

public interface DistortedShape
extends IWorldBounds {
    public Vector3d getStart();

    public Vector3d getEnd();

    public Vector3d getAnchor(Vector3d var1, double var2, double var4, double var6);

    default public boolean hasGeometry() {
        return this.getHighBoundX() > this.getLowBoundX() && this.getHighBoundY() > this.getLowBoundY() && this.getHighBoundZ() > this.getLowBoundZ();
    }

    public double getProjection(double var1, double var3);

    public boolean isValidProjection(double var1);

    public double getYAt(double var1);

    public double getWidthAt(double var1);

    public double getHeightAt(double var1);

    public double getHeightAtProjection(int var1, double var2, double var4, double var6, double var8, CaveType var10, ShapeDistortion var11);

    default public double getCeiling(double x, double z, double centerY, double height) {
        return centerY + height;
    }

    default public double getFloor(double x, double z, double centerY, double height) {
        return centerY - height;
    }

    public static interface Factory {
        public static final GeneralNoise.InterpolationFunction DEFAULT_INTERPOLATION = GeneralNoise.InterpolationMode.LINEAR.function;

        public DistortedShape create(Vector3d var1, Vector3d var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, GeneralNoise.InterpolationFunction var17);

        default public DistortedShape create(Vector3d origin, Vector3d direction, double length, double startWidth, double startHeight, double midWidth, double midHeight, double endWidth, double endHeight) {
            return this.create(origin, direction, length, startWidth, startHeight, midWidth, midHeight, endWidth, endHeight, DEFAULT_INTERPOLATION);
        }

        default public DistortedShape create(Vector3d origin, Vector3d direction, double length, double startWidth, double startHeight, double endWidth, double endHeight, GeneralNoise.InterpolationFunction interpolation) {
            double midWidth = (startWidth + endWidth) * 0.5;
            double midHeight = (startHeight + endHeight) * 0.5;
            return this.create(origin, direction, length, startWidth, startHeight, midWidth, midHeight, endWidth, endHeight, interpolation);
        }

        default public DistortedShape create(Vector3d origin, Vector3d direction, double length, double startWidth, double startHeight, double endWidth, double endHeight) {
            return this.create(origin, direction, length, startWidth, startHeight, endWidth, endHeight, DEFAULT_INTERPOLATION);
        }

        default public DistortedShape create(Vector3d origin, Vector3d direction, double length, double width, double height, GeneralNoise.InterpolationFunction interpolation) {
            return this.create(origin, direction, length, width, height, width, height, width, height, interpolation);
        }
    }
}

