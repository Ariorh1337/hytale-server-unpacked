/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.StandardCharset;
import java.util.Arrays;

final class Base64Codec {
    Base64Codec() {
    }

    static int computeEncodedLength(int inputLength, boolean urlSafe) {
        if (inputLength == 0) {
            return 0;
        }
        if (urlSafe) {
            int fullQuadLength = inputLength / 3 << 2;
            int remainder = inputLength % 3;
            return remainder == 0 ? fullQuadLength : fullQuadLength + remainder + 1;
        }
        return (inputLength - 1) / 3 + 1 << 2;
    }

    static int tpSelect(int bool, int when_true, int when_false) {
        int mask = bool - 1;
        return when_true ^ mask & (when_true ^ when_false);
    }

    static int tpLT(int a, int b) {
        return (int)((long)a - (long)b >>> 63);
    }

    static int tpGT(int a, int b) {
        return (int)((long)b - (long)a >>> 63);
    }

    static int tpEq(int a, int b) {
        int bit_diff = a ^ b;
        int msb_iff_zero_diff = bit_diff - 1 & ~bit_diff;
        return msb_iff_zero_diff >>> 63;
    }

    static byte encodeDigitBase64(int digit_idx) {
        assert (digit_idx >= 0 && digit_idx <= 63);
        int is_uppercase = Base64Codec.tpLT(digit_idx, 26);
        int is_lowercase = Base64Codec.tpGT(digit_idx, 25) & Base64Codec.tpLT(digit_idx, 52);
        int is_decimal = Base64Codec.tpGT(digit_idx, 51) & Base64Codec.tpLT(digit_idx, 62);
        int is_62 = Base64Codec.tpEq(digit_idx, 62);
        int is_63 = Base64Codec.tpEq(digit_idx, 63);
        int as_uppercase = digit_idx - 0 + 65;
        int as_lowercase = digit_idx - 26 + 97;
        int as_decimal = digit_idx - 52 + 48;
        int as_62 = 43;
        int as_63 = 47;
        int ascii = Base64Codec.tpSelect(is_uppercase, as_uppercase, 0) | Base64Codec.tpSelect(is_lowercase, as_lowercase, 0) | Base64Codec.tpSelect(is_decimal, as_decimal, 0) | Base64Codec.tpSelect(is_62, 43, 0) | Base64Codec.tpSelect(is_63, 47, 0);
        return (byte)ascii;
    }

    static byte encodeDigitBase64URL(int digit_idx) {
        assert (digit_idx >= 0 && digit_idx <= 63);
        int is_uppercase = Base64Codec.tpLT(digit_idx, 26);
        int is_lowercase = Base64Codec.tpGT(digit_idx, 25) & Base64Codec.tpLT(digit_idx, 52);
        int is_decimal = Base64Codec.tpGT(digit_idx, 51) & Base64Codec.tpLT(digit_idx, 62);
        int is_62 = Base64Codec.tpEq(digit_idx, 62);
        int is_63 = Base64Codec.tpEq(digit_idx, 63);
        int as_uppercase = digit_idx - 0 + 65;
        int as_lowercase = digit_idx - 26 + 97;
        int as_decimal = digit_idx - 52 + 48;
        int as_62 = 45;
        int as_63 = 95;
        int ascii = Base64Codec.tpSelect(is_uppercase, as_uppercase, 0) | Base64Codec.tpSelect(is_lowercase, as_lowercase, 0) | Base64Codec.tpSelect(is_decimal, as_decimal, 0) | Base64Codec.tpSelect(is_62, 45, 0) | Base64Codec.tpSelect(is_63, 95, 0);
        return (byte)ascii;
    }

    static int decodeDigit(byte ascii) {
        int is_uppercase = Base64Codec.tpGT(ascii, 64) & Base64Codec.tpLT(ascii, 91);
        int is_lowercase = Base64Codec.tpGT(ascii, 96) & Base64Codec.tpLT(ascii, 123);
        int is_decimal = Base64Codec.tpGT(ascii, 47) & Base64Codec.tpLT(ascii, 58);
        int is_62 = Base64Codec.tpEq(ascii, 45) | Base64Codec.tpEq(ascii, 43);
        int is_63 = Base64Codec.tpEq(ascii, 95) | Base64Codec.tpEq(ascii, 47);
        int is_valid = is_uppercase | is_lowercase | is_decimal | is_62 | is_63;
        int from_uppercase = ascii - 65 + 0;
        int from_lowercase = ascii - 97 + 26;
        int from_decimal = ascii - 48 + 52;
        int from_62 = 62;
        int from_63 = 63;
        int digit_idx = Base64Codec.tpSelect(is_uppercase, from_uppercase, 0) | Base64Codec.tpSelect(is_lowercase, from_lowercase, 0) | Base64Codec.tpSelect(is_decimal, from_decimal, 0) | Base64Codec.tpSelect(is_62, 62, 0) | Base64Codec.tpSelect(is_63, 63, 0) | Base64Codec.tpSelect(is_valid, 0, -1);
        assert (digit_idx >= -1 && digit_idx <= 63);
        return digit_idx;
    }

