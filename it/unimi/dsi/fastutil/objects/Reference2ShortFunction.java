/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2ShortFunction;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Reference2ShortFunction<K>
extends Function<K, Short>,
ToIntFunction<K> {
    @Override
    default public int applyAsInt(K operand) {
        return this.getShort(operand);
    }

    @Override
    default public short put(K key, short value) {
        throw new UnsupportedOperationException();
    }

    public short getShort(Object var1);

    @Override
    default public short getOrDefault(Object key, short defaultValue) {
        short v = this.getShort(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public short removeShort(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Short put(K key, Short value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        short v = this.put(k, (short)value);
        return containsKey ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short get(Object key) {
        Object k = key;
        short v = this.getShort(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short getOrDefault(Object key, Short defaultValue) {
        Object k = key;
        short v = this.getShort(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Short remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Short.valueOf(this.removeShort(k)) : null;
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Reference2ByteFunction<K> andThenByte(Short2ByteFunction after) {
        return k -> after.get(this.getShort(k));
    }

    default public Byte2ShortFunction composeByte(Byte2ReferenceFunction<K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public Reference2ShortFunction<K> andThenShort(Short2ShortFunction after) {
        return k -> after.get(this.getShort(k));
    }

    default public Short2ShortFunction composeShort(Short2ReferenceFunction<K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public Reference2IntFunction<K> andThenInt(Short2IntFunction after) {
        return k -> after.get(this.getShort(k));
    }

    default public Int2ShortFunction composeInt(Int2ReferenceFunction<K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public Reference2LongFunction<K> andThenLong(Short2LongFunction after) {
        return k -> after.get(this.getShort(k));
    }

    default public Long2ShortFunction composeLong(Long2ReferenceFunction<K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public Reference2CharFunction<K> andThenChar(Short2CharFunction after) {
        return k -> after.get(this.getShort(k));
    }

    default public Char2ShortFunction composeChar(Char2ReferenceFunction<K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public Reference2FloatFunction<K> andThenFloat(Short2FloatFunction after) {
        return k -> after.get(this.getShort(k));
    }

    default public Float2ShortFunction composeFloat(Float2ReferenceFunction<K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public Reference2DoubleFunction<K> andThenDouble(Short2DoubleFunction after) {
        return k -> after.get(this.getShort(k));
    }

    default public Double2ShortFunction composeDouble(Double2ReferenceFunction<K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public <T> Reference2ObjectFunction<K, T> andThenObject(Short2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getShort(k));
    }

    default public <T> Object2ShortFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getShort(before.get(k));
    }

    default public <T> Reference2ReferenceFunction<K, T> andThenReference(Short2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getShort(k));
    }

    default public <T> Reference2ShortFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getShort(before.get(k));
    }
}

