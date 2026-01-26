/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.storage.component;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.StoreSystem;
import com.hypixel.hytale.component.system.tick.RunWhenPausedSystem;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.events.ecs.ChunkSaveEvent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkSavingSystems {
    @Nonnull
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Nonnull
    private static final ComponentType<ChunkStore, WorldChunk> WORLD_CHUNK_COMPONENT_TYPE = WorldChunk.getComponentType();
    @Nonnull
    public static final Query<ChunkStore> QUERY = Query.and(WORLD_CHUNK_COMPONENT_TYPE, Query.not(ChunkStore.REGISTRY.getNonSerializedComponentType()));

    @Nonnull
    public static CompletableFuture<Void> saveChunksInWorld(@Nonnull Store<ChunkStore> store) {
        Ref<ChunkStore> reference;
        HytaleLogger logger = store.getExternalData().getWorld().getLogger();
        Data data = store.getResource(ChunkStore.SAVE_RESOURCE);
        logger.at(Level.INFO).log("Queuing Chunks...");
        store.forEachChunk(QUERY, (archetypeChunk, b) -> {
            for (int index = 0; index < archetypeChunk.size(); ++index) {
                ChunkSavingSystems.tryQueue(index, archetypeChunk, b.getStore());
            }
        });
        logger.at(Level.INFO).log("Saving All Chunks...");
        while ((reference = data.poll()) != null) {
            ChunkSavingSystems.saveChunk(reference, data, true, store);
        }
        logger.at(Level.INFO).log("Waiting for Saving Chunks...");
        return data.waitForSavingChunks();
    }

    public static void tryQueue(int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store) {
        WorldChunk worldChunkComponent = archetypeChunk.getComponent(index, WORLD_CHUNK_COMPONENT_TYPE);
        assert (worldChunkComponent != null);
        if (!worldChunkComponent.getNeedsSaving() || worldChunkComponent.isSaving()) {
            return;
        }
        Ref<ChunkStore> chunkRef = archetypeChunk.getReferenceTo(index);
        ChunkSaveEvent event = new ChunkSaveEvent(worldChunkComponent);
        store.invoke(chunkRef, event);
        if (event.isCancelled()) {
            return;
        }
        store.getResource(ChunkStore.SAVE_RESOURCE).push(chunkRef);
    }

    public static void tryQueueSync(@Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        Store<ChunkStore> store = commandBuffer.getStore();
        Data data = store.getResource(ChunkStore.SAVE_RESOURCE);
        for (int index = 0; index < archetypeChunk.size(); ++index) {
            WorldChunk worldChunkComponent = archetypeChunk.getComponent(index, WORLD_CHUNK_COMPONENT_TYPE);
            assert (worldChunkComponent != null);
            if (!worldChunkComponent.getNeedsSaving() || worldChunkComponent.isSaving()) continue;
            Ref<ChunkStore> chunkRef = archetypeChunk.getReferenceTo(index);
            ChunkSaveEvent event = new ChunkSaveEvent(worldChunkComponent);
            store.invoke(chunkRef, event);
            if (event.isCancelled()) continue;
            data.push(chunkRef);
        }
    }

    public static void saveChunk(@Nonnull Ref<ChunkStore> reference, @Nonnull Data data, boolean report, @Nonnull Store<ChunkStore> store) {
        if (!reference.isValid()) {
            LOGGER.at(Level.FINEST).log("Chunk reference in queue is for a chunk that has been removed!");
            return;
        }
        data.toSaveTotal.getAndIncrement();
        WorldChunk worldChunkComponent = store.getComponent(reference, WORLD_CHUNK_COMPONENT_TYPE);
        assert (worldChunkComponent != null);
        Holder<ChunkStore> holder = worldChunkComponent.toHolder();
        ChunkStore chunkStore = store.getExternalData();
        World world = chunkStore.getWorld();
        IChunkSaver saver = chunkStore.getSaver();
        CompletionStage future = saver.saveHolder(worldChunkComponent.getX(), worldChunkComponent.getZ(), holder).whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
                ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to save chunk (%d, %d):", worldChunkComponent.getX(), worldChunkComponent.getZ());
            } else {
                worldChunkComponent.setFlag(ChunkFlag.ON_DISK, true);
                LOGGER.at(Level.FINEST).log("Finished saving chunk (%d, %d)", worldChunkComponent.getX(), worldChunkComponent.getZ());
            }
        });
        data.chunkSavingFutures.add((CompletableFuture<Void>)future);
        if (report) {
            ((CompletableFuture)future).thenRunAsync(() -> HytaleServer.get().reportSaveProgress(world, data.savedCount.incrementAndGet(), data.toSaveTotal.get() + data.queue.size()));
        }
        worldChunkComponent.consumeNeedsSaving();
    }

    public static class Data
    implements Resource<ChunkStore> {
        public static final float QUEUE_UPDATE_INTERVAL = 0.5f;
        @Nonnull
        private final Set<Ref<ChunkStore>> set = ConcurrentHashMap.newKeySet();
        @Nonnull
        private final Deque<Ref<ChunkStore>> queue = new ConcurrentLinkedDeque<Ref<ChunkStore>>();
        @Nonnull
        private final List<CompletableFuture<Void>> chunkSavingFutures = new ObjectArrayList<CompletableFuture<Void>>();
        private float time;
        public boolean isSaving = true;
        @Nonnull
        private final AtomicInteger savedCount = new AtomicInteger();
        @Nonnull
        private final AtomicInteger toSaveTotal = new AtomicInteger();

        public Data() {
            this.time = 0.5f;
        }

        public Data(float time) {
            this.time = time;
        }

        @Override
        @Nonnull
        public Resource<ChunkStore> clone() {
            return new Data(this.time);
        }

        public void clearSaveQueue() {
            this.queue.clear();
            this.set.clear();
        }

        public void push(@Nonnull Ref<ChunkStore> reference) {
            if (this.set.add(reference)) {
                this.queue.push(reference);
            }
        }

        @Nullable
        public Ref<ChunkStore> poll() {
            Ref<ChunkStore> reference = this.queue.poll();
            if (reference == null) {
                return null;
            }
            this.set.remove(reference);
            return reference;
        }

        public boolean checkTimer(float dt) {
            this.time -= dt;
            if (this.time <= 0.0f) {
                this.time += 0.5f;
                return true;
            }
            return false;
        }

        @Nonnull
        public CompletableFuture<Void> waitForSavingChunks() {
            return CompletableFuture.allOf((CompletableFuture[])this.chunkSavingFutures.toArray(CompletableFuture[]::new));
        }
    }

    public static class Ticking
    extends TickingSystem<ChunkStore>
    implements RunWhenPausedSystem<ChunkStore> {
        @Override
        @Nonnull
        public Set<Dependency<ChunkStore>> getDependencies() {
            return RootDependency.lastSet();
        }

        @Override
        public void tick(float dt, int systemIndex, @Nonnull Store<ChunkStore> store) {
            Ref<ChunkStore> reference;
            Data data = store.getResource(ChunkStore.SAVE_RESOURCE);
            if (!data.isSaving || !store.getExternalData().getWorld().getWorldConfig().canSaveChunks()) {
                return;
            }
            data.chunkSavingFutures.removeIf(CompletableFuture::isDone);
            if (data.checkTimer(dt)) {
                store.forEachChunk(QUERY, ChunkSavingSystems::tryQueueSync);
            }
            World world = store.getExternalData().getWorld();
            IChunkSaver saver = store.getExternalData().getSaver();
            int parallelSaves = ForkJoinPool.commonPool().getParallelism();
            for (int i = 0; i < parallelSaves && (reference = data.poll()) != null; ++i) {
                if (!reference.isValid()) {
                    LOGGER.at(Level.FINEST).log("Chunk reference in queue is for a chunk that has been removed!");
                    return;
                }
                WorldChunk chunk = store.getComponent(reference, WORLD_CHUNK_COMPONENT_TYPE);
                chunk.setSaving(true);
                Holder<ChunkStore> holder = store.copySerializableEntity(reference);
                data.toSaveTotal.getAndIncrement();
                data.chunkSavingFutures.add((CompletableFuture<Void>)((CompletableFuture)CompletableFuture.supplyAsync(() -> saver.saveHolder(chunk.getX(), chunk.getZ(), holder)).thenCompose(Function.identity())).whenCompleteAsync((aVoid, throwable) -> {
                    if (throwable != null) {
                        ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to save chunk (%d, %d):", chunk.getX(), chunk.getZ());
                    } else {
                        chunk.setFlag(ChunkFlag.ON_DISK, true);
                        LOGGER.at(Level.FINEST).log("Finished saving chunk (%d, %d)", chunk.getX(), chunk.getZ());
                    }
                    chunk.consumeNeedsSaving();
                    chunk.setSaving(false);
                }, (Executor)world));
            }
        }
    }

    public static class WorldRemoved
    extends StoreSystem<ChunkStore> {
        @Nonnull
        private final Set<Dependency<ChunkStore>> dependencies = Set.of(new SystemDependency(Order.AFTER, ChunkStore.ChunkLoaderSaverSetupSystem.class));

        @Override
        @Nonnull
        public Set<Dependency<ChunkStore>> getDependencies() {
            return this.dependencies;
        }

        @Override
        public void onSystemAddedToStore(@Nonnull Store<ChunkStore> store) {
        }

        @Override
        public void onSystemRemovedFromStore(@Nonnull Store<ChunkStore> store) {
            World world = store.getExternalData().getWorld();
            world.getLogger().at(Level.INFO).log("Shutting down chunk generator...");
            world.getChunkStore().shutdownGenerator();
            if (!world.getWorldConfig().canSaveChunks()) {
                world.getLogger().at(Level.INFO).log("This world has opted to disable chunk saving so it will not be saved on shutdown");
                return;
            }
            world.getLogger().at(Level.INFO).log("Saving Chunks...");
            Data data = store.getResource(ChunkStore.SAVE_RESOURCE);
            data.savedCount.set(0);
            data.toSaveTotal.set(0);
            ChunkSavingSystems.saveChunksInWorld(store).join();
            world.getLogger().at(Level.INFO).log("Done Saving Chunks!");
        }
    }
}

