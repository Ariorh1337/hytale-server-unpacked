/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.io;

import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class NoopPacketStatsRecorder
implements PacketStatsRecorder {
    private static final PacketStatsRecorder.PacketStatsEntry EMPTY_ENTRY = new PacketStatsRecorder.PacketStatsEntry(){

        @Override
        public int getPacketId() {
            return 0;
        }

        @Override
        @Nullable
        public String getName() {
            return null;
        }

        @Override
        public boolean hasData() {
            return false;
        }

        @Override
        public int getSentCount() {
            return 0;
        }

        @Override
        public long getSentUncompressedTotal() {
            return 0L;
        }

        @Override
        public long getSentCompressedTotal() {
            return 0L;
        }

        @Override
        public long getSentUncompressedMin() {
            return 0L;
        }

        @Override
        public long getSentUncompressedMax() {
            return 0L;
        }

        @Override
        public long getSentCompressedMin() {
            return 0L;
        }

        @Override
        public long getSentCompressedMax() {
            return 0L;
        }

        @Override
        public double getSentUncompressedAvg() {
            return 0.0;
        }

        @Override
        public double getSentCompressedAvg() {
            return 0.0;
        }

        @Override
        @Nonnull
        public PacketStatsRecorder.RecentStats getSentRecently() {
            return PacketStatsRecorder.RecentStats.EMPTY;
        }

        @Override
        public int getReceivedCount() {
            return 0;
        }

        @Override
        public long getReceivedUncompressedTotal() {
            return 0L;
        }

        @Override
        public long getReceivedCompressedTotal() {
            return 0L;
        }

        @Override
        public long getReceivedUncompressedMin() {
            return 0L;
        }

        @Override
        public long getReceivedUncompressedMax() {
            return 0L;
        }

        @Override
        public long getReceivedCompressedMin() {
            return 0L;
        }

        @Override
        public long getReceivedCompressedMax() {
            return 0L;
        }

        @Override
        public double getReceivedUncompressedAvg() {
            return 0.0;
        }

        @Override
        public double getReceivedCompressedAvg() {
            return 0.0;
        }

        @Override
        @Nonnull
        public PacketStatsRecorder.RecentStats getReceivedRecently() {
            return PacketStatsRecorder.RecentStats.EMPTY;
        }
    };

    NoopPacketStatsRecorder() {
    }

    @Override
    public void recordSend(int packetId, int uncompressedSize, int compressedSize) {
    }

    @Override
    public void recordReceive(int packetId, int uncompressedSize, int compressedSize) {
    }

    @Override
    @Nonnull
    public PacketStatsRecorder.PacketStatsEntry getEntry(int packetId) {
        return EMPTY_ENTRY;
    }
}

