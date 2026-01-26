/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

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
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2ByteFunction
extends Function<Integer, Byte>,
IntUnaryOperator {
    @Override
    default public int applyAsInt(int operand) {
        return this.get(operand);
    }

    @Override
    default public byte put(int key, byte value) {
        throw new UnsupportedOperationException();
    }

    public byte get(int var1);

    default public byte getOrDefault(int key, byte defaultValue) {
        byte v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public byte remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Byte put(Integer key, Byte value) {
        int k = key;
        boolean containsKey = this.containsKey(k);
        byte v = this.put(k, (byte)value);
        return containsKey ? Byte.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Byte get(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        byte v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Byte getOrDefault(Object key, Byte defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        int k = (Integer)key;
        byte v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Byte remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Byte.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException();
    }

    default public byte defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Byte> compose(java.util.function.Function<? super T, ? extends Integer> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Integer, T> andThen(java.util.function.Function<? super Byte, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Int2ByteFunction andThenByte(Byte2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2ByteFunction composeByte(Byte2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2ShortFunction andThenShort(Byte2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2ByteFunction composeShort(Short2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2IntFunction andThenInt(Byte2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2ByteFunction composeInt(Int2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2LongFunction andThenLong(Byte2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2ByteFunction composeLong(Long2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2CharFunction andThenChar(Byte2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2ByteFunction composeChar(Char2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2FloatFunction andThenFloat(Byte2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2ByteFunction composeFloat(Float2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2DoubleFunction andThenDouble(Byte2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2ByteFunction composeDouble(Double2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Int2ObjectFunction<T> andThenObject(Byte2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ByteFunction<T> composeObject(Object2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }

    default public <T> Int2ReferenceFunction<T> andThenReference(Byte2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ByteFunction<T> composeReference(Reference2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }
}

