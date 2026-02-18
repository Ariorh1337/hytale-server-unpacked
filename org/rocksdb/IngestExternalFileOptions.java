/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class IngestExternalFileOptions
extends RocksObject {
    public IngestExternalFileOptions() {
        super(IngestExternalFileOptions.newIngestExternalFileOptions());
    }

    public IngestExternalFileOptions(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        super(IngestExternalFileOptions.newIngestExternalFileOptions(bl, bl2, bl3, bl4));
    }

    public boolean moveFiles() {
        return IngestExternalFileOptions.moveFiles(this.nativeHandle_);
    }

    public IngestExternalFileOptions setMoveFiles(boolean bl) {
        IngestExternalFileOptions.setMoveFiles(this.nativeHandle_, bl);
        return this;
    }

    public boolean snapshotConsistency() {
        return IngestExternalFileOptions.snapshotConsistency(this.nativeHandle_);
    }

    public IngestExternalFileOptions setSnapshotConsistency(boolean bl) {
        IngestExternalFileOptions.setSnapshotConsistency(this.nativeHandle_, bl);
        return this;
    }

    public boolean allowGlobalSeqNo() {
        return IngestExternalFileOptions.allowGlobalSeqNo(this.nativeHandle_);
    }

    public IngestExternalFileOptions setAllowGlobalSeqNo(boolean bl) {
        IngestExternalFileOptions.setAllowGlobalSeqNo(this.nativeHandle_, bl);
        return this;
    }

    public boolean allowBlockingFlush() {
        return IngestExternalFileOptions.allowBlockingFlush(this.nativeHandle_);
    }

    public IngestExternalFileOptions setAllowBlockingFlush(boolean bl) {
        IngestExternalFileOptions.setAllowBlockingFlush(this.nativeHandle_, bl);
        return this;
    }

    public boolean ingestBehind() {
        return IngestExternalFileOptions.ingestBehind(this.nativeHandle_);
    }

    public IngestExternalFileOptions setIngestBehind(boolean bl) {
        IngestExternalFileOptions.setIngestBehind(this.nativeHandle_, bl);
        return this;
    }

    public boolean writeGlobalSeqno() {
        return IngestExternalFileOptions.writeGlobalSeqno(this.nativeHandle_);
    }

    public IngestExternalFileOptions setWriteGlobalSeqno(boolean bl) {
        IngestExternalFileOptions.setWriteGlobalSeqno(this.nativeHandle_, bl);
        return this;
    }

    private static native long newIngestExternalFileOptions();

    private static native long newIngestExternalFileOptions(boolean var0, boolean var1, boolean var2, boolean var3);

    @Override
    protected final void disposeInternal(long l) {
        IngestExternalFileOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native boolean moveFiles(long var0);

    private static native void setMoveFiles(long var0, boolean var2);

    private static native boolean snapshotConsistency(long var0);

    private static native void setSnapshotConsistency(long var0, boolean var2);

    private static native boolean allowGlobalSeqNo(long var0);

    private static native void setAllowGlobalSeqNo(long var0, boolean var2);

    private static native boolean allowBlockingFlush(long var0);

    private static native void setAllowBlockingFlush(long var0, boolean var2);

    private static native boolean ingestBehind(long var0);

    private static native void setIngestBehind(long var0, boolean var2);

    private static native boolean writeGlobalSeqno(long var0);

    private static native void setWriteGlobalSeqno(long var0, boolean var2);
}

