/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2CharFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2CharFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2FloatFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction;
import it.unimi.dsi.fastutil.objects.Reference2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2CharFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Reference2CharFunction<K>
extends Function<K, Character>,
ToIntFunction<K> {
    @Override
    default public int applyAsInt(K operand) {
        return this.getChar(operand);
    }

    @Override
    default public char put(K key, char value) {
        throw new UnsupportedOperationException();
    }

    public char getChar(Object var1);

    @Override
    default public char getOrDefault(Object key, char defaultValue) {
        char v = this.getChar(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    default public char removeChar(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public Character put(K key, Character value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        char v = this.put(k, value.charValue());
        return containsKey ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character get(Object key) {
        Object k = key;
        char v = this.getChar(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        Object k = key;
        char v = this.getChar(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : defaultValue;
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Character.valueOf(this.removeChar(k)) : null;
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }

    @Override
    @Deprecated
    default public <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Character, ? extends T> after) {
        return Function.super.andThen(after);
    }

    default public Reference2ByteFunction<K> andThenByte(Char2ByteFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Byte2CharFunction composeByte(Byte2ReferenceFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Reference2ShortFunction<K> andThenShort(Char2ShortFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Short2CharFunction composeShort(Short2ReferenceFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Reference2IntFunction<K> andThenInt(Char2IntFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Int2CharFunction composeInt(Int2ReferenceFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Reference2LongFunction<K> andThenLong(Char2LongFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Long2CharFunction composeLong(Long2ReferenceFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Reference2CharFunction<K> andThenChar(Char2CharFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Char2CharFunction composeChar(Char2ReferenceFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Reference2FloatFunction<K> andThenFloat(Char2FloatFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Float2CharFunction composeFloat(Float2ReferenceFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public Reference2DoubleFunction<K> andThenDouble(Char2DoubleFunction after) {
        return k -> after.get(this.getChar(k));
    }

    default public Double2CharFunction composeDouble(Double2ReferenceFunction<K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public <T> Reference2ObjectFunction<K, T> andThenObject(Char2ObjectFunction<? extends T> after) {
        return k -> after.get(this.getChar(k));
    }

    default public <T> Object2CharFunction<T> composeObject(Object2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getChar(before.get(k));
    }

    default public <T> Reference2ReferenceFunction<K, T> andThenReference(Char2ReferenceFunction<? extends T> after) {
        return k -> after.get(this.getChar(k));
    }

    default public <T> Reference2CharFunction<T> composeReference(Reference2ReferenceFunction<? super T, ? extends K> before) {
        return k -> this.getChar(before.get(k));
    }
}

