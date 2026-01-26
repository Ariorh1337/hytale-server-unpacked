/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

public class RootSolver {
    public static final double M_PI = Math.PI;
    public static final double EQN_EPS = 1.0E-15;

    protected static boolean isZero(double x) {
        return x > -1.0E-15 && x < 1.0E-15;
    }

    protected static double cubicRoot(double x) {
        return x > 0.0 ? Math.pow(x, 0.3333333333333333) : (x < 0.0 ? -Math.pow(-x, 0.3333333333333333) : 0.0);
    }

    public static int solveQuadric(double c2, double c1, double c0, double[] results, int resultIndex) {
        double p = c1 / (2.0 * c2);
        double q = c0 / c2;
        double D = p * p - q;
        if (RootSolver.isZero(D)) {
            results[resultIndex] = -p;
            return 1;
        }
        if (D < 0.0) {
            return 0;
        }
        double sqrt_D = Math.sqrt(D);
        results[resultIndex] = sqrt_D - p;
        results[resultIndex + 1] = -sqrt_D - p;
        return 2;
    }

    public static int solveCubic(double c3, double c2, double c1, double c0, double[] results) {
        int num;
        double A = c2 / c3;
        double sq_A = A * A;
        double B = c1 / c3;
        double C = c0 / c3;
        double q = 0.5 * (0.07407407407407407 * A * sq_A - 0.3333333333333333 * A * B + C);
        double p = 0.3333333333333333 * (-0.3333333333333333 * sq_A + B);
        double cb_p = p * p * p;
        double D = q * q + cb_p;
        if (RootSolver.isZero(D)) {
            if (RootSolver.isZero(q)) {
                results[0] = 0.0;
                num = 1;
            } else {
                double u = RootSolver.cubicRoot(-q);
                results[0] = 2.0 * u;
                results[1] = -u;
                num = 2;
            }
        } else if (D < 0.0) {
            double phi = 0.3333333333333333 * Math.acos(-q / Math.sqrt(-cb_p));
            double t = 2.0 * Math.sqrt(-p);
            results[0] = t * Math.cos(phi);
            results[1] = -t * Math.cos(phi + 1.0471975511965976);
            results[2] = -t * Math.cos(phi - 1.0471975511965976);
            num = 3;
        } else {
            double sqrt_D = Math.sqrt(D);
            double u = RootSolver.cubicRoot(sqrt_D - q);
            double v = -RootSolver.cubicRoot(sqrt_D + q);
            results[0] = u + v;
            num = 1;
        }
        double sub = 0.3333333333333333 * A;
        int i = 0;
        while (i < num) {
            int n = i++;
            results[n] = results[n] - sub;
        }
        return num;
    }

    public static int solveQuartic(double c4, double c3, double c2, double c1, double c0, double[] results) {
        int num;
        double A = c3 / c4;
        double B = c2 / c4;
        double C = c1 / c4;
        double D = c0 / c4;
        double sq_A = A * A;
        double p = -0.375 * sq_A + B;
        double q = 0.125 * sq_A * A - 0.5 * A * B + C;
        double r = -0.01171875 * sq_A * sq_A + 0.0625 * sq_A * B - 0.25 * A * C + D;
        if (RootSolver.isZero(r)) {
            coeff0 = q;
            coeff1 = p;
            coeff2 = 0.0;
            coeff3 = 1.0;
            num = RootSolver.solveCubic(coeff3, coeff2, coeff1, coeff0, results);
            results[num++] = 0.0;
        } else {
            coeff0 = 0.5 * r * p - 0.125 * q * q;
            coeff1 = -r;
            coeff2 = -0.5 * p;
            coeff3 = 1.0;
            RootSolver.solveCubic(coeff3, coeff2, coeff1, coeff0, results);
            double z = results[0];
            double u = z * z - r;
            double v = 2.0 * z - p;
            if (RootSolver.isZero(u)) {
                u = 0.0;
            } else if (u > 0.0) {
                u = Math.sqrt(u);
            } else {
                return 0;
            }
            if (RootSolver.isZero(v)) {
                v = 0.0;
            } else if (v > 0.0) {
                v = Math.sqrt(v);
            } else {
                return 0;
            }
            coeff0 = z - u;
            coeff1 = q < 0.0 ? -v : v;
            coeff2 = 1.0;
            num = RootSolver.solveQuadric(coeff2, coeff1, coeff0, results, 0);
            coeff0 = z + u;
            coeff1 = q < 0.0 ? v : -v;
            coeff2 = 1.0;
            num += RootSolver.solveQuadric(coeff2, coeff1, coeff0, results, num);
        }
        double sub = 0.25 * A;
        int i = 0;
        while (i < num) {
            int n = i++;
            results[n] = results[n] - sub;
        }
        return num;
    }
}

