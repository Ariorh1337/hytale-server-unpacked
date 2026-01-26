/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
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
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface Byte2LongFunction
extends Function<Byte, Long>,
IntToLongFunction {
    @Override
    @Deprecated
    default public long applyAsLong(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public long put(byte key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(byte var1);

    default public long getOrDefault(byte key, long defaultValue) {
        long v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public long remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Long put(Byte key, Long value) {
        byte k = key;
        boolean containsKey = this.containsKey(k);
        long v = this.put(k, (long)value);
        return containsKey ? Long.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Long get(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Long getOrDefault(Object key, Long defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        byte k = (Byte)key;
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Long> compose(java.util.function.Function<? super T, ? extends Byte> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Byte, T> andThen(java.util.function.Function<? super Long, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Byte2ByteFunction andThenByte(Long2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2LongFunction composeByte(Byte2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2ShortFunction andThenShort(Long2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2LongFunction composeShort(Short2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2IntFunction andThenInt(Long2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2LongFunction composeInt(Int2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2LongFunction andThenLong(Long2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2LongFunction composeLong(Long2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2CharFunction andThenChar(Long2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2LongFunction composeChar(Char2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2FloatFunction andThenFloat(Long2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2LongFunction composeFloat(Float2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2DoubleFunction andThenDouble(Long2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2LongFunction composeDouble(Double2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Byte2ObjectFunction<T> andThenObject(Long2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2LongFunction<T> composeObject(Object2ByteFunction<? super T> before) {
        return k -> this.get(before.getByte(k));
    }

    default public <T> Byte2ReferenceFunction<T> andThenReference(Long2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2LongFunction<T> composeReference(Reference2ByteFunction<? super T> before) {
        return k -> this.get(before.getByte(k));
    }
}

