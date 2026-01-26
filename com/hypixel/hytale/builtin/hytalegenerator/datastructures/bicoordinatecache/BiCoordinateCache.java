/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures.bicoordinatecache;

public interface BiCoordinateCache<T> {
    public T get(int var1, int var2);

    public boolean isCached(int var1, int var2);

    public T save(int var1, int var2, T var3);

    public void flush(int var1, int var2);

    public void flush();

    public int size();
}

