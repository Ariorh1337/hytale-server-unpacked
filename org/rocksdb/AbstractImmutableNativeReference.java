/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.concurrent.atomic.AtomicBoolean;
import org.rocksdb.AbstractNativeReference;

public abstract class AbstractImmutableNativeReference
extends AbstractNativeReference {
    protected final AtomicBoolean owningHandle_;

    protected AbstractImmutableNativeReference(boolean bl) {
        this.owningHandle_ = new AtomicBoolean(bl);
    }

    @Override
    public boolean isOwningHandle() {
        return this.owningHandle_.get();
    }

    protected final void disOwnNativeHandle() {
        this.owningHandle_.set(false);
    }

    @Override
    public void close() {
        if (this.owningHandle_.compareAndSet(true, false)) {
            this.disposeInternal();
        }
    }

    protected abstract void disposeInternal();
}

