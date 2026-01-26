/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.metrics.metric.AverageCollector;
import com.hypixel.hytale.protocol.PacketRegistry;
import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PacketStatsRecorderImpl
implements PacketStatsRecorder {
    public static final MetricsRegistry<PacketStatsRecorderImpl> METRICS_REGISTRY = new MetricsRegistry<PacketStatsRecorderImpl>().register("Packets", recorder -> {
        ArrayList<PacketStatsEntry> entries = new ArrayList<PacketStatsEntry>();
        for (int i = 0; i < 512; ++i) {
            PacketStatsEntry entry = recorder.entries[i];
            if (!entry.hasData()) continue;
            entries.add(entry);
        }
        return (PacketStatsEntry[])entries.toArray(PacketStatsEntry[]::new);
    }, new ArrayCodec<PacketStatsEntry>(PacketStatsEntry.METRICS_REGISTRY, PacketStatsEntry[]::new));
    private final PacketStatsEntry[] entries = new PacketStatsEntry[512];

    public PacketStatsRecorderImpl() {
        for (int i = 0; i < this.entries.length; ++i) {
            this.entries[i] = new PacketStatsEntry(i);
        }
    }

    @Override
    public void recordSend(int packetId, int uncompressedSize, int compressedSize) {
        if (packetId < 0 || packetId >= this.entries.length) {
            return;
        }
        this.entries[packetId].recordSend(uncompressedSize, compressedSize);
    }

    @Override
    public void recordReceive(int packetId, int uncompressedSize, int compressedSize) {
        if (packetId < 0 || packetId >= this.entries.length) {
            return;
        }
        this.entries[packetId].recordReceive(uncompressedSize, compressedSize);
    }

    @Override
    @Nonnull
    public PacketStatsEntry getEntry(int packetId) {
        return this.entries[packetId];
    }

    public static class PacketStatsEntry
    implements PacketStatsRecorder.PacketStatsEntry {
        public static final MetricsRegistry<PacketStatsEntry> METRICS_REGISTRY = new MetricsRegistry<PacketStatsEntry>().register("PacketId", PacketStatsEntry::getPacketId, Codec.INTEGER).register("Name", PacketStatsEntry::getName, Codec.STRING).register("SentCount", PacketStatsEntry::getSentCount, Codec.INTEGER).register("SentUncompressedTotal", PacketStatsEntry::getSentUncompressedTotal, Codec.LONG).register("SentCompressedTotal", PacketStatsEntry::getSentCompressedTotal, Codec.LONG).register("SentUncompressedMin", PacketStatsEntry::getSentUncompressedMin, Codec.LONG).register("SentUncompressedMax", PacketStatsEntry::getSentUncompressedMax, Codec.LONG).register("SentCompressedMin", PacketStatsEntry::getSentCompressedMin, Codec.LONG).register("SentCompressedMax", PacketStatsEntry::getSentCompressedMax, Codec.LONG).register("ReceivedCount", PacketStatsEntry::getReceivedCount, Codec.INTEGER).register("ReceivedUncompressedTotal", PacketStatsEntry::getReceivedUncompressedTotal, Codec.LONG).register("ReceivedCompressedTotal", PacketStatsEntry::getReceivedCompressedTotal, Codec.LONG).register("ReceivedUncompressedMin", PacketStatsEntry::getReceivedUncompressedMin, Codec.LONG).register("ReceivedUncompressedMax", PacketStatsEntry::getReceivedUncompressedMax, Codec.LONG).register("ReceivedCompressedMin", PacketStatsEntry::getReceivedCompressedMin, Codec.LONG).register("ReceivedCompressedMax", PacketStatsEntry::getReceivedCompressedMax, Codec.LONG);
        private final int packetId;
        private final AtomicInteger sentCount = new AtomicInteger();
        private final AtomicLong sentUncompressedTotal = new AtomicLong();
        private final AtomicLong sentCompressedTotal = new AtomicLong();
        private final AtomicLong sentUncompressedMin = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong sentUncompressedMax = new AtomicLong();
        private final AtomicLong sentCompressedMin = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong sentCompressedMax = new AtomicLong();
        private final AverageCollector sentUncompressedAvg = new AverageCollector();
        private final AverageCollector sentCompressedAvg = new AverageCollector();
        private final Queue<SizeRecord> sentRecently = new ConcurrentLinkedQueue<SizeRecord>();
        private final AtomicInteger receivedCount = new AtomicInteger();
        private final AtomicLong receivedUncompressedTotal = new AtomicLong();
        private final AtomicLong receivedCompressedTotal = new AtomicLong();
        private final AtomicLong receivedUncompressedMin = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong receivedUncompressedMax = new AtomicLong();
        private final AtomicLong receivedCompressedMin = new AtomicLong(Long.MAX_VALUE);
        private final AtomicLong receivedCompressedMax = new AtomicLong();
        private final AverageCollector receivedUncompressedAvg = new AverageCollector();
        private final AverageCollector receivedCompressedAvg = new AverageCollector();
        private final Queue<SizeRecord> receivedRecently = new ConcurrentLinkedQueue<SizeRecord>();

        public PacketStatsEntry(int packetId) {
            this.packetId = packetId;
        }

        void recordSend(int uncompressedSize, int compressedSize) {
            this.sentCount.incrementAndGet();
            this.sentUncompressedTotal.addAndGet(uncompressedSize);
            this.sentCompressedTotal.addAndGet(compressedSize);
            this.sentUncompressedMin.accumulateAndGet(uncompressedSize, Math::min);
            this.sentUncompressedMax.accumulateAndGet(uncompressedSize, Math::max);
            this.sentCompressedMin.accumulateAndGet(compressedSize, Math::min);
            this.sentCompressedMax.accumulateAndGet(compressedSize, Math::max);
            this.sentUncompressedAvg.add(uncompressedSize);
            this.sentCompressedAvg.add(compressedSize);
            long now = System.nanoTime();
            this.sentRecently.add(new SizeRecord(now, uncompressedSize, compressedSize));
            this.pruneOld(this.sentRecently, now);
        }

        void recordReceive(int uncompressedSize, int compressedSize) {
            this.receivedCount.incrementAndGet();
            this.receivedUncompressedTotal.addAndGet(uncompressedSize);
            this.receivedCompressedTotal.addAndGet(compressedSize);
            this.receivedUncompressedMin.accumulateAndGet(uncompressedSize, Math::min);
            this.receivedUncompressedMax.accumulateAndGet(uncompressedSize, Math::max);
            this.receivedCompressedMin.accumulateAndGet(compressedSize, Math::min);
            this.receivedCompressedMax.accumulateAndGet(compressedSize, Math::max);
            this.receivedUncompressedAvg.add(uncompressedSize);
            this.receivedCompressedAvg.add(compressedSize);
            long now = System.nanoTime();
            this.receivedRecently.add(new SizeRecord(now, uncompressedSize, compressedSize));
            this.pruneOld(this.receivedRecently, now);
        }

        private void pruneOld(Queue<SizeRecord> queue, long now) {
            long cutoff = now - TimeUnit.SECONDS.toNanos(30L);
            SizeRecord head = queue.peek();
            while (head != null && head.nanos < cutoff) {
                queue.poll();
                head = queue.peek();
            }
        }

        @Override
        public boolean hasData() {
            return this.sentCount.get() > 0 || this.receivedCount.get() > 0;
        }

        @Override
        public int getPacketId() {
            return this.packetId;
        }

        @Override
        @Nullable
        public String getName() {
            PacketRegistry.PacketInfo info = PacketRegistry.getById(this.packetId);
            return info != null ? info.name() : null;
        }

        @Override
        public int getSentCount() {
            return this.sentCount.get();
        }

        @Override
        public long getSentUncompressedTotal() {
            return this.sentUncompressedTotal.get();
        }

        @Override
        public long getSentCompressedTotal() {
            return this.sentCompressedTotal.get();
        }

        @Override
        public long getSentUncompressedMin() {
            return this.sentCount.get() > 0 ? this.sentUncompressedMin.get() : 0L;
        }

        @Override
        public long getSentUncompressedMax() {
            return this.sentUncompressedMax.get();
        }

        @Override
        public long getSentCompressedMin() {
            return this.sentCount.get() > 0 ? this.sentCompressedMin.get() : 0L;
        }

        @Override
        public long getSentCompressedMax() {
            return this.sentCompressedMax.get();
        }

        @Override
        public double getSentUncompressedAvg() {
            return this.sentUncompressedAvg.get();
        }

        @Override
        public double getSentCompressedAvg() {
            return this.sentCompressedAvg.get();
        }

        @Override
        public int getReceivedCount() {
            return this.receivedCount.get();
        }

        @Override
        public long getReceivedUncompressedTotal() {
            return this.receivedUncompressedTotal.get();
        }

        @Override
        public long getReceivedCompressedTotal() {
            return this.receivedCompressedTotal.get();
        }

        @Override
        public long getReceivedUncompressedMin() {
            return this.receivedCount.get() > 0 ? this.receivedUncompressedMin.get() : 0L;
        }

        @Override
        public long getReceivedUncompressedMax() {
            return this.receivedUncompressedMax.get();
        }

        @Override
        public long getReceivedCompressedMin() {
            return this.receivedCount.get() > 0 ? this.receivedCompressedMin.get() : 0L;
        }

        @Override
        public long getReceivedCompressedMax() {
            return this.receivedCompressedMax.get();
        }

        @Override
        public double getReceivedUncompressedAvg() {
            return this.receivedUncompressedAvg.get();
        }

        @Override
        public double getReceivedCompressedAvg() {
            return this.receivedCompressedAvg.get();
        }

        @Override
        @Nonnull
        public PacketStatsRecorder.RecentStats getSentRecently() {
            return this.computeRecentStats(this.sentRecently);
        }

        @Override
        @Nonnull
        public PacketStatsRecorder.RecentStats getReceivedRecently() {
            return this.computeRecentStats(this.receivedRecently);
        }

        private PacketStatsRecorder.RecentStats computeRecentStats(Queue<SizeRecord> queue) {
            int count = 0;
            long uncompressedTotal = 0L;
            long compressedTotal = 0L;
            int uncompressedMin = Integer.MAX_VALUE;
            int uncompressedMax = 0;
            int compressedMin = Integer.MAX_VALUE;
            int compressedMax = 0;
            for (SizeRecord record : queue) {
                ++count;
                uncompressedTotal += (long)record.uncompressedSize;
                compressedTotal += (long)record.compressedSize;
                uncompressedMin = Math.min(uncompressedMin, record.uncompressedSize);
                uncompressedMax = Math.max(uncompressedMax, record.uncompressedSize);
                compressedMin = Math.min(compressedMin, record.compressedSize);
                compressedMax = Math.max(compressedMax, record.compressedSize);
            }
            if (count == 0) {
                return PacketStatsRecorder.RecentStats.EMPTY;
            }
            return new PacketStatsRecorder.RecentStats(count, uncompressedTotal, compressedTotal, uncompressedMin, uncompressedMax, compressedMin, compressedMax);
        }

        public void reset() {
            this.sentCount.set(0);
            this.sentUncompressedTotal.set(0L);
            this.sentCompressedTotal.set(0L);
            this.sentUncompressedMin.set(Long.MAX_VALUE);
            this.sentUncompressedMax.set(0L);
            this.sentCompressedMin.set(Long.MAX_VALUE);
            this.sentCompressedMax.set(0L);
            this.sentUncompressedAvg.clear();
            this.sentCompressedAvg.clear();
            this.sentRecently.clear();
            this.receivedCount.set(0);
            this.receivedUncompressedTotal.set(0L);
            this.receivedCompressedTotal.set(0L);
            this.receivedUncompressedMin.set(Long.MAX_VALUE);
            this.receivedUncompressedMax.set(0L);
            this.receivedCompressedMin.set(Long.MAX_VALUE);
            this.receivedCompressedMax.set(0L);
            this.receivedUncompressedAvg.clear();
            this.receivedCompressedAvg.clear();
            this.receivedRecently.clear();
        }

        public record SizeRecord(long nanos, int uncompressedSize, int compressedSize) {
        }
    }
}

