/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;

public interface FlushStrategy {
    public static final FlushStrategy DEFAULT = FlushStrategy.afterNumBytes(27000);

    public boolean shouldFlushNow(int var1, int var2);

    public static FlushStrategy afterNumBytes(int bytes) {
        ObjectUtil.checkPositive(bytes, "bytes");
        return (numPackets, numBytes) -> numBytes > bytes;
    }

    public static FlushStrategy afterNumPackets(int packets) {
        ObjectUtil.checkPositive(packets, "packets");
        return (numPackets, numBytes) -> numPackets > packets;
    }
}

