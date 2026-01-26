/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
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
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Double2IntFunction
extends Function<Double, Integer>,
DoubleToIntFunction {
    @Override
    default public int applyAsInt(double operand) {
        return this.get(operand);
    }

    @Override
    default public int put(double key, int value) {
        throw new UnsupportedOperationException();
    }

    public int get(double var1);

    default public int getOrDefault(double key, int defaultValue) {
        int v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public int remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Integer put(Double key, Integer value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        int v = this.put(k, (int)value);
        return containsKey ? Integer.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Integer get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        int v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Integer getOrDefault(Object key, Integer defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        double k = (Double)key;
        int v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Integer remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Integer.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Integer> compose(java.util.function.Function<? super T, ? extends Double> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Double, T> andThen(java.util.function.Function<? super Integer, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Double2ByteFunction andThenByte(Int2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2IntFunction composeByte(Byte2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2ShortFunction andThenShort(Int2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2IntFunction composeShort(Short2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2IntFunction andThenInt(Int2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2IntFunction composeInt(Int2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2LongFunction andThenLong(Int2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2IntFunction composeLong(Long2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2CharFunction andThenChar(Int2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2IntFunction composeChar(Char2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2FloatFunction andThenFloat(Int2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2IntFunction composeFloat(Float2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Double2DoubleFunction andThenDouble(Int2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2IntFunction composeDouble(Double2DoubleFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Double2ObjectFunction<T> andThenObject(Int2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2IntFunction<T> composeObject(Object2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }

    default public <T> Double2ReferenceFunction<T> andThenReference(Int2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2IntFunction<T> composeReference(Reference2DoubleFunction<? super T> before) {
        return k -> this.get(before.getDouble(k));
    }
}

