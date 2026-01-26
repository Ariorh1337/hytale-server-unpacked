/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.provider;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IChunkStorageProvider;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class MigrationChunkStorageProvider
implements IChunkStorageProvider {
    public static final String ID = "Migration";
    @Nonnull
    public static final BuilderCodec<MigrationChunkStorageProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MigrationChunkStorageProvider.class, MigrationChunkStorageProvider::new).documentation("A provider that combines multiple storage providers in a chain to assist with migrating worlds between storage formats.\n\nCan also be used to set storage to load chunks but block saving them if combined with the **Empty** storage provider")).append(new KeyedCodec<T[]>("Loaders", new ArrayCodec<IChunkStorageProvider>(IChunkStorageProvider.CODEC, IChunkStorageProvider[]::new)), (migration, o) -> {
        migration.from = o;
    }, migration -> migration.from).documentation("A list of storage providers to use as chunk loaders.\n\nEach loader will be tried in order to load a chunk, returning the chunk if found otherwise trying the next loaded until found or none are left.").add()).append(new KeyedCodec<IChunkStorageProvider>("Saver", IChunkStorageProvider.CODEC), (migration, o) -> {
        migration.to = o;
    }, migration -> migration.to).documentation("The storage provider to use to save chunks.").add()).build();
    private IChunkStorageProvider[] from;
    private IChunkStorageProvider to;

    public MigrationChunkStorageProvider() {
    }

    public MigrationChunkStorageProvider(@Nonnull IChunkStorageProvider[] from, @Nonnull IChunkStorageProvider to) {
        this.from = from;
        this.to = to;
    }

    @Override
    @Nonnull
    public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) throws IOException {
        IChunkLoader[] loaders = new IChunkLoader[this.from.length];
        for (int i = 0; i < this.from.length; ++i) {
            loaders[i] = this.from[i].getLoader(store);
        }
        return new MigrationChunkLoader(loaders);
    }

    @Override
    @Nonnull
    public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) throws IOException {
        return this.to.getSaver(store);
    }

    @Nonnull
    public String toString() {
        return "MigrationChunkStorageProvider{from=" + Arrays.toString(this.from) + ", to=" + String.valueOf(this.to) + "}";
    }

    public static class MigrationChunkLoader
    implements IChunkLoader {
        @Nonnull
        private final IChunkLoader[] loaders;

        public MigrationChunkLoader(IChunkLoader ... loaders) {
            this.loaders = loaders;
        }

        @Override
        public void close() throws IOException {
            Throwable exception = null;
            for (IChunkLoader loader : this.loaders) {
                try {
                    loader.close();
                }
                catch (Exception e) {
                    if (exception == null) {
                        exception = new IOException("Failed to close one or more loaders!");
                    }
                    exception.addSuppressed(e);
                }
            }
            if (exception != null) {
                throw exception;
            }
        }

        @Override
        @Nonnull
        public CompletableFuture<Holder<ChunkStore>> loadHolder(int x, int z) {
            CompletionStage<Holder<ChunkStore>> future = this.loaders[0].loadHolder(x, z);
            for (int i = 1; i < this.loaders.length; ++i) {
                IChunkLoader loader = this.loaders[i];
                CompletableFuture<Holder<ChunkStore>> previous = future;
                future = ((CompletableFuture)previous.handle((worldChunk, throwable) -> {
                    if (throwable != null) {
                        return loader.loadHolder(x, z).exceptionally(throwable1 -> {
                            throwable1.addSuppressed((Throwable)throwable);
                            throw SneakyThrow.sneakyThrow(throwable1);
                        });
                    }
                    if (worldChunk == null) {
                        return loader.loadHolder(x, z);
                    }
                    return previous;
                })).thenCompose(Function.identity());
            }
            return future;
        }

        @Override
        @Nonnull
        public LongSet getIndexes() throws IOException {
            LongOpenHashSet indexes = new LongOpenHashSet();
            for (IChunkLoader loader : this.loaders) {
                indexes.addAll(loader.getIndexes());
            }
            return indexes;
        }
    }
}

