/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

interface ChunkInfo {
    public int capacity();

    public boolean isDirect();

    public long memoryAddress();
}

