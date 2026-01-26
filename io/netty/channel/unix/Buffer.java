/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.unix;

import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Buffer {
    private Buffer() {
    }

    @Deprecated
    public static void free(ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer(buffer);
    }

    @Deprecated
    public static ByteBuffer allocateDirectWithNativeOrder(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    }

    public static CleanableDirectBuffer allocateDirectBufferWithNativeOrder(int capacity) {
        CleanableDirectBuffer cleanableDirectBuffer = PlatformDependent.allocateDirect(capacity);
        cleanableDirectBuffer.buffer().order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        return cleanableDirectBuffer;
    }

    public static long memoryAddress(ByteBuffer buffer) {
        assert (buffer.isDirect());
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.directBufferAddress(buffer);
        }
        return Buffer.memoryAddress0(buffer);
    }

    public static int addressSize() {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.addressSize();
        }
        return Buffer.addressSize0();
    }

    private static native int addressSize0();

    private static native long memoryAddress0(ByteBuffer var0);

    public static ByteBuffer wrapMemoryAddressWithNativeOrder(long memoryAddress, int capacity) {
        return Buffer.wrapMemoryAddress(memoryAddress, capacity).order(ByteOrder.nativeOrder());
    }

    public static native ByteBuffer wrapMemoryAddress(long var0, int var2);
}

