/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2ShortFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2ShortFunction
extends Function<Integer, Short>,
IntUnaryOperator {
    @Override
    default public int applyAsInt(int operand) {
        return this.get(operand);
    }

    @Override
    default public short put(int key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(int var1);

    default public short getOrDefault(int key, short defaultValue) {
        short v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public short remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Short put(Integer key, Short value) {
        int k = key;
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
        int k = (Integer)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Short getOrDefault(Object key, Short defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        int k = (Integer)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Short> compose(java.util.function.Function<? super T, ? extends Integer> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Integer, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Int2ByteFunction andThenByte(Short2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2ShortFunction composeByte(Byte2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2ShortFunction andThenShort(Short2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2ShortFunction composeShort(Short2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2IntFunction andThenInt(Short2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2ShortFunction composeInt(Int2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2LongFunction andThenLong(Short2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2ShortFunction composeLong(Long2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2CharFunction andThenChar(Short2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2ShortFunction composeChar(Char2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2FloatFunction andThenFloat(Short2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2ShortFunction composeFloat(Float2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Int2DoubleFunction andThenDouble(Short2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2ShortFunction composeDouble(Double2IntFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Int2ObjectFunction<T> andThenObject(Short2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ShortFunction<T> composeObject(Object2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }

    default public <T> Int2ReferenceFunction<T> andThenReference(Short2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ShortFunction<T> composeReference(Reference2IntFunction<? super T> before) {
        return k -> this.get(before.getInt(k));
    }
}

