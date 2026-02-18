/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class CompactionOptionsFIFO
extends RocksObject {
    public CompactionOptionsFIFO() {
        super(CompactionOptionsFIFO.newCompactionOptionsFIFO());
    }

    public CompactionOptionsFIFO setMaxTableFilesSize(long l) {
        CompactionOptionsFIFO.setMaxTableFilesSize(this.nativeHandle_, l);
        return this;
    }

    public long maxTableFilesSize() {
        return CompactionOptionsFIFO.maxTableFilesSize(this.nativeHandle_);
    }

    public CompactionOptionsFIFO setAllowCompaction(boolean bl) {
        CompactionOptionsFIFO.setAllowCompaction(this.nativeHandle_, bl);
        return this;
    }

    public boolean allowCompaction() {
        return CompactionOptionsFIFO.allowCompaction(this.nativeHandle_);
    }

    private static native long newCompactionOptionsFIFO();

    @Override
    protected final void disposeInternal(long l) {
        CompactionOptionsFIFO.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void setMaxTableFilesSize(long var0, long var2);

    private static native long maxTableFilesSize(long var0);

    private static native void setAllowCompaction(long var0, boolean var2);

    private static native boolean allowCompaction(long var0);
}

