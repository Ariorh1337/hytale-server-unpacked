/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.Byte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2IntFunction;
import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2ByteFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2CharFunction
extends Function<Byte, Character>,
IntUnaryOperator {
    @Override
    @Deprecated
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public char put(byte key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(byte var1);

    default public char getOrDefault(byte key, char defaultValue) {
        char v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public char remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Character put(Byte key, Character value) {
        byte k = key;
        boolean containsKey = this.containsKey(k);
        char v = this.put(k, value.charValue());
        return containsKey ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character get(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        if (key == null) {
            return defaultValue;
        }
        byte k = (Byte)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<T, Character> compose(java.util.function.Function<? super T, ? extends Byte> before) {
        return Function.super.compose(before);
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<Byte, T> andThen(java.util.function.Function<? super Character, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Byte2ByteFunction andThenByte(Char2ByteFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Byte2CharFunction composeByte(Byte2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2ShortFunction andThenShort(Char2ShortFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Short2CharFunction composeShort(Short2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2IntFunction andThenInt(Char2IntFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Int2CharFunction composeInt(Int2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2LongFunction andThenLong(Char2LongFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Long2CharFunction composeLong(Long2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2CharFunction andThenChar(Char2CharFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Char2CharFunction composeChar(Char2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2FloatFunction andThenFloat(Char2FloatFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Float2CharFunction composeFloat(Float2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public Byte2DoubleFunction andThenDouble(Char2DoubleFunction after) {
        return k -> after.get(this.get(k));
    }

    default public Double2CharFunction composeDouble(Double2ByteFunction before) {
        return k -> this.get(before.get(k));
    }

    default public <T> Byte2ObjectFunction<T> andThenObject(Char2ObjectFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Object2CharFunction<T> composeObject(Object2ByteFunction<? super T> before) {
        return k -> this.get(before.getByte(k));
    }

    default public <T> Byte2ReferenceFunction<T> andThenReference(Char2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.get(k));
    }

    default public <T> Reference2CharFunction<T> composeReference(Reference2ByteFunction<? super T> before) {
        return k -> this.get(before.getByte(k));
    }
}

