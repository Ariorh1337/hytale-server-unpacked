/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

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
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
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
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.util.function.LongPredicate;

@FunctionalInterface
public interface Long2BooleanFunction
extends Function<Long, Boolean>,
LongPredicate {
    @Override
    default public boolean test(long operand) {
        return this.get(operand);
    }

    @Override
    default public boolean put(long key, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean get(long var1);

    default public boolean getOrDefault(long key, boolean defaultValue) {
        boolean v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public boolean remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Boolean put(Long key, Boolean value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        boolean v = this.put(k, (boolean)value);
        return containsKey ? Boolean.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Boolean get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        boolean v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Boolean getOrDefault(Object key, Boolean defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        long k = (Long)key;
        boolean v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Boolean remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Boolean.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException();
    }

    default public boolean defaultReturnValue() {
        return false;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Boolean> compose(java.util.function.Function<? super T, ? extends Long> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Long, T> andThen(java.util.function.Function<? super Boolean, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Long2ByteFunction andThenByte(Boolean2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2BooleanFunction composeByte(Byte2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2ShortFunction andThenShort(Boolean2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2BooleanFunction composeShort(Short2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2IntFunction andThenInt(Boolean2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2BooleanFunction composeInt(Int2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2LongFunction andThenLong(Boolean2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2BooleanFunction composeLong(Long2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2CharFunction andThenChar(Boolean2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2BooleanFunction composeChar(Char2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2FloatFunction andThenFloat(Boolean2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2BooleanFunction composeFloat(Float2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Long2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2BooleanFunction composeDouble(Double2LongFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Long2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2BooleanFunction<T> composeObject(Object2LongFunction<? super T> before) {
        return k -> this.get(before.getLong(k));
    }

    default public <T> Long2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2BooleanFunction<T> composeReference(Reference2LongFunction<? super T> before) {
        return k -> this.get(before.getLong(k));
    }
}

