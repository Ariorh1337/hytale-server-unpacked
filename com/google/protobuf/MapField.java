/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Internal;
import com.google.protobuf.MapEntry;
import com.google.protobuf.MapFieldLite;
import com.google.protobuf.MapFieldReflectionAccessor;
import com.google.protobuf.Message;
import com.google.protobuf.MutabilityOracle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapField<K, V>
extends MapFieldReflectionAccessor
implements MutabilityOracle {
    private volatile boolean isMutable;
    private volatile StorageMode mode;
    private MutabilityAwareMap<K, V> mapData;
    private List<Message> listData;
    private final Converter<K, V> converter;

    private MapField(Converter<K, V> converter, StorageMode mode, Map<K, V> mapData) {
        this.converter = converter;
        this.isMutable = true;
        this.mode = mode;
        this.mapData = new MutabilityAwareMap<K, V>(this, mapData);
        this.listData = null;
    }

    private MapField(MapEntry<K, V> defaultEntry, StorageMode mode, Map<K, V> mapData) {
        this(new ImmutableMessageConverter<K, V>(defaultEntry), mode, mapData);
    }

    public static <K, V> MapField<K, V> emptyMapField(MapEntry<K, V> defaultEntry) {
        return new MapField<K, V>(defaultEntry, StorageMode.MAP, Collections.emptyMap());
    }

    public static <K, V> MapField<K, V> newMapField(MapEntry<K, V> defaultEntry) {
        return new MapField<K, V>(defaultEntry, StorageMode.MAP, new LinkedHashMap());
    }

    private Message convertKeyAndValueToMessage(K key, V value) {
        return this.converter.convertKeyAndValueToMessage(key, value);
    }

    private void convertMessageToKeyAndValue(Message message, Map<K, V> map) {
        this.converter.convertMessageToKeyAndValue(message, map);
    }

    private List<Message> convertMapToList(MutabilityAwareMap<K, V> mapData) {
        ArrayList<Message> listData = new ArrayList<Message>();
        for (Map.Entry<K, V> entry : mapData.entrySet()) {
            listData.add(this.convertKeyAndValueToMessage(entry.getKey(), entry.getValue()));
        }
        return listData;
    }

    private MutabilityAwareMap<K, V> convertListToMap(List<Message> listData) {
        LinkedHashMap mapData = new LinkedHashMap();
        for (Message item : listData) {
            this.convertMessageToKeyAndValue(item, mapData);
        }
        return new MutabilityAwareMap(this, mapData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<K, V> getMap() {
        if (this.mode == StorageMode.LIST) {
            MapField mapField = this;
            synchronized (mapField) {
                if (this.mode == StorageMode.LIST) {
                    this.mapData = this.convertListToMap(this.listData);
                    this.mode = StorageMode.BOTH;
                }
            }
        }
        return Collections.unmodifiableMap(this.mapData);
    }

    public Map<K, V> getMutableMap() {
        if (this.mode != StorageMode.MAP) {
            if (this.mode == StorageMode.LIST) {
                this.mapData = this.convertListToMap(this.listData);
            }
            this.listData = null;
            this.mode = StorageMode.MAP;
        }
        return this.mapData;
    }

    public void mergeFrom(MapField<K, V> other) {
        this.getMutableMap().putAll(MapFieldLite.copy(other.getMap()));
    }

    public void clear() {
        this.mapData = new MutabilityAwareMap(this, new LinkedHashMap());
        this.mode = StorageMode.MAP;
    }

    public boolean equals(Object object) {
        if (!(object instanceof MapField)) {
            return false;
        }
        MapField other = (MapField)object;
        return MapFieldLite.equals(this.getMap(), other.getMap());
    }

    public int hashCode() {
        return MapFieldLite.calculateHashCodeForMap(this.getMap());
    }

    public MapField<K, V> copy() {
        return new MapField<K, V>(this.converter, StorageMode.MAP, MapFieldLite.copy(this.getMap()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    List<Message> getList() {
        if (this.mode == StorageMode.MAP) {
            MapField mapField = this;
            synchronized (mapField) {
                if (this.mode == StorageMode.MAP) {
                    this.listData = this.convertMapToList(this.mapData);
                    this.mode = StorageMode.BOTH;
                }
            }
        }
        return Collections.unmodifiableList(this.listData);
    }

    @Override
    List<Message> getMutableList() {
        if (this.mode != StorageMode.LIST) {
            if (this.mode == StorageMode.MAP) {
                this.listData = this.convertMapToList(this.mapData);
            }
            this.mapData = null;
            this.mode = StorageMode.LIST;
        }
        return this.listData;
    }

    @Override
    Message getMapEntryMessageDefaultInstance() {
        return this.converter.getMessageDefaultInstance();
    }

    public void makeImmutable() {
        this.isMutable = false;
    }

    public boolean isMutable() {
        return this.isMutable;
    }

    @Override
    public void ensureMutable() {
        if (!this.isMutable()) {
            throw new UnsupportedOperationException();
        }
    }

    private static interface Converter<K, V> {
        public Message convertKeyAndValueToMessage(K var1, V var2);

        public void convertMessageToKeyAndValue(Message var1, Map<K, V> var2);

        public Message getMessageDefaultInstance();
    }

    private static enum StorageMode {
        MAP,
        LIST,
        BOTH;

    }

    static class MutabilityAwareMap<K, V>
    implements Map<K, V> {
        private final MutabilityOracle mutabilityOracle;
        private final Map<K, V> delegate;

        MutabilityAwareMap(MutabilityOracle mutabilityOracle, Map<K, V> delegate) {
            this.mutabilityOracle = mutabilityOracle;
            this.delegate = delegate;
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
        public V get(Object key) {
            return this.delegate.get(key);
        }

        @Override
        public V put(K key, V value) {
            this.mutabilityOracle.ensureMutable();
            Internal.checkNotNull(key);
            Internal.checkNotNull(value);
            return this.delegate.put(key, value);
        }

        @Override
        public V remove(Object key) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            this.mutabilityOracle.ensureMutable();
            for (K key : m.keySet()) {
                Internal.checkNotNull(key);
                Internal.checkNotNull(m.get(key));
            }
            this.delegate.putAll(m);
        }

        @Override
        public void clear() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.clear();
        }

        @Override
        public Set<K> keySet() {
            return new MutabilityAwareSet<K>(this.mutabilityOracle, this.delegate.keySet());
        }

        @Override
        public Collection<V> values() {
            return new MutabilityAwareCollection<V>(this.mutabilityOracle, this.delegate.values());
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return new MutabilityAwareSet<Map.Entry<K, V>>(this.mutabilityOracle, this.delegate.entrySet());
        }

        @Override
        public boolean equals(Object o) {
            return this.delegate.equals(o);
        }

        @Override
        public int hashCode() {
            return this.delegate.hashCode();
        }

        public String toString() {
            return this.delegate.toString();
        }

        private static class MutabilityAwareSet<E>
        implements Set<E> {
            private final MutabilityOracle mutabilityOracle;
            private final Set<E> delegate;

            MutabilityAwareSet(MutabilityOracle mutabilityOracle, Set<E> delegate) {
                this.mutabilityOracle = mutabilityOracle;
                this.delegate = delegate;
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
            public boolean contains(Object o) {
                return this.delegate.contains(o);
            }

            @Override
            public Iterator<E> iterator() {
                return new MutabilityAwareIterator<E>(this.mutabilityOracle, this.delegate.iterator());
            }

            @Override
            public Object[] toArray() {
                return this.delegate.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return this.delegate.toArray(a);
            }

            @Override
            public boolean add(E e) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.add(e);
            }

            @Override
            public boolean remove(Object o) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.remove(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return this.delegate.containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends E> c) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.addAll(c);
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.retainAll(c);
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.removeAll(c);
            }

            @Override
            public void clear() {
                this.mutabilityOracle.ensureMutable();
                this.delegate.clear();
            }

            @Override
            public boolean equals(Object o) {
                return this.delegate.equals(o);
            }

            @Override
            public int hashCode() {
                return this.delegate.hashCode();
            }

            public String toString() {
                return this.delegate.toString();
            }
        }

        private static class MutabilityAwareCollection<E>
        implements Collection<E> {
            private final MutabilityOracle mutabilityOracle;
            private final Collection<E> delegate;

            MutabilityAwareCollection(MutabilityOracle mutabilityOracle, Collection<E> delegate) {
                this.mutabilityOracle = mutabilityOracle;
                this.delegate = delegate;
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
            public boolean contains(Object o) {
                return this.delegate.contains(o);
            }

            @Override
            public Iterator<E> iterator() {
                return new MutabilityAwareIterator<E>(this.mutabilityOracle, this.delegate.iterator());
            }

            @Override
            public Object[] toArray() {
                return this.delegate.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return this.delegate.toArray(a);
            }

            @Override
            public boolean add(E e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.remove(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return this.delegate.containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends E> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.removeAll(c);
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                this.mutabilityOracle.ensureMutable();
                return this.delegate.retainAll(c);
            }

            @Override
            public void clear() {
                this.mutabilityOracle.ensureMutable();
                this.delegate.clear();
            }

            @Override
            public boolean equals(Object o) {
                return this.delegate.equals(o);
            }

            @Override
            public int hashCode() {
                return this.delegate.hashCode();
            }

            public String toString() {
                return this.delegate.toString();
            }
        }

        private static class MutabilityAwareIterator<E>
        implements Iterator<E> {
            private final MutabilityOracle mutabilityOracle;
            private final Iterator<E> delegate;

            MutabilityAwareIterator(MutabilityOracle mutabilityOracle, Iterator<E> delegate) {
                this.mutabilityOracle = mutabilityOracle;
                this.delegate = delegate;
            }

            @Override
            public boolean hasNext() {
                return this.delegate.hasNext();
            }

            @Override
            public E next() {
                return this.delegate.next();
            }

            @Override
            public void remove() {
                this.mutabilityOracle.ensureMutable();
                this.delegate.remove();
            }

            public boolean equals(Object obj) {
                return this.delegate.equals(obj);
            }

            public int hashCode() {
                return this.delegate.hashCode();
            }

            public String toString() {
                return this.delegate.toString();
            }
        }
    }

    private static class ImmutableMessageConverter<K, V>
    implements Converter<K, V> {
        private final MapEntry<K, V> defaultEntry;

        public ImmutableMessageConverter(MapEntry<K, V> defaultEntry) {
            this.defaultEntry = defaultEntry;
        }

        @Override
        public Message convertKeyAndValueToMessage(K key, V value) {
            return ((MapEntry.Builder)this.defaultEntry.newBuilderForType()).setKey(key).setValue(value).buildPartial();
        }

        @Override
        public void convertMessageToKeyAndValue(Message message, Map<K, V> map) {
            MapEntry entry = (MapEntry)message;
            map.put(entry.getKey(), entry.getValue());
        }

        @Override
        public Message getMessageDefaultInstance() {
            return this.defaultEntry;
        }
    }
}

