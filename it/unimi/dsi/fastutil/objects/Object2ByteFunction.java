/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2ByteFunction<K>
extends Function<K, Byte>,
ToIntFunction<K> {
    @Override
    default public int applyAsInt(K operand) {
        return this.getByte(operand);
    }

    @Override
    default public byte put(K key, byte value) {
        throw new UnsupportedOperationException();
    }

    public byte getByte(Object var1);

    @Override
    default public byte getOrDefault(Object key, byte defaultValue) {
        byte v = this.getByte(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public byte removeByte(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Byte put(K key, Byte value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        byte v = this.put(k, (byte)value);
        return containsKey ? Byte.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Byte get(Object key) {
        Object k = key;
        byte v = this.getByte(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Byte getOrDefault(Object key, Byte defaultValue) {
        Object k = key;
        byte v = this.getByte(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Byte remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Byte.valueOf(this.removeByte(k)) : null;
    }

    default public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException();
    }

    default public byte defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Byte, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Object2ByteFunction<K> andThenByte(Byte2ByteFunction after) {
        return k -> after.get(this.getByte(k));
    }

    default public Byte2ByteFunction composeByte(Byte2ObjectFunction<K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public Object2ShortFunction<K> andThenShort(Byte2ShortFunction after) {
        return k -> after.get(this.getByte(k));
    }

    default public Short2ByteFunction composeShort(Short2ObjectFunction<K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public Object2IntFunction<K> andThenInt(Byte2IntFunction after) {
        return k -> after.get(this.getByte(k));
    }

    default public Int2ByteFunction composeInt(Int2ObjectFunction<K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public Object2LongFunction<K> andThenLong(Byte2LongFunction after) {
        return k -> after.get(this.getByte(k));
    }

    default public Long2ByteFunction composeLong(Long2ObjectFunction<K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public Object2CharFunction<K> andThenChar(Byte2CharFunction after) {
        return k -> after.get(this.getByte(k));
    }

    default public Char2ByteFunction composeChar(Char2ObjectFunction<K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public Object2FloatFunction<K> andThenFloat(Byte2FloatFunction after) {
        return k -> after.get(this.getByte(k));
    }

    default public Float2ByteFunction composeFloat(Float2ObjectFunction<K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public Object2DoubleFunction<K> andThenDouble(Byte2DoubleFunction after) {
        return k -> after.get(this.getByte(k));
    }

    default public Double2ByteFunction composeDouble(Double2ObjectFunction<K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public <T> Object2ObjectFunction<K, T> andThenObject(Byte2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getByte(k));
    }

    default public <T> Object2ByteFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getByte(before.get(k));
    }

    default public <T> Object2ReferenceFunction<K, T> andThenReference(Byte2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getByte(k));
    }

    default public <T> Reference2ByteFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getByte(before.get(k));
    }
}

