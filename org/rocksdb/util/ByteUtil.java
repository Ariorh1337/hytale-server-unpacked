/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteUtil {
    public static byte[] bytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static int memcmp(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int n) {
        for (int i = 0; i < n; ++i) {
            int n2;
            int n3 = byteBuffer.get(i) & 0xFF;
            if (n3 == (n2 = byteBuffer2.get(i) & 0xFF)) continue;
            return n3 - n2;
        }
        return 0;
    }
}

