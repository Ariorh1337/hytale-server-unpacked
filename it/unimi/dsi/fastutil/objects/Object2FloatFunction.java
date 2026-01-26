/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
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
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2FloatFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Object2FloatFunction<K>
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

    default public Object2ByteFunction<K> andThenByte(Float2ByteFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Byte2FloatFunction composeByte(Byte2ObjectFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Object2ShortFunction<K> andThenShort(Float2ShortFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Short2FloatFunction composeShort(Short2ObjectFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Object2IntFunction<K> andThenInt(Float2IntFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Int2FloatFunction composeInt(Int2ObjectFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Object2LongFunction<K> andThenLong(Float2LongFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Long2FloatFunction composeLong(Long2ObjectFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Object2CharFunction<K> andThenChar(Float2CharFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Char2FloatFunction composeChar(Char2ObjectFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Object2FloatFunction<K> andThenFloat(Float2FloatFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Float2FloatFunction composeFloat(Float2ObjectFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public Object2DoubleFunction<K> andThenDouble(Float2DoubleFunction after) {
        return k -> after.get(this.getFloat(k));
    }

    default public Double2FloatFunction composeDouble(Double2ObjectFunction<K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public <T> Object2ObjectFunction<K, T> andThenObject(Float2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getFloat(k));
    }

    default public <T> Object2FloatFunction<T> composeObject(Object2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getFloat(before.get(k));
    }

    default public <T> Object2ReferenceFunction<K, T> andThenReference(Float2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getFloat(k));
    }

    default public <T> Reference2FloatFunction<T> composeReference(Reference2ObjectFunction<? super T, ? extends K> before) {
        return k -> this.getFloat(before.get(k));
    }
}

