/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.ReusedSynchronisationType;
import org.rocksdb.RocksObject;

public class ComparatorOptions
extends RocksObject {
    public ComparatorOptions() {
        super(ComparatorOptions.newComparatorOptions());
    }

    public ReusedSynchronisationType reusedSynchronisationType() {
        assert (this.isOwningHandle());
        return ReusedSynchronisationType.getReusedSynchronisationType(ComparatorOptions.reusedSynchronisationType(this.nativeHandle_));
    }

    public ComparatorOptions setReusedSynchronisationType(ReusedSynchronisationType reusedSynchronisationType) {
        assert (this.isOwningHandle());
        ComparatorOptions.setReusedSynchronisationType(this.nativeHandle_, reusedSynchronisationType.getValue());
        return this;
    }

    public boolean useDirectBuffer() {
        assert (this.isOwningHandle());
        return ComparatorOptions.useDirectBuffer(this.nativeHandle_);
    }

    public ComparatorOptions setUseDirectBuffer(boolean bl) {
        assert (this.isOwningHandle());
        ComparatorOptions.setUseDirectBuffer(this.nativeHandle_, bl);
        return this;
    }

    public int maxReusedBufferSize() {
        assert (this.isOwningHandle());
        return ComparatorOptions.maxReusedBufferSize(this.nativeHandle_);
    }

    public ComparatorOptions setMaxReusedBufferSize(int n) {
        assert (this.isOwningHandle());
        ComparatorOptions.setMaxReusedBufferSize(this.nativeHandle_, n);
        return this;
    }

    private static native long newComparatorOptions();

    private static native byte reusedSynchronisationType(long var0);

    private static native void setReusedSynchronisationType(long var0, byte var2);

    private static native boolean useDirectBuffer(long var0);

    private static native void setUseDirectBuffer(long var0, boolean var2);

    private static native int maxReusedBufferSize(long var0);

    private static native void setMaxReusedBufferSize(long var0, int var2);

    @Override
    protected final void disposeInternal(long l) {
        ComparatorOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

