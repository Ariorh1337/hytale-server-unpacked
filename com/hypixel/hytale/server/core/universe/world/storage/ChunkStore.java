/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage;

import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.store.CodecKey;
import com.hypixel.hytale.codec.store.CodecStore;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.IResourceStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.SystemType;
import com.hypixel.hytale.component.system.StoreSystem;
import com.hypixel.hytale.component.system.data.EntityDataSystem;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.metrics.MetricProvider;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldProvider;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import com.hypixel.hytale.server.core.universe.world.storage.component.ChunkSavingSystems;
import com.hypixel.hytale.server.core.universe.world.storage.component.ChunkUnloadingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.provider.IChunkStorageProvider;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkStore
implements WorldProvider {
    @Nonnull
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final MetricsRegistry<ChunkStore> METRICS_REGISTRY = new MetricsRegistry<ChunkStore>().register("Store", ChunkStore::getStore, Store.METRICS_REGISTRY).register("ChunkLoader", MetricProvider.maybe(ChunkStore::getLoader)).register("ChunkSaver", MetricProvider.maybe(ChunkStore::getSaver)).register("WorldGen", MetricProvider.maybe(ChunkStore::getGenerator)).register("TotalGeneratedChunkCount", chunkComponentStore -> chunkComponentStore.totalGeneratedChunksCount.get(), Codec.LONG).register("TotalLoadedChunkCount", chunkComponentStore -> chunkComponentStore.totalLoadedChunksCount.get(), Codec.LONG);
    public static final long MAX_FAILURE_BACKOFF_NANOS = TimeUnit.SECONDS.toNanos(10L);
    public static final long FAILURE_BACKOFF_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
    public static final ComponentRegistry<ChunkStore> REGISTRY = new ComponentRegistry();
    public static final CodecKey<Holder<ChunkStore>> HOLDER_CODEC_KEY = new CodecKey("ChunkHolder");
    @Nonnull
    public static final SystemType<ChunkStore, LoadPacketDataQuerySystem> LOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE;
    @Nonnull
    public static final SystemType<ChunkStore, LoadFuturePacketDataQuerySystem> LOAD_FUTURE_PACKETS_DATA_QUERY_SYSTEM_TYPE;
    @Nonnull
    public static final SystemType<ChunkStore, UnloadPacketDataQuerySystem> UNLOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE;
    @Nonnull
    public static final ResourceType<ChunkStore, ChunkUnloadingSystem.Data> UNLOAD_RESOURCE;
    @Nonnull
    public static final ResourceType<ChunkStore, ChunkSavingSystems.Data> SAVE_RESOURCE;
    public static final SystemGroup<ChunkStore> INIT_GROUP;
    @Nonnull
    private final World world;
    @Nonnull
    private final Long2ObjectConcurrentHashMap<ChunkLoadState> chunks = new Long2ObjectConcurrentHashMap(true, ChunkUtil.NOT_FOUND);
    private Store<ChunkStore> store;
    @Nullable
    private IChunkLoader loader;
    @Nullable
    private IChunkSaver saver;
    @Nullable
    private IWorldGen generator;
    @Nonnull
    private CompletableFuture<Void> generatorLoaded = new CompletableFuture();
    private final StampedLock generatorLock = new StampedLock();
    private final AtomicInteger totalGeneratedChunksCount = new AtomicInteger();
    private final AtomicInteger totalLoadedChunksCount = new AtomicInteger();

    public ChunkStore(@Nonnull World world) {
        this.world = world;
    }

    @Override
    @Nonnull
    public World getWorld() {
        return this.world;
    }

    @Nonnull
    public Store<ChunkStore> getStore() {
        return this.store;
    }

    @Nullable
    public IChunkLoader getLoader() {
        return this.loader;
    }

    @Nullable
    public IChunkSaver getSaver() {
        return this.saver;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public IWorldGen getGenerator() {
        long readStamp = this.generatorLock.readLock();
        try {
            IWorldGen iWorldGen = this.generator;
            return iWorldGen;
        }
        finally {
            this.generatorLock.unlockRead(readStamp);
        }
    }

    public void shutdownGenerator() {
        this.setGenerator(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setGenerator(@Nullable IWorldGen generator) {
        long writeStamp = this.generatorLock.writeLock();
        try {
            if (this.generator != null) {
                this.generator.shutdown();
            }
            this.totalGeneratedChunksCount.set(0);
            this.generator = generator;
            if (generator != null) {
                this.generatorLoaded.complete(null);
                this.generatorLoaded = new CompletableFuture();
            }
        }
        finally {
            this.generatorLock.unlockWrite(writeStamp);
        }
    }

    @Nonnull
    public LongSet getChunkIndexes() {
        return LongSets.unmodifiable(this.chunks.keySet());
    }

    public int getLoadedChunksCount() {
        return this.chunks.size();
    }

    public int getTotalGeneratedChunksCount() {
        return this.totalGeneratedChunksCount.get();
    }

    public int getTotalLoadedChunksCount() {
        return this.totalLoadedChunksCount.get();
    }

    public void start(@Nonnull IResourceStorage resourceStorage) {
        this.store = REGISTRY.addStore(this, resourceStorage, store -> {
            this.store = store;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void waitForLoadingChunks() {
        boolean hasLoadingChunks;
        long start = System.nanoTime();
        block3: do {
            this.world.consumeTaskQueue();
            Thread.yield();
            hasLoadingChunks = false;
            for (Long2ObjectMap.Entry entry : this.chunks.long2ObjectEntrySet()) {
                ChunkLoadState chunkState = (ChunkLoadState)entry.getValue();
                long stamp = chunkState.lock.readLock();
                try {
                    CompletableFuture<Ref<ChunkStore>> future = chunkState.future;
                    if (future == null || future.isDone()) continue;
                    hasLoadingChunks = true;
                    continue block3;
                }
                finally {
                    chunkState.lock.unlockRead(stamp);
                }
            }
        } while (hasLoadingChunks && System.nanoTime() - start <= 5000000000L);
        this.world.consumeTaskQueue();
    }

    public void shutdown() {
        this.store.shutdown();
        this.chunks.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    private Ref<ChunkStore> add(@Nonnull Holder<ChunkStore> holder) {
        Ref<ChunkStore> ref;
        this.world.debugAssertInTickingThread();
        WorldChunk worldChunkComponent = holder.getComponent(WorldChunk.getComponentType());
        assert (worldChunkComponent != null);
        ChunkLoadState chunkState = this.chunks.get(worldChunkComponent.getIndex());
        if (chunkState == null) {
            throw new IllegalStateException("Expected the ChunkLoadState to exist!");
        }
        Ref<ChunkStore> oldReference = null;
        long stamp = chunkState.lock.writeLock();
        try {
            if (chunkState.future == null) {
                throw new IllegalStateException("Expected the ChunkLoadState to have a future!");
            }
            if (chunkState.reference != null) {
                oldReference = chunkState.reference;
                chunkState.reference = null;
            }
        }
        finally {
            chunkState.lock.unlockWrite(stamp);
        }
        if (oldReference != null) {
            WorldChunk oldWorldChunkComponent = this.store.getComponent(oldReference, WorldChunk.getComponentType());
            assert (oldWorldChunkComponent != null);
            oldWorldChunkComponent.setFlag(ChunkFlag.TICKING, false);
            this.store.removeEntity(oldReference, RemoveReason.REMOVE);
            this.world.getNotificationHandler().updateChunk(worldChunkComponent.getIndex());
        }
        if ((ref = this.store.addEntity(holder, AddReason.SPAWN)) == null) {
            throw new UnsupportedOperationException("Unable to add the chunk to the world!");
        }
        worldChunkComponent.setReference(ref);
        stamp = chunkState.lock.writeLock();
        try {
            chunkState.reference = ref;
            chunkState.flags = 0;
            chunkState.future = null;
            chunkState.throwable = null;
            chunkState.failedWhen = 0L;
            chunkState.failedCounter = 0;
            Ref<ChunkStore> ref2 = ref;
            return ref2;
        }
        finally {
            chunkState.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(@Nonnull Ref<ChunkStore> reference, @Nonnull RemoveReason reason) {
        this.world.debugAssertInTickingThread();
        WorldChunk worldChunkComponent = this.store.getComponent(reference, WorldChunk.getComponentType());
        assert (worldChunkComponent != null);
        long index = worldChunkComponent.getIndex();
        ChunkLoadState chunkState = this.chunks.get(index);
        long stamp = chunkState.lock.readLock();
        try {
            worldChunkComponent.setFlag(ChunkFlag.TICKING, false);
            this.store.removeEntity(reference, reason);
            if (chunkState.future != null) {
                chunkState.reference = null;
            } else {
                this.chunks.remove(index, chunkState);
            }
        }
        finally {
            chunkState.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public Ref<ChunkStore> getChunkReference(long index) {
        ChunkLoadState chunkState = this.chunks.get(index);
        if (chunkState == null) {
            return null;
        }
        long stamp = chunkState.lock.tryOptimisticRead();
        Ref<ChunkStore> reference = chunkState.reference;
        if (chunkState.lock.validate(stamp)) {
            return reference;
        }
        stamp = chunkState.lock.readLock();
        try {
            Ref<ChunkStore> ref = chunkState.reference;
            return ref;
        }
        finally {
            chunkState.lock.unlockRead(stamp);
        }
    }

    @Nullable
    public Ref<ChunkStore> getChunkSectionReference(int x, int y, int z) {
        Ref<ChunkStore> ref = this.getChunkReference(ChunkUtil.indexChunk(x, z));
        if (ref == null) {
            return null;
        }
        ChunkColumn chunkColumnComponent = this.store.getComponent(ref, ChunkColumn.getComponentType());
        if (chunkColumnComponent == null) {
            return null;
        }
        return chunkColumnComponent.getSection(y);
    }

    @Nullable
    public Ref<ChunkStore> getChunkSectionReference(@Nonnull ComponentAccessor<ChunkStore> commandBuffer, int x, int y, int z) {
        Ref<ChunkStore> ref = this.getChunkReference(ChunkUtil.indexChunk(x, z));
        if (ref == null) {
            return null;
        }
        ChunkColumn chunkColumnComponent = commandBuffer.getComponent(ref, ChunkColumn.getComponentType());
        if (chunkColumnComponent == null) {
            return null;
        }
        return chunkColumnComponent.getSection(y);
    }

    @Nonnull
    public CompletableFuture<Ref<ChunkStore>> getChunkSectionReferenceAsync(int x, int y, int z) {
        if (y < 0 || y >= 10) {
            return CompletableFuture.failedFuture(new IndexOutOfBoundsException("Invalid y: " + y));
        }
        return this.getChunkReferenceAsync(ChunkUtil.indexChunk(x, z)).thenApplyAsync(ref -> {
            if (ref == null || !ref.isValid()) {
                return null;
            }
            Store<ChunkStore> store = ref.getStore();
            ChunkColumn chunkColumnComponent = store.getComponent((Ref<ChunkStore>)ref, ChunkColumn.getComponentType());
            if (chunkColumnComponent == null) {
                return null;
            }
            return chunkColumnComponent.getSection(y);
        }, (Executor)this.store.getExternalData().getWorld());
    }

    @Nullable
    public <T extends Component<ChunkStore>> T getChunkComponent(long index, @Nonnull ComponentType<ChunkStore, T> componentType) {
        Ref<ChunkStore> reference = this.getChunkReference(index);
        return reference == null || !reference.isValid() ? null : (T)this.store.getComponent(reference, componentType);
    }

    @Nonnull
    public CompletableFuture<Ref<ChunkStore>> getChunkReferenceAsync(long index) {
        return this.getChunkReferenceAsync(index, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public CompletableFuture<Ref<ChunkStore>> getChunkReferenceAsync(long index, int flags) {
        long stamp;
        ChunkLoadState chunkState;
        block35: {
            if (this.store.isShutdown()) {
                return CompletableFuture.completedFuture(null);
            }
            if ((flags & 3) == 3) {
                chunkState = this.chunks.get(index);
                if (chunkState == null) {
                    return CompletableFuture.completedFuture(null);
                }
                stamp = chunkState.lock.readLock();
                try {
                    if ((flags & 4) == 0 || (chunkState.flags & 4) != 0) {
                        if (chunkState.reference != null) {
                            CompletableFuture<Ref<ChunkStore>> completableFuture = CompletableFuture.completedFuture(chunkState.reference);
                            return completableFuture;
                        }
                        if (chunkState.future == null) {
                            CompletableFuture<Object> completableFuture = CompletableFuture.completedFuture(null);
                            return completableFuture;
                        }
                        CompletableFuture<Ref<ChunkStore>> completableFuture = chunkState.future;
                        return completableFuture;
                    }
                    break block35;
                }
                finally {
                    chunkState.lock.unlockRead(stamp);
                }
            }
            chunkState = this.chunks.computeIfAbsent(index, l -> new ChunkLoadState());
        }
        stamp = chunkState.lock.writeLock();
        if (chunkState.future == null && chunkState.reference != null && (flags & 8) == 0) {
            Ref<ChunkStore> reference2 = chunkState.reference;
            if ((flags & 4) == 0) {
                chunkState.lock.unlockWrite(stamp);
                return CompletableFuture.completedFuture(reference2);
            }
            if (this.world.isInThread() && (flags & Integer.MIN_VALUE) == 0) {
                chunkState.lock.unlockWrite(stamp);
                WorldChunk worldChunkComponent = this.store.getComponent(reference2, WorldChunk.getComponentType());
                assert (worldChunkComponent != null);
                worldChunkComponent.setFlag(ChunkFlag.TICKING, true);
                return CompletableFuture.completedFuture(reference2);
            }
            chunkState.lock.unlockWrite(stamp);
            return CompletableFuture.supplyAsync(() -> {
                WorldChunk worldChunkComponent = this.store.getComponent(reference2, WorldChunk.getComponentType());
                assert (worldChunkComponent != null);
                worldChunkComponent.setFlag(ChunkFlag.TICKING, true);
                return reference2;
            }, this.world);
        }
        try {
            boolean isNew;
            if (chunkState.throwable != null) {
                int count;
                long nanosSince = System.nanoTime() - chunkState.failedWhen;
                if (nanosSince < Math.min(MAX_FAILURE_BACKOFF_NANOS, (long)((count = chunkState.failedCounter) * count) * FAILURE_BACKOFF_NANOS)) {
                    CompletableFuture<Ref<ChunkStore>> completableFuture = CompletableFuture.failedFuture(new RuntimeException("Chunk failure backoff", chunkState.throwable));
                    return completableFuture;
                }
                chunkState.throwable = null;
                chunkState.failedWhen = 0L;
            }
            boolean bl = isNew = chunkState.future == null;
            if (isNew) {
                chunkState.flags = flags;
            }
            int x = ChunkUtil.xOfChunkIndex(index);
            int z = ChunkUtil.zOfChunkIndex(index);
            if ((isNew || (chunkState.flags & 1) != 0) && (flags & 1) == 0) {
                if (chunkState.future == null) {
                    chunkState.future = ((CompletableFuture)((CompletableFuture)this.loader.loadHolder(x, z).thenApplyAsync(holder -> {
                        if (holder == null || this.store.isShutdown()) {
                            return null;
                        }
                        this.totalLoadedChunksCount.getAndIncrement();
                        return this.preLoadChunkAsync(index, (Holder<ChunkStore>)holder, false);
                    })).thenApplyAsync(this::postLoadChunk, (Executor)this.world)).exceptionally(throwable -> {
                        ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to load chunk! %s, %s", x, z);
                        chunkState.fail((Throwable)throwable);
                        throw SneakyThrow.sneakyThrow(throwable);
                    });
                } else {
                    chunkState.flags &= 0xFFFFFFFE;
                    chunkState.future = chunkState.future.thenCompose(reference -> {
                        if (reference != null) {
                            return CompletableFuture.completedFuture(reference);
                        }
                        return ((CompletableFuture)((CompletableFuture)this.loader.loadHolder(x, z).thenApplyAsync(holder -> {
                            if (holder == null || this.store.isShutdown()) {
                                return null;
                            }
                            this.totalLoadedChunksCount.getAndIncrement();
                            return this.preLoadChunkAsync(index, (Holder<ChunkStore>)holder, false);
                        })).thenApplyAsync(this::postLoadChunk, (Executor)this.world)).exceptionally(throwable -> {
                            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to load chunk! %s, %s", x, z);
                            chunkState.fail((Throwable)throwable);
                            throw SneakyThrow.sneakyThrow(throwable);
                        });
                    });
                }
            }
            if ((isNew || (chunkState.flags & 2) != 0) && (flags & 2) == 0) {
                int seed = (int)this.world.getWorldConfig().getSeed();
                if (chunkState.future == null) {
                    CompletableFuture<GeneratedChunk> future;
                    long readStamp = this.generatorLock.readLock();
                    try {
                        future = this.generator == null ? this.generatorLoaded.thenCompose(aVoid -> this.generator.generate(seed, index, x, z, (flags & 0x10) != 0 ? this::isChunkStillNeeded : null)) : this.generator.generate(seed, index, x, z, (flags & 0x10) != 0 ? this::isChunkStillNeeded : null);
                    }
                    finally {
                        this.generatorLock.unlockRead(readStamp);
                    }
                    chunkState.future = ((CompletableFuture)((CompletableFuture)future.thenApplyAsync(generatedChunk -> {
                        if (generatedChunk == null || this.store.isShutdown()) {
                            return null;
                        }
                        this.totalGeneratedChunksCount.getAndIncrement();
                        return this.preLoadChunkAsync(index, generatedChunk.toHolder(this.world), true);
                    })).thenApplyAsync(this::postLoadChunk, (Executor)this.world)).exceptionally(throwable -> {
                        ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to generate chunk! %s, %s", x, z);
                        chunkState.fail((Throwable)throwable);
                        throw SneakyThrow.sneakyThrow(throwable);
                    });
                } else {
                    chunkState.flags &= 0xFFFFFFFD;
                    chunkState.future = chunkState.future.thenCompose(reference -> {
                        CompletionStage<GeneratedChunk> future;
                        if (reference != null) {
                            return CompletableFuture.completedFuture(reference);
                        }
                        long readStamp = this.generatorLock.readLock();
                        try {
                            future = this.generator == null ? this.generatorLoaded.thenCompose(aVoid -> this.generator.generate(seed, index, x, z, null)) : this.generator.generate(seed, index, x, z, null);
                        }
                        finally {
                            this.generatorLock.unlockRead(readStamp);
                        }
                        return ((CompletableFuture)((CompletableFuture)future.thenApplyAsync(generatedChunk -> {
                            if (generatedChunk == null || this.store.isShutdown()) {
                                return null;
                            }
                            this.totalGeneratedChunksCount.getAndIncrement();
                            return this.preLoadChunkAsync(index, generatedChunk.toHolder(this.world), true);
                        })).thenApplyAsync(this::postLoadChunk, (Executor)this.world)).exceptionally(throwable -> {
                            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to generate chunk! %s, %s", x, z);
                            chunkState.fail((Throwable)throwable);
                            throw SneakyThrow.sneakyThrow(throwable);
                        });
                    });
                }
            }
            if ((isNew || (chunkState.flags & 4) == 0) && (flags & 4) != 0) {
                chunkState.flags |= 4;
                if (chunkState.future != null) {
                    chunkState.future = ((CompletableFuture)chunkState.future.thenApplyAsync(reference -> {
                        if (reference != null) {
                            WorldChunk worldChunkComponent = this.store.getComponent((Ref<ChunkStore>)reference, WorldChunk.getComponentType());
                            assert (worldChunkComponent != null);
                            worldChunkComponent.setFlag(ChunkFlag.TICKING, true);
                        }
                        return reference;
                    }, (Executor)this.world)).exceptionally(throwable -> {
                        ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to set chunk ticking! %s, %s", x, z);
                        chunkState.fail((Throwable)throwable);
                        throw SneakyThrow.sneakyThrow(throwable);
                    });
                }
            }
            if (chunkState.future == null) {
                CompletableFuture<Object> completableFuture = CompletableFuture.completedFuture(null);
                return completableFuture;
            }
            CompletableFuture<Ref<ChunkStore>> completableFuture = chunkState.future;
            return completableFuture;
        }
        finally {
            chunkState.lock.unlockWrite(stamp);
        }
    }

    private boolean isChunkStillNeeded(long index) {
        for (PlayerRef playerRef : this.world.getPlayerRefs()) {
            if (!playerRef.getChunkTracker().shouldBeVisible(index)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isChunkOnBackoff(long index, long maxFailureBackoffNanos) {
        ChunkLoadState chunkState = this.chunks.get(index);
        if (chunkState == null) {
            return false;
        }
        long stamp = chunkState.lock.readLock();
        try {
            int count;
            if (chunkState.throwable == null) {
                boolean bl = false;
                return bl;
            }
            long nanosSince = System.nanoTime() - chunkState.failedWhen;
            boolean bl = nanosSince < Math.min(maxFailureBackoffNanos, (long)((count = chunkState.failedCounter) * count) * FAILURE_BACKOFF_NANOS);
            return bl;
        }
        finally {
            chunkState.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    private Holder<ChunkStore> preLoadChunkAsync(long index, @Nonnull Holder<ChunkStore> holder, boolean newlyGenerated) {
        WorldChunk worldChunkComponent = holder.getComponent(WorldChunk.getComponentType());
        if (worldChunkComponent == null) {
            throw new IllegalStateException(String.format("Holder missing WorldChunk component! (%d, %d)", ChunkUtil.xOfChunkIndex(index), ChunkUtil.zOfChunkIndex(index)));
        }
        if (worldChunkComponent.getIndex() != index) {
            throw new IllegalStateException(String.format("Incorrect chunk index! Got (%d, %d) expected (%d, %d)", worldChunkComponent.getX(), worldChunkComponent.getZ(), ChunkUtil.xOfChunkIndex(index), ChunkUtil.zOfChunkIndex(index)));
        }
        BlockChunk blockChunk = holder.getComponent(BlockChunk.getComponentType());
        if (blockChunk == null) {
            throw new IllegalStateException(String.format("Holder missing BlockChunk component! (%d, %d)", ChunkUtil.xOfChunkIndex(index), ChunkUtil.zOfChunkIndex(index)));
        }
        blockChunk.loadFromHolder(holder);
        worldChunkComponent.setFlag(ChunkFlag.NEWLY_GENERATED, newlyGenerated);
        worldChunkComponent.setLightingUpdatesEnabled(false);
        if (newlyGenerated && this.world.getWorldConfig().shouldSaveNewChunks()) {
            worldChunkComponent.markNeedsSaving();
        }
        try {
            long end;
            long diff;
            ChunkPreLoadProcessEvent event;
            long start = System.nanoTime();
            IEventDispatcher<ChunkPreLoadProcessEvent, ChunkPreLoadProcessEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(ChunkPreLoadProcessEvent.class, this.world.getName());
            if (dispatcher.hasListener() && !(event = dispatcher.dispatch(new ChunkPreLoadProcessEvent(holder, worldChunkComponent, newlyGenerated, start))).didLog() && (diff = (end = System.nanoTime()) - start) > (long)this.world.getTickStepNanos()) {
                LOGGER.at(Level.SEVERE).log("Took too long to pre-load process chunk: %s > TICK_STEP, Has GC Run: %s, %s", FormatUtil.nanosToString(diff), this.world.consumeGCHasRun(), worldChunkComponent);
            }
        }
        finally {
            worldChunkComponent.setLightingUpdatesEnabled(true);
        }
        return holder;
    }

    @Nullable
    private Ref<ChunkStore> postLoadChunk(@Nullable Holder<ChunkStore> holder) {
        this.world.debugAssertInTickingThread();
        if (holder == null || this.store.isShutdown()) {
            return null;
        }
        long start = System.nanoTime();
        WorldChunk worldChunkComponent = holder.getComponent(WorldChunk.getComponentType());
        assert (worldChunkComponent != null);
        worldChunkComponent.setFlag(ChunkFlag.START_INIT, true);
        if (worldChunkComponent.is(ChunkFlag.TICKING)) {
            holder.tryRemoveComponent(REGISTRY.getNonTickingComponentType());
        } else {
            holder.ensureComponent(REGISTRY.getNonTickingComponentType());
        }
        Ref<ChunkStore> reference = this.add(holder);
        worldChunkComponent.initFlags();
        this.world.getChunkLighting().init(worldChunkComponent);
        long end = System.nanoTime();
        long diff = end - start;
        if (diff > (long)this.world.getTickStepNanos()) {
            LOGGER.at(Level.SEVERE).log("Took too long to post-load process chunk: %s > TICK_STEP, Has GC Run: %s, %s", FormatUtil.nanosToString(diff), this.world.consumeGCHasRun(), worldChunkComponent);
        }
        return reference;
    }

    static {
        CodecStore.STATIC.putCodecSupplier(HOLDER_CODEC_KEY, REGISTRY::getEntityCodec);
        LOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE = REGISTRY.registerSystemType(LoadPacketDataQuerySystem.class);
        LOAD_FUTURE_PACKETS_DATA_QUERY_SYSTEM_TYPE = REGISTRY.registerSystemType(LoadFuturePacketDataQuerySystem.class);
        UNLOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE = REGISTRY.registerSystemType(UnloadPacketDataQuerySystem.class);
        UNLOAD_RESOURCE = REGISTRY.registerResource(ChunkUnloadingSystem.Data.class, ChunkUnloadingSystem.Data::new);
        SAVE_RESOURCE = REGISTRY.registerResource(ChunkSavingSystems.Data.class, ChunkSavingSystems.Data::new);
        INIT_GROUP = REGISTRY.registerSystemGroup();
        REGISTRY.registerSystem(new ChunkLoaderSaverSetupSystem());
        REGISTRY.registerSystem(new ChunkUnloadingSystem());
        REGISTRY.registerSystem(new ChunkSavingSystems.WorldRemoved());
        REGISTRY.registerSystem(new ChunkSavingSystems.Ticking());
    }

    private static class ChunkLoadState {
        private final StampedLock lock = new StampedLock();
        private int flags = 0;
        @Nullable
        private CompletableFuture<Ref<ChunkStore>> future;
        @Nullable
        private Ref<ChunkStore> reference;
        @Nullable
        private Throwable throwable;
        private long failedWhen;
        private int failedCounter;

        private ChunkLoadState() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void fail(Throwable throwable) {
            long stamp = this.lock.writeLock();
            try {
                this.flags = 0;
                this.future = null;
                this.throwable = throwable;
                this.failedWhen = System.nanoTime();
                ++this.failedCounter;
            }
            finally {
                this.lock.unlockWrite(stamp);
            }
        }
    }

    public static abstract class LoadPacketDataQuerySystem
    extends EntityDataSystem<ChunkStore, PlayerRef, Packet> {
    }

    public static abstract class LoadFuturePacketDataQuerySystem
    extends EntityDataSystem<ChunkStore, PlayerRef, CompletableFuture<Packet>> {
    }

    public static abstract class UnloadPacketDataQuerySystem
    extends EntityDataSystem<ChunkStore, PlayerRef, Packet> {
    }

    public static class ChunkLoaderSaverSetupSystem
    extends StoreSystem<ChunkStore> {
        @Override
        @Nullable
        public SystemGroup<ChunkStore> getGroup() {
            return INIT_GROUP;
        }

        @Override
        public void onSystemAddedToStore(@Nonnull Store<ChunkStore> store) {
            ChunkStore data = store.getExternalData();
            World world = data.getWorld();
            IChunkStorageProvider chunkStorageProvider = world.getWorldConfig().getChunkStorageProvider();
            try {
                data.loader = chunkStorageProvider.getLoader(store);
                data.saver = chunkStorageProvider.getSaver(store);
            }
            catch (IOException e) {
                throw SneakyThrow.sneakyThrow(e);
            }
        }

        @Override
        public void onSystemRemovedFromStore(@Nonnull Store<ChunkStore> store) {
            ChunkStore data = store.getExternalData();
            try {
                if (data.loader != null) {
                    IChunkLoader oldLoader = data.loader;
                    data.loader = null;
                    oldLoader.close();
                }
                if (data.saver != null) {
                    IChunkSaver oldSaver = data.saver;
                    data.saver = null;
                    oldSaver.close();
                }
            }
            catch (IOException e) {
                ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to close storage!");
            }
        }
    }
}

