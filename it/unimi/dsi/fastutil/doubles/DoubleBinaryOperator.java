/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface DoubleBinaryOperator
extends BinaryOperator<Double>,
java.util.function.DoubleBinaryOperator {
    @Override
    public double apply(double var1, double var3);

    @Override
    @Deprecated
    default public double applyAsDouble(double x, double y) {
        return this.apply(x, y);
    }

    @Override
    @Deprecated
    default public Double apply(Double x, Double y) {
        return this.apply((double)x, (double)y);
    }
}

