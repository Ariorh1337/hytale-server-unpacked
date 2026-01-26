/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharBinaryOperator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;

public interface Float2CharMap
extends Float2CharFunction,
Map<Float, Character> {
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

    public ObjectSet<Entry> float2CharEntrySet();

    @Override
    @Deprecated
    default public ObjectSet<Map.Entry<Float, Character>> entrySet() {
        return this.float2CharEntrySet();
    }

    @Override
    @Deprecated
    default public Character put(Float key, Character value) {
        return Float2CharFunction.super.put(key, value);
    }

    @Override
    @Deprecated
    default public Character get(Object key) {
        return Float2CharFunction.super.get(key);
    }

    @Override
    @Deprecated
    default public Character remove(Object key) {
        return Float2CharFunction.super.remove(key);
    }

    public FloatSet keySet();

    public CharCollection values();

    @Override
    public boolean containsKey(float var1);

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return Float2CharFunction.super.containsKey(key);
    }

    public boolean containsValue(char var1);

    @Override
    @Deprecated
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue(((Character)value).charValue());
    }

    @Override
    default public void forEach(BiConsumer<? super Float, ? super Character> consumer) {
        ObjectSet<Entry> entrySet = this.float2CharEntrySet();
        Consumer<Entry> wrappingConsumer = entry -> consumer.accept(Float.valueOf(entry.getFloatKey()), Character.valueOf(entry.getCharValue()));
        if (entrySet instanceof FastEntrySet) {
            ((FastEntrySet)entrySet).fastForEach(wrappingConsumer);
        } else {
            entrySet.forEach(wrappingConsumer);
        }
    }

    @Override
    default public char getOrDefault(float key, char defaultValue) {
        char v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    @Deprecated
    default public Character getOrDefault(Object key, Character defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    default public char putIfAbsent(float key, char value) {
        char drv;
        char v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(float key, char value) {
        char curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(float key, char oldValue, char newValue) {
        char curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public char replace(float key, char value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public char computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        char v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        char newValue = SafeMath.safeIntToChar(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public char computeIfAbsentNullable(float key, DoubleFunction<? extends Character> mappingFunction) {
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

    default public char computeIfAbsent(float key, Float2CharFunction mappingFunction) {
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
    default public char computeIfAbsentPartial(float key, Float2CharFunction mappingFunction) {
        return this.computeIfAbsent(key, mappingFunction);
    }

    @Override
    default public char computeIfPresent(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Character newValue = remappingFunction.apply(Float.valueOf(key), Character.valueOf(oldValue));
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        char newVal = newValue.charValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public char compute(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Character newValue = remappingFunction.apply(Float.valueOf(key), contained ? Character.valueOf(oldValue) : null);
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
    default public char merge(float key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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

    default public char mergeChar(float key, char value, CharBinaryOperator remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        char newValue = oldValue != drv || this.containsKey(key) ? remappingFunction.apply(oldValue, value) : value;
        this.put(key, newValue);
        return newValue;
    }

    default public char mergeChar(float key, char value, IntBinaryOperator remappingFunction) {
        return this.mergeChar(key, value, remappingFunction instanceof CharBinaryOperator ? (CharBinaryOperator)remappingFunction : (x, y) -> SafeMath.safeIntToChar(remappingFunction.applyAsInt(x, y)));
    }

    @Override
    @Deprecated
    default public Character putIfAbsent(Float key, Character value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Override
    @Deprecated
    default public boolean replace(Float key, Character oldValue, Character newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    @Deprecated
    default public Character replace(Float key, Character value) {
        return Map.super.replace(key, value);
    }

    @Override
    @Deprecated
    default public Character computeIfAbsent(Float key, Function<? super Float, ? extends Character> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    @Deprecated
    default public Character computeIfPresent(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Character compute(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Character merge(Float key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
    extends Map.Entry<Float, Character> {
        public float getFloatKey();

        @Override
        @Deprecated
        default public Float getKey() {
            return Float.valueOf(this.getFloatKey());
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

