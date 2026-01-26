/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicheQuicChannel;
import io.netty.handler.codec.quic.SipHash;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ConnectionIdChannelMap {
    private static final SecureRandom random = new SecureRandom();
    private final Map<ConnectionIdKey, QuicheQuicChannel> channelMap = new HashMap<ConnectionIdKey, QuicheQuicChannel>();
    private final SipHash sipHash;

    ConnectionIdChannelMap() {
        byte[] seed = new byte[16];
        random.nextBytes(seed);
        this.sipHash = new SipHash(1, 3, seed);
    }

    private ConnectionIdKey key(ByteBuffer cid) {
        long hash = this.sipHash.macHash(cid);
        return new ConnectionIdKey(hash, cid);
    }

    @Nullable
    QuicheQuicChannel put(ByteBuffer cid, QuicheQuicChannel channel) {
        return this.channelMap.put(this.key(cid), channel);
    }

    @Nullable
    QuicheQuicChannel remove(ByteBuffer cid) {
        return this.channelMap.remove(this.key(cid));
    }

    @Nullable
    QuicheQuicChannel get(ByteBuffer cid) {
        return this.channelMap.get(this.key(cid));
    }

    void clear() {
        this.channelMap.clear();
    }

    private static final class ConnectionIdKey
    implements Comparable<ConnectionIdKey> {
        private final long hash;
        private final ByteBuffer key;

        ConnectionIdKey(long hash, ByteBuffer key) {
            this.hash = hash;
            this.key = key;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ConnectionIdKey that = (ConnectionIdKey)o;
            return this.hash == that.hash && Objects.equals(this.key, that.key);
        }

        public int hashCode() {
            return (int)this.hash;
        }

        @Override
        public int compareTo(@NotNull ConnectionIdKey o) {
            int result = Long.compare(this.hash, o.hash);
            return result != 0 ? result : this.key.compareTo(o.key);
        }
    }
}

