/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.player;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.common.fastutil.HLongOpenHashSet;
import com.hypixel.hytale.common.fastutil.HLongSet;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.iterator.CircleSpiralIterator;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.world.UnloadChunk;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkTracker
implements Component<EntityStore> {
    @Nonnull
    public static final MetricsRegistry<ChunkTracker> METRICS_REGISTRY = new MetricsRegistry<ChunkTracker>().register("ViewRadius", tracker -> tracker.chunkViewRadius, Codec.INTEGER).register("SentViewRadius", tracker -> tracker.sentViewRadius, Codec.INTEGER).register("HotRadius", tracker -> tracker.hotRadius, Codec.INTEGER).register("LoadedChunksCount", ChunkTracker::getLoadedChunksCount, Codec.INTEGER).register("LoadingChunksCount", ChunkTracker::getLoadingChunksCount, Codec.INTEGER).register("MaxChunksPerSecond", ChunkTracker::getMaxChunksPerSecond, Codec.INTEGER).register("MaxChunksPerTick", ChunkTracker::getMaxChunksPerTick, Codec.INTEGER).register("ReadyForChunks", ChunkTracker::isReadyForChunks, Codec.BOOLEAN).register("LastChunkX", tracker -> tracker.lastChunkX, Codec.INTEGER).register("LastChunkZ", tracker -> tracker.lastChunkZ, Codec.INTEGER);
    public static final int MAX_CHUNKS_PER_SECOND_LOCAL = 256;
    public static final int MAX_CHUNKS_PER_SECOND_LAN = 128;
    public static final int MAX_CHUNKS_PER_SECOND = 36;
    public static final int MAX_CHUNKS_PER_TICK = 4;
    public static final int MIN_LOADED_CHUNKS_RADIUS = 2;
    public static final int MAX_HOT_LOADED_CHUNKS_RADIUS = 8;
    public static final long MAX_FAILURE_BACKOFF_NANOS = TimeUnit.SECONDS.toNanos(10L);
    @Nullable
    private TransformComponent transformComponent;
    private int chunkViewRadius;
    @Nonnull
    private final CircleSpiralIterator spiralIterator = new CircleSpiralIterator();
    @Nonnull
    private final StampedLock loadedLock = new StampedLock();
    @Nonnull
    private final HLongSet loading = new HLongOpenHashSet();
    @Nonnull
    private final HLongSet loaded = new HLongOpenHashSet();
    @Nonnull
    private final HLongSet reload = new HLongOpenHashSet();
    private int maxChunksPerSecond;
    private float inverseMaxChunksPerSecond;
    private int maxChunksPerTick;
    private int minLoadedChunksRadius;
    private int maxHotLoadedChunksRadius;
    private float accumulator;
    private int sentViewRadius;
    private int hotRadius;
    private int lastChunkX;
    private int lastChunkZ;
    private boolean readyForChunks;

    public static ComponentType<EntityStore, ChunkTracker> getComponentType() {
        return EntityModule.get().getChunkTrackerComponentType();
    }

    public ChunkTracker() {
        this.minLoadedChunksRadius = 2;
        this.maxHotLoadedChunksRadius = 8;
        this.maxChunksPerTick = 4;
    }

    private ChunkTracker(@Nonnull ChunkTracker other) {
        this.copyFrom(other);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unloadAll(@Nonnull PlayerRef playerRefComponent) {
        long stamp = this.loadedLock.writeLock();
        try {
            this.loading.clear();
            LongIterator iterator = this.loaded.iterator();
            while (iterator.hasNext()) {
                long chunkIndex = iterator.nextLong();
                int chunkX = ChunkUtil.xOfChunkIndex(chunkIndex);
                int chunkZ = ChunkUtil.zOfChunkIndex(chunkIndex);
                playerRefComponent.getPacketHandler().writeNoCache(new UnloadChunk(chunkX, chunkZ));
            }
            this.loaded.clear();
            this.sentViewRadius = 0;
            this.hotRadius = 0;
        }
        finally {
            this.loadedLock.unlockWrite(stamp);
        }
    }

    public void clear() {
        long stamp = this.loadedLock.writeLock();
        try {
            this.loading.clear();
            this.loaded.clear();
            this.sentViewRadius = 0;
            this.hotRadius = 0;
        }
        finally {
            this.loadedLock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick(@Nonnull Player playerComponent, @Nonnull PlayerRef playerRefComponent, @Nonnull TransformComponent transformComponent, float dt, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        if (!this.readyForChunks) {
            return;
        }
        this.transformComponent = transformComponent;
        int chunkViewRadius = this.chunkViewRadius = playerComponent.getViewRadius();
        Vector3d position = transformComponent.getPosition();
        int chunkX = MathUtil.floor(position.getX()) >> 5;
        int chunkZ = MathUtil.floor(position.getZ()) >> 5;
        int xDiff = Math.abs(this.lastChunkX - chunkX);
        int zDiff = Math.abs(this.lastChunkZ - chunkZ);
        int chunkMoveDistance = xDiff > 0 || zDiff > 0 ? (int)Math.ceil(Math.sqrt(xDiff * xDiff + zDiff * zDiff)) : 0;
        this.sentViewRadius = Math.max(0, this.sentViewRadius - chunkMoveDistance);
        this.hotRadius = Math.max(0, this.hotRadius - chunkMoveDistance);
        this.lastChunkX = chunkX;
        this.lastChunkZ = chunkZ;
        if (this.sentViewRadius == chunkViewRadius && this.hotRadius == Math.min(this.maxHotLoadedChunksRadius, chunkViewRadius) && this.reload.isEmpty()) {
            return;
        }
        if (this.sentViewRadius > chunkViewRadius) {
            this.sentViewRadius = chunkViewRadius;
        }
        if (this.hotRadius > chunkViewRadius) {
            this.hotRadius = chunkViewRadius;
        }
        World world = commandBuffer.getExternalData().getWorld();
        ChunkStore chunkStore = world.getChunkStore();
        int minLoadedRadius = Math.max(this.minLoadedChunksRadius, chunkViewRadius);
        int minLoadedRadiusSq = minLoadedRadius * minLoadedRadius;
        long stamp = this.loadedLock.writeLock();
        try {
            long chunkCoordinates;
            this.loaded.removeIf(ChunkTracker::tryUnloadChunk, minLoadedRadiusSq, chunkX, chunkZ, playerRefComponent, this.loading);
            this.accumulator += dt;
            int toLoad = Math.min((int)((float)this.maxChunksPerSecond * this.accumulator), this.maxChunksPerTick);
            int loadingSize = this.loading.size();
            toLoad -= loadingSize;
            if (!this.reload.isEmpty()) {
                LongIterator iterator = this.reload.iterator();
                while (iterator.hasNext()) {
                    chunkCoordinates = iterator.nextLong();
                    if (chunkStore.isChunkOnBackoff(chunkCoordinates, MAX_FAILURE_BACKOFF_NANOS) || !this.loading.add(chunkCoordinates)) continue;
                    this.tryLoadChunkAsync(chunkStore, playerRefComponent, chunkCoordinates, transformComponent, commandBuffer);
                    iterator.remove();
                    --toLoad;
                    this.accumulator -= this.inverseMaxChunksPerSecond;
                }
            }
            if (this.sentViewRadius < minLoadedRadius) {
                boolean areAllLoaded = true;
                this.spiralIterator.init(chunkX, chunkZ, this.sentViewRadius, minLoadedRadius);
                while (toLoad > 0 && this.spiralIterator.hasNext()) {
                    chunkCoordinates = this.spiralIterator.next();
                    if (!this.loaded.contains(chunkCoordinates)) {
                        areAllLoaded = false;
                        if (chunkStore.isChunkOnBackoff(chunkCoordinates, MAX_FAILURE_BACKOFF_NANOS) || !this.loading.add(chunkCoordinates)) continue;
                        this.tryLoadChunkAsync(chunkStore, playerRefComponent, chunkCoordinates, transformComponent, commandBuffer);
                        --toLoad;
                        this.accumulator -= this.inverseMaxChunksPerSecond;
                        continue;
                    }
                    if (!areAllLoaded) continue;
                    this.sentViewRadius = this.spiralIterator.getCompletedRadius();
                }
                if (areAllLoaded) {
                    this.sentViewRadius = this.spiralIterator.getCompletedRadius();
                }
            }
        }
        finally {
            this.loadedLock.unlockWrite(stamp);
        }
        int maxHotRadius = Math.min(this.maxHotLoadedChunksRadius, this.sentViewRadius);
        if (this.hotRadius < maxHotRadius) {
            this.spiralIterator.init(chunkX, chunkZ, this.hotRadius, maxHotRadius);
            while (this.spiralIterator.hasNext()) {
                Ref<ChunkStore> chunkReference = chunkStore.getChunkReference(this.spiralIterator.next());
                if (chunkReference == null || !chunkReference.isValid()) continue;
                WorldChunk worldChunkComponent = chunkStore.getStore().getComponent(chunkReference, WorldChunk.getComponentType());
                assert (worldChunkComponent != null);
                if (worldChunkComponent.is(ChunkFlag.TICKING)) continue;
                commandBuffer.run(_store -> worldChunkComponent.setFlag(ChunkFlag.TICKING, true));
            }
            this.hotRadius = maxHotRadius;
        }
        if (this.sentViewRadius == chunkViewRadius) {
            this.accumulator = 0.0f;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isLoaded(long indexChunk) {
        long stamp = this.loadedLock.readLock();
        try {
            boolean bl = this.loaded.contains(indexChunk);
            return bl;
        }
        finally {
            this.loadedLock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeForReload(long indexChunk) {
        if (this.shouldBeVisible(indexChunk)) {
            long stamp = this.loadedLock.writeLock();
            try {
                this.reload.add(indexChunk);
            }
            finally {
                this.loadedLock.unlockWrite(stamp);
            }
        }
    }

    public boolean shouldBeVisible(long chunkCoordinates) {
        if (this.transformComponent == null) {
            return false;
        }
        Vector3d position = this.transformComponent.getPosition();
        int chunkX = MathUtil.floor(position.getX()) >> 5;
        int chunkZ = MathUtil.floor(position.getZ()) >> 5;
        int x = ChunkUtil.xOfChunkIndex(chunkCoordinates);
        int z = ChunkUtil.zOfChunkIndex(chunkCoordinates);
        int minLoadedRadius = Math.max(this.minLoadedChunksRadius, this.chunkViewRadius);
        return ChunkTracker.shouldBeVisible(minLoadedRadius * minLoadedRadius, chunkX, chunkZ, x, z);
    }

    @Nonnull
    public ChunkVisibility getChunkVisibility(long indexChunk) {
        int minLoadedRadius;
        boolean shouldBeVisible;
        int zDiff;
        if (this.transformComponent == null) {
            return ChunkVisibility.NONE;
        }
        Vector3d position = this.transformComponent.getPosition();
        int chunkX = MathUtil.floor(position.getX()) >> 5;
        int chunkZ = MathUtil.floor(position.getZ()) >> 5;
        int x = ChunkUtil.xOfChunkIndex(indexChunk);
        int z = ChunkUtil.zOfChunkIndex(indexChunk);
        int xDiff = Math.abs(x - chunkX);
        int distanceSq = xDiff * xDiff + (zDiff = Math.abs(z - chunkZ)) * zDiff;
        boolean bl = shouldBeVisible = distanceSq <= (minLoadedRadius = Math.max(this.minLoadedChunksRadius, this.chunkViewRadius)) * minLoadedRadius;
        if (shouldBeVisible) {
            boolean isHot = distanceSq <= this.maxHotLoadedChunksRadius * this.maxHotLoadedChunksRadius;
            return isHot ? ChunkVisibility.HOT : ChunkVisibility.COLD;
        }
        return ChunkVisibility.NONE;
    }

    public int getMaxChunksPerSecond() {
        return this.maxChunksPerSecond;
    }

    public void setMaxChunksPerSecond(int maxChunksPerSecond) {
        this.maxChunksPerSecond = maxChunksPerSecond;
        this.inverseMaxChunksPerSecond = 1.0f / (float)maxChunksPerSecond;
    }

    public void setDefaultMaxChunksPerSecond(@Nonnull PlayerRef playerRef) {
        this.maxChunksPerSecond = playerRef.getPacketHandler().isLocalConnection() ? 256 : (playerRef.getPacketHandler().isLANConnection() ? 128 : 36);
        this.inverseMaxChunksPerSecond = 1.0f / (float)this.maxChunksPerSecond;
    }

    public int getMaxChunksPerTick() {
        return this.maxChunksPerTick;
    }

    public void setMaxChunksPerTick(int maxChunksPerTick) {
        this.maxChunksPerTick = maxChunksPerTick;
    }

    public int getMinLoadedChunksRadius() {
        return this.minLoadedChunksRadius;
    }

    public void setMinLoadedChunksRadius(int minLoadedChunksRadius) {
        this.minLoadedChunksRadius = minLoadedChunksRadius;
    }

    public int getMaxHotLoadedChunksRadius() {
        return this.maxHotLoadedChunksRadius;
    }

    public void setMaxHotLoadedChunksRadius(int maxHotLoadedChunksRadius) {
        this.maxHotLoadedChunksRadius = maxHotLoadedChunksRadius;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getLoadedChunksCount() {
        long stamp = this.loadedLock.tryOptimisticRead();
        int size = this.loaded.size();
        if (this.loadedLock.validate(stamp)) {
            return size;
        }
        stamp = this.loadedLock.readLock();
        try {
            int n = this.loaded.size();
            return n;
        }
        finally {
            this.loadedLock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getLoadingChunksCount() {
        long stamp = this.loadedLock.tryOptimisticRead();
        int size = this.loading.size();
        if (this.loadedLock.validate(stamp)) {
            return size;
        }
        stamp = this.loadedLock.readLock();
        try {
            int n = this.loading.size();
            return n;
        }
        finally {
            this.loadedLock.unlockRead(stamp);
        }
    }

    @Nonnull
    private String getLoadedChunksGrid() {
        int viewRadius = this.chunkViewRadius;
        int chunkXMin = this.lastChunkX - viewRadius;
        int chunkZMin = this.lastChunkZ - viewRadius;
        int chunkXMax = this.lastChunkX + viewRadius;
        int chunkZMax = this.lastChunkZ + viewRadius;
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(chunkXMin).append(", ").append(chunkZMin).append(") -> (").append(chunkXMax).append(", ").append(chunkZMax).append(")\n");
        for (int x = chunkXMin; x <= chunkXMax; ++x) {
            for (int z = chunkZMin; z <= chunkZMax; ++z) {
                long index = ChunkUtil.indexChunk(x, z);
                if (this.loaded.contains(index)) {
                    ChunkVisibility chunkVisibility = this.getChunkVisibility(index);
                    switch (chunkVisibility.ordinal()) {
                        case 0: {
                            sb.append('X');
                            break;
                        }
                        case 1: {
                            sb.append('#');
                            break;
                        }
                        case 2: {
                            sb.append('&');
                        }
                    }
                    continue;
                }
                if (this.loading.contains(index)) {
                    sb.append('%');
                    continue;
                }
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Message getLoadedChunksMessage() {
        long stamp = this.loadedLock.readLock();
        try {
            Message message = Message.translation("server.commands.chunkTracker.loaded").monospace(true).param("grid", this.getLoadedChunksGrid()).param("viewRadius", this.chunkViewRadius).param("sentViewRadius", this.sentViewRadius).param("hotRadius", this.hotRadius).param("readyForChunks", this.readyForChunks).param("loaded", this.loaded.size()).param("loading", this.loading.size());
            return message;
        }
        finally {
            this.loadedLock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public String getLoadedChunksDebug() {
        long stamp = this.loadedLock.readLock();
        try {
            String string = "Chunks (#: Loaded, &: Loading, ' ': Not loaded):\n" + this.getLoadedChunksGrid() + "\nView Radius: " + this.chunkViewRadius + "\nSent View Radius: " + this.sentViewRadius + "\nHot Radius: " + this.hotRadius + "\nReady For Chunks: " + this.readyForChunks + "\nLoaded: " + this.loaded.size() + "\nLoading: " + this.loading.size();
            return string;
        }
        finally {
            this.loadedLock.unlockRead(stamp);
        }
    }

    public void setReadyForChunks(boolean readyForChunks) {
        this.readyForChunks = readyForChunks;
    }

    public boolean isReadyForChunks() {
        return this.readyForChunks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void copyFrom(@Nonnull ChunkTracker chunkTracker) {
        long stamp = this.loadedLock.writeLock();
        try {
            long otherStamp = chunkTracker.loadedLock.readLock();
            try {
                this.loading.addAll(chunkTracker.loading);
                this.loaded.addAll(chunkTracker.loaded);
                this.reload.addAll(chunkTracker.reload);
                this.sentViewRadius = 0;
            }
            finally {
                chunkTracker.loadedLock.unlockRead(otherStamp);
            }
        }
        finally {
            this.loadedLock.unlockWrite(stamp);
        }
    }

    private static boolean shouldBeVisible(int chunkViewRadiusSquared, int chunkX, int chunkZ, int x, int z) {
        int zDiff;
        int xDiff = Math.abs(x - chunkX);
        int distanceSq = xDiff * xDiff + (zDiff = Math.abs(z - chunkZ)) * zDiff;
        return distanceSq <= chunkViewRadiusSquared;
    }

    public static boolean tryUnloadChunk(long chunkIndex, int chunkViewRadiusSquared, int chunkX, int chunkZ, @Nonnull PlayerRef playerRef, @Nonnull LongSet loading) {
        int z;
        int x = ChunkUtil.xOfChunkIndex(chunkIndex);
        if (ChunkTracker.shouldBeVisible(chunkViewRadiusSquared, x, z = ChunkUtil.zOfChunkIndex(chunkIndex), chunkX, chunkZ)) {
            return false;
        }
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            loading.remove(chunkIndex);
            return true;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        PacketHandler packetHandler = playerRef.getPacketHandler();
        ChunkStore chunkComponentStore = world.getChunkStore();
        Ref<ChunkStore> chunkRef = chunkComponentStore.getChunkReference(chunkIndex);
        if (chunkRef != null) {
            Store<ChunkStore> chunkStore = chunkComponentStore.getStore();
            ObjectArrayList packets = new ObjectArrayList();
            chunkStore.fetch(Collections.singletonList(chunkRef), ChunkStore.UNLOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE, playerRef, packets);
            for (int i = 0; i < packets.size(); ++i) {
                packetHandler.write((Packet)packets.get(i));
            }
        }
        packetHandler.writeNoCache(new UnloadChunk(x, z));
        loading.remove(chunkIndex);
        return true;
    }

    public void tryLoadChunkAsync(@Nonnull ChunkStore chunkStore, @Nonnull PlayerRef playerRefComponent, long chunkIndex, @Nonnull TransformComponent transformComponent, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        World world = componentAccessor.getExternalData().getWorld();
        Vector3d position = transformComponent.getPosition();
        int chunkX = MathUtil.floor(position.getX()) >> 5;
        int chunkZ = MathUtil.floor(position.getZ()) >> 5;
        int x = ChunkUtil.xOfChunkIndex(chunkIndex);
        int z = ChunkUtil.zOfChunkIndex(chunkIndex);
        boolean isHot = ChunkTracker.shouldBeVisible(this.maxHotLoadedChunksRadius, chunkX, chunkZ, x, z);
        Ref<ChunkStore> chunkReference = chunkStore.getChunkReference(chunkIndex);
        if (chunkReference != null) {
            WorldChunk worldChunkComponent = chunkStore.getStore().getComponent(chunkReference, WorldChunk.getComponentType());
            assert (worldChunkComponent != null);
            if (worldChunkComponent.is(ChunkFlag.TICKING)) {
                this._loadChunkAsync(chunkIndex, playerRefComponent, chunkReference, chunkStore);
                return;
            }
        }
        int flags = -2147483632;
        if (isHot) {
            flags |= 4;
        }
        ((CompletableFuture)chunkStore.getChunkReferenceAsync(chunkIndex, flags).thenComposeAsync(reference -> {
            if (reference == null || !reference.isValid()) {
                long stamp = this.loadedLock.writeLock();
                try {
                    this.loading.remove(chunkIndex);
                }
                finally {
                    this.loadedLock.unlockWrite(stamp);
                }
                return CompletableFuture.completedFuture(null);
            }
            long stamp = this.loadedLock.readLock();
            try {
                if (!this.loading.contains(chunkIndex)) {
                    CompletableFuture<Object> completableFuture = CompletableFuture.completedFuture(null);
                    return completableFuture;
                }
            }
            finally {
                this.loadedLock.unlockRead(stamp);
            }
            return this._loadChunkAsync(chunkIndex, playerRefComponent, (Ref<ChunkStore>)reference, chunkStore);
        }, (Executor)world)).exceptionallyAsync(throwable -> {
            long stamp = this.loadedLock.writeLock();
            try {
                this.loading.remove(chunkIndex);
            }
            finally {
                this.loadedLock.unlockWrite(stamp);
            }
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause((Throwable)throwable)).log("Failed to load chunk! %s, %s", chunkX, chunkZ);
            return null;
        });
    }

    @Nonnull
    private CompletableFuture<Void> _loadChunkAsync(long chunkIndex, @Nonnull PlayerRef playerRefComponent, @Nonnull Ref<ChunkStore> chunkRef, @Nonnull ChunkStore chunkComponentStore) {
        ObjectArrayList packets = new ObjectArrayList();
        chunkComponentStore.getStore().fetch(Collections.singletonList(chunkRef), ChunkStore.LOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE, playerRefComponent, packets);
        ObjectArrayList futurePackets = new ObjectArrayList();
        chunkComponentStore.getStore().fetch(Collections.singletonList(chunkRef), ChunkStore.LOAD_FUTURE_PACKETS_DATA_QUERY_SYSTEM_TYPE, playerRefComponent, futurePackets);
        return CompletableFuture.allOf((CompletableFuture[])futurePackets.toArray(CompletableFuture[]::new)).thenAcceptAsync(o -> {
            for (CompletableFuture futurePacket : futurePackets) {
                Packet packet = (Packet)futurePacket.join();
                if (packet == null) continue;
                packets.add(packet);
            }
            long writeStamp = this.loadedLock.writeLock();
            try {
                if (this.loading.remove(chunkIndex)) {
                    for (int i = 0; i < packets.size(); ++i) {
                        playerRefComponent.getPacketHandler().write((Packet)packets.get(i));
                    }
                    this.loaded.add(chunkIndex);
                }
            }
            finally {
                this.loadedLock.unlockWrite(writeStamp);
            }
        });
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        return new ChunkTracker(this);
    }

    public static enum ChunkVisibility {
        NONE,
        HOT,
        COLD;

    }
}

