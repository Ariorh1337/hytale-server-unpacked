/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
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
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface Float2ReferenceFunction<V>
extends Function<Float, V>,
DoubleFunction<V> {
    @Override
    @Deprecated
    default public V apply(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public V put(float key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(float var1);

    default public V getOrDefault(float key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public V remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public V put(Float key, V value) {
        float k = key.floatValue();
        boolean containsKey = this.containsKey(k);
        V v = this.put(k, value);
        return (V)(containsKey ? v : null);
    }

    @Override
    @Deprecated
    default public V get(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Override
    @Deprecated
    default public V getOrDefault(Object key, V defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        float k = ((Float)key).floatValue();
        V v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? v : defaultValue;
    }

    @Override
    @Deprecated
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, V> compose(java.util.function.Function<? super T, ? extends Float> before) {
        return Function.super.compose(before);
    }

    default public Float2ByteFunction andThenByte(Reference2ByteFunction<V> after) {
        return k -> after.getByte(this.get(k));
    }

    default public Byte2ReferenceFunction<V> composeByte(Byte2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2ShortFunction andThenShort(Reference2ShortFunction<V> after) {
        return k -> after.getShort(this.get(k));
    }

    default public Short2ReferenceFunction<V> composeShort(Short2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2IntFunction andThenInt(Reference2IntFunction<V> after) {
        return k -> after.getInt(this.get(k));
    }

    default public Int2ReferenceFunction<V> composeInt(Int2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2LongFunction andThenLong(Reference2LongFunction<V> after) {
        return k -> after.getLong(this.get(k));
    }

    default public Long2ReferenceFunction<V> composeLong(Long2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2CharFunction andThenChar(Reference2CharFunction<V> after) {
        return k -> after.getChar(this.get(k));
    }

    default public Char2ReferenceFunction<V> composeChar(Char2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2FloatFunction andThenFloat(Reference2FloatFunction<V> after) {
        return k -> after.getFloat(this.get(k));
    }

    default public Float2ReferenceFunction<V> composeFloat(Float2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Float2DoubleFunction andThenDouble(Reference2DoubleFunction<V> after) {
        return k -> after.getDouble(this.get(k));
    }

    default public Double2ReferenceFunction<V> composeDouble(Double2FloatFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Float2ObjectFunction<T> andThenObject(Reference2ObjectFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ReferenceFunction<T, V> composeObject(Object2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }

    default public <T> Float2ReferenceFunction<T> andThenReference(Reference2ReferenceFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ReferenceFunction<T, V> composeReference(Reference2FloatFunction<? super T> before) {
        return k -> this.get(before.getFloat(k));
    }
}

