/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.provider;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IChunkStorageProvider;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class EmptyChunkStorageProvider
implements IChunkStorageProvider {
    public static final String ID = "Empty";
    @Nonnull
    public static final EmptyChunkStorageProvider INSTANCE = new EmptyChunkStorageProvider();
    @Nonnull
    public static final BuilderCodec<EmptyChunkStorageProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(EmptyChunkStorageProvider.class, () -> INSTANCE).documentation("A chunk storage provider that discards any chunks to save and will always fail to find chunks.")).build();
    @Nonnull
    public static final EmptyChunkLoader EMPTY_CHUNK_LOADER = new EmptyChunkLoader();
    @Nonnull
    public static final EmptyChunkSaver EMPTY_CHUNK_SAVER = new EmptyChunkSaver();

    @Override
    @Nonnull
    public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) {
        return EMPTY_CHUNK_LOADER;
    }

    @Override
    @Nonnull
    public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) {
        return EMPTY_CHUNK_SAVER;
    }

    @Nonnull
    public String toString() {
        return "EmptyChunkStorageProvider{}";
    }

    private static class EmptyChunkLoader
    implements IChunkLoader {
        private EmptyChunkLoader() {
        }

        @Override
        public void close() {
        }

        @Override
        @Nonnull
        public CompletableFuture<Holder<ChunkStore>> loadHolder(int x, int z) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        @Nonnull
        public LongSet getIndexes() {
            return LongSets.EMPTY_SET;
        }
    }

    private static class EmptyChunkSaver
    implements IChunkSaver {
        private EmptyChunkSaver() {
        }

        @Override
        public void close() {
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> saveHolder(int x, int z, @Nonnull Holder<ChunkStore> holder) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> removeHolder(int x, int z) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        @Nonnull
        public LongSet getIndexes() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public void flush() {
        }
    }
}

