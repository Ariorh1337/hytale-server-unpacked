/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.io;

import com.hypixel.hytale.protocol.io.ProtocolException;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public final class VarInt {
    private VarInt() {
    }

    public static void write(@Nonnull ByteBuf buf, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("VarInt cannot encode negative values: " + value);
        }
        while ((value & 0xFFFFFF80) != 0) {
            buf.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
        buf.writeByte(value);
    }

    public static int read(@Nonnull ByteBuf buf) {
        int value = 0;
        int shift = 0;
        do {
            byte b = buf.readByte();
            value |= (b & 0x7F) << shift;
            if ((b & 0x80) != 0) continue;
            return value;
        } while ((shift += 7) <= 28);
        throw new ProtocolException("VarInt exceeds maximum length (5 bytes)");
    }

    public static int peek(@Nonnull ByteBuf buf, int index) {
        int value = 0;
        int shift = 0;
        int pos = index;
        while (pos < buf.writerIndex()) {
            byte b = buf.getByte(pos++);
            value |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return value;
            }
            if ((shift += 7) <= 28) continue;
            return -1;
        }
        return -1;
    }

    public static int length(@Nonnull ByteBuf buf, int index) {
        int pos = index;
        while (pos < buf.writerIndex()) {
            if ((buf.getByte(pos++) & 0x80) == 0) {
                return pos - index;
            }
            if (pos - index <= 5) continue;
            return -1;
        }
        return -1;
    }

    public static int size(int value) {
        if ((value & 0xFFFFFF80) == 0) {
            return 1;
        }
        if ((value & 0xFFFFC000) == 0) {
            return 2;
        }
        if ((value & 0xFFE00000) == 0) {
            return 3;
        }
        if ((value & 0xF0000000) == 0) {
            return 4;
        }
        return 5;
    }
}

