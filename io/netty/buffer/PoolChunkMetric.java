/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

public interface PoolChunkMetric {
    public int usage();

    public int chunkSize();

    public int freeBytes();
}

