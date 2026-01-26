/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class VarHandleByteBufferAccess {
    private VarHandleByteBufferAccess() {
    }

    static short getShortBE(ByteBuffer buffer, int index) {
        return PlatformDependent.shortBeByteBufferView().get(buffer, index);
    }

    static void setShortBE(ByteBuffer buffer, int index, int value) {
        PlatformDependent.shortBeByteBufferView().set(buffer, index, (short)value);
    }

    static short getShortLE(ByteBuffer buffer, int index) {
        return PlatformDependent.shortLeByteBufferView().get(buffer, index);
    }

    static void setShortLE(ByteBuffer buffer, int index, int value) {
        PlatformDependent.shortLeByteBufferView().set(buffer, index, (short)value);
    }

    static int getIntBE(ByteBuffer buffer, int index) {
        return PlatformDependent.intBeByteBufferView().get(buffer, index);
    }

    static void setIntBE(ByteBuffer buffer, int index, int value) {
        PlatformDependent.intBeByteBufferView().set(buffer, index, value);
    }

    static int getIntLE(ByteBuffer buffer, int index) {
        return PlatformDependent.intLeByteBufferView().get(buffer, index);
    }

    static void setIntLE(ByteBuffer buffer, int index, int value) {
        PlatformDependent.intLeByteBufferView().set(buffer, index, value);
    }

    static long getLongBE(ByteBuffer buffer, int index) {
        return PlatformDependent.longBeByteBufferView().get(buffer, index);
    }

    static void setLongBE(ByteBuffer buffer, int index, long value) {
        PlatformDependent.longBeByteBufferView().set(buffer, index, value);
    }

    static long getLongLE(ByteBuffer buffer, int index) {
        return PlatformDependent.longLeByteBufferView().get(buffer, index);
    }

    static void setLongLE(ByteBuffer buffer, int index, long value) {
        PlatformDependent.longLeByteBufferView().set(buffer, index, value);
    }

    static short getShortBE(byte[] memory, int index) {
        return PlatformDependent.shortBeArrayView().get(memory, index);
    }

    static void setShortBE(byte[] memory, int index, int value) {
        PlatformDependent.shortBeArrayView().set(memory, index, (short)value);
    }

    static short getShortLE(byte[] memory, int index) {
        return PlatformDependent.shortLeArrayView().get(memory, index);
    }

    static void setShortLE(byte[] memory, int index, int value) {
        PlatformDependent.shortLeArrayView().set(memory, index, (short)value);
    }

    static int getIntBE(byte[] memory, int index) {
        return PlatformDependent.intBeArrayView().get(memory, index);
    }

    static void setIntBE(byte[] memory, int index, int value) {
        PlatformDependent.intBeArrayView().set(memory, index, value);
    }

    static int getIntLE(byte[] memory, int index) {
        return PlatformDependent.intLeArrayView().get(memory, index);
    }

    static void setIntLE(byte[] memory, int index, int value) {
        PlatformDependent.intLeArrayView().set(memory, index, value);
    }

    static long getLongBE(byte[] memory, int index) {
        return PlatformDependent.longBeArrayView().get(memory, index);
    }

    static void setLongBE(byte[] memory, int index, long value) {
        PlatformDependent.longBeArrayView().set(memory, index, value);
    }

    static long getLongLE(byte[] memory, int index) {
        return PlatformDependent.longLeArrayView().get(memory, index);
    }

    static void setLongLE(byte[] memory, int index, long value) {
        PlatformDependent.longLeArrayView().set(memory, index, value);
    }
}

