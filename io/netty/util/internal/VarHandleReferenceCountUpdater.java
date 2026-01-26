/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ReferenceCountUpdater;
import java.lang.invoke.VarHandle;

public abstract class VarHandleReferenceCountUpdater<T extends ReferenceCounted>
extends ReferenceCountUpdater<T> {
    protected VarHandleReferenceCountUpdater() {
    }

    protected abstract VarHandle varHandle();

    @Override
    protected final void safeInitializeRawRefCnt(T refCntObj, int value) {
        this.varHandle().set((ReferenceCounted)refCntObj, value);
    }

    @Override
    protected final int getAndAddRawRefCnt(T refCntObj, int increment) {
        return this.varHandle().getAndAdd((ReferenceCounted)refCntObj, increment);
    }

    @Override
    protected final int getRawRefCnt(T refCnt) {
        return this.varHandle().get((ReferenceCounted)refCnt);
    }

    @Override
    protected final int getAcquireRawRefCnt(T refCnt) {
        return this.varHandle().getAcquire((ReferenceCounted)refCnt);
    }

    @Override
    protected final void setReleaseRawRefCnt(T refCnt, int value) {
        this.varHandle().setRelease((ReferenceCounted)refCnt, value);
    }

    @Override
    protected final boolean casRawRefCnt(T refCnt, int expected, int value) {
        return this.varHandle().compareAndSet((ReferenceCounted)refCnt, expected, value);
    }
}

