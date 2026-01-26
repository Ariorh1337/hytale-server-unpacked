/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.subtle.Bytes;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class Poly1305 {
    public static final int MAC_TAG_SIZE_IN_BYTES = 16;
    public static final int MAC_KEY_SIZE_IN_BYTES = 32;

    private Poly1305() {
    }

    private static long load32(byte[] in, int idx) {
        return (long)(in[idx] & 0xFF | (in[idx + 1] & 0xFF) << 8 | (in[idx + 2] & 0xFF) << 16 | (in[idx + 3] & 0xFF) << 24) & 0xFFFFFFFFL;
    }

    private static long load26(byte[] in, int idx, int shift) {
        return Poly1305.load32(in, idx) >> shift & 0x3FFFFFFL;
    }

    private static void toByteArray(byte[] output, long num, int idx) {
        int i = 0;
        while (i < 4) {
            output[idx + i] = (byte)(num & 0xFFL);
            ++i;
            num >>= 8;
        }
    }

    private static void copyBlockSize(byte[] output, byte[] in, int idx) {
        int copyCount = Math.min(16, in.length - idx);
        System.arraycopy(in, idx, output, 0, copyCount);
        output[copyCount] = 1;
        if (copyCount != 16) {
            Arrays.fill(output, copyCount + 1, output.length, (byte)0);
        }
    }

    public static byte[] computeMac(byte[] key, byte[] data) {
        long c;
        if (key.length != 32) {
            throw new IllegalArgumentException("The key length in bytes must be 32.");
        }
        long h0 = 0L;
        long h1 = 0L;
        long h2 = 0L;
        long h3 = 0L;
        long h4 = 0L;
        long r0 = Poly1305.load26(key, 0, 0) & 0x3FFFFFFL;
        long r1 = Poly1305.load26(key, 3, 2) & 0x3FFFF03L;
        long r2 = Poly1305.load26(key, 6, 4) & 0x3FFC0FFL;
        long r3 = Poly1305.load26(key, 9, 6) & 0x3F03FFFL;
        long r4 = Poly1305.load26(key, 12, 8) & 0xFFFFFL;
        long s1 = r1 * 5L;
        long s2 = r2 * 5L;
        long s3 = r3 * 5L;
        long s4 = r4 * 5L;
        byte[] buf = new byte[17];
        for (int i = 0; i < data.length; i += 16) {
            Poly1305.copyBlockSize(buf, data, i);
            long d0 = (h0 += Poly1305.load26(buf, 0, 0)) * r0 + (h1 += Poly1305.load26(buf, 3, 2)) * s4 + (h2 += Poly1305.load26(buf, 6, 4)) * s3 + (h3 += Poly1305.load26(buf, 9, 6)) * s2 + (h4 += Poly1305.load26(buf, 12, 8) | (long)(buf[16] << 24)) * s1;
            long d1 = h0 * r1 + h1 * r0 + h2 * s4 + h3 * s3 + h4 * s2;
            long d2 = h0 * r2 + h1 * r1 + h2 * r0 + h3 * s4 + h4 * s3;
            long d3 = h0 * r3 + h1 * r2 + h2 * r1 + h3 * r0 + h4 * s4;
            long d4 = h0 * r4 + h1 * r3 + h2 * r2 + h3 * r1 + h4 * r0;
            c = d0 >> 26;
            h0 = d0 & 0x3FFFFFFL;
            d1 += c;
            c = d1 >> 26;
            h1 = d1 & 0x3FFFFFFL;
            d2 += c;
            c = d2 >> 26;
            h2 = d2 & 0x3FFFFFFL;
            d3 += c;
            c = d3 >> 26;
            h3 = d3 & 0x3FFFFFFL;
            d4 += c;
            c = d4 >> 26;
            h4 = d4 & 0x3FFFFFFL;
            h0 += c * 5L;
            c = h0 >> 26;
            h0 &= 0x3FFFFFFL;
            h1 += c;
        }
        c = h1 >> 26;
        h1 &= 0x3FFFFFFL;
        h2 += c;
        c = h2 >> 26;
        h2 &= 0x3FFFFFFL;
        h3 += c;
        c = h3 >> 26;
        h3 &= 0x3FFFFFFL;
        h4 += c;
        c = h4 >> 26;
        h4 &= 0x3FFFFFFL;
        h0 += c * 5L;
        c = h0 >> 26;
        h1 += c;
        long g0 = (h0 &= 0x3FFFFFFL) + 5L;
        c = g0 >> 26;
        g0 &= 0x3FFFFFFL;
        long g1 = h1 + c;
        c = g1 >> 26;
        g1 &= 0x3FFFFFFL;
        long g2 = h2 + c;
        c = g2 >> 26;
        g2 &= 0x3FFFFFFL;
        long g3 = h3 + c;
        c = g3 >> 26;
        g3 &= 0x3FFFFFFL;
        long g4 = h4 + c - 0x4000000L;
        long mask = g4 >> 63;
        h0 &= mask;
        h1 &= mask;
        h2 &= mask;
        h3 &= mask;
        h4 &= mask;
        h0 |= g0 & (mask ^= 0xFFFFFFFFFFFFFFFFL);
        h0 = (h0 | (h1 |= g1 & mask) << 26) & 0xFFFFFFFFL;
        h1 = (h1 >> 6 | (h2 |= g2 & mask) << 20) & 0xFFFFFFFFL;
        h2 = (h2 >> 12 | (h3 |= g3 & mask) << 14) & 0xFFFFFFFFL;
        h3 = (h3 >> 18 | (h4 |= g4 & mask) << 8) & 0xFFFFFFFFL;
        c = h0 + Poly1305.load32(key, 16);
        h0 = c & 0xFFFFFFFFL;
        c = h1 + Poly1305.load32(key, 20) + (c >> 32);
        h1 = c & 0xFFFFFFFFL;
        c = h2 + Poly1305.load32(key, 24) + (c >> 32);
        h2 = c & 0xFFFFFFFFL;
        c = h3 + Poly1305.load32(key, 28) + (c >> 32);
        h3 = c & 0xFFFFFFFFL;
        byte[] mac = new byte[16];
        Poly1305.toByteArray(mac, h0, 0);
        Poly1305.toByteArray(mac, h1, 4);
        Poly1305.toByteArray(mac, h2, 8);
        Poly1305.toByteArray(mac, h3, 12);
        return mac;
    }

    public static void verifyMac(byte[] key, byte[] data, byte[] mac) throws GeneralSecurityException {
        if (!Bytes.equal(Poly1305.computeMac(key, data), mac)) {
            throw new GeneralSecurityException("invalid MAC");
        }
    }
}

