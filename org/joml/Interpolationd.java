/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.Vector2d;
import org.joml.Vector3d;

public class Interpolationd {
    public static double interpolateTriangle(double v0X, double v0Y, double f0, double v1X, double v1Y, double f1, double v2X, double v2Y, double f2, double x, double y) {
        double v12Y = v1Y - v2Y;
        double v21X = v2X - v1X;
        double v02X = v0X - v2X;
        double yv2Y = y - v2Y;
        double xv2X = x - v2X;
        double v02Y = v0Y - v2Y;
        double invDen = 1.0 / (v12Y * v02X + v21X * v02Y);
        double l1 = (v12Y * xv2X + v21X * yv2Y) * invDen;
        double l2 = (v02X * yv2Y - v02Y * xv2X) * invDen;
        return l1 * f0 + l2 * f1 + (1.0 - l1 - l2) * f2;
    }

    public static Vector2d interpolateTriangle(double v0X, double v0Y, double f0X, double f0Y, double v1X, double v1Y, double f1X, double f1Y, double v2X, double v2Y, double f2X, double f2Y, double x, double y, Vector2d dest) {
        double v12Y = v1Y - v2Y;
        double v21X = v2X - v1X;
        double v02X = v0X - v2X;
        double yv2Y = y - v2Y;
        double xv2X = x - v2X;
        double v02Y = v0Y - v2Y;
        double invDen = 1.0 / (v12Y * v02X + v21X * v02Y);
        double l1 = (v12Y * xv2X + v21X * yv2Y) * invDen;
        double l2 = (v02X * yv2Y - v02Y * xv2X) * invDen;
        double l3 = 1.0 - l1 - l2;
        dest.x = l1 * f0X + l2 * f1X + l3 * f2X;
        dest.y = l1 * f0Y + l2 * f1Y + l3 * f2Y;
        return dest;
    }

    public static Vector2d dFdxLinear(double v0X, double v0Y, double f0X, double f0Y, double v1X, double v1Y, double f1X, double f1Y, double v2X, double v2Y, double f2X, double f2Y, Vector2d dest) {
        double v12Y = v1Y - v2Y;
        double v02Y = v0Y - v2Y;
        double den = v12Y * (v0X - v2X) + (v2X - v1X) * v02Y;
        double l3_1 = den - v12Y + v02Y;
        double invDen = 1.0 / den;
        dest.x = invDen * (v12Y * f0X - v02Y * f1X + l3_1 * f2X) - f2X;
        dest.y = invDen * (v12Y * f0Y - v02Y * f1Y + l3_1 * f2Y) - f2Y;
        return dest;
    }

    public static Vector2d dFdyLinear(double v0X, double v0Y, double f0X, double f0Y, double v1X, double v1Y, double f1X, double f1Y, double v2X, double v2Y, double f2X, double f2Y, Vector2d dest) {
        double v21X = v2X - v1X;
        double v02X = v0X - v2X;
        double den = (v1Y - v2Y) * v02X + v21X * (v0Y - v2Y);
        double l3_1 = den - v21X - v02X;
        double invDen = 1.0 / den;
        dest.x = invDen * (v21X * f0X + v02X * f1X + l3_1 * f2X) - f2X;
        dest.y = invDen * (v21X * f0Y + v02X * f1Y + l3_1 * f2Y) - f2Y;
        return dest;
    }

    public static Vector3d interpolateTriangle(double v0X, double v0Y, double f0X, double f0Y, double f0Z, double v1X, double v1Y, double f1X, double f1Y, double f1Z, double v2X, double v2Y, double f2X, double f2Y, double f2Z, double x, double y, Vector3d dest) {
        Vector3d t = dest;
        Interpolationd.interpolationFactorsTriangle(v0X, v0Y, v1X, v1Y, v2X, v2Y, x, y, t);
        return dest.set(t.x * f0X + t.y * f1X + t.z * f2X, t.x * f0Y + t.y * f1Y + t.z * f2Y, t.x * f0Z + t.y * f1Z + t.z * f2Z);
    }

    public static Vector3d interpolationFactorsTriangle(double v0X, double v0Y, double v1X, double v1Y, double v2X, double v2Y, double x, double y, Vector3d dest) {
        double v12Y = v1Y - v2Y;
        double v21X = v2X - v1X;
        double v02X = v0X - v2X;
        double yv2Y = y - v2Y;
        double xv2X = x - v2X;
        double v02Y = v0Y - v2Y;
        double invDen = 1.0 / (v12Y * v02X + v21X * v02Y);
        dest.x = (v12Y * xv2X + v21X * yv2Y) * invDen;
        dest.y = (v02X * yv2Y - v02Y * xv2X) * invDen;
        dest.z = 1.0 - dest.x - dest.y;
        return dest;
    }
}

