/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import java.nio.ByteBuffer;

public interface BufferPool {
    public ByteBuffer get(int var1);

    public void release(ByteBuffer var1);
}

