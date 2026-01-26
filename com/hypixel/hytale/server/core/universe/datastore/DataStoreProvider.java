/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.datastore;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
import com.hypixel.hytale.server.core.universe.datastore.DataStore;

public interface DataStoreProvider {
    public static final BuilderCodecMapCodec<DataStoreProvider> CODEC = new BuilderCodecMapCodec("Type");

    public <T> DataStore<T> create(BuilderCodec<T> var1);
}

