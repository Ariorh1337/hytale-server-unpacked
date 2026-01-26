/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

public interface ByteBufAllocatorMetric {
    public long usedHeapMemory();

    public long usedDirectMemory();
}

