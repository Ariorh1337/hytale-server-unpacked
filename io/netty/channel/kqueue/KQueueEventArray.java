/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.kqueue;

import io.netty.channel.kqueue.Native;
import io.netty.channel.unix.Buffer;
import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class KQueueEventArray {
    private static final int KQUEUE_EVENT_SIZE = Native.sizeofKEvent();
    private static final int KQUEUE_IDENT_OFFSET = Native.offsetofKEventIdent();
    private static final int KQUEUE_FILTER_OFFSET = Native.offsetofKEventFilter();
    private static final int KQUEUE_FFLAGS_OFFSET = Native.offsetofKEventFFlags();
    private static final int KQUEUE_FLAGS_OFFSET = Native.offsetofKEventFlags();
    private static final int KQUEUE_DATA_OFFSET = Native.offsetofKeventData();
    private static final int KQUEUE_UDATA_OFFSET = Native.offsetofKeventUdata();
    private CleanableDirectBuffer memoryCleanable;
    private ByteBuffer memory;
    private long memoryAddress;
    private int size;
    private int capacity;

    KQueueEventArray(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1 but was " + capacity);
        }
        this.memoryCleanable = Buffer.allocateDirectBufferWithNativeOrder(KQueueEventArray.calculateBufferCapacity(capacity));
        this.memory = this.memoryCleanable.buffer();
        this.memoryAddress = Buffer.memoryAddress(this.memory);
        this.capacity = capacity;
    }

    long memoryAddress() {
        return this.memoryAddress;
    }

    int capacity() {
        return this.capacity;
    }

    int size() {
        return this.size;
    }

    void clear() {
        this.size = 0;
    }

    void evSet(int ident, short filter, short flags, int fflags, long data, long udata) {
        this.reallocIfNeeded();
        KQueueEventArray.evSet((long)KQueueEventArray.getKEventOffset(this.size++) + this.memoryAddress, ident, filter, flags, fflags, data, udata);
    }

    private void reallocIfNeeded() {
        if (this.size == this.capacity) {
            this.realloc(true);
        }
    }

    void realloc(boolean throwIfFail) {
        block2: {
            int newLength = this.capacity <= 65536 ? this.capacity << 1 : this.capacity + this.capacity >> 1;
            try {
                int newCapacity = KQueueEventArray.calculateBufferCapacity(newLength);
                CleanableDirectBuffer buffer = Buffer.allocateDirectBufferWithNativeOrder(newCapacity);
                this.memory.position(0).limit(this.size);
                buffer.buffer().put(this.memory);
                buffer.buffer().position(0);
                this.memoryCleanable.clean();
                this.memoryCleanable = buffer;
                this.memory = buffer.buffer();
                this.memoryAddress = Buffer.memoryAddress(this.memory);
            }
            catch (OutOfMemoryError e) {
                if (!throwIfFail) break block2;
                OutOfMemoryError error = new OutOfMemoryError("unable to allocate " + newLength + " new bytes! Existing capacity is: " + this.capacity);
                error.initCause(e);
                throw error;
            }
        }
    }

    void free() {
        this.memoryCleanable.clean();
        this.capacity = 0;
        this.size = 0;
        this.memoryAddress = 0;
    }

    private static int getKEventOffset(int index) {
        return index * KQUEUE_EVENT_SIZE;
    }

    private long getKEventOffsetAddress(int index) {
        return (long)KQueueEventArray.getKEventOffset(index) + this.memoryAddress;
    }

    private short getShort(int index, int offset) {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.getShort(this.getKEventOffsetAddress(index) + (long)offset);
        }
        return this.memory.getShort(KQueueEventArray.getKEventOffset(index) + offset);
    }

    short flags(int index) {
        return this.getShort(index, KQUEUE_FLAGS_OFFSET);
    }

    short filter(int index) {
        return this.getShort(index, KQUEUE_FILTER_OFFSET);
    }

    short fflags(int index) {
        return this.getShort(index, KQUEUE_FFLAGS_OFFSET);
    }

    int ident(int index) {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.getInt(this.getKEventOffsetAddress(index) + (long)KQUEUE_IDENT_OFFSET);
        }
        return this.memory.getInt(KQueueEventArray.getKEventOffset(index) + KQUEUE_IDENT_OFFSET);
    }

    long data(int index) {
        return this.getLong(index, KQUEUE_DATA_OFFSET);
    }

    long udata(int index) {
        return this.getLong(index, KQUEUE_UDATA_OFFSET);
    }

    private long getLong(int index, int offset) {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.getLong(this.getKEventOffsetAddress(index) + (long)offset);
        }
        return this.memory.getLong(KQueueEventArray.getKEventOffset(index) + offset);
    }

    private static int calculateBufferCapacity(int capacity) {
        return capacity * KQUEUE_EVENT_SIZE;
    }

    private static native void evSet(long var0, int var2, short var3, short var4, int var5, long var6, long var8);
}

