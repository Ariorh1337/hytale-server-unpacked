/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures.bicoordinatecache;

import com.hypixel.hytale.builtin.hytalegenerator.datastructures.bicoordinatecache.BiCoordinateCache;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public class HashedBiCoordinateCache<T>
implements BiCoordinateCache<T> {
    @Nonnull
    private final ConcurrentHashMap<Long, T> values = new ConcurrentHashMap();

    public static long hash(int x, int z) {
        long hash = x;
        hash <<= 32;
        return hash += (long)z;
    }

    @Override
    public T get(int x, int z) {
        long key = HashedBiCoordinateCache.hash(x, z);
        if (!this.values.containsKey(key)) {
            throw new IllegalStateException("doesn't contain coordinates");
        }
        return this.values.get(key);
    }

    @Override
    public boolean isCached(int x, int z) {
        return this.values.containsKey(HashedBiCoordinateCache.hash(x, z));
    }

    @Override
    @Nonnull
    public T save(int x, int z, @Nonnull T value) {
        long key = HashedBiCoordinateCache.hash(x, z);
        this.values.put(key, value);
        return value;
    }

    @Override
    public void flush(int x, int z) {
        long key = HashedBiCoordinateCache.hash(x, z);
        if (!this.values.containsKey(key)) {
            return;
        }
        this.values.remove(key);
    }

    @Override
    public void flush() {
        Iterator iterator = ((ConcurrentHashMap.KeySetView)this.values.keySet()).iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            this.values.remove(key);
        }
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Nonnull
    public String toString() {
        return "HashedBiCoordinateCache{values=" + String.valueOf(this.values) + "}";
    }
}