    public static String encodeToString(byte[] byteArray, boolean urlSafe) {
        int sLen;
        int n = sLen = byteArray != null ? byteArray.length : 0;
        if (sLen == 0) {
            return "";
        }
        int eLen = sLen / 3 * 3;
        int dLen = Base64Codec.computeEncodedLength(sLen, urlSafe);
        byte[] out = new byte[dLen];
        int s = 0;
        int d = 0;
        while (s < eLen) {
            int i = (byteArray[s++] & 0xFF) << 16 | (byteArray[s++] & 0xFF) << 8 | byteArray[s++] & 0xFF;
            if (urlSafe) {
                out[d++] = Base64Codec.encodeDigitBase64URL(i >>> 18 & 0x3F);
                out[d++] = Base64Codec.encodeDigitBase64URL(i >>> 12 & 0x3F);
                out[d++] = Base64Codec.encodeDigitBase64URL(i >>> 6 & 0x3F);
                out[d++] = Base64Codec.encodeDigitBase64URL(i & 0x3F);
                continue;
            }
            out[d++] = Base64Codec.encodeDigitBase64(i >>> 18 & 0x3F);
            out[d++] = Base64Codec.encodeDigitBase64(i >>> 12 & 0x3F);
            out[d++] = Base64Codec.encodeDigitBase64(i >>> 6 & 0x3F);
            out[d++] = Base64Codec.encodeDigitBase64(i & 0x3F);
        }
        int left = sLen - eLen;
        if (left > 0) {
            int i = (byteArray[eLen] & 0xFF) << 10 | (left == 2 ? (byteArray[sLen - 1] & 0xFF) << 2 : 0);
            if (urlSafe) {
                if (left == 2) {
                    out[dLen - 3] = Base64Codec.encodeDigitBase64URL(i >> 12);
                    out[dLen - 2] = Base64Codec.encodeDigitBase64URL(i >>> 6 & 0x3F);
                    out[dLen - 1] = Base64Codec.encodeDigitBase64URL(i & 0x3F);
                } else {
                    out[dLen - 2] = Base64Codec.encodeDigitBase64URL(i >> 12);
                    out[dLen - 1] = Base64Codec.encodeDigitBase64URL(i >>> 6 & 0x3F);
                }
            } else {
                out[dLen - 4] = Base64Codec.encodeDigitBase64(i >> 12);
                out[dLen - 3] = Base64Codec.encodeDigitBase64(i >>> 6 & 0x3F);
                out[dLen - 2] = left == 2 ? (int)Base64Codec.encodeDigitBase64(i & 0x3F) : 61;
                out[dLen - 1] = 61;
            }
        }
        return new String(out, StandardCharset.UTF_8);
    }

    public static byte[] decode(String b64String) {
        if (b64String == null || b64String.isEmpty()) {
            return new byte[0];
        }
        byte[] srcBytes = b64String.getBytes(StandardCharset.UTF_8);
        int sLen = srcBytes.length;
        int maxOutputLen = Base64Codec.checkedCast((long)sLen * 6L >> 3);
        byte[] dstBytes = new byte[maxOutputLen];
        int d = 0;
        int s = 0;
        while (s < srcBytes.length) {
            int i = 0;
            int j = 0;
            while (j < 4 && s < sLen) {
                int c;
                if ((c = Base64Codec.decodeDigit(srcBytes[s++])) < 0) continue;
                i |= c << 18 - j * 6;
                ++j;
            }
            if (j < 2) continue;
            dstBytes[d++] = (byte)(i >> 16);
            if (j < 3) continue;
            dstBytes[d++] = (byte)(i >> 8);
            if (j < 4) continue;
            dstBytes[d++] = (byte)i;
        }
        return Arrays.copyOf(dstBytes, d);
    }

    private static int checkedCast(long value) {
        int result = (int)value;
        if ((long)result != value) {
            throw new IllegalArgumentException(value + " cannot be cast to int without changing its value.");
        }
        return result;
    }
}

