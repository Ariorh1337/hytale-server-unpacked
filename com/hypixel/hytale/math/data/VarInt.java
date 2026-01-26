/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.annotation.Nonnull;

public final class VarInt {
    private VarInt() {
        throw new UnsupportedOperationException("Do not instantiate.");
    }

    public static void writeSignedVarLong(long value, @Nonnull DataOutput out) throws IOException {
        VarInt.writeUnsignedVarLong(value << 1 ^ value >> 63, out);
    }

    public static void writeUnsignedVarLong(long value, @Nonnull DataOutput out) throws IOException {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
            out.writeByte((int)value & 0x7F | 0x80);
            value >>>= 7;
        }
        out.writeByte((int)value & 0x7F);
    }

    public static void writeSignedVarInt(int value, @Nonnull DataOutput out) throws IOException {
        VarInt.writeUnsignedVarInt(value << 1 ^ value >> 31, out);
    }

    public static void writeUnsignedVarInt(int value, @Nonnull DataOutput out) throws IOException {
        while ((long)(value & 0xFFFFFF80) != 0L) {
            out.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
        out.writeByte(value & 0x7F);
    }

    public static byte[] writeSignedVarInt(int value) {
        return VarInt.writeUnsignedVarInt(value << 1 ^ value >> 31);
    }

    public static byte[] writeUnsignedVarInt(int value) {
        byte[] byteArrayList = new byte[10];
        int i = 0;
        while ((long)(value & 0xFFFFFF80) != 0L) {
            byteArrayList[i++] = (byte)(value & 0x7F | 0x80);
            value >>>= 7;
        }
        byteArrayList[i] = (byte)(value & 0x7F);
        byte[] out = new byte[i + 1];
        while (i >= 0) {
            out[i] = byteArrayList[i];
            --i;
        }
        return out;
    }

    public static long readSignedVarLong(@Nonnull DataInput in) throws IOException {
        long raw = VarInt.readUnsignedVarLong(in);
        long temp = (raw << 63 >> 63 ^ raw) >> 1;
        return temp ^ raw & Long.MIN_VALUE;
    }

    public static long readUnsignedVarLong(@Nonnull DataInput in) throws IOException {
        long b;
        long value = 0L;
        int i = 0;
        while (((b = (long)in.readByte()) & 0x80L) != 0L) {
            value |= (b & 0x7FL) << i;
            if ((i += 7) <= 63) continue;
            throw new IllegalArgumentException("Variable length quantity is too long");
        }
        return value | b << i;
    }

    public static int readSignedVarInt(@Nonnull DataInput in) throws IOException {
        int raw = VarInt.readUnsignedVarInt(in);
        int temp = (raw << 31 >> 31 ^ raw) >> 1;
        return temp ^ raw & Integer.MIN_VALUE;
    }

    public static int readUnsignedVarInt(@Nonnull DataInput in) throws IOException {
        byte b;
        int value = 0;
        int i = 0;
        while (((b = in.readByte()) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            if ((i += 7) <= 35) continue;
            throw new IllegalArgumentException("Variable length quantity is too long");
        }
        return value | b << i;
    }

    public static int readSignedVarInt(@Nonnull byte[] bytes) {
        int raw = VarInt.readUnsignedVarInt(bytes);
        int temp = (raw << 31 >> 31 ^ raw) >> 1;
        return temp ^ raw & Integer.MIN_VALUE;
    }

    public static int readUnsignedVarInt(@Nonnull byte[] bytes) {
        int value = 0;
        int i = 0;
        int rb = -128;
        byte[] byArray = bytes;
        int n = byArray.length;
        for (int j = 0; j < n; ++j) {
            int b;
            rb = b = byArray[j];
            if ((b & 0x80) == 0) break;
            value |= (b & 0x7F) << i;
            if ((i += 7) <= 35) continue;
            throw new IllegalArgumentException("Variable length quantity is too long");
        }
        return value | rb << i;
    }
}

