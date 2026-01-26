/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

@Deprecated
public abstract class ReferenceCountUpdater<T extends ReferenceCounted> {
    protected ReferenceCountUpdater() {
    }

    protected abstract void safeInitializeRawRefCnt(T var1, int var2);

    protected abstract int getAndAddRawRefCnt(T var1, int var2);

    protected abstract int getRawRefCnt(T var1);

    protected abstract int getAcquireRawRefCnt(T var1);

    protected abstract void setReleaseRawRefCnt(T var1, int var2);

    protected abstract boolean casRawRefCnt(T var1, int var2, int var3);

    public final int initialValue() {
        return 2;
    }

    public final void setInitialValue(T instance) {
        this.safeInitializeRawRefCnt(instance, this.initialValue());
    }

    private static int realRefCnt(int rawCnt) {
        return rawCnt >>> 1;
    }

    public final int refCnt(T instance) {
        return ReferenceCountUpdater.realRefCnt(this.getAcquireRawRefCnt(instance));
    }

    public final boolean isLiveNonVolatile(T instance) {
        int rawCnt = this.getRawRefCnt(instance);
        if (rawCnt == 2) {
            return true;
        }
        return (rawCnt & 1) == 0;
    }

    public final void setRefCnt(T instance, int refCnt) {
        int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
        this.setReleaseRawRefCnt(instance, rawRefCnt);
    }

    public final void resetRefCnt(T instance) {
        this.setReleaseRawRefCnt(instance, this.initialValue());
    }

    public final T retain(T instance) {
        return this.retain0(instance, 2);
    }

    public final T retain(T instance, int increment) {
        return this.retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
    }

    private T retain0(T instance, int increment) {
        int oldRef = this.getAndAddRawRefCnt(instance, increment);
        if ((oldRef & 0x80000001) != 0 || oldRef > Integer.MAX_VALUE - increment) {
            this.getAndAddRawRefCnt(instance, -increment);
            throw new IllegalReferenceCountException(0, increment >>> 1);
        }
        return instance;
    }

    public final boolean release(T instance) {
        return this.release0(instance, 2);
    }

    public final boolean release(T instance, int decrement) {
        return this.release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
    }

    private boolean release0(T instance, int decrement) {
        int next;
        int curr;
        do {
            if ((curr = this.getRawRefCnt(instance)) == decrement) {
                next = 1;
                continue;
            }
            if (curr < decrement || (curr & 1) == 1) {
                ReferenceCountUpdater.throwIllegalRefCountOnRelease(decrement, curr);
            }
            next = curr - decrement;
        } while (!this.casRawRefCnt(instance, curr, next));
        return (next & 1) == 1;
    }

    private static void throwIllegalRefCountOnRelease(int decrement, int curr) {
        throw new IllegalReferenceCountException(curr >>> 1, -(decrement >>> 1));
    }

    public static <T extends ReferenceCounted> UpdaterType updaterTypeOf(Class<T> clz, String fieldName) {
        long fieldOffset = ReferenceCountUpdater.getUnsafeOffset(clz, fieldName);
        if (fieldOffset >= 0L) {
            return UpdaterType.Unsafe;
        }
        if (PlatformDependent.hasVarHandle()) {
            return UpdaterType.VarHandle;
        }
        return UpdaterType.Atomic;
    }

    public static long getUnsafeOffset(Class<? extends ReferenceCounted> clz, String fieldName) {
        try {
            if (PlatformDependent.hasUnsafe()) {
                return PlatformDependent.objectFieldOffset(clz.getDeclaredField(fieldName));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return -1L;
    }

    public static enum UpdaterType {
        Unsafe,
        VarHandle,
        Atomic;

    }
}

