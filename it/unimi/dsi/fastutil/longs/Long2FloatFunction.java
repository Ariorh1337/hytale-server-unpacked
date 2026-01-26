/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface Long2FloatFunction
extends Function<Long, Float>,
LongToDoubleFunction {
    @Override
    default public double applyAsDouble(long operand) {
        return this.get(operand);
    }

    @Override
    default public float put(long key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(long var1);

    default public float getOrDefault(long key, float defaultValue) {
        float v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public float remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Float put(Long key, Float value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        float v = this.put(k, value.floatValue());
        return containsKey ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float getOrDefault(Object key, Float defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        long k = (Long)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Float> compose(java.util.function.Function<? super T, ? extends Long> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Long, T> andThen(java.util.function.Function<? super Float, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Long2ByteFunction andThenByte(Float2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2FloatFunction composeByte(Byte2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2ShortFunction andThenShort(Float2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2FloatFunction composeShort(Short2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2IntFunction andThenInt(Float2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2FloatFunction composeInt(Int2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2LongFunction andThenLong(Float2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2FloatFunction composeLong(Long2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2CharFunction andThenChar(Float2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2FloatFunction composeChar(Char2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2FloatFunction andThenFloat(Float2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2FloatFunction composeFloat(Float2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2DoubleFunction andThenDouble(Float2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2FloatFunction composeDouble(Double2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Long2ObjectFunction<T> andThenObject(Float2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2FloatFunction<T> composeObject(Object2LongFunction<? super T> before) {
        return k -> this.get(before.getLong(k));
    }

    default public <T> Long2ReferenceFunction<T> andThenReference(Float2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2FloatFunction<T> composeReference(Reference2LongFunction<? super T> before) {
        return k -> this.get(before.getLong(k));
    }
}

