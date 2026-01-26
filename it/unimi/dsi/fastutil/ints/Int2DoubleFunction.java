/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Int2DoubleFunction
extends Function<Integer, Double>,
IntToDoubleFunction {
    @Override
    default public double applyAsDouble(int operand) {
        return this.get(operand);
    }

    @Override
    default public double put(int key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(int var1);

    default public double getOrDefault(int key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public double remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Double put(Integer key, Double value) {
        int k = key;
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
        int k = (Integer)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double getOrDefault(Object key, Double defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        int k = (Integer)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Double> compose(java.util.function.Function<? super T, ? extends Integer> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Integer, T> andThen(java.util.function.Function<? super Double, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Int2ByteFunction andThenByte(Double2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2DoubleFunction composeByte(Byte2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2ShortFunction andThenShort(Double2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2DoubleFunction composeShort(Short2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2IntFunction andThenInt(Double2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2DoubleFunction composeInt(Int2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2LongFunction andThenLong(Double2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2DoubleFunction composeLong(Long2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2CharFunction andThenChar(Double2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2DoubleFunction composeChar(Char2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2FloatFunction andThenFloat(Double2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2DoubleFunction composeFloat(Float2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2DoubleFunction andThenDouble(Double2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2DoubleFunction composeDouble(Double2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Int2ObjectFunction<T> andThenObject(Double2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2DoubleFunction<T> composeObject(Object2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }

    default public <T> Int2ReferenceFunction<T> andThenReference(Double2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2DoubleFunction<T> composeReference(Reference2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }
}

