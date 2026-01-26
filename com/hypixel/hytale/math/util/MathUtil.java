/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.util;

import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;

public class MathUtil {
    public static final double EPSILON_DOUBLE = Math.ulp(1.0);
    public static final float EPSILON_FLOAT = Math.ulp(1.0f);
    public static float PITCH_EDGE_PADDING = 0.01f;

    public static int abs(int i) {
        int mask = i >> 31;
        return i + mask ^ mask;
    }

    public static int floor(double d) {
        int i = (int)d;
        if ((double)i <= d) {
            return i;
        }
        if (d < -2.147483648E9) {
            return Integer.MIN_VALUE;
        }
        return i - 1;
    }

    public static int ceil(double d) {
        int i = (int)d;
        if (d > 0.0 && d != (double)i) {
            if (d > 2.147483647E9) {
                return Integer.MAX_VALUE;
            }
            return i + 1;
        }
        return i;
    }

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double randomDouble(double min, double max) {
        return min + Math.random() * (max - min);
    }

    public static float randomFloat(float min, float max) {
        return min + (float)Math.random() * (max - min);
    }

    public static double round(double d, int p) {
        double pow = Math.pow(10.0, p);
        return (double)Math.round(d * pow) / pow;
    }

    public static boolean within(double val, double min, double max) {
        return val >= min && val <= max;
    }

    public static double minValue(double v, double a, double c) {
        if (a < v) {
            v = a;
        }
        if (c < v) {
            v = c;
        }
        return v;
    }

    public static int minValue(int v, int a, int c) {
        if (a < v) {
            v = a;
        }
        if (c < v) {
            v = c;
        }
        return v;
    }

    public static double maxValue(double v, double a, double b, double c) {
        if (a > v) {
            v = a;
        }
        if (b > v) {
            v = b;
        }
        if (c > v) {
            v = c;
        }
        return v;
    }

    public static double maxValue(double v, double a, double b) {
        if (a > v) {
            v = a;
        }
        if (b > v) {
            v = b;
        }
        return v;
    }

    public static byte maxValue(byte v, byte a, byte b) {
        if (a > v) {
            v = a;
        }
        if (b > v) {
            v = b;
        }
        return v;
    }

    public static byte maxValue(byte v, byte a, byte b, byte c) {
        if (a > v) {
            v = a;
        }
        if (b > v) {
            v = b;
        }
        if (c > v) {
            v = c;
        }
        return v;
    }

    public static int maxValue(int v, int a, int b) {
        if (a > v) {
            v = a;
        }
        if (b > v) {
            v = b;
        }
        return v;
    }

    public static double lengthSquared(double x, double y) {
        return x * x + y * y;
    }

    public static double length(double x, double y) {
        return Math.sqrt(MathUtil.lengthSquared(x, y));
    }

    public static double lengthSquared(double x, double y, double z) {
        return x * x + y * y + z * z;
    }

    public static double length(double x, double y, double z) {
        return Math.sqrt(MathUtil.lengthSquared(x, y, z));
    }

    public static double maxValue(double v, double a) {
        return a > v ? a : v;
    }

    public static double clipToZero(double v) {
        return MathUtil.clipToZero(v, EPSILON_DOUBLE);
    }

    public static double clipToZero(double v, double epsilon) {
        return v >= -epsilon && v <= epsilon ? 0.0 : v;
    }

    public static float clipToZero(float v) {
        return MathUtil.clipToZero(v, EPSILON_FLOAT);
    }

    public static float clipToZero(float v, float epsilon) {
        return v >= -epsilon && v <= epsilon ? 0.0f : v;
    }

    public static boolean closeToZero(double v) {
        return MathUtil.closeToZero(v, EPSILON_DOUBLE);
    }

    public static boolean closeToZero(double v, double epsilon) {
        return v >= -epsilon && v <= epsilon;
    }

    public static boolean closeToZero(float v) {
        return MathUtil.closeToZero(v, EPSILON_FLOAT);
    }

    public static boolean closeToZero(float v, float epsilon) {
        return v >= -epsilon && v <= epsilon;
    }

