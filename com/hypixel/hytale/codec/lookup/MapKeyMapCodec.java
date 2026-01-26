/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.lookup;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.lookup.AMapProvidedMapCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class MapKeyMapCodec<V>
extends AMapProvidedMapCodec<Class<? extends V>, V, Codec<V>, TypeMap<V>> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Set<Reference<TypeMap<?>>> ACTIVE_MAPS = ConcurrentHashMap.newKeySet();
    private static final ReferenceQueue<TypeMap<?>> MAP_REFERENCE_QUEUE = new ReferenceQueue();
    private static final StampedLock DATA_LOCK = new StampedLock();
    protected final Map<String, Class<? extends V>> idToClass = new ConcurrentHashMap<String, Class<? extends V>>();
    protected final Map<Class<? extends V>, String> classToId = new ConcurrentHashMap<Class<? extends V>, String>();

    public MapKeyMapCodec() {
        this(true);
    }

    public MapKeyMapCodec(boolean unmodifiable) {
        super(new ConcurrentHashMap(), Function.identity(), unmodifiable);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends V> void register(@Nonnull Class<T> tClass, @Nonnull String id, @Nonnull Codec<T> codec) {
        long lock = DATA_LOCK.writeLock();
        try {
            if (this.codecProvider.put(tClass, codec) != null) {
                throw new IllegalArgumentException("Id already registered");
            }
            if (this.idToClass.put(id, tClass) != null) {
                throw new IllegalArgumentException("Id already registered");
            }
            if (this.classToId.put(tClass, id) != null) {
                throw new IllegalArgumentException("Class already registered");
            }
            for (Reference<TypeMap<?>> mapRef : ACTIVE_MAPS) {
                TypeMap<?> map = mapRef.get();
                if (map == null || map.codec != this) continue;
                map.tryUpgrade(tClass, id, codec);
            }
        }
        finally {
            DATA_LOCK.unlockWrite(lock);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T extends V> void unregister(@Nonnull Class<T> tClass) {
        long lock = DATA_LOCK.writeLock();
        try {
            Codec codec = (Codec)this.codecProvider.get(tClass);
            if (codec == null) {
                throw new IllegalStateException(String.valueOf(tClass) + " not registered");
            }
            String id = this.classToId.get(tClass);
            for (Reference<TypeMap<?>> mapRef : ACTIVE_MAPS) {
                TypeMap<?> map = mapRef.get();
                if (map == null || map.codec != this) continue;
                map.tryDowngrade(tClass, id, codec);
            }
            this.codecProvider.remove(tClass);
            this.classToId.remove(tClass);
            this.idToClass.remove(id);
        }
        finally {
            DATA_LOCK.unlockWrite(lock);
        }
    }

    @Nullable
    @Deprecated(forRemoval=true)
    public V decodeById(@Nonnull String id, BsonValue value, ExtraInfo extraInfo) {
        Codec codec = (Codec)this.codecProvider.get(this.getKeyForId(id));
        return (V)codec.decode(value, extraInfo);
    }

    @Override
    protected String getIdForKey(Class<? extends V> key) {
        return this.classToId.get(key);
    }

    @Override
    @Nonnull
    public TypeMap<V> createMap() {
        return new TypeMap(this);
    }

    @Override
    public void handleUnknown(@Nonnull TypeMap<V> map, @Nonnull String key, BsonValue value, @Nonnull ExtraInfo extraInfo) {
        extraInfo.addUnknownKey(key);
        map.unknownValues.put(key, value);
    }

    @Override
    public void handleUnknown(@Nonnull TypeMap<V> map, @Nonnull String key, @Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        extraInfo.addUnknownKey(key);
        map.unknownValues.put(key, RawJsonReader.readBsonValue(reader));
    }

    @Override
    protected void encodeExtra(@Nonnull BsonDocument document, @Nonnull TypeMap<V> map, ExtraInfo extraInfo) {
        document.putAll((Map<? extends String, ? extends BsonValue>)map.unknownValues);
    }

    @Override
    public Class<? extends V> getKeyForId(String id) {
        return this.idToClass.get(id);
    }

    @Override
    @Nonnull
    protected TypeMap<V> emptyMap() {
        return TypeMap.EMPTY;
    }

    @Override
    @Nonnull
    protected TypeMap<V> unmodifiableMap(@Nonnull TypeMap<V> m) {
        return new TypeMap(this, Collections.unmodifiableMap(m.map), m.map, m.unknownValues);
    }

    static {
        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    ACTIVE_MAPS.remove(MAP_REFERENCE_QUEUE.remove());
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "MapKeyMapCodec");
        thread.setDaemon(true);
        thread.start();
    }

    public static class TypeMap<V>
    implements Map<Class<? extends V>, V> {
        private static final TypeMap EMPTY = new TypeMap(null, Collections.emptyMap(), Collections.emptyMap());
        private final MapKeyMapCodec<V> codec;
        @Nonnull
        private final Map<Class<? extends V>, V> map;
        @Nonnull
        private final Map<Class<? extends V>, V> internalMap;
        @Nonnull
        private final Map<String, BsonValue> unknownValues;

        public TypeMap(MapKeyMapCodec<V> codec) {
            this(codec, new Object2ObjectOpenHashMap(), new Object2ObjectOpenHashMap<String, BsonValue>());
        }

        public TypeMap(MapKeyMapCodec<V> codec, @Nonnull Map<Class<? extends V>, V> map, @Nonnull Map<String, BsonValue> unknownValues) {
            this(codec, map, map, unknownValues);
        }

        public TypeMap(MapKeyMapCodec<V> codec, @Nonnull Map<Class<? extends V>, V> map, @Nonnull Map<Class<? extends V>, V> internalMap, @Nonnull Map<String, BsonValue> unknownValues) {
            this.codec = codec;
            this.map = map;
            this.internalMap = internalMap;
            this.unknownValues = unknownValues;
            ACTIVE_MAPS.add(new WeakReference(this, MAP_REFERENCE_QUEUE));
        }

        public <T extends V> void tryUpgrade(@Nonnull Class<T> tClass, @Nonnull String id, @Nonnull Codec<T> codec) {
            BsonValue unknownValue = this.unknownValues.remove(id);
            if (unknownValue == null) {
                return;
            }
            T value = codec.decode(unknownValue, EmptyExtraInfo.EMPTY);
            this.internalMap.put(tClass, value);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("Upgrade " + id + " from unknown value");
        }

        public <T extends V> void tryDowngrade(@Nonnull Class<T> tClass, @Nonnull String id, @Nonnull Codec<T> codec) {
            V value = this.internalMap.remove(tClass);
            if (value == null) {
                return;
            }
            BsonValue encoded = codec.encode(value, EmptyExtraInfo.EMPTY);
            this.unknownValues.put(id, encoded);
            ((HytaleLogger.Api)LOGGER.atInfo()).log("Downgraded " + id + " to unknown value");
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.map.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return this.map.get(key);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public <T extends V> T get(Class<? extends T> key) {
            long lock = DATA_LOCK.readLock();
            try {
                V v = this.map.get(key);
                return (T)v;
            }
            finally {
                DATA_LOCK.unlockRead(lock);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V put(@Nonnull Class<? extends V> key, V value) {
            long lock = DATA_LOCK.readLock();
            try {
                if (!key.isInstance(value)) {
                    throw new IllegalArgumentException("Passed value '" + String.valueOf(value) + "' isn't of type: " + String.valueOf(key));
                }
                V v = this.map.put(key, value);
                return v;
            }
            finally {
                DATA_LOCK.unlockRead(lock);
            }
        }

        @Override
        public V remove(Object key) {
            return this.map.remove(key);
        }

        @Override
        public void putAll(@Nonnull Map<? extends Class<? extends V>, ? extends V> m) {
            for (Map.Entry<Class<V>, V> e : m.entrySet()) {
                this.put(e.getKey(), e.getValue());
            }
        }

        @Override
        public void clear() {
            this.map.clear();
        }

        @Override
        @Nonnull
        public Set<Class<? extends V>> keySet() {
            return this.map.keySet();
        }

        @Override
        @Nonnull
        public Collection<V> values() {
            return this.map.values();
        }

        @Override
        @Nonnull
        public Set<Map.Entry<Class<? extends V>, V>> entrySet() {
            return this.map.entrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T extends V> T computeIfAbsent(Class<? extends T> key, @Nonnull Function<? super Class<? extends V>, T> mappingFunction) {
            long lock = DATA_LOCK.readLock();
            try {
                V v = this.map.computeIfAbsent(key, mappingFunction);
                return (T)v;
            }
            finally {
                DATA_LOCK.unlockRead(lock);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Map)) {
                return false;
            }
            return this.entrySet().equals(((Map)o).entrySet());
        }

        @Override
        public int hashCode() {
            return this.map.hashCode();
        }

        @Nonnull
        public String toString() {
            return "TypeMap{map=" + String.valueOf(this.map) + "}";
        }

        public static <V> TypeMap<V> empty() {
            return EMPTY;
        }
    }
}

