/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2IntFunction<K>
extends Function<K, Integer>,
ToIntFunction<K> {
    @Override
    default public int applyAsInt(K operand) {
        return this.getInt(operand);
    }

    @Override
    default public int put(K key, int value) {
        throw new UnsupportedOperationException();
    }

    public int getInt(Object var1);

    @Override
    default public int getOrDefault(Object key, int defaultValue) {
        int v = this.getInt(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public int removeInt(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Integer put(K key, Integer value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        int v = this.put(k, (int)value);
        return containsKey ? Integer.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Integer get(Object key) {
        Object k = key;
        int v = this.getInt(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Integer getOrDefault(Object key, Integer defaultValue) {
        Object k = key;
        int v = this.getInt(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Integer remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Integer.valueOf(this.removeInt(k)) : null;
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Integer, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Object2ByteFunction<K> andThenByte(Int2ByteFunction after) {
        return k -> after.get(this.getInt(k));
    }

    default public Byte2IntFunction composeByte(Byte2ObjectFunction<K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public Object2ShortFunction<K> andThenShort(Int2ShortFunction after) {
        return k -> after.get(this.getInt(k));
    }

    default public Short2IntFunction composeShort(Short2ObjectFunction<K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public Object2IntFunction<K> andThenInt(Int2IntFunction after) {
        return k -> after.get(this.getInt(k));
    }

    default public Int2IntFunction composeInt(Int2ObjectFunction<K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public Object2LongFunction<K> andThenLong(Int2LongFunction after) {
        return k -> after.get(this.getInt(k));
    }

    default public Long2IntFunction composeLong(Long2ObjectFunction<K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public Object2CharFunction<K> andThenChar(Int2CharFunction after) {
        return k -> after.get(this.getInt(k));
    }

    default public Char2IntFunction composeChar(Char2ObjectFunction<K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public Object2FloatFunction<K> andThenFloat(Int2FloatFunction after) {
        return k -> after.get(this.getInt(k));
    }

    default public Float2IntFunction composeFloat(Float2ObjectFunction<K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public Object2DoubleFunction<K> andThenDouble(Int2DoubleFunction after) {
        return k -> after.get(this.getInt(k));
    }

    default public Double2IntFunction composeDouble(Double2ObjectFunction<K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public <T> Object2ObjectFunction<K, T> andThenObject(Int2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getInt(k));
    }

    default public <T> Object2IntFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getInt(before.get(k));
    }

    default public <T> Object2ReferenceFunction<K, T> andThenReference(Int2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getInt(k));
    }

    default public <T> Reference2IntFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getInt(before.get(k));
    }
}

