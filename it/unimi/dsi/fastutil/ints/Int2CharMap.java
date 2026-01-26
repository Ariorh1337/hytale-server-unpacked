/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharBinaryOperator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.ints.Int2CharFunction;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Int2CharMap
extends Int2CharFunction,
Map<Integer, Character> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(char var1);

    @Override
    public char defaultReturnValue();

    public ObjectSet<Entry> int2CharEntrySet();

    @Override
    @Deprecated
    default public ObjectSet<Map.Entry<Integer, Character>> entrySet() {
        return this.int2CharEntrySet();
    }

    @Override
    @Deprecated
    default public Character put(Integer key, Character value) {
        return Int2CharFunction.super.put(key, value);
    }

    @Override
    @Deprecated
    default public Character get(Object key) {
        return Int2CharFunction.super.get(key);
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        return Int2CharFunction.super.remove(key);
    }

    public IntSet keySet();

    public CharCollection values();

    @Override
    public boolean containsKey(int var1);

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return Int2CharFunction.super.containsKey(key);
    }

    public boolean containsValue(char var1);

    @Override
    @Deprecated
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue(((Character)value).charValue());
    }

    @Override
    default public void forEach(BiConsumer<? super Integer, ? super Character> consumer) {
        ObjectSet<Entry> entrySet = this.int2CharEntrySet();
        Consumer<Entry> wrappingConsumer = entry -> consumer.accept(entry.getIntKey(), Character.valueOf(entry.getCharValue()));
        if (entrySet instanceof FastEntrySet) {
            ((FastEntrySet)entrySet).fastForEach(wrappingConsumer);
        } else {
            entrySet.forEach(wrappingConsumer);
        }
    }

    @Override
    default public char getOrDefault(int key, char defaultValue) {
        char v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    default public char putIfAbsent(int key, char value) {
        char drv;
        char v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(int key, char value) {
        char curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(int key, char oldValue, char newValue) {
        char curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public char replace(int key, char value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public char computeIfAbsent(int key, IntUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        char v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        char newValue = SafeMath.safeIntToChar(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public char computeIfAbsentNullable(int key, IntFunction<? extends Character> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        char v = this.get(key);
        char drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Character mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        char newValue = mappedValue.charValue();
        this.put(key, newValue);
        return newValue;
    }

    default public char computeIfAbsent(int key, Int2CharFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        char v = this.get(key);
        char drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        char newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Deprecated
    default public char computeIfAbsentPartial(int key, Int2CharFunction mappingFunction) {
        return this.computeIfAbsent(key, mappingFunction);
    }

    @Override
    default public char computeIfPresent(int key, BiFunction<? super Integer, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Character newValue = remappingFunction.apply((Integer)key, Character.valueOf(oldValue));
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        char newVal = newValue.charValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public char compute(int key, BiFunction<? super Integer, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Character newValue = remappingFunction.apply((Integer)key, contained ? Character.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        char newVal = newValue.charValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public char merge(int key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        char newValue;
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Character mergedValue = remappingFunction.apply(Character.valueOf(oldValue), Character.valueOf(value));
            if (mergedValue == null) {
                this.remove(key);
                return drv;
            }
            newValue = mergedValue.charValue();
        } else {
            newValue = value;
        }
        this.put(key, newValue);
        return newValue;
    }

    default public char mergeChar(int key, char value, CharBinaryOperator remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        char newValue = oldValue != drv || this.containsKey(key) ? remappingFunction.apply(oldValue, value) : value;
        this.put(key, newValue);
        return newValue;
    }

    default public char mergeChar(int key, char value, IntBinaryOperator remappingFunction) {
        return this.mergeChar(key, value, remappingFunction instanceof CharBinaryOperator ? (CharBinaryOperator)remappingFunction : (x, y) -> SafeMath.safeIntToChar(remappingFunction.applyAsInt(x, y)));
    }

    @Override
    @Deprecated
    default public Character putIfAbsent(Integer key, Character value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Override
    @Deprecated
    default public boolean replace(Integer key, Character oldValue, Character newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    @Deprecated
    default public Character replace(Integer key, Character value) {
        return Map.super.replace(key, value);
    }

    @Override
    @Deprecated
    default public Character computeIfAbsent(Integer key, Function<? super Integer, ? extends Character> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    @Deprecated
    default public Character computeIfPresent(Integer key, BiFunction<? super Integer, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Character compute(Integer key, BiFunction<? super Integer, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Character merge(Integer key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

    public static interface Entry
    extends Map.Entry<Integer, Character> {
        public int getIntKey();

        @Override
        @Deprecated
        default public Integer getKey() {
            return this.getIntKey();
        }

        public char getCharValue();

        @Override
        public char setValue(char var1);

        @Override
        @Deprecated
        default public Character getValue() {
            return Character.valueOf(this.getCharValue());
        }

        @Override
        @Deprecated
        default public Character setValue(Character value) {
            return Character.valueOf(this.setValue(value.charValue()));
        }
    }
}

