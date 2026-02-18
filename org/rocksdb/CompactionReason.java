/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum CompactionReason {
    kUnknown(0),
    kLevelL0FilesNum(1),
    kLevelMaxLevelSize(2),
    kUniversalSizeAmplification(3),
    kUniversalSizeRatio(4),
    kUniversalSortedRunNum(5),
    kFIFOMaxSize(6),
    kFIFOReduceNumFiles(7),
    kFIFOTtl(8),
    kManualCompaction(9),
    kFilesMarkedForCompaction(16),
    kBottommostFiles(10),
    kTtl(11),
    kFlush(12),
    kExternalSstIngestion(13),
    kPeriodicCompaction(14),
    kChangeTemperature(15),
    kForcedBlobGC(17),
    kRoundRobinTtl(18),
    kRefitLevel(19);

    private final byte value;

    private CompactionReason(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static CompactionReason fromValue(byte by) {
        for (CompactionReason compactionReason : CompactionReason.values()) {
            if (compactionReason.value != by) continue;
            return compactionReason;
        }
        throw new IllegalArgumentException("Illegal value provided for CompactionReason: " + by);
    }
}

