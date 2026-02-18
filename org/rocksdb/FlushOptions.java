/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;

public class FlushOptions
extends RocksObject {
    public FlushOptions() {
        super(FlushOptions.newFlushOptionsInance());
    }

    public FlushOptions setWaitForFlush(boolean bl) {
        assert (this.isOwningHandle());
        FlushOptions.setWaitForFlush(this.nativeHandle_, bl);
        return this;
    }

    public boolean waitForFlush() {
        assert (this.isOwningHandle());
        return FlushOptions.waitForFlush(this.nativeHandle_);
    }

    public FlushOptions setAllowWriteStall(boolean bl) {
        assert (this.isOwningHandle());
        FlushOptions.setAllowWriteStall(this.nativeHandle_, bl);
        return this;
    }

    public boolean allowWriteStall() {
        assert (this.isOwningHandle());
        return FlushOptions.allowWriteStall(this.nativeHandle_);
    }

    private static long newFlushOptionsInance() {
        RocksDB.loadLibrary();
        return FlushOptions.newFlushOptions();
    }

    private static native long newFlushOptions();

    @Override
    protected final void disposeInternal(long l) {
        FlushOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void setWaitForFlush(long var0, boolean var2);

    private static native boolean waitForFlush(long var0);

    private static native void setAllowWriteStall(long var0, boolean var2);

    private static native boolean allowWriteStall(long var0);
}

