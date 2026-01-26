/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import javax.annotation.Nonnull;

public class BitUtil {
    public static void setNibble(@Nonnull byte[] data, int idx, byte b) {
        int fieldIdx = idx >> 1;
        byte val = data[fieldIdx];
        b = (byte)(b & 0xF);
        int i = idx & 1;
        b = (byte)(b << ((i ^ 1) << 2));
        val = (byte)(val & 15 << (i << 2));
        data[fieldIdx] = val = (byte)(val | b);
    }

    public static byte getNibble(@Nonnull byte[] data, int idx) {
        int fieldIdx = idx >> 1;
        byte val = data[fieldIdx];
        int i = idx & 1;
        val = (byte)(val >> ((i ^ 1) << 2));
        val = (byte)(val & 0xF);
        return val;
    }

    public static byte getAndSetNibble(@Nonnull byte[] data, int idx, byte b) {
        int fieldIdx = idx >> 1;
        byte val = data[fieldIdx];
        int i = idx & 1;
        byte oldVal = val;
        oldVal = (byte)(oldVal >> ((i ^ 1) << 2));
        oldVal = (byte)(oldVal & 0xF);
        b = (byte)(b & 0xF);
        b = (byte)(b << ((i ^ 1) << 2));
        val = (byte)(val & 15 << (i << 2));
        data[fieldIdx] = val = (byte)(val | b);
        return oldVal;
    }
}

