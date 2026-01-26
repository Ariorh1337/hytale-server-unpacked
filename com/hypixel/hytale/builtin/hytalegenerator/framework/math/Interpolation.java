/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

public class Interpolation {
    public static double linear(double valueA, double valueB, double weight) {
        if (weight < 0.0 || weight > 1.0) {
            throw new IllegalArgumentException("weight outside range");
        }
        return valueA * (1.0 - weight) + valueB * weight;
    }
}

