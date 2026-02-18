/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class ImportColumnFamilyOptions
extends RocksObject {
    public ImportColumnFamilyOptions() {
        super(ImportColumnFamilyOptions.newImportColumnFamilyOptions());
    }

    public boolean moveFiles() {
        return this.moveFiles(this.nativeHandle_);
    }

    public ImportColumnFamilyOptions setMoveFiles(boolean bl) {
        this.setMoveFiles(this.nativeHandle_, bl);
        return this;
    }

    private static native long newImportColumnFamilyOptions();

    private native boolean moveFiles(long var1);

    private native void setMoveFiles(long var1, boolean var3);

    @Override
    protected final native void disposeInternal(long var1);
}

