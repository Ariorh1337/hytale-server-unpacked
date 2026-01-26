/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.lighting;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.lighting.FloodLightCalculation;
import com.hypixel.hytale.server.core.universe.world.lighting.LightCalculation;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class ChunkLightingManager
implements Runnable {
    @Nonnull
    private final HytaleLogger logger;
    @Nonnull
    private final Thread thread;
    @Nonnull
    private final World world;
    private final Semaphore semaphore = new Semaphore(1);
    private final Set<Vector3i> set = ConcurrentHashMap.newKeySet();
    private final ObjectArrayFIFOQueue<Vector3i> queue = new ObjectArrayFIFOQueue();
    private LightCalculation lightCalculation;

    public ChunkLightingManager(@Nonnull World world) {
        this.logger = HytaleLogger.get("World|" + world.getName() + "|L");
        this.thread = new Thread((Runnable)this, "ChunkLighting - " + world.getName());
        this.thread.setDaemon(true);
        this.world = world;
        this.lightCalculation = new FloodLightCalculation(this);
    }

    @Nonnull
    protected HytaleLogger getLogger() {
        return this.logger;
    }

    @Nonnull
    public World getWorld() {
        return this.world;
    }

    public void setLightCalculation(LightCalculation lightCalculation) {
        this.lightCalculation = lightCalculation;
    }

    public LightCalculation getLightCalculation() {
        return this.lightCalculation;
    }

    public void start() {
        this.thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            int lastSize = 0;
            int count = 0;
            while (!this.thread.isInterrupted()) {
                int currentSize;
                Vector3i pos;
                this.semaphore.drainPermits();
                ObjectArrayFIFOQueue<Vector3i> objectArrayFIFOQueue = this.queue;
                synchronized (objectArrayFIFOQueue) {
                    pos = this.queue.isEmpty() ? null : this.queue.dequeue();
                }
                if (pos != null) {
                    this.process(pos);
                }
                Thread.yield();
                ObjectArrayFIFOQueue<Vector3i> objectArrayFIFOQueue2 = this.queue;
                synchronized (objectArrayFIFOQueue2) {
                    currentSize = this.queue.size();
                }
                if (currentSize != lastSize) {
                    count = 0;
                    lastSize = currentSize;
                    continue;
                }
                if (count <= currentSize) {
                    ++count;
                    continue;
                }
                this.semaphore.acquire();
            }
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void process(Vector3i chunkPosition) {
        try {
            switch (this.lightCalculation.calculateLight(chunkPosition)) {
                case NOT_LOADED: 
                case WAITING_FOR_NEIGHBOUR: 
                case DONE: {
                    this.set.remove(chunkPosition);
                    break;
                }
                case INVALIDATED: {
                    ObjectArrayFIFOQueue<Vector3i> objectArrayFIFOQueue = this.queue;
                    synchronized (objectArrayFIFOQueue) {
                        this.queue.enqueue(chunkPosition);
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            ((HytaleLogger.Api)this.logger.at(Level.WARNING).withCause(e)).log("Failed to calculate lighting for: %s", chunkPosition);
            this.set.remove(chunkPosition);
        }
    }

    public boolean interrupt() {
        if (this.thread.isAlive()) {
            this.thread.interrupt();
            return true;
        }
        return false;
    }

    public void stop() {
        try {
            int i = 0;
            while (this.thread.isAlive()) {
                this.thread.interrupt();
                this.thread.join(this.world.getTickStepNanos() / 1000000);
                if ((i += this.world.getTickStepNanos() / 1000000) <= 5000) continue;
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement traceElement : this.thread.getStackTrace()) {
                    sb.append("\tat ").append(traceElement).append('\n');
                }
                HytaleLogger.getLogger().at(Level.SEVERE).log("Forcing ChunkLighting Thread %s to stop:\n%s", (Object)this.thread, (Object)sb.toString());
                this.thread.stop();
                break;
            }
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public void init(WorldChunk worldChunk) {
        this.lightCalculation.init(worldChunk);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToQueue(Vector3i chunkPosition) {
        if (this.set.add(chunkPosition)) {
            ObjectArrayFIFOQueue<Vector3i> objectArrayFIFOQueue = this.queue;
            synchronized (objectArrayFIFOQueue) {
                this.queue.enqueue(chunkPosition);
            }
            this.semaphore.release(1);
        }
    }

    public boolean isQueued(int chunkX, int chunkZ) {
        Vector3i chunkPos = new Vector3i(chunkX, 0, chunkZ);
        for (int chunkY = 0; chunkY < 10; ++chunkY) {
            chunkPos.setY(chunkY);
            if (!this.isQueued(chunkPos)) continue;
            return true;
        }
        return false;
    }

    public boolean isQueued(Vector3i chunkPosition) {
        return this.set.contains(chunkPosition);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getQueueSize() {
        ObjectArrayFIFOQueue<Vector3i> objectArrayFIFOQueue = this.queue;
        synchronized (objectArrayFIFOQueue) {
            return this.queue.size();
        }
    }

    public boolean invalidateLightAtBlock(WorldChunk worldChunk, int blockX, int blockY, int blockZ, BlockType blockType, int oldHeight, int newHeight) {
        return this.lightCalculation.invalidateLightAtBlock(worldChunk, blockX, blockY, blockZ, blockType, oldHeight, newHeight);
    }

    public boolean invalidateLightInChunk(WorldChunk worldChunk) {
        return this.lightCalculation.invalidateLightInChunkSections(worldChunk, 0, 10);
    }

    public boolean invalidateLightInChunkSection(WorldChunk worldChunk, int sectionIndex) {
        return this.lightCalculation.invalidateLightInChunkSections(worldChunk, sectionIndex, sectionIndex + 1);
    }

    public boolean invalidateLightInChunkSections(WorldChunk worldChunk, int sectionIndexFrom, int sectionIndexTo) {
        return this.lightCalculation.invalidateLightInChunkSections(worldChunk, sectionIndexFrom, sectionIndexTo);
    }

    public void invalidateLoadedChunks() {
        this.world.getChunkStore().getStore().forEachEntityParallel(WorldChunk.getComponentType(), (index, archetypeChunk, storeCommandBuffer) -> {
            WorldChunk chunk = archetypeChunk.getComponent(index, WorldChunk.getComponentType());
            for (int y = 0; y < 10; ++y) {
                BlockSection section = chunk.getBlockChunk().getSectionAtIndex(y);
                section.invalidateLocalLight();
                if (!BlockChunk.SEND_LOCAL_LIGHTING_DATA && !BlockChunk.SEND_GLOBAL_LIGHTING_DATA) continue;
                chunk.getBlockChunk().invalidateChunkSection(y);
            }
        });
        this.world.getChunkStore().getChunkIndexes().forEach(index -> {
            int x = ChunkUtil.xOfChunkIndex(index);
            int z = ChunkUtil.zOfChunkIndex(index);
            for (int y = 0; y < 10; ++y) {
                this.addToQueue(new Vector3i(x, y, z));
            }
        });
    }
}

