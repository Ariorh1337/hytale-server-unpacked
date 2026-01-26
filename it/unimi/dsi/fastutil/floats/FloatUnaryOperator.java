/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface FloatUnaryOperator
extends UnaryOperator<Float>,
DoubleUnaryOperator {
    @Override
    public float apply(float var1);

    public static FloatUnaryOperator identity() {
        return i -> i;
    }

    public static FloatUnaryOperator negation() {
        return i -> -i;
    }

    @Override
    @Deprecated
    default public double applyAsDouble(double x) {
        return this.apply(SafeMath.safeDoubleToFloat(x));
    }

    @Override
    @Deprecated
    default public Float apply(Float x) {
        return Float.valueOf(this.apply(x.floatValue()));
    }
}

