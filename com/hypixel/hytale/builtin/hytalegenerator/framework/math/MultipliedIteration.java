/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

public class MultipliedIteration {
    public static double calculateMultiplier(double startValue, double endValue, int numberOfIterations, double precision) {
        double candidate;
        if (startValue < endValue) {
            throw new IllegalArgumentException("start smaller than end");
        }
        if (numberOfIterations <= 0) {
            throw new IllegalArgumentException("number of iterations must be greater than 0");
        }
        if (precision <= 0.0) {
            throw new IllegalArgumentException("precision must be greater than 0");
        }
        int result = 0;
        for (candidate = 0.0; candidate < 1.0 && (result = MultipliedIteration.calculateIterations(candidate, startValue, endValue)) < numberOfIterations; candidate += precision) {
        }
        return Math.min(candidate, 0.99999);
    }

    public static int calculateIterations(double multiplier, double startValue, double endValue) {
        double currentSize = startValue;
        int iterations = 0;
        while (currentSize > endValue) {
            currentSize *= multiplier;
            ++iterations;
        }
        return iterations;
    }
}

