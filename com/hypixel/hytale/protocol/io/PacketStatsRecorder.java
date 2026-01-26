/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.io;

import com.hypixel.hytale.protocol.io.NoopPacketStatsRecorder;
import io.netty.util.AttributeKey;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PacketStatsRecorder {
    public static final AttributeKey<PacketStatsRecorder> CHANNEL_KEY = AttributeKey.valueOf("PacketStatsRecorder");
    public static final PacketStatsRecorder NOOP = new NoopPacketStatsRecorder();

    public void recordSend(int var1, int var2, int var3);

    public void recordReceive(int var1, int var2, int var3);

    @Nonnull
    public PacketStatsEntry getEntry(int var1);

    public record RecentStats(int count, long uncompressedTotal, long compressedTotal, int uncompressedMin, int uncompressedMax, int compressedMin, int compressedMax) {
        public static final RecentStats EMPTY = new RecentStats(0, 0L, 0L, 0, 0, 0, 0);
    }

    public static interface PacketStatsEntry {
        public static final int RECENT_SECONDS = 30;

        public int getPacketId();

        @Nullable
        public String getName();

        public boolean hasData();

        public int getSentCount();

        public long getSentUncompressedTotal();

        public long getSentCompressedTotal();

        public long getSentUncompressedMin();

        public long getSentUncompressedMax();

        public long getSentCompressedMin();

        public long getSentCompressedMax();

        public double getSentUncompressedAvg();

        public double getSentCompressedAvg();

        @Nonnull
        public RecentStats getSentRecently();

        public int getReceivedCount();

        public long getReceivedUncompressedTotal();

        public long getReceivedCompressedTotal();

        public long getReceivedUncompressedMin();

        public long getReceivedUncompressedMax();

        public long getReceivedCompressedMin();

        public long getReceivedCompressedMax();

        public double getReceivedUncompressedAvg();

        public double getReceivedCompressedAvg();

        @Nonnull
        public RecentStats getReceivedRecently();
    }
}

