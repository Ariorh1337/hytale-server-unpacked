/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Normalizer;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class Stepinizer
implements Function<Double, Double>,
Double2DoubleFunction {
    private double stepSize;
    private double stepSizeHalf;
    private double slope;
    private double topSmooth;
    private double bottomSmooth;

    public Stepinizer() {
        this.setStep(1.0);
        this.setEdgeSlope(1.0);
        this.setSmooth(1.0, 1.0);
    }

    @Nonnull
    public Stepinizer setSmooth(double top, double bottom) {
        if (top <= 0.0 || bottom <= 0.0) {
            throw new IllegalArgumentException("invalid values provided");
        }
        this.topSmooth = top;
        this.bottomSmooth = bottom;
        return this;
    }

    @Nonnull
    public Stepinizer setEdgeSlope(double slope) {
        if (slope < 0.0) {
            throw new IllegalArgumentException("negative slope");
        }
        this.slope = slope;
        return this;
    }

    @Nonnull
    public Stepinizer setStep(double size) {
        if (size < 0.0) {
            throw new IllegalArgumentException("negative size");
        }
        this.stepSize = size;
        this.stepSizeHalf = size / 2.0;
        return this;
    }

    @Override
    public double apply(double x) {
        return this.get(x);
    }

    @Override
    public double get(double x) {
        double polarity = this.polarity(x);
        double steepness = this.steepness(polarity);
        double bottomStep = this.bottomStep(x);
        double topStep = this.topStep(x);
        double result = polarity < 0.0 ? Calculator.smoothMax(this.bottomSmooth, steepness, -1.0) : Calculator.smoothMin(this.topSmooth, steepness, 1.0);
        result = Normalizer.normalize(-1.0, 1.0, bottomStep, topStep, result);
        return result;
    }

    private double closestStep(double x) {
        double remainder = x % this.stepSize;
        if (remainder < this.stepSizeHalf) {
            return x - remainder;
        }
        return x - remainder + this.stepSize;
    }

    private double topStep(double x) {
        return x - x % this.stepSize + this.stepSize;
    }

    private double bottomStep(double x) {
        return x - x % this.stepSize;
    }

    private double polarity(double x) {
        double midPoint = this.bottomStep(x) + this.stepSizeHalf;
        return (x - midPoint) / this.stepSizeHalf;
    }

    private double steepness(double x) {
        return this.slope * x;
    }
}

