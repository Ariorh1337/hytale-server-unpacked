/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.provider;

import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.system.StoreSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.metrics.MetricProvider;
import com.hypixel.hytale.metrics.MetricResults;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IChunkStorageProvider;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import com.hypixel.hytale.storage.IndexedStorageFile;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IndexedStorageChunkStorageProvider
implements IChunkStorageProvider {
    public static final String ID = "IndexedStorage";
    @Nonnull
    public static final BuilderCodec<IndexedStorageChunkStorageProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IndexedStorageChunkStorageProvider.class, IndexedStorageChunkStorageProvider::new).documentation("Uses the indexed storage file format to store chunks.")).appendInherited(new KeyedCodec<Boolean>("FlushOnWrite", Codec.BOOLEAN), (o, i) -> {
        o.flushOnWrite = i;
    }, o -> o.flushOnWrite, (o, p) -> {
        o.flushOnWrite = p.flushOnWrite;
    }).documentation("Controls whether the indexed storage flushes during writes.\nRecommended to be enabled to prevent corruption of chunks during unclean shutdowns.").add()).build();
    private boolean flushOnWrite = false;

    @Override
    @Nonnull
    public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) {
        return new IndexedStorageChunkLoader(store, this.flushOnWrite);
    }

    @Override
    @Nonnull
    public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) {
        return new IndexedStorageChunkSaver(store, this.flushOnWrite);
    }

    @Nonnull
    public String toString() {
        return "IndexedStorageChunkStorageProvider{}";
    }

    @Nonnull
    private static String toFileName(int regionX, int regionZ) {
        return regionX + "." + regionZ + ".region.bin";
    }

    private static long fromFileName(@Nonnull String fileName) {
        String[] split = fileName.split("\\.");
        if (split.length != 4) {
            throw new IllegalArgumentException("Unexpected file name format!");
        }
        if (!"region".equals(split[2])) {
            throw new IllegalArgumentException("Unexpected file name format!");
        }
        if (!"bin".equals(split[3])) {
            throw new IllegalArgumentException("Unexpected file extension!");
        }
        int regionX = Integer.parseInt(split[0]);
        int regionZ = Integer.parseInt(split[1]);
        return ChunkUtil.indexChunk(regionX, regionZ);
    }

    public static class IndexedStorageChunkLoader
    extends BufferChunkLoader
    implements MetricProvider {
        private final boolean flushOnWrite;

        public IndexedStorageChunkLoader(@Nonnull Store<ChunkStore> store, boolean flushOnWrite) {
            super(store);
            this.flushOnWrite = flushOnWrite;
        }

        @Override
        public void close() throws IOException {
            this.getStore().getResource(IndexedStorageCache.getResourceType()).close();
        }

        @Override
        @Nonnull
        public CompletableFuture<ByteBuffer> loadBuffer(int x, int z) {
            int regionX = x >> 5;
            int regionZ = z >> 5;
            int localX = x & 0x1F;
            int localZ = z & 0x1F;
            int index = ChunkUtil.indexColumn(localX, localZ);
            IndexedStorageCache indexedStorageCache = this.getStore().getResource(IndexedStorageCache.getResourceType());
            return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
                IndexedStorageFile chunks = indexedStorageCache.getOrTryOpen(regionX, regionZ, this.flushOnWrite);
                if (chunks == null) {
                    return null;
                }
                return chunks.readBlob(index);
            }));
        }

        @Override
        @Nonnull
        public LongSet getIndexes() throws IOException {
            return this.getStore().getResource(IndexedStorageCache.getResourceType()).getIndexes();
        }

        @Override
        @Nullable
        public MetricResults toMetricResults() {
            if (this.getStore().getExternalData().getSaver() instanceof IndexedStorageChunkSaver) {
                return null;
            }
            return this.getStore().getResource(IndexedStorageCache.getResourceType()).toMetricResults();
        }
    }

    public static class IndexedStorageChunkSaver
    extends BufferChunkSaver
    implements MetricProvider {
        private final boolean flushOnWrite;

        protected IndexedStorageChunkSaver(@Nonnull Store<ChunkStore> store, boolean flushOnWrite) {
            super(store);
            this.flushOnWrite = flushOnWrite;
        }

        @Override
        public void close() throws IOException {
            IndexedStorageCache indexedStorageCache = this.getStore().getResource(IndexedStorageCache.getResourceType());
            indexedStorageCache.close();
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> saveBuffer(int x, int z, @Nonnull ByteBuffer buffer) {
            int regionX = x >> 5;
            int regionZ = z >> 5;
            int localX = x & 0x1F;
            int localZ = z & 0x1F;
            int index = ChunkUtil.indexColumn(localX, localZ);
            IndexedStorageCache indexedStorageCache = this.getStore().getResource(IndexedStorageCache.getResourceType());
            return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
                IndexedStorageFile chunks = indexedStorageCache.getOrCreate(regionX, regionZ, this.flushOnWrite);
                chunks.writeBlob(index, buffer);
            }));
        }

        @Override
        @Nonnull
        public CompletableFuture<Void> removeBuffer(int x, int z) {
            int regionX = x >> 5;
            int regionZ = z >> 5;
            int localX = x & 0x1F;
            int localZ = z & 0x1F;
            int index = ChunkUtil.indexColumn(localX, localZ);
            IndexedStorageCache indexedStorageCache = this.getStore().getResource(IndexedStorageCache.getResourceType());
            return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
                IndexedStorageFile chunks = indexedStorageCache.getOrTryOpen(regionX, regionZ, this.flushOnWrite);
                if (chunks != null) {
                    chunks.removeBlob(index);
                }
            }));
        }

        @Override
        @Nonnull
        public LongSet getIndexes() throws IOException {
            return this.getStore().getResource(IndexedStorageCache.getResourceType()).getIndexes();
        }

        @Override
        public void flush() throws IOException {
            this.getStore().getResource(IndexedStorageCache.getResourceType()).flush();
        }

        @Override
        public MetricResults toMetricResults() {
            return this.getStore().getResource(IndexedStorageCache.getResourceType()).toMetricResults();
        }
    }

    public static class IndexedStorageCacheSetupSystem
    extends StoreSystem<ChunkStore> {
        @Override
        @Nullable
        public SystemGroup<ChunkStore> getGroup() {
            return ChunkStore.INIT_GROUP;
        }

        @Override
        public void onSystemAddedToStore(@Nonnull Store<ChunkStore> store) {
            World world = store.getExternalData().getWorld();
            store.getResource(IndexedStorageCache.getResourceType()).path = world.getSavePath().resolve("chunks");
        }

        @Override
        public void onSystemRemovedFromStore(@Nonnull Store<ChunkStore> store) {
        }
    }

    public static class IndexedStorageCache
    implements Closeable,
    MetricProvider,
    Resource<ChunkStore> {
        @Nonnull
        public static final MetricsRegistry<IndexedStorageCache> METRICS_REGISTRY = new MetricsRegistry<IndexedStorageCache>().register("Files", cache -> (CacheEntryMetricData[])cache.cache.long2ObjectEntrySet().stream().map(CacheEntryMetricData::new).toArray(CacheEntryMetricData[]::new), new ArrayCodec<CacheEntryMetricData>(CacheEntryMetricData.CODEC, CacheEntryMetricData[]::new));
        private final Long2ObjectConcurrentHashMap<IndexedStorageFile> cache = new Long2ObjectConcurrentHashMap(true, ChunkUtil.NOT_FOUND);
        private Path path;

        public static ResourceType<ChunkStore, IndexedStorageCache> getResourceType() {
            return Universe.get().getIndexedStorageCacheResourceType();
        }

        @Nonnull
        public Long2ObjectConcurrentHashMap<IndexedStorageFile> getCache() {
            return this.cache;
        }

        @Override
        public void close() throws IOException {
            Throwable exception = null;
            Iterator iterator = this.cache.values().iterator();
            while (iterator.hasNext()) {
                try {
                    ((IndexedStorageFile)iterator.next()).close();
                    iterator.remove();
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

        @Nullable
        public IndexedStorageFile getOrTryOpen(int regionX, int regionZ, boolean flushOnWrite) {
            return this.cache.computeIfAbsent(ChunkUtil.indexChunk(regionX, regionZ), k -> {
                Path regionFile = this.path.resolve(IndexedStorageChunkStorageProvider.toFileName(regionX, regionZ));
                if (!Files.exists(regionFile, new LinkOption[0])) {
                    return null;
                }
                try {
                    IndexedStorageFile open = IndexedStorageFile.open(regionFile, StandardOpenOption.READ, StandardOpenOption.WRITE);
                    open.setFlushOnWrite(flushOnWrite);
                    return open;
                }
                catch (FileNotFoundException e) {
                    return null;
                }
                catch (IOException e) {
                    throw SneakyThrow.sneakyThrow(e);
                }
            });
        }

        @Nonnull
        public IndexedStorageFile getOrCreate(int regionX, int regionZ, boolean flushOnWrite) {
            return this.cache.computeIfAbsent(ChunkUtil.indexChunk(regionX, regionZ), k -> {
                try {
                    if (!Files.exists(this.path, new LinkOption[0])) {
                        try {
                            Files.createDirectory(this.path, new FileAttribute[0]);
                        }
                        catch (FileAlreadyExistsException fileAlreadyExistsException) {
                            // empty catch block
                        }
                    }
                    Path regionFile = this.path.resolve(IndexedStorageChunkStorageProvider.toFileName(regionX, regionZ));
                    IndexedStorageFile open = IndexedStorageFile.open(regionFile, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
                    open.setFlushOnWrite(flushOnWrite);
                    return open;
                }
                catch (IOException e) {
                    throw SneakyThrow.sneakyThrow(e);
                }
            });
        }

        @Nonnull
        public LongSet getIndexes() throws IOException {
            if (!Files.exists(this.path, new LinkOption[0])) {
                return LongSets.EMPTY_SET;
            }
            LongOpenHashSet chunkIndexes = new LongOpenHashSet();
            try (Stream<Path> stream = Files.list(this.path);){
                stream.forEach(path -> {
                    long regionIndex;
                    if (Files.isDirectory(path, new LinkOption[0])) {
                        return;
                    }
                    try {
                        regionIndex = IndexedStorageChunkStorageProvider.fromFileName(path.getFileName().toString());
                    }
                    catch (IllegalArgumentException e) {
                        return;
                    }
                    int regionX = ChunkUtil.xOfChunkIndex(regionIndex);
                    int regionZ = ChunkUtil.zOfChunkIndex(regionIndex);
                    IndexedStorageFile regionFile = this.getOrTryOpen(regionX, regionZ, true);
                    if (regionFile == null) {
                        return;
                    }
                    IntList blobIndexes = regionFile.keys();
                    IntListIterator iterator = blobIndexes.iterator();
                    while (iterator.hasNext()) {
                        int blobIndex = iterator.nextInt();
                        int localX = ChunkUtil.xFromColumn(blobIndex);
                        int localZ = ChunkUtil.zFromColumn(blobIndex);
                        int chunkX = regionX << 5 | localX;
                        int chunkZ = regionZ << 5 | localZ;
                        chunkIndexes.add(ChunkUtil.indexChunk(chunkX, chunkZ));
                    }
                });
            }
            return chunkIndexes;
        }

        public void flush() throws IOException {
            Throwable exception = null;
            for (IndexedStorageFile indexedStorageFile : this.cache.values()) {
                try {
                    indexedStorageFile.force(false);
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
        public MetricResults toMetricResults() {
            return METRICS_REGISTRY.toMetricResults(this);
        }

        @Override
        @Nonnull
        public Resource<ChunkStore> clone() {
            return new IndexedStorageCache();
        }

        private static class CacheEntryMetricData {
            @Nonnull
            private static final Codec<CacheEntryMetricData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CacheEntryMetricData.class, CacheEntryMetricData::new).append(new KeyedCodec<Long>("Key", Codec.LONG), (entry, o) -> {
                entry.key = o;
            }, entry -> entry.key).add()).append(new KeyedCodec<IndexedStorageFile>("File", IndexedStorageFile.METRICS_REGISTRY), (entry, o) -> {
                entry.value = o;
            }, entry -> entry.value).add()).build();
            private long key;
            private IndexedStorageFile value;

            public CacheEntryMetricData() {
            }

            public CacheEntryMetricData(@Nonnull Long2ObjectMap.Entry<IndexedStorageFile> entry) {
                this.key = entry.getLongKey();
                this.value = (IndexedStorageFile)entry.getValue();
            }
        }
    }
}

