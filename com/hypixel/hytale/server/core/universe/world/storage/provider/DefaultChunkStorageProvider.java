/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.provider;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IChunkStorageProvider;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IndexedStorageChunkStorageProvider;
import java.io.IOException;
import javax.annotation.Nonnull;

public class DefaultChunkStorageProvider
implements IChunkStorageProvider {
    @Nonnull
    public static final DefaultChunkStorageProvider INSTANCE = new DefaultChunkStorageProvider();
    public static final String ID = "Hytale";
    @Nonnull
    public static final BuilderCodec<DefaultChunkStorageProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DefaultChunkStorageProvider.class, () -> INSTANCE).documentation("Selects the default recommended storage as decided by the server.")).build();
    @Nonnull
    public static final IChunkStorageProvider DEFAULT = new IndexedStorageChunkStorageProvider();

    @Override
    @Nonnull
    public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) throws IOException {
        return DEFAULT.getLoader(store);
    }

    @Override
    @Nonnull
    public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) throws IOException {
        return DEFAULT.getSaver(store);
    }

    @Nonnull
    public String toString() {
        return "DefaultChunkStorageProvider{DEFAULT=" + String.valueOf(DEFAULT) + "}";
    }
}

