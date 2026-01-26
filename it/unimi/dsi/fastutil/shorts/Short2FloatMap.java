/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public interface Short2FloatMap
extends Short2FloatFunction,
Map<Short, Float> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(float var1);

    @Override
    public float defaultReturnValue();

    public ObjectSet<Entry> short2FloatEntrySet();

    @Override
    @Deprecated
    default public ObjectSet<Map.Entry<Short, Float>> entrySet() {
        return this.short2FloatEntrySet();
    }

    @Override
    @Deprecated
    default public Float put(Short key, Float value) {
        return Short2FloatFunction.super.put(key, value);
    }

    @Override
    @Deprecated
    default public Float get(Object key) {
        return Short2FloatFunction.super.get(key);
    }

    @Override
    @Deprecated
    default public Float remove(Object key) {
        return Short2FloatFunction.super.remove(key);
    }

    public ShortSet keySet();

    public FloatCollection values();

    @Override
    public boolean containsKey(short var1);

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return Short2FloatFunction.super.containsKey(key);
    }

    public boolean containsValue(float var1);

    @Override
    @Deprecated
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue(((Float)value).floatValue());
    }

    @Override
    default public void forEach(BiConsumer<? super Short, ? super Float> consumer) {
        ObjectSet<Entry> entrySet = this.short2FloatEntrySet();
        Consumer<Entry> wrappingConsumer = entry -> consumer.accept(entry.getShortKey(), Float.valueOf(entry.getFloatValue()));
        if (entrySet instanceof FastEntrySet) {
            ((FastEntrySet)entrySet).fastForEach(wrappingConsumer);
        } else {
            entrySet.forEach(wrappingConsumer);
        }
    }

    @Override
    default public float getOrDefault(short key, float defaultValue) {
        float v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    @Deprecated
    default public Float getOrDefault(Object key, Float defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    default public float putIfAbsent(short key, float value) {
        float drv;
        float v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(short key, float value) {
        float curValue = this.get(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(short key, float oldValue, float newValue) {
        float curValue = this.get(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public float replace(short key, float value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public float computeIfAbsent(short key, IntToDoubleFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(key));
        this.put(key, newValue);
        return newValue;
    }

    default public float computeIfAbsentNullable(short key, IntFunction<? extends Float> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.get(key);
        float drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Float mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        float newValue = mappedValue.floatValue();
        this.put(key, newValue);
        return newValue;
    }

    default public float computeIfAbsent(short key, Short2FloatFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.get(key);
        float drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        float newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Deprecated
    default public float computeIfAbsentPartial(short key, Short2FloatFunction mappingFunction) {
        return this.computeIfAbsent(key, mappingFunction);
    }

    @Override
    default public float computeIfPresent(short key, BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Float newValue = remappingFunction.apply((Short)key, Float.valueOf(oldValue));
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        float newVal = newValue.floatValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public float compute(short key, BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Float newValue = remappingFunction.apply((Short)key, contained ? Float.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        float newVal = newValue.floatValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public float merge(short key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        float newValue;
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Float mergedValue = remappingFunction.apply(Float.valueOf(oldValue), Float.valueOf(value));
            if (mergedValue == null) {
                this.remove(key);
                return drv;
            }
            newValue = mergedValue.floatValue();
        } else {
            newValue = value;
        }
        this.put(key, newValue);
        return newValue;
    }

    default public float mergeFloat(short key, float value, FloatBinaryOperator remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        float newValue = oldValue != drv || this.containsKey(key) ? remappingFunction.apply(oldValue, value) : value;
        this.put(key, newValue);
        return newValue;
    }

    default public float mergeFloat(short key, float value, DoubleBinaryOperator remappingFunction) {
        return this.mergeFloat(key, value, remappingFunction instanceof FloatBinaryOperator ? (FloatBinaryOperator)remappingFunction : (x, y) -> SafeMath.safeDoubleToFloat(remappingFunction.applyAsDouble(x, y)));
    }

    @Override
    @Deprecated
    default public Float putIfAbsent(Short key, Float value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Override
    @Deprecated
    default public boolean replace(Short key, Float oldValue, Float newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    @Deprecated
    default public Float replace(Short key, Float value) {
        return Map.super.replace(key, value);
    }

    @Override
    @Deprecated
    default public Float computeIfAbsent(Short key, Function<? super Short, ? extends Float> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    @Deprecated
    default public Float computeIfPresent(Short key, BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Float compute(Short key, BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Float merge(Short key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
    extends Map.Entry<Short, Float> {
        public short getShortKey();

        @Override
        @Deprecated
        default public Short getKey() {
            return this.getShortKey();
        }

        public float getFloatValue();

        @Override
        public float setValue(float var1);

        @Override
        @Deprecated
        default public Float getValue() {
            return Float.valueOf(this.getFloatValue());
        }

        @Override
        @Deprecated
        default public Float setValue(Float value) {
            return Float.valueOf(this.setValue(value.floatValue()));
        }
    }
}