    public static double clamp(double v, double min, double max) {
        if (v > max) {
            return v < min ? min : max;
        }
        return v < min ? min : v;
    }

    public static float clamp(float v, float min, float max) {
        if (v > max) {
            return v < min ? min : max;
        }
        return v < min ? min : v;
    }

    public static int clamp(int v, int min, int max) {
        if (v > max) {
            return v < min ? min : max;
        }
        return v < min ? min : v;
    }

    public static long clamp(long v, long min, long max) {
        if (v > max) {
            return v < min ? min : max;
        }
        return v < min ? min : v;
    }

    public static int getPercentageOf(int index, int max) {
        return (int)((double)index / ((double)max - 1.0) * 100.0);
    }

    public static double percent(int v, int total) {
        if (total == 0) {
            return 0.0;
        }
        return (double)v * 100.0 / (double)total;
    }

    public static int fastRound(float f) {
        return MathUtil.fastFloor(f + 0.5f);
    }

    public static long fastRound(double d) {
        return MathUtil.fastFloor(d + 0.5);
    }

    public static int fastFloor(float f) {
        int i = (int)f;
        if ((float)i <= f) {
            return i;
        }
        if (f < -2.1474836E9f) {
            return Integer.MIN_VALUE;
        }
        return i - 1;
    }

    public static long fastFloor(double d) {
        long i = (long)d;
        if ((double)i <= d) {
            return i;
        }
        if (d < -9.223372036854776E18) {
            return Long.MIN_VALUE;
        }
        return i - 1L;
    }

    public static int fastCeil(float f) {
        int i = (int)f;
        if (f > 0.0f && f != (float)i) {
            if (f > 2.1474836E9f) {
                return Integer.MAX_VALUE;
            }
            return i + 1;
        }
        return i;
    }

    public static long fastCeil(double d) {
        long i = (long)d;
        if (d > 0.0 && d != (double)i) {
            if (d > 9.223372036854776E18) {
                return Long.MAX_VALUE;
            }
            return i + 1L;
        }
        return i;
    }

    private MathUtil() {
    }

