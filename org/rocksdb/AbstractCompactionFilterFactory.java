/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractCompactionFilter;
import org.rocksdb.AbstractImmutableNativeReference;
import org.rocksdb.RocksCallbackObject;

public abstract class AbstractCompactionFilterFactory<T extends AbstractCompactionFilter<?>>
extends RocksCallbackObject {
    public AbstractCompactionFilterFactory() {
        super(0L);
    }

    @Override
    protected long initializeNative(long ... lArray) {
        return this.createNewCompactionFilterFactory0();
    }

    private long createCompactionFilter(boolean bl, boolean bl2) {
        T t = this.createCompactionFilter(new AbstractCompactionFilter.Context(bl, bl2));
        ((AbstractImmutableNativeReference)t).disOwnNativeHandle();
        return ((AbstractCompactionFilter)t).nativeHandle_;
    }

    public abstract T createCompactionFilter(AbstractCompactionFilter.Context var1);

    public abstract String name();

    @Override
    protected void disposeInternal() {
        AbstractCompactionFilterFactory.disposeInternal(this.nativeHandle_);
    }

    private native long createNewCompactionFilterFactory0();

    private static native void disposeInternal(long var0);
}

