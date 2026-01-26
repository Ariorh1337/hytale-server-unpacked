/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.util;

import javax.annotation.Nonnull;

public class TrigMathUtil {
    public static final float PI = (float)Math.PI;
    public static final float PI_HALF = 1.5707964f;
    public static final float PI_QUARTER = 0.7853982f;
    public static final float PI2 = (float)Math.PI * 2;
    public static final float PI4 = (float)Math.PI * 4;
    public static final float radToDeg = 57.295776f;
    public static final float degToRad = (float)Math.PI / 180;

    public static float sin(float radians) {
        return Riven.sin(radians);
    }

    public static float cos(float radians) {
        return Riven.cos(radians);
    }

    public static float sin(double radians) {
        return Riven.sin((float)radians);
    }

    public static float cos(double radians) {
        return Riven.cos((float)radians);
    }

    public static float atan2(float y, float x) {
        return Icecore.atan2(y, x);
    }

    public static float atan2(double y, double x) {
        return Icecore.atan2((float)y, (float)x);
    }

    public static float atan(double d) {
        return (float)Math.atan(d);
    }

    public static float asin(double d) {
        return (float)Math.asin(d);
    }

    private TrigMathUtil() {
    }

    private static final class Riven {
        private static final int SIN_BITS;
        private static final int SIN_MASK;
        private static final int SIN_COUNT;
        private static final float radFull;
        private static final float radToIndex;
        private static final float degFull;
        private static final float degToIndex;
        @Nonnull
        private static final float[] SIN;
        @Nonnull
        private static final float[] COS;

        private Riven() {
        }

        public static float sin(float rad) {
            return SIN[(int)(rad * radToIndex) & SIN_MASK];
        }

        public static float cos(float rad) {
            return COS[(int)(rad * radToIndex) & SIN_MASK];
        }

        static {
            int i;
            SIN_BITS = 12;
            SIN_MASK = ~(-1 << SIN_BITS);
            SIN_COUNT = SIN_MASK + 1;
            radFull = (float)Math.PI * 2;
            degFull = 360.0f;
            radToIndex = (float)SIN_COUNT / radFull;
            degToIndex = (float)SIN_COUNT / degFull;
            SIN = new float[SIN_COUNT];
            COS = new float[SIN_COUNT];
            for (i = 0; i < SIN_COUNT; ++i) {
                Riven.SIN[i] = (float)Math.sin(((float)i + 0.5f) / (float)SIN_COUNT * radFull);
                Riven.COS[i] = (float)Math.cos(((float)i + 0.5f) / (float)SIN_COUNT * radFull);
            }
            for (i = 0; i < 360; i += 90) {
                Riven.SIN[(int)((float)i * Riven.degToIndex) & Riven.SIN_MASK] = (float)Math.sin((double)i * Math.PI / 180.0);
                Riven.COS[(int)((float)i * Riven.degToIndex) & Riven.SIN_MASK] = (float)Math.cos((double)i * Math.PI / 180.0);
            }
        }
    }

    private static final class Icecore {
        private static final int SIZE_AC = 100000;
        private static final int SIZE_AR = 100001;
        private static final float[] ATAN2 = new float[100001];

        private Icecore() {
        }

        public static float atan2(float y, float x) {
            if (y < 0.0f) {
                if (x < 0.0f) {
                    if (y < x) {
                        return -ATAN2[(int)(x / y * 100000.0f)] - 1.5707964f;
                    }
                    return ATAN2[(int)(y / x * 100000.0f)] - (float)Math.PI;
                }
                if ((y = -y) > x) {
                    return ATAN2[(int)(x / y * 100000.0f)] - 1.5707964f;
                }
                return -ATAN2[(int)(y / x * 100000.0f)];
            }
            if (x < 0.0f) {
                if (y > (x = -x)) {
                    return ATAN2[(int)(x / y * 100000.0f)] + 1.5707964f;
                }
                return -ATAN2[(int)(y / x * 100000.0f)] + (float)Math.PI;
            }
            if (y > x) {
                return -ATAN2[(int)(x / y * 100000.0f)] + 1.5707964f;
            }
            return ATAN2[(int)(y / x * 100000.0f)];
        }

        static {
            for (int i = 0; i <= 100000; ++i) {
                float v;
                double d = (double)i / 100000.0;
                double x = 1.0;
                double y = x * d;
                Icecore.ATAN2[i] = v = (float)Math.atan2(y, x);
            }
        }
    }
}

