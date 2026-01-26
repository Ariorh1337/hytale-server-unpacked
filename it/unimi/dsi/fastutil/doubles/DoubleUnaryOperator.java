/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface DoubleUnaryOperator
extends UnaryOperator<Double>,
java.util.function.DoubleUnaryOperator {
    @Override
    public double apply(double var1);

    public static DoubleUnaryOperator identity() {
        return i -> i;
    }

    public static DoubleUnaryOperator negation() {
        return i -> -i;
    }

    @Override
    @Deprecated
    default public double applyAsDouble(double x) {
        return this.apply(x);
    }

    @Override
    @Deprecated
    default public Double apply(Double x) {
        return this.apply((double)x);
    }
}

