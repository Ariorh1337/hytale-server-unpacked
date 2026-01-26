/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.booleans.Boolean2ByteFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2CharFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2DoubleFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2FloatFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2IntFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2LongFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ReferenceFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ShortFunction;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
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
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.Predicate;

@FunctionalInterface
public interface Reference2BooleanFunction<K>
extends Function<K, Boolean>,
Predicate<K> {
    @Override
    default public boolean test(K operand) {
        return this.getBoolean(operand);
    }

    @Override
    default public boolean put(K key, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(Object var1);

    @Override
    default public boolean getOrDefault(Object key, boolean defaultValue) {
        boolean v = this.getBoolean(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public boolean removeBoolean(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Boolean put(K key, Boolean value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        boolean v = this.put(k, (boolean)value);
        return containsKey ? Boolean.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Boolean get(Object key) {
        Object k = key;
        boolean v = this.getBoolean(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Boolean getOrDefault(Object key, Boolean defaultValue) {
        Object k = key;
        boolean v = this.getBoolean(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Boolean remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Boolean.valueOf(this.removeBoolean(k)) : null;
    }

    default public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException();
    }

    default public boolean defaultReturnValue() {
        return false;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Boolean, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Reference2ByteFunction<K> andThenByte(Boolean2ByteFunction after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public Byte2BooleanFunction composeByte(Byte2ReferenceFunction<K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public Reference2ShortFunction<K> andThenShort(Boolean2ShortFunction after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public Short2BooleanFunction composeShort(Short2ReferenceFunction<K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public Reference2IntFunction<K> andThenInt(Boolean2IntFunction after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public Int2BooleanFunction composeInt(Int2ReferenceFunction<K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public Reference2LongFunction<K> andThenLong(Boolean2LongFunction after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public Long2BooleanFunction composeLong(Long2ReferenceFunction<K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public Reference2CharFunction<K> andThenChar(Boolean2CharFunction after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public Char2BooleanFunction composeChar(Char2ReferenceFunction<K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public Reference2FloatFunction<K> andThenFloat(Boolean2FloatFunction after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public Float2BooleanFunction composeFloat(Float2ReferenceFunction<K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public Reference2DoubleFunction<K> andThenDouble(Boolean2DoubleFunction after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public Double2BooleanFunction composeDouble(Double2ReferenceFunction<K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public <T> Reference2ObjectFunction<K, T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public <T> Object2BooleanFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getBoolean(before.get(k));
    }

    default public <T> Reference2ReferenceFunction<K, T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getBoolean(k));
    }

    default public <T> Reference2BooleanFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getBoolean(before.get(k));
    }
}

