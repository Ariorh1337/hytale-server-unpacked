/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;

public class InterpolatedCurve
implements Double2DoubleFunction {
    @Nonnull
    private final Double2DoubleFunction functionA;
    @Nonnull
    private final Double2DoubleFunction functionB;
    private final double positionA;
    private final double positionB;
    private final double distance;
    private final double smoothTransition;

    public InterpolatedCurve(double positionA, double positionB, double smoothTransition, @Nonnull Double2DoubleFunction functionA, @Nonnull Double2DoubleFunction functionB) {
        if (smoothTransition < 0.0 || smoothTransition > 1.0) {
            throw new IllegalArgumentException();
        }
        this.smoothTransition = smoothTransition;
        this.positionA = Math.min(positionA, positionB);
        this.positionB = Math.max(positionA, positionB);
        this.distance = positionB - positionA;
        this.functionA = functionA;
        this.functionB = functionB;
    }

    @Override
    public double get(double x) {
        if (x < this.positionA) {
            return this.functionA.get(x);
        }
        if (x > this.positionB) {
            return this.functionB.get(x);
        }
        if (this.distance == 0.0) {
            return (this.functionA.get(x) + this.functionB.get(x)) * 0.5;
        }
        double bRatio = this.transitionCurve((x - this.positionA) / this.distance);
        double aRatio = 1.0 - bRatio;
        return aRatio * this.functionA.get(x) + bRatio * this.functionB.get(x);
    }

    public double transitionCurve(double ratio) {
        double a = ratio * Math.PI;
        double v = Math.cos(a);
        v += 1.0;
        v /= 2.0;
        v = 1.0 - v;
        v = v * this.smoothTransition + ratio * (1.0 - this.smoothTransition);
        return v;
    }
}

