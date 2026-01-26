/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public final class RefCnt {
    private static final int UNSAFE = 0;
    private static final int VAR_HANDLE = 1;
    private static final int ATOMIC_UPDATER = 2;
    private static final int REF_CNT_IMPL = PlatformDependent.hasUnsafe() ? 0 : (PlatformDependent.hasVarHandle() ? 1 : 2);
    volatile int value;

    public RefCnt() {
        switch (REF_CNT_IMPL) {
            case 0: {
                UnsafeRefCnt.init(this);
                break;
            }
            case 1: {
                VarHandleRefCnt.init(this);
                break;
            }
            default: {
                AtomicRefCnt.init(this);
            }
        }
    }

    public static int refCnt(RefCnt ref) {
        switch (REF_CNT_IMPL) {
            case 0: {
                return UnsafeRefCnt.refCnt(ref);
            }
            case 1: {
                return VarHandleRefCnt.refCnt(ref);
            }
        }
        return AtomicRefCnt.refCnt(ref);
    }

    public static void retain(RefCnt ref) {
        switch (REF_CNT_IMPL) {
            case 0: {
                UnsafeRefCnt.retain(ref);
                break;
            }
            case 1: {
                VarHandleRefCnt.retain(ref);
                break;
            }
            default: {
                AtomicRefCnt.retain(ref);
            }
        }
    }

    public static void retain(RefCnt ref, int increment) {
        switch (REF_CNT_IMPL) {
            case 0: {
                UnsafeRefCnt.retain(ref, increment);
                break;
            }
            case 1: {
                VarHandleRefCnt.retain(ref, increment);
                break;
            }
            default: {
                AtomicRefCnt.retain(ref, increment);
            }
        }
    }

    public static boolean release(RefCnt ref) {
        switch (REF_CNT_IMPL) {
            case 0: {
                return UnsafeRefCnt.release(ref);
            }
            case 1: {
                return VarHandleRefCnt.release(ref);
            }
        }
        return AtomicRefCnt.release(ref);
    }

    public static boolean release(RefCnt ref, int decrement) {
        switch (REF_CNT_IMPL) {
            case 0: {
                return UnsafeRefCnt.release(ref, decrement);
            }
            case 1: {
                return VarHandleRefCnt.release(ref, decrement);
            }
        }
        return AtomicRefCnt.release(ref, decrement);
    }

    public static boolean isLiveNonVolatile(RefCnt ref) {
        switch (REF_CNT_IMPL) {
            case 0: {
                return UnsafeRefCnt.isLiveNonVolatile(ref);
            }
            case 1: {
                return VarHandleRefCnt.isLiveNonVolatile(ref);
            }
        }
        return AtomicRefCnt.isLiveNonVolatile(ref);
    }

    public static void setRefCnt(RefCnt ref, int refCnt) {
        switch (REF_CNT_IMPL) {
            case 0: {
                UnsafeRefCnt.setRefCnt(ref, refCnt);
                break;
            }
            case 1: {
                VarHandleRefCnt.setRefCnt(ref, refCnt);
                break;
            }
            default: {
                AtomicRefCnt.setRefCnt(ref, refCnt);
            }
        }
    }

    public static void resetRefCnt(RefCnt ref) {
        switch (REF_CNT_IMPL) {
            case 0: {
                UnsafeRefCnt.resetRefCnt(ref);
                break;
            }
            case 1: {
                VarHandleRefCnt.resetRefCnt(ref);
                break;
            }
            default: {
                AtomicRefCnt.resetRefCnt(ref);
            }
        }
    }

    static void throwIllegalRefCountOnRelease(int decrement, int curr) {
        throw new IllegalReferenceCountException(curr >>> 1, -(decrement >>> 1));
    }

    private static final class UnsafeRefCnt {
        private static final long VALUE_OFFSET = UnsafeRefCnt.getUnsafeOffset(RefCnt.class, "value");

        private UnsafeRefCnt() {
        }

        private static long getUnsafeOffset(Class<?> clz, String fieldName) {
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

        static void init(RefCnt instance) {
            PlatformDependent.safeConstructPutInt(instance, VALUE_OFFSET, 2);
        }

        static int refCnt(RefCnt instance) {
            return PlatformDependent.getVolatileInt(instance, VALUE_OFFSET) >>> 1;
        }

        static void retain(RefCnt instance) {
            UnsafeRefCnt.retain0(instance, 2);
        }

        static void retain(RefCnt instance, int increment) {
            UnsafeRefCnt.retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
        }

        private static void retain0(RefCnt instance, int increment) {
            int oldRef = PlatformDependent.getAndAddInt(instance, VALUE_OFFSET, increment);
            if ((oldRef & 0x80000001) != 0 || oldRef > Integer.MAX_VALUE - increment) {
                PlatformDependent.getAndAddInt(instance, VALUE_OFFSET, -increment);
                throw new IllegalReferenceCountException(0, increment >>> 1);
            }
        }

        static boolean release(RefCnt instance) {
            return UnsafeRefCnt.release0(instance, 2);
        }

        static boolean release(RefCnt instance, int decrement) {
            return UnsafeRefCnt.release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
        }

        private static boolean release0(RefCnt instance, int decrement) {
            int next;
            int curr;
            do {
                if ((curr = PlatformDependent.getInt(instance, VALUE_OFFSET)) == decrement) {
                    next = 1;
                    continue;
                }
                if (curr < decrement || (curr & 1) == 1) {
                    RefCnt.throwIllegalRefCountOnRelease(decrement, curr);
                }
                next = curr - decrement;
            } while (!PlatformDependent.compareAndSwapInt(instance, VALUE_OFFSET, curr, next));
            return (next & 1) == 1;
        }

        static void setRefCnt(RefCnt instance, int refCnt) {
            int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
            PlatformDependent.putOrderedInt(instance, VALUE_OFFSET, rawRefCnt);
        }

        static void resetRefCnt(RefCnt instance) {
            PlatformDependent.putOrderedInt(instance, VALUE_OFFSET, 2);
        }

        static boolean isLiveNonVolatile(RefCnt instance) {
            int rawCnt = PlatformDependent.getInt(instance, VALUE_OFFSET);
            if (rawCnt == 2) {
                return true;
            }
            return (rawCnt & 1) == 0;
        }
    }

    private static final class VarHandleRefCnt {
        private static final VarHandle VH = PlatformDependent.findVarHandleOfIntField(MethodHandles.lookup(), RefCnt.class, "value");

        private VarHandleRefCnt() {
        }

        static void init(RefCnt instance) {
            VH.set(instance, 2);
            VarHandle.storeStoreFence();
        }

        static int refCnt(RefCnt instance) {
            return VH.getAcquire(instance) >>> 1;
        }

        static void retain(RefCnt instance) {
            VarHandleRefCnt.retain0(instance, 2);
        }

        static void retain(RefCnt instance, int increment) {
            VarHandleRefCnt.retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
        }

        private static void retain0(RefCnt instance, int increment) {
            int oldRef = VH.getAndAdd(instance, increment);
            if ((oldRef & 0x80000001) != 0 || oldRef > Integer.MAX_VALUE - increment) {
                VH.getAndAdd(instance, -increment);
                throw new IllegalReferenceCountException(0, increment >>> 1);
            }
        }

        static boolean release(RefCnt instance) {
            return VarHandleRefCnt.release0(instance, 2);
        }

        static boolean release(RefCnt instance, int decrement) {
            return VarHandleRefCnt.release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
        }

        private static boolean release0(RefCnt instance, int decrement) {
            int next;
            int curr;
            do {
                if ((curr = VH.get(instance)) == decrement) {
                    next = 1;
                    continue;
                }
                if (curr < decrement || (curr & 1) == 1) {
                    RefCnt.throwIllegalRefCountOnRelease(decrement, curr);
                }
                next = curr - decrement;
            } while (!VH.compareAndSet(instance, curr, next));
            return (next & 1) == 1;
        }

        static void setRefCnt(RefCnt instance, int refCnt) {
            int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
            VH.setRelease(instance, rawRefCnt);
        }

        static void resetRefCnt(RefCnt instance) {
            VH.setRelease(instance, 2);
        }

        static boolean isLiveNonVolatile(RefCnt instance) {
            int rawCnt = VH.get(instance);
            if (rawCnt == 2) {
                return true;
            }
            return (rawCnt & 1) == 0;
        }
    }

    private static final class AtomicRefCnt {
        private static final AtomicIntegerFieldUpdater<RefCnt> UPDATER = AtomicIntegerFieldUpdater.newUpdater(RefCnt.class, "value");

        private AtomicRefCnt() {
        }

        static void init(RefCnt instance) {
            UPDATER.set(instance, 2);
        }

        static int refCnt(RefCnt instance) {
            return UPDATER.get(instance) >>> 1;
        }

        static void retain(RefCnt instance) {
            AtomicRefCnt.retain0(instance, 2);
        }

        static void retain(RefCnt instance, int increment) {
            AtomicRefCnt.retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
        }

        private static void retain0(RefCnt instance, int increment) {
            int oldRef = UPDATER.getAndAdd(instance, increment);
            if ((oldRef & 0x80000001) != 0 || oldRef > Integer.MAX_VALUE - increment) {
                UPDATER.getAndAdd(instance, -increment);
                throw new IllegalReferenceCountException(0, increment >>> 1);
            }
        }

        static boolean release(RefCnt instance) {
            return AtomicRefCnt.release0(instance, 2);
        }

        static boolean release(RefCnt instance, int decrement) {
            return AtomicRefCnt.release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
        }

        private static boolean release0(RefCnt instance, int decrement) {
            int next;
            int curr;
            do {
                if ((curr = instance.value) == decrement) {
                    next = 1;
                    continue;
                }
                if (curr < decrement || (curr & 1) == 1) {
                    RefCnt.throwIllegalRefCountOnRelease(decrement, curr);
                }
                next = curr - decrement;
            } while (!UPDATER.compareAndSet(instance, curr, next));
            return (next & 1) == 1;
        }

        static void setRefCnt(RefCnt instance, int refCnt) {
            int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
            UPDATER.lazySet(instance, rawRefCnt);
        }

        static void resetRefCnt(RefCnt instance) {
            UPDATER.lazySet(instance, 2);
        }

        static boolean isLiveNonVolatile(RefCnt instance) {
            int rawCnt = instance.value;
            if (rawCnt == 2) {
                return true;
            }
            return (rawCnt & 1) == 0;
        }
    }
}

