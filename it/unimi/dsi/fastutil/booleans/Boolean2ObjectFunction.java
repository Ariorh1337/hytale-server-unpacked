/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.booleans.Boolean2ByteFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2CharFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2DoubleFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2FloatFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2IntFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2LongFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ReferenceFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ShortFunction;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;

@FunctionalInterface
public interface Boolean2ObjectFunction<V>
extends Function<Boolean, V> {
    @Override
    default public V put(boolean key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(boolean var1);

    default public V getOrDefault(boolean key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public V remove(boolean key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public V put(Boolean key, V value) {
        boolean k = key;
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
        boolean k = (Boolean)key;
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Override
    @Deprecated
    default public V getOrDefault(Object key, V defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        boolean k = (Boolean)key;
        V v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? v : defaultValue;
    }

    @Override
    @Deprecated
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        boolean k = (Boolean)key;
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(boolean key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Boolean)key);
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, V> compose(java.util.function.Function<? super T, ? extends Boolean> before) {
        return Function.super.compose(before);
    }

    default public Boolean2ByteFunction andThenByte(Object2ByteFunction<V> after) {
        return k -> after.getByte(this.get(k));
    }

    default public Byte2ObjectFunction<V> composeByte(Byte2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2ShortFunction andThenShort(Object2ShortFunction<V> after) {
        return k -> after.getShort(this.get(k));
    }

    default public Short2ObjectFunction<V> composeShort(Short2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2IntFunction andThenInt(Object2IntFunction<V> after) {
        return k -> after.getInt(this.get(k));
    }

    default public Int2ObjectFunction<V> composeInt(Int2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2LongFunction andThenLong(Object2LongFunction<V> after) {
        return k -> after.getLong(this.get(k));
    }

    default public Long2ObjectFunction<V> composeLong(Long2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2CharFunction andThenChar(Object2CharFunction<V> after) {
        return k -> after.getChar(this.get(k));
    }

    default public Char2ObjectFunction<V> composeChar(Char2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2FloatFunction andThenFloat(Object2FloatFunction<V> after) {
        return k -> after.getFloat(this.get(k));
    }

    default public Float2ObjectFunction<V> composeFloat(Float2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2DoubleFunction andThenDouble(Object2DoubleFunction<V> after) {
        return k -> after.getDouble(this.get(k));
    }

    default public Double2ObjectFunction<V> composeDouble(Double2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Boolean2ObjectFunction<T> andThenObject(Object2ObjectFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2ObjectFunction<T, V> composeObject(Object2BooleanFunction<? super T> before) {
        return k -> this.get(before.getBoolean(k));
    }

    default public <T> Boolean2ReferenceFunction<T> andThenReference(Object2ReferenceFunction<? super V, ? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2ObjectFunction<T, V> composeReference(Reference2BooleanFunction<? super T> before) {
        return k -> this.get(before.getBoolean(k));
    }
}

