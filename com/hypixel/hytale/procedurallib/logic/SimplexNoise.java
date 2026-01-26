/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic;

import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.logic.GeneralNoise;
import javax.annotation.Nonnull;

public class SimplexNoise
implements NoiseFunction {
    public static final SimplexNoise INSTANCE = new SimplexNoise();
    private static final double F2 = 0.5;
    private static final double P1_F2 = -0.5;
    private static final double G2 = 0.25;
    private static final double F3 = 0.3333333333333333;
    private static final double G3 = 0.16666666666666666;
    private static final double G33 = -0.5;

    private SimplexNoise() {
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y) {
        double n2;
        double n1;
        double n0;
        int j1;
        int i1;
        double Y0;
        double y0;
        int j;
        double t = (x + y) * 0.5;
        int i = GeneralNoise.fastFloor(x + t);
        double X0 = (double)i - (t = (double)(i + (j = GeneralNoise.fastFloor(y + t))) * 0.25);
        double x0 = x - X0;
        if (x0 > (y0 = y - (Y0 = (double)j - t))) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }
        t = 0.5 - x0 * x0 - y0 * y0;
        if (t < 0.0) {
            n0 = 0.0;
        } else {
            t *= t;
            n0 = t * t * GeneralNoise.gradCoord2D(offsetSeed, i, j, x0, y0);
        }
        double x1 = x0 - (double)i1 + 0.25;
        double y1 = y0 - (double)j1 + 0.25;
        t = 0.5 - x1 * x1 - y1 * y1;
        if (t < 0.0) {
            n1 = 0.0;
        } else {
            t *= t;
            n1 = t * t * GeneralNoise.gradCoord2D(offsetSeed, i + i1, j + j1, x1, y1);
        }
        double x2 = x0 + -0.5;
        double y2 = y0 + -0.5;
        t = 0.5 - x2 * x2 - y2 * y2;
        if (t < 0.0) {
            n2 = 0.0;
        } else {
            t *= t;
            n2 = t * t * GeneralNoise.gradCoord2D(offsetSeed, i + 1, j + 1, x2, y2);
        }
        return 50.0 * (n0 + n1 + n2);
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y, double z) {
        double n3;
        double n2;
        double n1;
        double n0;
        int k2;
        int j2;
        int i2;
        int k1;
        int j1;
        int i1;
        double t = (x + y + z) * 0.3333333333333333;
        int i = GeneralNoise.fastFloor(x + t);
        int j = GeneralNoise.fastFloor(y + t);
        int k = GeneralNoise.fastFloor(z + t);
        t = (double)(i + j + k) * 0.16666666666666666;
        double x0 = x - ((double)i - t);
        double y0 = y - ((double)j - t);
        double z0 = z - ((double)k - t);
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else if (y0 < z0) {
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else if (x0 < z0) {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        }
        double x1 = x0 - (double)i1 + 0.16666666666666666;
        double y1 = y0 - (double)j1 + 0.16666666666666666;
        double z1 = z0 - (double)k1 + 0.16666666666666666;
        double x2 = x0 - (double)i2 + 0.3333333333333333;
        double y2 = y0 - (double)j2 + 0.3333333333333333;
        double z2 = z0 - (double)k2 + 0.3333333333333333;
        double x3 = x0 + -0.5;
        double y3 = y0 + -0.5;
        double z3 = z0 + -0.5;
        t = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t < 0.0) {
            n0 = 0.0;
        } else {
            t *= t;
            n0 = t * t * GeneralNoise.gradCoord3D(offsetSeed, i, j, k, x0, y0, z0);
        }
        t = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t < 0.0) {
            n1 = 0.0;
        } else {
            t *= t;
            n1 = t * t * GeneralNoise.gradCoord3D(offsetSeed, i + i1, j + j1, k + k1, x1, y1, z1);
        }
        t = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t < 0.0) {
            n2 = 0.0;
        } else {
            t *= t;
            n2 = t * t * GeneralNoise.gradCoord3D(offsetSeed, i + i2, j + j2, k + k2, x2, y2, z2);
        }
        t = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t < 0.0) {
            n3 = 0.0;
        } else {
            t *= t;
            n3 = t * t * GeneralNoise.gradCoord3D(offsetSeed, i + 1, j + 1, k + 1, x3, y3, z3);
        }
        return 32.0 * (n0 + n1 + n2 + n3);
    }

    @Nonnull
    public String toString() {
        return "SimplexNoise{}";
    }
}

