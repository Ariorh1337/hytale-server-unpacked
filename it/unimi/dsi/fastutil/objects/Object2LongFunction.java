/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface Object2LongFunction<K>
extends Function<K, Long>,
ToLongFunction<K> {
    @Override
    default public long applyAsLong(K operand) {
        return this.getLong(operand);
    }

    @Override
    default public long put(K key, long value) {
        throw new UnsupportedOperationException();
    }

    public long getLong(Object var1);

    @Override
    default public long getOrDefault(Object key, long defaultValue) {
        long v = this.getLong(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public long removeLong(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Long put(K key, Long value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        long v = this.put(k, (long)value);
        return containsKey ? Long.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Long get(Object key) {
        Object k = key;
        long v = this.getLong(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Long getOrDefault(Object key, Long defaultValue) {
        Object k = key;
        long v = this.getLong(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Long remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Long.valueOf(this.removeLong(k)) : null;
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Long, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Object2ByteFunction<K> andThenByte(Long2ByteFunction after) {
        return k -> after.get(this.getLong(k));
    }

    default public Byte2LongFunction composeByte(Byte2ObjectFunction<K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public Object2ShortFunction<K> andThenShort(Long2ShortFunction after) {
        return k -> after.get(this.getLong(k));
    }

    default public Short2LongFunction composeShort(Short2ObjectFunction<K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public Object2IntFunction<K> andThenInt(Long2IntFunction after) {
        return k -> after.get(this.getLong(k));
    }

    default public Int2LongFunction composeInt(Int2ObjectFunction<K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public Object2LongFunction<K> andThenLong(Long2LongFunction after) {
        return k -> after.get(this.getLong(k));
    }

    default public Long2LongFunction composeLong(Long2ObjectFunction<K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public Object2CharFunction<K> andThenChar(Long2CharFunction after) {
        return k -> after.get(this.getLong(k));
    }

    default public Char2LongFunction composeChar(Char2ObjectFunction<K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public Object2FloatFunction<K> andThenFloat(Long2FloatFunction after) {
        return k -> after.get(this.getLong(k));
    }

    default public Float2LongFunction composeFloat(Float2ObjectFunction<K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public Object2DoubleFunction<K> andThenDouble(Long2DoubleFunction after) {
        return k -> after.get(this.getLong(k));
    }

    default public Double2LongFunction composeDouble(Double2ObjectFunction<K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public <T> Object2ObjectFunction<K, T> andThenObject(Long2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getLong(k));
    }

    default public <T> Object2LongFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getLong(before.get(k));
    }

    default public <T> Object2ReferenceFunction<K, T> andThenReference(Long2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getLong(k));
    }

    default public <T> Reference2LongFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getLong(before.get(k));
    }
}

