/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.datastore;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.universe.datastore.DataStore;
import com.hypixel.hytale.server.core.universe.datastore.DataStoreProvider;
import com.hypixel.hytale.server.core.universe.datastore.DiskDataStore;
import javax.annotation.Nonnull;

public class DiskDataStoreProvider
implements DataStoreProvider {
    public static final String ID = "Disk";
    public static final BuilderCodec<DiskDataStoreProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DiskDataStoreProvider.class, DiskDataStoreProvider::new).append(new KeyedCodec<String>("Path", Codec.STRING), (diskDataStoreProvider, s) -> {
        diskDataStoreProvider.path = s;
    }, diskDataStoreProvider -> diskDataStoreProvider.path).addValidator(Validators.nonNull()).add()).build();
    private String path;

    public DiskDataStoreProvider(String path) {
        this.path = path;
    }

    protected DiskDataStoreProvider() {
    }

    @Override
    @Nonnull
    public <T> DataStore<T> create(BuilderCodec<T> builderCodec) {
        return new DiskDataStore<T>(this.path, builderCodec);
    }

    @Nonnull
    public String toString() {
        return "DiskDataStoreProvider{path='" + this.path + "'}";
    }
}

