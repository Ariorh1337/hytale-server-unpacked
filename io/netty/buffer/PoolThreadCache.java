/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

final class PoolThreadCache {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
    private static final int INTEGER_SIZE_MINUS_ONE = 31;
    final PoolArena<byte[]> heapArena;
    final PoolArena<ByteBuffer> directArena;
    private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
    private final MemoryRegionCache<byte[]>[] normalHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
    private final int freeSweepAllocationThreshold;
    private final AtomicBoolean freed = new AtomicBoolean();
    private final FreeOnFinalize freeOnFinalize;
    private int allocations;

    PoolThreadCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold, boolean useFinalizer) {
        ObjectUtil.checkPositiveOrZero(maxCachedBufferCapacity, "maxCachedBufferCapacity");
        this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
        this.heapArena = heapArena;
        this.directArena = directArena;
        if (directArena != null) {
            this.smallSubPageDirectCaches = PoolThreadCache.createSubPageCaches(smallCacheSize, directArena.sizeClass.nSubpages);
            this.normalDirectCaches = PoolThreadCache.createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
            directArena.numThreadCaches.getAndIncrement();
        } else {
            this.smallSubPageDirectCaches = null;
            this.normalDirectCaches = null;
        }
        if (heapArena != null) {
            this.smallSubPageHeapCaches = PoolThreadCache.createSubPageCaches(smallCacheSize, heapArena.sizeClass.nSubpages);
            this.normalHeapCaches = PoolThreadCache.createNormalCaches(normalCacheSize, maxCachedBufferCapacity, heapArena);
            heapArena.numThreadCaches.getAndIncrement();
        } else {
            this.smallSubPageHeapCaches = null;
            this.normalHeapCaches = null;
        }
        if ((this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null) && freeSweepAllocationThreshold < 1) {
            throw new IllegalArgumentException("freeSweepAllocationThreshold: " + freeSweepAllocationThreshold + " (expected: > 0)");
        }
        this.freeOnFinalize = useFinalizer ? new FreeOnFinalize(this) : null;
    }

    private static <T> MemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches) {
        if (cacheSize > 0 && numCaches > 0) {
            MemoryRegionCache[] cache = new MemoryRegionCache[numCaches];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new SubPageMemoryRegionCache(cacheSize);
            }
            return cache;
        }
        return null;
    }

    private static <T> MemoryRegionCache<T>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<T> area) {
        if (cacheSize > 0 && maxCachedBufferCapacity > 0) {
            int max = Math.min(area.sizeClass.chunkSize, maxCachedBufferCapacity);
            ArrayList cache = new ArrayList();
            for (int idx = area.sizeClass.nSubpages; idx < area.sizeClass.nSizes && area.sizeClass.sizeIdx2size(idx) <= max; ++idx) {
                cache.add(new NormalMemoryRegionCache(cacheSize));
            }
            return cache.toArray(new MemoryRegionCache[0]);
        }
        return null;
    }

    static int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }

    boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int sizeIdx) {
        return this.allocate(this.cacheForSmall(area, sizeIdx), buf, reqCapacity);
    }

    boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int sizeIdx) {
        return this.allocate(this.cacheForNormal(area, sizeIdx), buf, reqCapacity);
    }

    private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf buf, int reqCapacity) {
        if (cache == null) {
            return false;
        }
        boolean allocated = cache.allocate(buf, reqCapacity, this);
        if (++this.allocations >= this.freeSweepAllocationThreshold) {
            this.allocations = 0;
            this.trim();
        }
        return allocated;
    }

    boolean add(PoolArena<?> area, PoolChunk chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolArena.SizeClass sizeClass) {
        int sizeIdx = area.sizeClass.size2SizeIdx(normCapacity);
        MemoryRegionCache<?> cache = this.cache(area, sizeIdx, sizeClass);
        if (cache == null) {
            return false;
        }
        if (this.freed.get()) {
            return false;
        }
        return cache.add(chunk, nioBuffer, handle, normCapacity);
    }

    private MemoryRegionCache<?> cache(PoolArena<?> area, int sizeIdx, PoolArena.SizeClass sizeClass) {
        switch (sizeClass) {
            case Normal: {
                return this.cacheForNormal(area, sizeIdx);
            }
            case Small: {
                return this.cacheForSmall(area, sizeIdx);
            }
        }
        throw new Error("Unexpected size class: " + (Object)((Object)sizeClass));
    }

    void free(boolean finalizer) {
        if (this.freed.compareAndSet(false, true)) {
            int numFreed;
            if (this.freeOnFinalize != null) {
                this.freeOnFinalize.cache = null;
            }
            if ((numFreed = PoolThreadCache.free(this.smallSubPageDirectCaches, finalizer) + PoolThreadCache.free(this.normalDirectCaches, finalizer) + PoolThreadCache.free(this.smallSubPageHeapCaches, finalizer) + PoolThreadCache.free(this.normalHeapCaches, finalizer)) > 0 && logger.isDebugEnabled()) {
                logger.debug("Freed {} thread-local buffer(s) from thread: {}", (Object)numFreed, (Object)Thread.currentThread().getName());
            }
            if (this.directArena != null) {
                this.directArena.numThreadCaches.getAndDecrement();
            }
            if (this.heapArena != null) {
                this.heapArena.numThreadCaches.getAndDecrement();
            }
        }
    }

    private static int free(MemoryRegionCache<?>[] caches, boolean finalizer) {
        if (caches == null) {
            return 0;
        }
        int numFreed = 0;
        for (MemoryRegionCache<?> c : caches) {
            numFreed += PoolThreadCache.free(c, finalizer);
        }
        return numFreed;
    }

    private static int free(MemoryRegionCache<?> cache, boolean finalizer) {
        if (cache == null) {
            return 0;
        }
        return cache.free(finalizer);
    }

    void trim() {
        PoolThreadCache.trim(this.smallSubPageDirectCaches);
        PoolThreadCache.trim(this.normalDirectCaches);
        PoolThreadCache.trim(this.smallSubPageHeapCaches);
        PoolThreadCache.trim(this.normalHeapCaches);
    }

    private static void trim(MemoryRegionCache<?>[] caches) {
        if (caches == null) {
            return;
        }
        for (MemoryRegionCache<?> c : caches) {
            PoolThreadCache.trim(c);
        }
    }

    private static void trim(MemoryRegionCache<?> cache) {
        if (cache == null) {
            return;
        }
        cache.trim();
    }

    private MemoryRegionCache<?> cacheForSmall(PoolArena<?> area, int sizeIdx) {
        if (area.isDirect()) {
            return PoolThreadCache.cache(this.smallSubPageDirectCaches, sizeIdx);
        }
        return PoolThreadCache.cache(this.smallSubPageHeapCaches, sizeIdx);
    }

    private MemoryRegionCache<?> cacheForNormal(PoolArena<?> area, int sizeIdx) {
        int idx = sizeIdx - area.sizeClass.nSubpages;
        if (area.isDirect()) {
            return PoolThreadCache.cache(this.normalDirectCaches, idx);
        }
        return PoolThreadCache.cache(this.normalHeapCaches, idx);
    }

    private static <T> MemoryRegionCache<T> cache(MemoryRegionCache<T>[] cache, int sizeIdx) {
        if (cache == null || sizeIdx >= cache.length) {
            return null;
        }
        return cache[sizeIdx];
    }

    private static final class FreeOnFinalize {
        private volatile PoolThreadCache cache;

        private FreeOnFinalize(PoolThreadCache cache) {
            this.cache = cache;
        }

        protected void finalize() throws Throwable {
            try {
                super.finalize();
            }
            finally {
                PoolThreadCache cache = this.cache;
                this.cache = null;
                if (cache != null) {
                    cache.free(true);
                }
            }
        }
    }

    private static abstract class MemoryRegionCache<T> {
        private final int size;
        private final Queue<Entry<T>> queue;
        private final PoolArena.SizeClass sizeClass;
        private int allocations;
        private static final Recycler<Entry> RECYCLER = new Recycler<Entry>(){

            @Override
            protected Entry newObject(Recycler.Handle<Entry> handle) {
                return new Entry(handle);
            }
        };

        MemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
            this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
            this.queue = PlatformDependent.newFixedMpscUnpaddedQueue(this.size);
            this.sizeClass = sizeClass;
        }

        protected abstract void initBuf(PoolChunk<T> var1, ByteBuffer var2, long var3, PooledByteBuf<T> var5, int var6, PoolThreadCache var7);

        public final boolean add(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int normCapacity) {
            Entry entry = MemoryRegionCache.newEntry(chunk, nioBuffer, handle, normCapacity);
            boolean queued = this.queue.offer(entry);
            if (!queued) {
                entry.unguardedRecycle();
            }
            return queued;
        }

        public final boolean allocate(PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
            Entry<T> entry = this.queue.poll();
            if (entry == null) {
                return false;
            }
            this.initBuf(entry.chunk, entry.nioBuffer, entry.handle, buf, reqCapacity, threadCache);
            entry.unguardedRecycle();
            ++this.allocations;
            return true;
        }

        public final int free(boolean finalizer) {
            return this.free(Integer.MAX_VALUE, finalizer);
        }

        private int free(int max, boolean finalizer) {
            int numFreed;
            for (numFreed = 0; numFreed < max; ++numFreed) {
                Entry<T> entry = this.queue.poll();
                if (entry == null) {
                    return numFreed;
                }
                this.freeEntry(entry, finalizer);
            }
            return numFreed;
        }

        public final void trim() {
            int free = this.size - this.allocations;
            this.allocations = 0;
            if (free > 0) {
                this.free(free, false);
            }
        }

        private void freeEntry(Entry entry, boolean finalizer) {
            PoolChunk chunk = entry.chunk;
            long handle = entry.handle;
            ByteBuffer nioBuffer = entry.nioBuffer;
            int normCapacity = entry.normCapacity;
            if (!finalizer) {
                entry.recycle();
            }
            chunk.arena.freeChunk(chunk, handle, normCapacity, this.sizeClass, nioBuffer, finalizer);
        }

        private static Entry newEntry(PoolChunk<?> chunk, ByteBuffer nioBuffer, long handle, int normCapacity) {
            Entry entry = RECYCLER.get();
            entry.chunk = chunk;
            entry.nioBuffer = nioBuffer;
            entry.handle = handle;
            entry.normCapacity = normCapacity;
            return entry;
        }

        static final class Entry<T> {
            final Recycler.EnhancedHandle<Entry<?>> recyclerHandle;
            PoolChunk<T> chunk;
            ByteBuffer nioBuffer;
            long handle = -1L;
            int normCapacity;

            Entry(ObjectPool.Handle<Entry<?>> recyclerHandle) {
                this.recyclerHandle = (Recycler.EnhancedHandle)recyclerHandle;
            }

            void recycle() {
                this.chunk = null;
                this.nioBuffer = null;
                this.handle = -1L;
                this.recyclerHandle.recycle(this);
            }

            void unguardedRecycle() {
                this.chunk = null;
                this.nioBuffer = null;
                this.handle = -1L;
                this.recyclerHandle.unguardedRecycle(this);
            }
        }
    }

    private static final class NormalMemoryRegionCache<T>
    extends MemoryRegionCache<T> {
        NormalMemoryRegionCache(int size) {
            super(size, PoolArena.SizeClass.Normal);
        }

        @Override
        protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
            chunk.initBuf(buf, nioBuffer, handle, reqCapacity, threadCache, true);
        }
    }

    private static final class SubPageMemoryRegionCache<T>
    extends MemoryRegionCache<T> {
        SubPageMemoryRegionCache(int size) {
            super(size, PoolArena.SizeClass.Small);
        }

        @Override
        protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
            chunk.initBufWithSubpage(buf, nioBuffer, handle, reqCapacity, threadCache, true);
        }
    }
}

