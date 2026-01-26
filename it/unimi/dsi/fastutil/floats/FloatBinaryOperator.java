/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;

@FunctionalInterface
public interface FloatBinaryOperator
extends BinaryOperator<Float>,
DoubleBinaryOperator {
    @Override
    public float apply(float var1, float var2);

    @Override
    @Deprecated
    default public double applyAsDouble(double x, double y) {
        return this.apply(SafeMath.safeDoubleToFloat(x), SafeMath.safeDoubleToFloat(y));
    }

    @Override
    @Deprecated
    default public Float apply(Float x, Float y) {
        return Float.valueOf(this.apply(x.floatValue(), y.floatValue()));
    }
}

