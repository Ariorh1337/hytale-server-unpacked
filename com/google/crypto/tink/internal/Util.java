/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.BuildDispatchedCode;
import com.google.crypto.tink.internal.Random;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class Util {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static int randKeyId() {
        int result = 0;
        while (result == 0) {
            byte[] rand = Random.randBytes(4);
            result = (rand[0] & 0xFF) << 24 | (rand[1] & 0xFF) << 16 | (rand[2] & 0xFF) << 8 | rand[3] & 0xFF;
        }
        return result;
    }

    private static final byte toByteFromPrintableAscii(char c) {
        if (c < '!' || c > '~') {
            throw new TinkBugException("Not a printable ASCII character: " + c);
        }
        return (byte)c;
    }

    private static final byte checkedToByteFromPrintableAscii(char c) throws GeneralSecurityException {
        if (c < '!' || c > '~') {
            throw new GeneralSecurityException("Not a printable ASCII character: " + c);
        }
        return (byte)c;
    }

    public static final Bytes toBytesFromPrintableAscii(String s) {
        byte[] result = new byte[s.length()];
        for (int i = 0; i < s.length(); ++i) {
            result[i] = Util.toByteFromPrintableAscii(s.charAt(i));
        }
        return Bytes.copyFrom(result);
    }

    public static final Bytes checkedToBytesFromPrintableAscii(String s) throws GeneralSecurityException {
        byte[] result = new byte[s.length()];
        for (int i = 0; i < s.length(); ++i) {
            result[i] = Util.checkedToByteFromPrintableAscii(s.charAt(i));
        }
        return Bytes.copyFrom(result);
    }

    public static boolean isAndroid() {
        return Objects.equals(System.getProperty("java.vendor"), "The Android Project");
    }

    @Nullable
    public static Integer getAndroidApiLevel() {
        if (!Util.isAndroid()) {
            return null;
        }
        return BuildDispatchedCode.getApiLevel();
    }

    public static boolean isPrefix(byte[] prefix, byte[] complete) {
        if (complete.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; ++i) {
            if (complete[i] == prefix[i]) continue;
            return false;
        }
        return true;
    }

    public static SecretBytes readIntoSecretBytes(InputStream input, int length, SecretKeyAccess access) throws GeneralSecurityException {
        byte[] output = new byte[length];
        try {
            int read;
            int len = output.length;
            for (int readTotal = 0; readTotal < len; readTotal += read) {
                read = input.read(output, readTotal, len - readTotal);
                if (read != -1) continue;
                throw new GeneralSecurityException("Not enough pseudorandomness provided");
            }
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Reading pseudorandomness failed");
        }
        return SecretBytes.copyFrom(output, access);
    }

    private Util() {
    }
}

