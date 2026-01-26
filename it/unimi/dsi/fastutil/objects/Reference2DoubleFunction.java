/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
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
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Reference2DoubleFunction<K>
extends Function<K, Double>,
ToDoubleFunction<K> {
    @Override
    default public double applyAsDouble(K operand) {
        return this.getDouble(operand);
    }

    @Override
    default public double put(K key, double value) {
        throw new UnsupportedOperationException();
    }

    public double getDouble(Object var1);

    @Override
    default public double getOrDefault(Object key, double defaultValue) {
        double v = this.getDouble(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public double removeDouble(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Double put(K key, Double value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        double v = this.put(k, (double)value);
        return containsKey ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double get(Object key) {
        Object k = key;
        double v = this.getDouble(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double getOrDefault(Object key, Double defaultValue) {
        Object k = key;
        double v = this.getDouble(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Double remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Double.valueOf(this.removeDouble(k)) : null;
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Double, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Reference2ByteFunction<K> andThenByte(Double2ByteFunction after) {
        return k -> after.get(this.getDouble(k));
    }

    default public Byte2DoubleFunction composeByte(Byte2ReferenceFunction<K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public Reference2ShortFunction<K> andThenShort(Double2ShortFunction after) {
        return k -> after.get(this.getDouble(k));
    }

    default public Short2DoubleFunction composeShort(Short2ReferenceFunction<K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public Reference2IntFunction<K> andThenInt(Double2IntFunction after) {
        return k -> after.get(this.getDouble(k));
    }

    default public Int2DoubleFunction composeInt(Int2ReferenceFunction<K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public Reference2LongFunction<K> andThenLong(Double2LongFunction after) {
        return k -> after.get(this.getDouble(k));
    }

    default public Long2DoubleFunction composeLong(Long2ReferenceFunction<K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public Reference2CharFunction<K> andThenChar(Double2CharFunction after) {
        return k -> after.get(this.getDouble(k));
    }

    default public Char2DoubleFunction composeChar(Char2ReferenceFunction<K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public Reference2FloatFunction<K> andThenFloat(Double2FloatFunction after) {
        return k -> after.get(this.getDouble(k));
    }

    default public Float2DoubleFunction composeFloat(Float2ReferenceFunction<K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public Reference2DoubleFunction<K> andThenDouble(Double2DoubleFunction after) {
        return k -> after.get(this.getDouble(k));
    }

    default public Double2DoubleFunction composeDouble(Double2ReferenceFunction<K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public <T> Reference2ObjectFunction<K, T> andThenObject(Double2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getDouble(k));
    }

    default public <T> Object2DoubleFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getDouble(before.get(k));
    }

    default public <T> Reference2ReferenceFunction<K, T> andThenReference(Double2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getDouble(k));
    }

    default public <T> Reference2DoubleFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getDouble(before.get(k));
    }
}

