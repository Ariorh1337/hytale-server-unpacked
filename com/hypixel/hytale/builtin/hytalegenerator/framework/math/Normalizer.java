/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

public class Normalizer {
    public static double normalizeNoise(double input) {
        return Normalizer.normalize(-1.0, 1.0, 0.0, 1.0, input);
    }

    public static double normalize(double fromMin, double fromMax, double toMin, double toMax, double input) {
        if (fromMin > fromMax || toMin > toMax) {
            throw new IllegalArgumentException("min larger than max");
        }
        input -= fromMin;
        input /= fromMax - fromMin;
        input *= toMax - toMin;
        return input += toMin;
    }
}