    public static float halfFloatToFloat(int hbits) {
        int mant = hbits & 0x3FF;
        int exp = hbits & 0x7C00;
        if (exp == 31744) {
            exp = 261120;
        } else if (exp != 0) {
            if (mant == 0 && (exp += 114688) > 115712) {
                return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13 | 0x3FF);
            }
        } else if (mant != 0) {
            exp = 115712;
            do {
                exp -= 1024;
            } while (((mant <<= 1) & 0x400) == 0);
            mant &= 0x3FF;
        }
        return Float.intBitsToFloat((hbits & 0x8000) << 16 | (exp | mant) << 13);
    }

    public static int halfFloatFromFloat(float fval) {
        int fbits = Float.floatToIntBits(fval);
        int sign = fbits >>> 16 & 0x8000;
        int val = (fbits & Integer.MAX_VALUE) + 4096;
        if (val >= 1199570944) {
            if ((fbits & Integer.MAX_VALUE) >= 1199570944) {
                if (val < 2139095040) {
                    return sign | 0x7C00;
                }
                return sign | 0x7C00 | (fbits & 0x7FFFFF) >>> 13;
            }
            return sign | 0x7BFF;
        }
        if (val >= 0x38800000) {
            return sign | val - 0x38000000 >>> 13;
        }
        if (val < 0x33000000) {
            return sign;
        }
        val = (fbits & Integer.MAX_VALUE) >>> 23;
        return sign | (fbits & 0x7FFFFF | 0x800000) + (0x800000 >>> val - 102) >>> 126 - val;
    }

    public static int byteCount(int i) {
        if (i > 65535) {
            return 4;
        }
        if (i > 255) {
            return 2;
        }
        if (i > 0) {
            return 1;
        }
        return 0;
    }

    public static int packInt(int x, int z) {
        return x << 16 | z & 0xFFFF;
    }

    public static int unpackLeft(int packed) {
        int i = packed >> 16 & 0xFFFF;
        if ((i & 0x8000) != 0) {
            i |= 0xFFFF0000;
        }
        return i;
    }

    public static int unpackRight(int packed) {
        int i = packed & 0xFFFF;
        if ((i & 0x8000) != 0) {
            i |= 0xFFFF0000;
        }
        return i;
    }

    public static long packLong(int left, int right) {
        return (long)left << 32 | (long)right & 0xFFFFFFFFL;
    }

    public static int unpackLeft(long packed) {
        return (int)(packed >> 32);
    }

    public static int unpackRight(long packed) {
        return (int)packed;
    }

    @Nonnull
    public static Vector3i rotateVectorYAxis(@Nonnull Vector3i vector, int angle, boolean clockwise) {
        int z1;
        int x1;
        float radAngle = (float)Math.PI / 180 * (float)angle;
        if (clockwise) {
            x1 = (int)((float)vector.x * TrigMathUtil.cos(radAngle) - (float)vector.z * TrigMathUtil.sin(radAngle));
            z1 = (int)((float)vector.x * TrigMathUtil.sin(radAngle) + (float)vector.z * TrigMathUtil.cos(radAngle));
        } else {
            x1 = (int)((float)vector.x * TrigMathUtil.cos(radAngle) + (float)vector.z * TrigMathUtil.sin(radAngle));
            z1 = (int)((float)(-vector.x) * TrigMathUtil.sin(radAngle) + (float)vector.z * TrigMathUtil.cos(radAngle));
        }
        return new Vector3i(x1, vector.y, z1);
    }

    @Nonnull
    public static Vector3d rotateVectorYAxis(@Nonnull Vector3d vector, int angle, boolean clockwise) {
        double z1;
        double x1;
        float radAngle = (float)Math.PI / 180 * (float)angle;
        if (clockwise) {
            x1 = vector.x * (double)TrigMathUtil.cos(radAngle) - vector.z * (double)TrigMathUtil.sin(radAngle);
            z1 = vector.x * (double)TrigMathUtil.sin(radAngle) + vector.z * (double)TrigMathUtil.cos(radAngle);
        } else {
            x1 = vector.x * (double)TrigMathUtil.cos(radAngle) + vector.z * (double)TrigMathUtil.sin(radAngle);
            z1 = -vector.x * (double)TrigMathUtil.sin(radAngle) + vector.z * (double)TrigMathUtil.cos(radAngle);
        }
        return new Vector3d(x1, vector.y, z1);
    }

    public static float wrapAngle(float angle) {
        if ((angle %= (float)Math.PI * 2) <= (float)(-Math.PI)) {
            angle += (float)Math.PI * 2;
        } else if (angle > (float)Math.PI) {
            angle -= (float)Math.PI * 2;
        }
        return angle;
    }

    public static float lerp(float a, float b, float t) {
        return MathUtil.lerpUnclamped(a, b, MathUtil.clamp(t, 0.0f, 1.0f));
    }

    public static float lerpUnclamped(float a, float b, float t) {
        return a + t * (b - a);
    }

    public static double lerp(double a, double b, double t) {
        return MathUtil.lerpUnclamped(a, b, MathUtil.clamp(t, 0.0, 1.0));
    }

    public static double lerpUnclamped(double a, double b, double t) {
        return a + t * (b - a);
    }

    public static float shortAngleDistance(float a, float b) {
        float distance = (b - a) % ((float)Math.PI * 2);
        return 2.0f * distance % ((float)Math.PI * 2) - distance;
    }

    public static float lerpAngle(float a, float b, float t) {
        return a + MathUtil.shortAngleDistance(a, b) * t;
    }

    public static double floorMod(double x, double y) {
        return x - Math.floor(x / y) * y;
    }

    public static double compareAngle(double a, double b) {
        double diff = b - a;
        return MathUtil.floorMod(diff + Math.PI, Math.PI * 2) - Math.PI;
    }

    public static double percentile(@Nonnull long[] sortedData, double percentile) {
        long right;
        long left;
        if (sortedData.length == 1) {
            return sortedData[0];
        }
        if (percentile >= 1.0) {
            return sortedData[sortedData.length - 1];
        }
        double position = (double)(sortedData.length + 1) * percentile;
        double n = percentile * (double)(sortedData.length - 1) + 1.0;
        if (position >= 1.0) {
            left = sortedData[MathUtil.floor(n) - 1];
            right = sortedData[MathUtil.floor(n)];
        } else {
            left = sortedData[0];
            right = sortedData[1];
        }
        if (left == right) {
            return left;
        }
        double part = n - (double)MathUtil.floor(n);
        return (double)left + part * (double)(right - left);
    }

    public static double distanceToLineSq(double x, double y, double ax, double ay, double bx, double by) {
        double dx0 = x - ax;
        double dy0 = y - ay;
        double dx1 = bx - ax;
        double dy1 = by - ay;
        return MathUtil.distanceToLineSq(x, y, ax, ay, bx, by, dx0, dy0, dx1, dy1);
    }

    public static double distanceToLineSq(double x, double y, double ax, double ay, double bx, double by, double dxAx, double dyAy, double dBxAx, double dByAy) {
        double t = dxAx * dBxAx + dyAy * dByAy;
        t /= dBxAx * dBxAx + dByAy * dByAy;
        double px = ax;
        double py = ay;
        if (t > 1.0) {
            px = bx;
            py = by;
        } else if (t > 0.0) {
            px = ax + t * dBxAx;
            py = ay + t * dByAy;
        }
        dBxAx = x - px;
        dByAy = y - py;
        return dBxAx * dBxAx + dByAy * dByAy;
    }

    public static double distanceToInfLineSq(double x, double y, double ax, double ay, double bx, double by) {
        double dx0 = x - ax;
        double dy0 = y - ay;
        double dx1 = bx - ax;
        double dy1 = by - ay;
        return MathUtil.distanceToInfLineSq(x, y, ax, ay, dx0, dy0, dx1, dy1);
    }

    public static double distanceToInfLineSq(double x, double y, double ax, double ay, double dxAx, double dyAy, double dBxAx, double dByAy) {
        double t = dxAx * dBxAx + dyAy * dByAy;
        double px = ax + (t /= dBxAx * dBxAx + dByAy * dByAy) * dBxAx;
        double py = ay + t * dByAy;
        dBxAx = x - px;
        dByAy = y - py;
        return dBxAx * dBxAx + dByAy * dByAy;
    }

    public static int sideOfLine(double x, double y, double ax, double ay, double bx, double by) {
        return (ax - x) * (by - y) - (ay - y) * (bx - x) >= 0.0 ? 1 : -1;
    }

    public static Vector3f getRotationForHitNormal(Vector3f normal) {
        if (normal == null) {
            return Vector3f.ZERO;
        }
        if (normal.y == 1.0f) {
            return Vector3f.ZERO;
        }
        if (normal.y == -1.0f) {
            return new Vector3f(0.0f, 0.0f, (float)Math.PI);
        }
        if (normal.x == 1.0f) {
            return new Vector3f(0.0f, 0.0f, -1.5707964f);
        }
        if (normal.x == -1.0f) {
            return new Vector3f(0.0f, 0.0f, 1.5707964f);
        }
        if (normal.z == 1.0f) {
            return new Vector3f(1.5707964f, 0.0f, 0.0f);
        }
        if (normal.z == -1.0f) {
            return new Vector3f(-1.5707964f, 0.0f, 0.0f);
        }
        return Vector3f.ZERO;
    }

    public static String getNameForHitNormal(Vector3f normal) {
        if (normal == null) {
            return "UP";
        }
        if (normal.y == 1.0f) {
            return "UP";
        }
        if (normal.y == -1.0f) {
            return "DOWN";
        }
        if (normal.x == 1.0f) {
            return "WEST";
        }
        if (normal.x == -1.0f) {
            return "EAST";
        }
        if (normal.z == 1.0f) {
            return "NORTH";
        }
        if (normal.z == -1.0f) {
            return "SOUTH";
        }
        return "UP";
    }

    public static float mapToRange(float value, float valueMin, float valueMax, float rangeMin, float rangeMax) {
        float alpha = (value - valueMin) / (valueMax - valueMin);
        return rangeMin + alpha * (rangeMax - rangeMin);
    }
}

