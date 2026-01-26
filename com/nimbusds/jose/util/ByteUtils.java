/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.IntegerOverflowException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteUtils {
    public static byte[] concat(byte[] ... byteArrays) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (byte[] bytes : byteArrays) {
                if (bytes == null) continue;
                baos.write(bytes);
            }
            return baos.toByteArray();
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static byte[] subArray(byte[] byteArray, int beginIndex, int length) {
        byte[] subArray = new byte[length];
        System.arraycopy(byteArray, beginIndex, subArray, 0, subArray.length);
        return subArray;
    }

    public static int bitLength(int byteLength) {
        return byteLength * 8;
    }

    public static int safeBitLength(int byteLength) throws IntegerOverflowException {
        long longResult = (long)byteLength * 8L;
        if ((long)((int)longResult) != longResult) {
            throw new IntegerOverflowException();
        }
        return (int)longResult;
    }

    public static int bitLength(byte[] byteArray) {
        if (byteArray == null) {
            return 0;
        }
        return ByteUtils.bitLength(byteArray.length);
    }

    public static int safeBitLength(byte[] byteArray) throws IntegerOverflowException {
        if (byteArray == null) {
            return 0;
        }
        return ByteUtils.safeBitLength(byteArray.length);
    }

    public static int byteLength(int bitLength) {
        return bitLength / 8;
    }

    public static boolean isZeroFilled(byte[] byteArray) {
        for (byte b : byteArray) {
            if (b == 0) continue;
            return false;
        }
        return true;
    }
}

