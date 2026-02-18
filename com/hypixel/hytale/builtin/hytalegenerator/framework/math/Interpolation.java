/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

public class Interpolation {
    public static double linear(double value0, double value1, double weight) {
        if (weight <= 0.0) {
            return value0;
        }
        if (weight >= 1.0) {
            return value1;
        }
        return value0 * (1.0 - weight) + value1 * weight;
    }
}

