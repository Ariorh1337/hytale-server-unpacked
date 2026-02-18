/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Interpolationf {
    public static float interpolateTriangle(float v0X, float v0Y, float f0, float v1X, float v1Y, float f1, float v2X, float v2Y, float f2, float x, float y) {
        float v12Y = v1Y - v2Y;
        float v21X = v2X - v1X;
        float v02X = v0X - v2X;
        float yv2Y = y - v2Y;
        float xv2X = x - v2X;
        float v02Y = v0Y - v2Y;
        float invDen = 1.0f / (v12Y * v02X + v21X * v02Y);
        float l1 = (v12Y * xv2X + v21X * yv2Y) * invDen;
        float l2 = (v02X * yv2Y - v02Y * xv2X) * invDen;
        return l1 * f0 + l2 * f1 + (1.0f - l1 - l2) * f2;
    }

    public static Vector2f interpolateTriangle(float v0X, float v0Y, float f0X, float f0Y, float v1X, float v1Y, float f1X, float f1Y, float v2X, float v2Y, float f2X, float f2Y, float x, float y, Vector2f dest) {
        float v12Y = v1Y - v2Y;
        float v21X = v2X - v1X;
        float v02X = v0X - v2X;
        float yv2Y = y - v2Y;
        float xv2X = x - v2X;
        float v02Y = v0Y - v2Y;
        float invDen = 1.0f / (v12Y * v02X + v21X * v02Y);
        float l1 = (v12Y * xv2X + v21X * yv2Y) * invDen;
        float l2 = (v02X * yv2Y - v02Y * xv2X) * invDen;
        float l3 = 1.0f - l1 - l2;
        dest.x = l1 * f0X + l2 * f1X + l3 * f2X;
        dest.y = l1 * f0Y + l2 * f1Y + l3 * f2Y;
        return dest;
    }

    public static Vector2f dFdxLinear(float v0X, float v0Y, float f0X, float f0Y, float v1X, float v1Y, float f1X, float f1Y, float v2X, float v2Y, float f2X, float f2Y, Vector2f dest) {
        float v12Y = v1Y - v2Y;
        float v02Y = v0Y - v2Y;
        float den = v12Y * (v0X - v2X) + (v2X - v1X) * v02Y;
        float l3_1 = den - v12Y + v02Y;
        float invDen = 1.0f / den;
        dest.x = invDen * (v12Y * f0X - v02Y * f1X + l3_1 * f2X) - f2X;
        dest.y = invDen * (v12Y * f0Y - v02Y * f1Y + l3_1 * f2Y) - f2Y;
        return dest;
    }

    public static Vector2f dFdyLinear(float v0X, float v0Y, float f0X, float f0Y, float v1X, float v1Y, float f1X, float f1Y, float v2X, float v2Y, float f2X, float f2Y, Vector2f dest) {
        float v21X = v2X - v1X;
        float v02X = v0X - v2X;
        float den = (v1Y - v2Y) * v02X + v21X * (v0Y - v2Y);
        float l3_1 = den - v21X - v02X;
        float invDen = 1.0f / den;
        dest.x = invDen * (v21X * f0X + v02X * f1X + l3_1 * f2X) - f2X;
        dest.y = invDen * (v21X * f0Y + v02X * f1Y + l3_1 * f2Y) - f2Y;
        return dest;
    }

    public static Vector3f interpolateTriangle(float v0X, float v0Y, float f0X, float f0Y, float f0Z, float v1X, float v1Y, float f1X, float f1Y, float f1Z, float v2X, float v2Y, float f2X, float f2Y, float f2Z, float x, float y, Vector3f dest) {
        Vector3f t = dest;
        Interpolationf.interpolationFactorsTriangle(v0X, v0Y, v1X, v1Y, v2X, v2Y, x, y, t);
        return dest.set(t.x * f0X + t.y * f1X + t.z * f2X, t.x * f0Y + t.y * f1Y + t.z * f2Y, t.x * f0Z + t.y * f1Z + t.z * f2Z);
    }

    public static Vector3f interpolationFactorsTriangle(float v0X, float v0Y, float v1X, float v1Y, float v2X, float v2Y, float x, float y, Vector3f dest) {
        float v12Y = v1Y - v2Y;
        float v21X = v2X - v1X;
        float v02X = v0X - v2X;
        float yv2Y = y - v2Y;
        float xv2X = x - v2X;
        float v02Y = v0Y - v2Y;
        float invDen = 1.0f / (v12Y * v02X + v21X * v02Y);
        dest.x = (v12Y * xv2X + v21X * yv2Y) * invDen;
        dest.y = (v02X * yv2Y - v02Y * xv2X) * invDen;
        dest.z = 1.0f - dest.x - dest.y;
        return dest;
    }
}

