/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
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
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2ShortFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
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
import java.util.function.IntPredicate;

@FunctionalInterface
public interface Short2BooleanFunction
extends Function<Short, Boolean>,
IntPredicate {
    @Override
    @Deprecated
    default public boolean test(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public boolean put(short key, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean get(short var1);

    default public boolean getOrDefault(short key, boolean defaultValue) {
        boolean v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public boolean remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Boolean put(Short key, Boolean value) {
        short k = key;
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
        short k = (Short)key;
        boolean v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Boolean getOrDefault(Object key, Boolean defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        short k = (Short)key;
        boolean v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Boolean remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Boolean.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException();
    }

    default public boolean defaultReturnValue() {
        return false;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Boolean> compose(java.util.function.Function<? super T, ? extends Short> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Short, T> andThen(java.util.function.Function<? super Boolean, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Short2ByteFunction andThenByte(Boolean2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2BooleanFunction composeByte(Byte2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2ShortFunction andThenShort(Boolean2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2BooleanFunction composeShort(Short2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2IntFunction andThenInt(Boolean2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2BooleanFunction composeInt(Int2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2LongFunction andThenLong(Boolean2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2BooleanFunction composeLong(Long2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2CharFunction andThenChar(Boolean2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2BooleanFunction composeChar(Char2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2FloatFunction andThenFloat(Boolean2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2BooleanFunction composeFloat(Float2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Short2DoubleFunction andThenDouble(Boolean2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2BooleanFunction composeDouble(Double2ShortFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Short2ObjectFunction<T> andThenObject(Boolean2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2BooleanFunction<T> composeObject(Object2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }

    default public <T> Short2ReferenceFunction<T> andThenReference(Boolean2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2BooleanFunction<T> composeReference(Reference2ShortFunction<? super T> before) {
        return k -> this.get(before.getShort(k));
    }
}

