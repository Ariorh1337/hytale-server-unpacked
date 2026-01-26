/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import java.nio.ByteBuffer;

public interface CleanableDirectBuffer {
    public ByteBuffer buffer();

    public void clean();

    default public boolean hasMemoryAddress() {
        return false;
    }

    default public long memoryAddress() {
        return 0L;
    }
}

