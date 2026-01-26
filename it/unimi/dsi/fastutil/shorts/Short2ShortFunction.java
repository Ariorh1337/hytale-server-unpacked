/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Short2ShortFunction
extends Function<Short, Short>,
IntUnaryOperator {
    @Override
    @Deprecated
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public short put(short key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(short var1);

    default public short getOrDefault(short key, short defaultValue) {
        short v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public short remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Short put(Short key, Short value) {
        short k = key;
        boolean containsKey = this.containsKey(k);
        short v = this.put(k, (short)value);
        return containsKey ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short get(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short getOrDefault(Object key, Short defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        short k = (Short)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }

    public static Short2ShortFunction identity() {
        return k -> k;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Short> compose(java.util.function.Function<? super T, ? extends Short> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Short, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Short2ByteFunction andThenByte(Short2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2ShortFunction composeByte(Byte2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2ShortFunction andThenShort(Short2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2ShortFunction composeShort(Short2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2IntFunction andThenInt(Short2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2ShortFunction composeInt(Int2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2LongFunction andThenLong(Short2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2ShortFunction composeLong(Long2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2CharFunction andThenChar(Short2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2ShortFunction composeChar(Char2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2FloatFunction andThenFloat(Short2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2ShortFunction composeFloat(Float2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2DoubleFunction andThenDouble(Short2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2ShortFunction composeDouble(Double2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Short2ObjectFunction<T> andThenObject(Short2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ShortFunction<T> composeObject(Object2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }

    default public <T> Short2ReferenceFunction<T> andThenReference(Short2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ShortFunction<T> composeReference(Reference2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }
}

