/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.meta;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.meta.IMetaRegistry;
import com.hypixel.hytale.server.core.meta.IMetaStore;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.meta.PersistentMetaKey;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MetaRegistry<K>
implements IMetaRegistry<K> {
    private final Map<String, MetaRegistryEntry> parameterMapping = new Object2ObjectOpenHashMap<String, MetaRegistryEntry>();
    private final List<MetaRegistryEntry> suppliers = new ObjectArrayList<MetaRegistryEntry>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> MetaKey<T> registerMetaObject(Function<K, T> function, boolean persistent, String keyName, @Nonnull Codec<T> codec) {
        this.lock.writeLock().lock();
        try {
            if (persistent && codec == null) {
                throw new IllegalStateException("Codec cannot be null if persistence is enabled.");
            }
            int metaId = this.suppliers.size();
            MetaKey key = persistent ? new PersistentMetaKey<T>(metaId, keyName, codec) : new MetaKey(metaId);
            MetaRegistryEntry<T> metaEntry = new MetaRegistryEntry<T>(this, function, key);
            this.suppliers.add(metaEntry);
            if (persistent) {
                if (this.parameterMapping.containsKey(keyName)) {
                    throw new IllegalStateException("Codec key is already registered. Given: " + keyName);
                }
                this.parameterMapping.put(keyName, metaEntry);
            }
            MetaKey<T> metaKey = metaEntry.getKey();
            return metaKey;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T newMetaObject(@Nonnull MetaKey<T> key, K parent) {
        this.lock.readLock().lock();
        try {
            Object t = this.suppliers.get(key.getId()).getFunction().apply(parent);
            return t;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void forEachMetaEntry(@Nonnull IMetaStore<K> store, final @Nonnull IMetaRegistry.MetaEntryConsumer consumer) {
        store.forEachMetaObject(new IMetaStore.MetaEntryConsumer(){
            final /* synthetic */ MetaRegistry this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public <T> void accept(int id, T value) {
                MetaRegistryEntry entry = this.this$0.suppliers.get(id);
                consumer.accept(entry.getKey(), value);
            }
        });
    }

    @Override
    @Nullable
    public PersistentMetaKey<?> getMetaKeyForCodecKey(String codecKey) {
        MetaRegistryEntry entry = this.parameterMapping.get(codecKey);
        if (entry == null) {
            return null;
        }
        return (PersistentMetaKey)entry.getKey();
    }

    private class MetaRegistryEntry<T> {
        private final Function<K, T> function;
        private final MetaKey<T> key;

        public MetaRegistryEntry(MetaRegistry metaRegistry, Function<K, T> function, MetaKey<T> key) {
            this.function = function;
            this.key = key;
        }

        public Function<K, T> getFunction() {
            return this.function;
        }

        public MetaKey<T> getKey() {
            return this.key;
        }
    }
}

