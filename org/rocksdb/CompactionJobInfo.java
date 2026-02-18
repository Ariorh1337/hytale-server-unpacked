/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.rocksdb.CompactionJobStats;
import org.rocksdb.CompactionReason;
import org.rocksdb.CompressionType;
import org.rocksdb.RocksObject;
import org.rocksdb.Status;
import org.rocksdb.TableProperties;

public class CompactionJobInfo
extends RocksObject {
    public CompactionJobInfo() {
        super(CompactionJobInfo.newCompactionJobInfo());
    }

    private CompactionJobInfo(long l) {
        super(l);
        this.disOwnNativeHandle();
    }

    public byte[] columnFamilyName() {
        return CompactionJobInfo.columnFamilyName(this.nativeHandle_);
    }

    public Status status() {
        return CompactionJobInfo.status(this.nativeHandle_);
    }

    public long threadId() {
        return CompactionJobInfo.threadId(this.nativeHandle_);
    }

    public int jobId() {
        return CompactionJobInfo.jobId(this.nativeHandle_);
    }

    public int baseInputLevel() {
        return CompactionJobInfo.baseInputLevel(this.nativeHandle_);
    }

    public int outputLevel() {
        return CompactionJobInfo.outputLevel(this.nativeHandle_);
    }

    public List<String> inputFiles() {
        return Arrays.asList(CompactionJobInfo.inputFiles(this.nativeHandle_));
    }

    public List<String> outputFiles() {
        return Arrays.asList(CompactionJobInfo.outputFiles(this.nativeHandle_));
    }

    public Map<String, TableProperties> tableProperties() {
        return CompactionJobInfo.tableProperties(this.nativeHandle_);
    }

    public CompactionReason compactionReason() {
        return CompactionReason.fromValue(CompactionJobInfo.compactionReason(this.nativeHandle_));
    }

    public CompressionType compression() {
        return CompressionType.getCompressionType(CompactionJobInfo.compression(this.nativeHandle_));
    }

    public CompactionJobStats stats() {
        long l = CompactionJobInfo.stats(this.nativeHandle_);
        if (l == 0L) {
            return null;
        }
        return new CompactionJobStats(l);
    }

    private static native long newCompactionJobInfo();

    @Override
    protected void disposeInternal(long l) {
        CompactionJobInfo.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native byte[] columnFamilyName(long var0);

    private static native Status status(long var0);

    private static native long threadId(long var0);

    private static native int jobId(long var0);

    private static native int baseInputLevel(long var0);

    private static native int outputLevel(long var0);

    private static native String[] inputFiles(long var0);

    private static native String[] outputFiles(long var0);

    private static native Map<String, TableProperties> tableProperties(long var0);

    private static native byte compactionReason(long var0);

    private static native byte compression(long var0);

    private static native long stats(long var0);
}

