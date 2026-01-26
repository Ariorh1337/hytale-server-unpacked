/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.booleans.Boolean2ByteFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2CharFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2FloatFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2IntFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2LongFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ReferenceFunction;
import it.unimi.dsi.fastutil.booleans.Boolean2ShortFunction;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;

@FunctionalInterface
public interface Boolean2DoubleFunction
extends Function<Boolean, Double> {
    @Override
    default public double put(boolean key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(boolean var1);

    default public double getOrDefault(boolean key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public double remove(boolean key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Double put(Boolean key, Double value) {
        boolean k = key;
        boolean containsKey = this.containsKey(k);
        double v = this.put(k, (double)value);
        return containsKey ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double get(Object key) {
        if (key == null) {
            return null;
        }
        boolean k = (Boolean)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Double getOrDefault(Object key, Double defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        boolean k = (Boolean)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        boolean k = (Boolean)key;
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(boolean key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Boolean)key);
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Double> compose(java.util.function.Function<? super T, ? extends Boolean> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Boolean, T> andThen(java.util.function.Function<? super Double, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Boolean2ByteFunction andThenByte(Double2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2DoubleFunction composeByte(Byte2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2ShortFunction andThenShort(Double2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2DoubleFunction composeShort(Short2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2IntFunction andThenInt(Double2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2DoubleFunction composeInt(Int2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2LongFunction andThenLong(Double2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2DoubleFunction composeLong(Long2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2CharFunction andThenChar(Double2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2DoubleFunction composeChar(Char2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2FloatFunction andThenFloat(Double2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2DoubleFunction composeFloat(Float2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Boolean2DoubleFunction andThenDouble(Double2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2DoubleFunction composeDouble(Double2BooleanFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Boolean2ObjectFunction<T> andThenObject(Double2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2DoubleFunction<T> composeObject(Object2BooleanFunction<? super T> before) {
        return k -> this.get(before.getBoolean(k));
    }

    default public <T> Boolean2ReferenceFunction<T> andThenReference(Double2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2DoubleFunction<T> composeReference(Reference2BooleanFunction<? super T> before) {
        return k -> this.get(before.getBoolean(k));
    }
}

