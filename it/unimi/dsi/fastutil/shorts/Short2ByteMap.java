/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteBinaryOperator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Short2ByteMap
extends Short2ByteFunction,
Map<Short, Byte> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(byte var1);

    @Override
    public byte defaultReturnValue();

    public ObjectSet<Entry> short2ByteEntrySet();

    @Override
    @Deprecated
    default public ObjectSet<Map.Entry<Short, Byte>> entrySet() {
        return this.short2ByteEntrySet();
    }

    @Override
    @Deprecated
    default public Byte put(Short key, Byte value) {
        return Short2ByteFunction.super.put(key, value);
    }

    @Override
    @Deprecated
    default public Byte get(Object key) {
        return Short2ByteFunction.super.get(key);
    }

    @Override
    @Deprecated
    default public Byte remove(Object key) {
        return Short2ByteFunction.super.remove(key);
    }

    public ShortSet keySet();

    public ByteCollection values();

    @Override
    public boolean containsKey(short var1);

    @Override
    @Deprecated
    default public boolean containsKey(Object key) {
        return Short2ByteFunction.super.containsKey(key);
    }

    public boolean containsValue(byte var1);

    @Override
    @Deprecated
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Byte)value);
    }

    @Override
    default public void forEach(BiConsumer<? super Short, ? super Byte> consumer) {
        ObjectSet<Entry> entrySet = this.short2ByteEntrySet();
        Consumer<Entry> wrappingConsumer = entry -> consumer.accept(entry.getShortKey(), entry.getByteValue());
        if (entrySet instanceof FastEntrySet) {
            ((FastEntrySet)entrySet).fastForEach(wrappingConsumer);
        } else {
            entrySet.forEach(wrappingConsumer);
        }
    }

    @Override
    default public byte getOrDefault(short key, byte defaultValue) {
        byte v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    @Deprecated
    default public Byte getOrDefault(Object key, Byte defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Override
    default public byte putIfAbsent(short key, byte value) {
        byte drv;
        byte v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(short key, byte value) {
        byte curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(short key, byte oldValue, byte newValue) {
        byte curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public byte replace(short key, byte value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public byte computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        byte newValue = SafeMath.safeIntToByte(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public byte computeIfAbsentNullable(short key, IntFunction<? extends Byte> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.get(key);
        byte drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Byte mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        byte newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public byte computeIfAbsent(short key, Short2ByteFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.get(key);
        byte drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        byte newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Deprecated
    default public byte computeIfAbsentPartial(short key, Short2ByteFunction mappingFunction) {
        return this.computeIfAbsent(key, mappingFunction);
    }

    @Override
    default public byte computeIfPresent(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.get(key);
        byte drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Byte newValue = remappingFunction.apply((Short)key, (Byte)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        byte newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public byte compute(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.get(key);
        byte drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Byte newValue = remappingFunction.apply((Short)key, contained ? Byte.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        byte newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public byte merge(short key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        byte newValue;
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.get(key);
        byte drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Byte mergedValue = remappingFunction.apply((Byte)oldValue, (Byte)value);
            if (mergedValue == null) {
                this.remove(key);
                return drv;
            }
            newValue = mergedValue;
        } else {
            newValue = value;
        }
        this.put(key, newValue);
        return newValue;
    }

    default public byte mergeByte(short key, byte value, ByteBinaryOperator remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.get(key);
        byte drv = this.defaultReturnValue();
        byte newValue = oldValue != drv || this.containsKey(key) ? remappingFunction.apply(oldValue, value) : value;
        this.put(key, newValue);
        return newValue;
    }

    default public byte mergeByte(short key, byte value, IntBinaryOperator remappingFunction) {
        return this.mergeByte(key, value, remappingFunction instanceof ByteBinaryOperator ? (ByteBinaryOperator)remappingFunction : (x, y) -> SafeMath.safeIntToByte(remappingFunction.applyAsInt(x, y)));
    }

    @Override
    @Deprecated
    default public Byte putIfAbsent(Short key, Byte value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Override
    @Deprecated
    default public boolean replace(Short key, Byte oldValue, Byte newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Override
    @Deprecated
    default public Byte replace(Short key, Byte value) {
        return Map.super.replace(key, value);
    }

    @Override
    @Deprecated
    default public Byte computeIfAbsent(Short key, Function<? super Short, ? extends Byte> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Override
    @Deprecated
    default public Byte computeIfPresent(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Byte compute(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Override
    @Deprecated
    default public Byte merge(Short key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
    extends Map.Entry<Short, Byte> {
        public short getShortKey();

        @Override
        @Deprecated
        default public Short getKey() {
            return this.getShortKey();
        }

        public byte getByteValue();

        @Override
        public byte setValue(byte var1);

        @Override
        @Deprecated
        default public Byte getValue() {
            return this.getByteValue();
        }

        @Override
        @Deprecated
        default public Byte setValue(Byte value) {
            return this.setValue((byte)value);
        }
    }
}

