/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
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
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Float2ByteFunction
extends Function<Float, Byte>,
DoubleToIntFunction {
    @Override
    @Deprecated
    default public int applyAsInt(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public byte put(float key, byte value) {
        throw new UnsupportedOperationException();
    }

    public byte get(float var1);

    default public byte getOrDefault(float key, byte defaultValue) {
        byte v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public byte remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Byte put(Float key, Byte value) {
        float k = key.floatValue();
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
        float k = ((Float)key).floatValue();
        byte v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Byte getOrDefault(Object key, Byte defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        float k = ((Float)key).floatValue();
        byte v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Byte remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Byte.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException();
    }

    default public byte defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Byte> compose(java.util.function.Function<? super T, ? extends Float> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Float, T> andThen(java.util.function.Function<? super Byte, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Float2ByteFunction andThenByte(Byte2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2ByteFunction composeByte(Byte2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2ShortFunction andThenShort(Byte2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2ByteFunction composeShort(Short2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2IntFunction andThenInt(Byte2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2ByteFunction composeInt(Int2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2LongFunction andThenLong(Byte2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2ByteFunction composeLong(Long2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2CharFunction andThenChar(Byte2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2ByteFunction composeChar(Char2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2FloatFunction andThenFloat(Byte2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2ByteFunction composeFloat(Float2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2DoubleFunction andThenDouble(Byte2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2ByteFunction composeDouble(Double2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Float2ObjectFunction<T> andThenObject(Byte2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ByteFunction<T> composeObject(Object2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }

    default public <T> Float2ReferenceFunction<T> andThenReference(Byte2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ByteFunction<T> composeReference(Reference2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }
}

