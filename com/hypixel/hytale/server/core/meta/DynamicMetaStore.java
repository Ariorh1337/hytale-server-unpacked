/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.meta;

import com.hypixel.hytale.server.core.meta.AbstractMetaStore;
import com.hypixel.hytale.server.core.meta.IMetaRegistry;
import com.hypixel.hytale.server.core.meta.IMetaStore;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.meta.PersistentMetaKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DynamicMetaStore<K>
extends AbstractMetaStore<K> {
    @Nonnull
    private final Int2ObjectMap<Object> map = new Int2ObjectOpenHashMap<Object>();

    public DynamicMetaStore(K parent, IMetaRegistry<K> registry) {
        this(parent, registry, false);
    }

    public DynamicMetaStore(K parent, IMetaRegistry<K> registry, boolean bypassEncodedCache) {
        super(parent, registry, bypassEncodedCache);
    }

    @Override
    protected <T> T get0(@Nonnull MetaKey<T> key) {
        return (T)this.map.get(key.getId());
    }

    @Override
    public <T> T getMetaObject(@Nonnull MetaKey<T> key) {
        T o = this.get0(key);
        if (o == null) {
            o = this.decodeOrNewMetaObject(key);
            this.map.put(key.getId(), (Object)o);
        }
        return o;
    }

    @Override
    public <T> T getIfPresentMetaObject(@Nonnull MetaKey<T> key) {
        return this.get0(key);
    }

    @Override
    public <T> T putMetaObject(@Nonnull MetaKey<T> key, T obj) {
        this.markMetaStoreDirty();
        return this.map.put(key.getId(), (Object)obj);
    }

    @Override
    public <T> T removeMetaObject(@Nonnull MetaKey<T> key) {
        this.markMetaStoreDirty();
        return (T)this.map.remove(key.getId());
    }

    @Override
    @Nullable
    public <T> T removeSerializedMetaObject(MetaKey<T> key) {
        this.markMetaStoreDirty();
        if (key instanceof PersistentMetaKey) {
            this.tryDecodeUnknownKey((PersistentMetaKey)key);
        }
        return this.removeMetaObject(key);
    }

    @Override
    public boolean hasMetaObject(@Nonnull MetaKey<?> key) {
        return this.map.containsKey(key.getId());
    }

    @Override
    public void forEachMetaObject(@Nonnull IMetaStore.MetaEntryConsumer consumer) {
        for (Int2ObjectMap.Entry entry : this.map.int2ObjectEntrySet()) {
            consumer.accept(entry.getIntKey(), entry.getValue());
        }
    }

    @Nonnull
    public DynamicMetaStore<K> clone(K parent) {
        DynamicMetaStore<K> clone = new DynamicMetaStore<K>(parent, this.registry);
        clone.map.putAll(this.map);
        return clone;
    }

    public void copyFrom(@Nonnull DynamicMetaStore<K> other) {
        this.markMetaStoreDirty();
        if (this.registry != other.registry) {
            throw new IllegalArgumentException("Wrong registry used in `copyFrom`.");
        }
        this.map.putAll(other.map);
    }
}

