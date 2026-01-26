/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractPooledDerivedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.nio.ByteOrder;

class SimpleLeakAwareByteBuf
extends WrappedByteBuf {
    private final ByteBuf trackedByteBuf;
    final ResourceLeakTracker<ByteBuf> leak;

    SimpleLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leak) {
        super(wrapped);
        this.trackedByteBuf = ObjectUtil.checkNotNull(trackedByteBuf, "trackedByteBuf");
        this.leak = ObjectUtil.checkNotNull(leak, "leak");
    }

    SimpleLeakAwareByteBuf(ByteBuf wrapped, ResourceLeakTracker<ByteBuf> leak) {
        this(wrapped, wrapped, leak);
    }

    @Override
    public ByteBuf slice() {
        return this.newSharedLeakAwareByteBuf(super.slice());
    }

    @Override
    public ByteBuf retainedSlice() {
        try {
            return this.unwrappedDerived(super.retainedSlice());
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        try {
            return this.unwrappedDerived(super.retainedSlice(index, length));
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    @Override
    public ByteBuf retainedDuplicate() {
        try {
            return this.unwrappedDerived(super.retainedDuplicate());
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        try {
            return this.unwrappedDerived(super.readRetainedSlice(length));
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.newSharedLeakAwareByteBuf(super.slice(index, length));
    }

    @Override
    public ByteBuf duplicate() {
        return this.newSharedLeakAwareByteBuf(super.duplicate());
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.newSharedLeakAwareByteBuf(super.readSlice(length));
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.newSharedLeakAwareByteBuf(super.asReadOnly());
    }

    @Override
    public ByteBuf touch() {
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        return this;
    }

    @Override
    public ByteBuf retain() {
        try {
            return super.retain();
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    @Override
    public ByteBuf retain(int increment) {
        try {
            return super.retain(increment);
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    @Override
    public boolean release() {
        try {
            if (super.release()) {
                this.closeLeak();
                return true;
            }
            return false;
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    @Override
    public boolean release(int decrement) {
        try {
            if (super.release(decrement)) {
                this.closeLeak();
                return true;
            }
            return false;
        }
        catch (IllegalReferenceCountException irce) {
            ThrowableUtil.addSuppressed((Throwable)irce, this.leak.getCloseStackTraceIfAny());
            throw irce;
        }
    }

    private void closeLeak() {
        boolean closed = this.leak.close(this.trackedByteBuf);
        assert (closed);
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (this.order() == endianness) {
            return this;
        }
        return this.newSharedLeakAwareByteBuf(super.order(endianness));
    }

    private ByteBuf unwrappedDerived(ByteBuf derived) {
        ByteBuf unwrappedDerived = SimpleLeakAwareByteBuf.unwrapSwapped(derived);
        if (unwrappedDerived instanceof AbstractPooledDerivedByteBuf) {
            ((AbstractPooledDerivedByteBuf)unwrappedDerived).parent(this);
            return this.newLeakAwareByteBuf(derived, AbstractByteBuf.leakDetector.trackForcibly(derived));
        }
        return this.newSharedLeakAwareByteBuf(derived);
    }

    private static ByteBuf unwrapSwapped(ByteBuf buf) {
        if (buf instanceof SwappedByteBuf) {
            while ((buf = buf.unwrap()) instanceof SwappedByteBuf) {
            }
            return buf;
        }
        return buf;
    }

    private SimpleLeakAwareByteBuf newSharedLeakAwareByteBuf(ByteBuf wrapped) {
        return this.newLeakAwareByteBuf(wrapped, this.trackedByteBuf, this.leak);
    }

    private SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped, ResourceLeakTracker<ByteBuf> leakTracker) {
        return this.newLeakAwareByteBuf(wrapped, wrapped, leakTracker);
    }

    protected SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf buf, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
        return new SimpleLeakAwareByteBuf(buf, trackedByteBuf, leakTracker);
    }
}

