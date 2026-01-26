/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Double2DoubleFunction
extends Function<Double, Double>,
DoubleUnaryOperator {
    @Override
    default public double applyAsDouble(double operand) {
        return this.get(operand);
    }

    @Override
    default public double put(double key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(double var1);

    default public double getOrDefault(double key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public double remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Double put(Double key, Double value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        double v = this.put(k, (double)value);
        return containsKey ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double getOrDefault(Object key, Double defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        double k = (Double)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }

    public static Double2DoubleFunction identity() {
        return k -> k;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Double> compose(java.util.function.Function<? super T, ? extends Double> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Double, T> andThen(java.util.function.Function<? super Double, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Double2ByteFunction andThenByte(Double2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2DoubleFunction composeByte(Byte2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2ShortFunction andThenShort(Double2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2DoubleFunction composeShort(Short2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2IntFunction andThenInt(Double2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2DoubleFunction composeInt(Int2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2LongFunction andThenLong(Double2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2DoubleFunction composeLong(Long2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2CharFunction andThenChar(Double2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2DoubleFunction composeChar(Char2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2FloatFunction andThenFloat(Double2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2DoubleFunction composeFloat(Float2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2DoubleFunction andThenDouble(Double2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2DoubleFunction composeDouble(Double2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Double2ObjectFunction<T> andThenObject(Double2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2DoubleFunction<T> composeObject(Object2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }

    default public <T> Double2ReferenceFunction<T> andThenReference(Double2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2DoubleFunction<T> composeReference(Reference2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }
}

