/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.random;

import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.procedurallib.random.ICoordinateRandomizer;
import javax.annotation.Nonnull;

public class CoordinateRotator
implements ICoordinateRandomizer {
    public static final CoordinateRotator NONE = new CoordinateRotator(0.0, 0.0);
    public static final int X0 = 0;
    public static final int Y0 = 1;
    public static final int Z0 = 2;
    public static final int X1 = 3;
    public static final int Y1 = 4;
    public static final int Z1 = 5;
    public static final int X2 = 6;
    public static final int Y2 = 7;
    public static final int Z2 = 8;
    protected final double pitch;
    protected final double yaw;
    @Nonnull
    protected final double[] matrix;

    public CoordinateRotator(double pitch, double yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.matrix = CoordinateRotator.createRotationMatrix(pitch, yaw);
    }

    public double rotateX(double x, double y) {
        return x * this.matrix[0] + y * this.matrix[2];
    }

    public double rotateY(double x, double y) {
        return x * this.matrix[6] + y * this.matrix[8];
    }

    public double rotateX(double x, double y, double z) {
        return x * this.matrix[0] + y * this.matrix[1] + z * this.matrix[2];
    }

    public double rotateY(double x, double y, double z) {
        return x * this.matrix[3] + y * this.matrix[4] + z * this.matrix[5];
    }

    public double rotateZ(double x, double y, double z) {
        return x * this.matrix[6] + y * this.matrix[7] + z * this.matrix[8];
    }

    @Override
    public double randomDoubleX(int seed, double x, double y) {
        return this.rotateX(x, y);
    }

    @Override
    public double randomDoubleY(int seed, double x, double y) {
        return this.rotateY(x, y);
    }

    @Override
    public double randomDoubleX(int seed, double x, double y, double z) {
        return this.rotateX(x, y, z);
    }

    @Override
    public double randomDoubleY(int seed, double x, double y, double z) {
        return this.rotateY(x, y, z);
    }

    @Override
    public double randomDoubleZ(int seed, double x, double y, double z) {
        return this.rotateZ(x, y, z);
    }

    @Nonnull
    public String toString() {
        return "CoordinateRotator{pitch=" + this.pitch + ", yaw=" + this.yaw + "}";
    }

    public static double[] createRotationMatrix(double pitch, double yaw) {
        double sinYaw = TrigMathUtil.sin(yaw);
        double cosYaw = TrigMathUtil.cos(yaw);
        double sinPitch = TrigMathUtil.sin(pitch);
        double cosPitch = TrigMathUtil.cos(pitch);
        double px1 = 1.0;
        double px2 = 0.0;
        double px3 = 0.0;
        double py1 = 0.0;
        double py2 = cosPitch;
        double py3 = -sinPitch;
        double pz1 = 0.0;
        double pz2 = sinPitch;
        double pz3 = cosPitch;
        double yx1 = cosYaw;
        double yx2 = 0.0;
        double yx3 = sinYaw;
        double yy1 = 0.0;
        double yy2 = 1.0;
        double yy3 = 0.0;
        double yz1 = -sinYaw;
        double yz2 = 0.0;
        double yz3 = cosYaw;
        return new double[]{CoordinateRotator.dot(px1, px2, px3, yx1, yy1, yz1), CoordinateRotator.dot(px1, px2, px3, yx2, yy2, yz2), CoordinateRotator.dot(px1, px2, px3, yx3, yy3, yz3), CoordinateRotator.dot(py1, py2, py3, yx1, yy1, yz1), CoordinateRotator.dot(py1, py2, py3, yx2, yy2, yz2), CoordinateRotator.dot(py1, py2, py3, yx3, yy3, yz3), CoordinateRotator.dot(pz1, pz2, pz3, yx1, yy1, yz1), CoordinateRotator.dot(pz1, pz2, pz3, yx2, yy2, yz2), CoordinateRotator.dot(pz1, pz2, pz3, yx3, yy3, yz3)};
    }

    private static double dot(double x1, double y1, double z1, double x2, double y2, double z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }
}

