/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.util.concurrent.FastThreadLocal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

final class WebSocketUtil {
    private static final FastThreadLocal<MessageDigest> MD5 = new FastThreadLocal<MessageDigest>(){

        @Override
        protected MessageDigest initialValue() throws Exception {
            try {
                return MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e) {
                throw new InternalError("MD5 not supported on this platform - Outdated?");
            }
        }
    };
    private static final FastThreadLocal<MessageDigest> SHA1 = new FastThreadLocal<MessageDigest>(){

        @Override
        protected MessageDigest initialValue() throws Exception {
            try {
                return MessageDigest.getInstance("SHA1");
            }
            catch (NoSuchAlgorithmException e) {
                throw new InternalError("SHA-1 not supported on this platform - Outdated?");
            }
        }
    };

    static byte[] md5(byte[] data) {
        return WebSocketUtil.digest(MD5, data);
    }

    static byte[] sha1(byte[] data) {
        return WebSocketUtil.digest(SHA1, data);
    }

    private static byte[] digest(FastThreadLocal<MessageDigest> digestFastThreadLocal, byte[] data) {
        MessageDigest digest = digestFastThreadLocal.get();
        digest.reset();
        return digest.digest(data);
    }

    static String base64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        ThreadLocalRandom.current().nextBytes(bytes);
        return bytes;
    }

    static int randomNumber(int minimum, int maximum) {
        assert (minimum < maximum);
        double fraction = ThreadLocalRandom.current().nextDouble();
        return (int)((double)minimum + fraction * (double)(maximum - minimum));
    }

    static int byteAtIndex(int mask, int index) {
        return mask >> 8 * (3 - index) & 0xFF;
    }

    private WebSocketUtil() {
    }
}

