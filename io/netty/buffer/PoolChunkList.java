/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolChunkListMetric;
import io.netty.buffer.PoolChunkMetric;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class PoolChunkList<T>
implements PoolChunkListMetric {
    private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.emptyIterator();
    private final PoolArena<T> arena;
    private final PoolChunkList<T> nextList;
    private final int minUsage;
    private final int maxUsage;
    private final int maxCapacity;
    private PoolChunk<T> head;
    private final int freeMinThreshold;
    private final int freeMaxThreshold;
    private PoolChunkList<T> prevList;

    PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
        assert (minUsage <= maxUsage);
        this.arena = arena;
        this.nextList = nextList;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
        this.maxCapacity = PoolChunkList.calculateMaxCapacity(minUsage, chunkSize);
        this.freeMinThreshold = maxUsage == 100 ? 0 : (int)((double)chunkSize * (100.0 - (double)maxUsage + 0.99999999) / 100.0);
        this.freeMaxThreshold = minUsage == 100 ? 0 : (int)((double)chunkSize * (100.0 - (double)minUsage + 0.99999999) / 100.0);
    }

    private static int calculateMaxCapacity(int minUsage, int chunkSize) {
        if ((minUsage = PoolChunkList.minUsage0(minUsage)) == 100) {
            return 0;
        }
        return (int)((long)chunkSize * (100L - (long)minUsage) / 100L);
    }

    void prevList(PoolChunkList<T> prevList) {
        assert (this.prevList == null);
        this.prevList = prevList;
    }

    boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolThreadCache threadCache) {
        int normCapacity = this.arena.sizeClass.sizeIdx2size(sizeIdx);
        if (normCapacity > this.maxCapacity) {
            return false;
        }
        PoolChunk<T> cur = this.head;
        while (cur != null) {
            if (cur.allocate(buf, reqCapacity, sizeIdx, threadCache)) {
                if (cur.freeBytes <= this.freeMinThreshold) {
                    this.remove(cur);
                    this.nextList.add(cur);
                }
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    boolean free(PoolChunk<T> chunk, long handle, int normCapacity, ByteBuffer nioBuffer) {
        chunk.free(handle, normCapacity, nioBuffer);
        if (chunk.freeBytes > this.freeMaxThreshold) {
            this.remove(chunk);
            return this.move0(chunk);
        }
        return true;
    }

    private boolean move(PoolChunk<T> chunk) {
        assert (chunk.usage() < this.maxUsage);
        if (chunk.freeBytes > this.freeMaxThreshold) {
            return this.move0(chunk);
        }
        this.add0(chunk);
        return true;
    }

    private boolean move0(PoolChunk<T> chunk) {
        if (this.prevList == null) {
            assert (chunk.usage() == 0);
            return false;
        }
        return super.move(chunk);
    }

    void add(PoolChunk<T> chunk) {
        if (chunk.freeBytes <= this.freeMinThreshold) {
            this.nextList.add(chunk);
            return;
        }
        this.add0(chunk);
    }

    void add0(PoolChunk<T> chunk) {
        chunk.parent = this;
        if (this.head == null) {
            this.head = chunk;
            chunk.prev = null;
            chunk.next = null;
        } else {
            chunk.prev = null;
            chunk.next = this.head;
            this.head.prev = chunk;
            this.head = chunk;
        }
    }

    private void remove(PoolChunk<T> cur) {
        if (cur == this.head) {
            this.head = cur.next;
            if (this.head != null) {
                this.head.prev = null;
            }
        } else {
            PoolChunk next;
            cur.prev.next = next = cur.next;
            if (next != null) {
                next.prev = cur.prev;
            }
        }
    }

    @Override
    public int minUsage() {
        return PoolChunkList.minUsage0(this.minUsage);
    }

    @Override
    public int maxUsage() {
        return Math.min(this.maxUsage, 100);
    }

    private static int minUsage0(int value) {
        return Math.max(1, value);
    }

    @Override
    public Iterator<PoolChunkMetric> iterator() {
        this.arena.lock();
        try {
            if (this.head == null) {
                Iterator<PoolChunkMetric> iterator = EMPTY_METRICS;
                return iterator;
            }
            ArrayList<PoolChunk<T>> metrics = new ArrayList<PoolChunk<T>>();
            PoolChunk<T> cur = this.head;
            do {
                metrics.add(cur);
            } while ((cur = cur.next) != null);
            Iterator<PoolChunkMetric> iterator = metrics.iterator();
            return iterator;
        }
        finally {
            this.arena.unlock();
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        this.arena.lock();
        try {
            if (this.head == null) {
                String string = "none";
                return string;
            }
            PoolChunk<T> cur = this.head;
            while (true) {
                buf.append(cur);
                cur = cur.next;
                if (cur == null) {
                    break;
                }
                buf.append(StringUtil.NEWLINE);
            }
        }
        finally {
            this.arena.unlock();
        }
        return buf.toString();
    }

    void destroy(PoolArena<T> arena) {
        PoolChunk<T> chunk = this.head;
        while (chunk != null) {
            arena.destroyChunk(chunk);
            chunk = chunk.next;
        }
        this.head = null;
    }
}

