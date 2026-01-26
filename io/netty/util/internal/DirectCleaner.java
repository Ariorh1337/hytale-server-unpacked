/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.Cleaner;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class DirectCleaner
implements Cleaner {
    DirectCleaner() {
    }

    @Override
    public CleanableDirectBuffer allocate(int capacity) {
        return new CleanableDirectBufferImpl(PlatformDependent.allocateDirectNoCleaner(capacity));
    }

    @Override
    public void freeDirectBuffer(ByteBuffer buffer) {
        PlatformDependent.freeDirectNoCleaner(buffer);
    }

    CleanableDirectBuffer reallocate(CleanableDirectBuffer buffer, int capacity) {
        ByteBuffer newByteBuffer = PlatformDependent.reallocateDirectNoCleaner(buffer.buffer(), capacity);
        return new CleanableDirectBufferImpl(newByteBuffer);
    }

    private static final class CleanableDirectBufferImpl
    implements CleanableDirectBuffer {
        private final ByteBuffer buffer;

        private CleanableDirectBufferImpl(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public ByteBuffer buffer() {
            return this.buffer;
        }

        @Override
        public void clean() {
            PlatformDependent.freeDirectNoCleaner(this.buffer);
        }
    }
}

