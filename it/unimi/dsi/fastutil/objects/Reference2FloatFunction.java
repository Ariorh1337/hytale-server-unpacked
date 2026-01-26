/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
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
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Reference2FloatFunction<K>
extends Function<K, Float>,
ToDoubleFunction<K> {
    @Override
    default public double applyAsDouble(K operand) {
        return this.getFloat(operand);
    }

    @Override
    default public float put(K key, float value) {
        throw new UnsupportedOperationException();
    }

    public float getFloat(Object var1);

    @Override
    default public float getOrDefault(Object key, float defaultValue) {
        float v = this.getFloat(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public float removeFloat(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Float put(K key, Float value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        float v = this.put(k, value.floatValue());
        return containsKey ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float get(Object key) {
        Object k = key;
        float v = this.getFloat(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Float getOrDefault(Object key, Float defaultValue) {
        Object k = key;
        float v = this.getFloat(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Float remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Float.valueOf(this.removeFloat(k)) : null;
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Float, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Reference2ByteFunction<K> andThenByte(Float2ByteFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Byte2FloatFunction composeByte(Byte2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2ShortFunction<K> andThenShort(Float2ShortFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Short2FloatFunction composeShort(Short2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2IntFunction<K> andThenInt(Float2IntFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Int2FloatFunction composeInt(Int2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2LongFunction<K> andThenLong(Float2LongFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Long2FloatFunction composeLong(Long2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2CharFunction<K> andThenChar(Float2CharFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Char2FloatFunction composeChar(Char2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2FloatFunction<K> andThenFloat(Float2FloatFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Float2FloatFunction composeFloat(Float2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Reference2DoubleFunction<K> andThenDouble(Float2DoubleFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Double2FloatFunction composeDouble(Double2ReferenceFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public <T> Reference2ObjectFunction<K, T> andThenObject(Float2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getFloat(k));
    }

    default public <T> Object2FloatFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public <T> Reference2ReferenceFunction<K, T> andThenReference(Float2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getFloat(k));
    }

    default public <T> Reference2FloatFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getFloat(before.get(k));
    }
}

