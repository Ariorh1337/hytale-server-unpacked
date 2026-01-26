/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.util.cache;

import javax.annotation.Nullable;

public interface Cache<K, V> {
    public void shutdown();

    public void cleanup();

    @Nullable
    public V get(K var1);
}

