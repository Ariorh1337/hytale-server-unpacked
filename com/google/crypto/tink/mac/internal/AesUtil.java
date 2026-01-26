/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.mac.internal;

import java.util.Arrays;

public final class AesUtil {
    public static final int BLOCK_SIZE = 16;

    public static byte[] dbl(byte[] value) {
        if (value.length != 16) {
            throw new IllegalArgumentException("value must be a block.");
        }
        byte[] res = new byte[16];
        for (int i = 0; i < 16; ++i) {
            res[i] = (byte)(0xFE & value[i] << 1);
            if (i >= 15) continue;
            int n = i;
            res[n] = (byte)(res[n] | (byte)(1 & value[i + 1] >> 7));
        }
        res[15] = (byte)(res[15] ^ (byte)(0x87 & value[0] >> 7));
        return res;
    }

    public static byte[] cmacPad(byte[] x) {
        if (x.length >= 16) {
            throw new IllegalArgumentException("x must be smaller than a block.");
        }
        byte[] result = Arrays.copyOf(x, 16);
        result[x.length] = -128;
        return result;
    }

    private AesUtil() {
    }
}

