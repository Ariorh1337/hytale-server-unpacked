/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultMap<K, V>
implements Map<K, V> {
    private final Map<K, V> delegate;
    private final boolean allowReplacing;
    private final boolean replaceNullWithDefault;
    private V defaultValue;

    public DefaultMap(V defaultValue) {
        this(defaultValue, new HashMap());
    }

    public DefaultMap(V defaultValue, Map<K, V> delegate) {
        this(defaultValue, delegate, true);
    }

    public DefaultMap(V defaultValue, Map<K, V> delegate, boolean allowReplacing) {
        this(defaultValue, delegate, allowReplacing, true);
    }

    public DefaultMap(V defaultValue, Map<K, V> delegate, boolean allowReplacing, boolean replaceNullWithDefault) {
        this.defaultValue = defaultValue;
        this.delegate = delegate;
        this.allowReplacing = allowReplacing;
        this.replaceNullWithDefault = replaceNullWithDefault;
    }

    public V getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<K, V> getDelegate() {
        return this.delegate;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(@Nullable Object key) {
        if (this.replaceNullWithDefault && key == null) {
            return this.defaultValue;
        }
        V value = this.delegate.get(key);
        return value != null ? value : this.defaultValue;
    }

    @Override
    public V put(K key, V value) {
        if (this.allowReplacing) {
            return this.delegate.put(key, value);
        }
        V oldValue = this.delegate.putIfAbsent(key, value);
        if (oldValue == null) {
            return null;
        }
        throw new IllegalArgumentException("Attachment (" + String.valueOf(key) + ") is already registered!");
    }

    @Override
    public V remove(Object key) {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(@Nonnull Map<? extends K, ? extends V> m) {
        this.delegate.putAll(m);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    @Nonnull
    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    @Override
    @Nonnull
    public Collection<V> values() {
        return this.delegate.values();
    }

    @Override
    @Nonnull
    public Set<Map.Entry<K, V>> entrySet() {
        return this.delegate.entrySet();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultMap that = (DefaultMap)o;
        if (this.allowReplacing != that.allowReplacing) {
            return false;
        }
        if (this.replaceNullWithDefault != that.replaceNullWithDefault) {
            return false;
        }
        if (this.delegate != null ? !this.delegate.equals(that.delegate) : that.delegate != null) {
            return false;
        }
        return this.defaultValue != null ? this.defaultValue.equals(that.defaultValue) : that.defaultValue == null;
    }

    @Override
    public int hashCode() {
        int result = this.delegate != null ? this.delegate.hashCode() : 0;
        result = 31 * result + (this.allowReplacing ? 1 : 0);
        result = 31 * result + (this.replaceNullWithDefault ? 1 : 0);
        result = 31 * result + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        return result;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return this.delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.delegate.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return this.delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.delegate.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return this.delegate.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return this.delegate.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, @Nonnull Function<? super K, ? extends V> mappingFunction) {
        return this.delegate.computeIfAbsent((K)key, mappingFunction);
    }

    @Override
    @Nullable
    public V computeIfPresent(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegate.computeIfPresent((K)key, (BiFunction<? super K, ? extends V, ? extends V>)remappingFunction);
    }

    @Override
    public V compute(K key, @Nonnull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegate.compute((K)key, (BiFunction<? super K, ? extends V, ? extends V>)remappingFunction);
    }

    @Override
    public V merge(K key, @Nonnull V value, @Nonnull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.delegate.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
    }

    @Nonnull
    public String toString() {
        return "DefaultMap{defaultValue=" + String.valueOf(this.defaultValue) + ", delegate=" + String.valueOf(this.delegate) + "}";
    }
}

