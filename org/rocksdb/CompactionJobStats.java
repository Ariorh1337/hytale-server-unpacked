/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class CompactionJobStats
extends RocksObject {
    public CompactionJobStats() {
        super(CompactionJobStats.newCompactionJobStats());
    }

    CompactionJobStats(long l) {
        super(l);
    }

    public void reset() {
        CompactionJobStats.reset(this.nativeHandle_);
    }

    public void add(CompactionJobStats compactionJobStats) {
        CompactionJobStats.add(this.nativeHandle_, compactionJobStats.nativeHandle_);
    }

    public long elapsedMicros() {
        return CompactionJobStats.elapsedMicros(this.nativeHandle_);
    }

    public long numInputRecords() {
        return CompactionJobStats.numInputRecords(this.nativeHandle_);
    }

    public long numInputFiles() {
        return CompactionJobStats.numInputFiles(this.nativeHandle_);
    }

    public long numInputFilesAtOutputLevel() {
        return CompactionJobStats.numInputFilesAtOutputLevel(this.nativeHandle_);
    }

    public long numOutputRecords() {
        return CompactionJobStats.numOutputRecords(this.nativeHandle_);
    }

    public long numOutputFiles() {
        return CompactionJobStats.numOutputFiles(this.nativeHandle_);
    }

    public boolean isManualCompaction() {
        return CompactionJobStats.isManualCompaction(this.nativeHandle_);
    }

    public long totalInputBytes() {
        return CompactionJobStats.totalInputBytes(this.nativeHandle_);
    }

    public long totalOutputBytes() {
        return CompactionJobStats.totalOutputBytes(this.nativeHandle_);
    }

    public long numRecordsReplaced() {
        return CompactionJobStats.numRecordsReplaced(this.nativeHandle_);
    }

    public long totalInputRawKeyBytes() {
        return CompactionJobStats.totalInputRawKeyBytes(this.nativeHandle_);
    }

    public long totalInputRawValueBytes() {
        return CompactionJobStats.totalInputRawValueBytes(this.nativeHandle_);
    }

    public long numInputDeletionRecords() {
        return CompactionJobStats.numInputDeletionRecords(this.nativeHandle_);
    }

    public long numExpiredDeletionRecords() {
        return CompactionJobStats.numExpiredDeletionRecords(this.nativeHandle_);
    }

    public long numCorruptKeys() {
        return CompactionJobStats.numCorruptKeys(this.nativeHandle_);
    }

    public long fileWriteNanos() {
        return CompactionJobStats.fileWriteNanos(this.nativeHandle_);
    }

    public long fileRangeSyncNanos() {
        return CompactionJobStats.fileRangeSyncNanos(this.nativeHandle_);
    }

    public long fileFsyncNanos() {
        return CompactionJobStats.fileFsyncNanos(this.nativeHandle_);
    }

    public long filePrepareWriteNanos() {
        return CompactionJobStats.filePrepareWriteNanos(this.nativeHandle_);
    }

    public byte[] smallestOutputKeyPrefix() {
        return CompactionJobStats.smallestOutputKeyPrefix(this.nativeHandle_);
    }

    public byte[] largestOutputKeyPrefix() {
        return CompactionJobStats.largestOutputKeyPrefix(this.nativeHandle_);
    }

    public long numSingleDelFallthru() {
        return CompactionJobStats.numSingleDelFallthru(this.nativeHandle_);
    }

    public long numSingleDelMismatch() {
        return CompactionJobStats.numSingleDelMismatch(this.nativeHandle_);
    }

    private static native long newCompactionJobStats();

    @Override
    protected void disposeInternal(long l) {
        CompactionJobStats.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void reset(long var0);

    private static native void add(long var0, long var2);

    private static native long elapsedMicros(long var0);

    private static native long numInputRecords(long var0);

    private static native long numInputFiles(long var0);

    private static native long numInputFilesAtOutputLevel(long var0);

    private static native long numOutputRecords(long var0);

    private static native long numOutputFiles(long var0);

    private static native boolean isManualCompaction(long var0);

    private static native long totalInputBytes(long var0);

    private static native long totalOutputBytes(long var0);

    private static native long numRecordsReplaced(long var0);

    private static native long totalInputRawKeyBytes(long var0);

    private static native long totalInputRawValueBytes(long var0);

    private static native long numInputDeletionRecords(long var0);

    private static native long numExpiredDeletionRecords(long var0);

    private static native long numCorruptKeys(long var0);

    private static native long fileWriteNanos(long var0);

    private static native long fileRangeSyncNanos(long var0);

    private static native long fileFsyncNanos(long var0);

    private static native long filePrepareWriteNanos(long var0);

    private static native byte[] smallestOutputKeyPrefix(long var0);

    private static native byte[] largestOutputKeyPrefix(long var0);

    private static native long numSingleDelFallthru(long var0);

    private static native long numSingleDelMismatch(long var0);
}

