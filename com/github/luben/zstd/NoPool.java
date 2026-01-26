/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BufferPool;
import java.nio.ByteBuffer;

public class NoPool
implements BufferPool {
    public static final BufferPool INSTANCE = new NoPool();

    private NoPool() {
    }

    @Override
    public ByteBuffer get(int n) {
        return ByteBuffer.allocate(n);
    }

    @Override
    public void release(ByteBuffer byteBuffer) {
    }
}

