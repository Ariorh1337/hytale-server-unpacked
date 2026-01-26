/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.SslSessionTicketKey;
import io.netty.util.internal.PlatformDependent;
import org.jetbrains.annotations.Nullable;

final class BoringSSLSessionTicketCallback {
    private volatile byte[][] sessionKeys;

    BoringSSLSessionTicketCallback() {
    }

    byte @Nullable [] findSessionTicket(byte @Nullable [] keyname) {
        byte[][] keys = this.sessionKeys;
        if (keys == null || keys.length == 0) {
            return null;
        }
        if (keyname == null) {
            return keys[0];
        }
        for (int i = 0; i < keys.length; ++i) {
            byte[] key = keys[i];
            if (!PlatformDependent.equals(keyname, 0, key, 1, keyname.length)) continue;
            return key;
        }
        return null;
    }

    void setSessionTicketKeys(SslSessionTicketKey @Nullable [] keys) {
        if (keys != null && keys.length != 0) {
            byte[][] sessionKeys = new byte[keys.length][];
            for (int i = 0; i < keys.length; ++i) {
                SslSessionTicketKey key = keys[i];
                byte[] binaryKey = new byte[49];
                binaryKey[0] = i == 0 ? (byte)1 : 0;
                int dstCurPos = 1;
                System.arraycopy(key.name, 0, binaryKey, dstCurPos, 16);
                System.arraycopy(key.hmacKey, 0, binaryKey, dstCurPos += 16, 16);
                System.arraycopy(key.aesKey, 0, binaryKey, dstCurPos += 16, 16);
                sessionKeys[i] = binaryKey;
            }
            this.sessionKeys = sessionKeys;
        } else {
            this.sessionKeys = null;
        }
    }
}

