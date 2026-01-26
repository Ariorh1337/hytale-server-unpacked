/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BufferPool;
import com.github.luben.zstd.ZstdInputStreamNoFinalizer;
import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RecyclingBufferPool
implements BufferPool {
    public static final BufferPool INSTANCE = new RecyclingBufferPool();
    private static final int buffSize = Math.max(Math.max((int)ZstdOutputStreamNoFinalizer.recommendedCOutSize(), (int)ZstdInputStreamNoFinalizer.recommendedDInSize()), (int)ZstdInputStreamNoFinalizer.recommendedDOutSize());
    private final ConcurrentLinkedQueue<SoftReference<ByteBuffer>> pool = new ConcurrentLinkedQueue();

    private RecyclingBufferPool() {
    }

    @Override
    public ByteBuffer get(int n) {
        SoftReference<ByteBuffer> softReference;
        ByteBuffer byteBuffer;
        if (n > buffSize) {
            throw new RuntimeException("Unsupported buffer size: " + n + ". Supported buffer sizes: " + buffSize + " or smaller.");
        }
        do {
            if ((softReference = this.pool.poll()) != null) continue;
            return ByteBuffer.allocate(buffSize);
        } while ((byteBuffer = softReference.get()) == null);
        return byteBuffer;
    }

    @Override
    public void release(ByteBuffer byteBuffer) {
        if (byteBuffer.capacity() >= buffSize) {
            byteBuffer.clear();
            this.pool.add(new SoftReference<ByteBuffer>(byteBuffer));
        }
    }
}

