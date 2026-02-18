/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class WriteOptions
extends RocksObject {
    public WriteOptions() {
        super(WriteOptions.newWriteOptions());
    }

    WriteOptions(long l) {
        super(l);
        this.disOwnNativeHandle();
    }

    public WriteOptions(WriteOptions writeOptions) {
        super(WriteOptions.copyWriteOptions(writeOptions.nativeHandle_));
    }

    public WriteOptions setSync(boolean bl) {
        WriteOptions.setSync(this.nativeHandle_, bl);
        return this;
    }

    public boolean sync() {
        return WriteOptions.sync(this.nativeHandle_);
    }

    public WriteOptions setDisableWAL(boolean bl) {
        WriteOptions.setDisableWAL(this.nativeHandle_, bl);
        return this;
    }

    public boolean disableWAL() {
        return WriteOptions.disableWAL(this.nativeHandle_);
    }

    public WriteOptions setIgnoreMissingColumnFamilies(boolean bl) {
        WriteOptions.setIgnoreMissingColumnFamilies(this.nativeHandle_, bl);
        return this;
    }

    public boolean ignoreMissingColumnFamilies() {
        return WriteOptions.ignoreMissingColumnFamilies(this.nativeHandle_);
    }

    public WriteOptions setNoSlowdown(boolean bl) {
        WriteOptions.setNoSlowdown(this.nativeHandle_, bl);
        return this;
    }

    public boolean noSlowdown() {
        return WriteOptions.noSlowdown(this.nativeHandle_);
    }

    public WriteOptions setLowPri(boolean bl) {
        WriteOptions.setLowPri(this.nativeHandle_, bl);
        return this;
    }

    public boolean lowPri() {
        return WriteOptions.lowPri(this.nativeHandle_);
    }

    public boolean memtableInsertHintPerBatch() {
        return WriteOptions.memtableInsertHintPerBatch(this.nativeHandle_);
    }

    public WriteOptions setMemtableInsertHintPerBatch(boolean bl) {
        WriteOptions.setMemtableInsertHintPerBatch(this.nativeHandle_, bl);
        return this;
    }

    private static native long newWriteOptions();

    private static native long copyWriteOptions(long var0);

    @Override
    protected final void disposeInternal(long l) {
        WriteOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void setSync(long var0, boolean var2);

    private static native boolean sync(long var0);

    private static native void setDisableWAL(long var0, boolean var2);

    private static native boolean disableWAL(long var0);

    private static native void setIgnoreMissingColumnFamilies(long var0, boolean var2);

    private static native boolean ignoreMissingColumnFamilies(long var0);

    private static native void setNoSlowdown(long var0, boolean var2);

    private static native boolean noSlowdown(long var0);

    private static native void setLowPri(long var0, boolean var2);

    private static native boolean lowPri(long var0);

    private static native boolean memtableInsertHintPerBatch(long var0);

    private static native void setMemtableInsertHintPerBatch(long var0, boolean var2);
}

