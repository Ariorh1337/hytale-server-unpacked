/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalRunnable;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.LongLongHashMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.atomic.AtomicReference;

public class FastThreadLocalThread
extends Thread {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(FastThreadLocalThread.class);
    private static final AtomicReference<FallbackThreadSet> fallbackThreads = new AtomicReference<FallbackThreadSet>(FallbackThreadSet.EMPTY);
    private final boolean cleanupFastThreadLocals;
    private InternalThreadLocalMap threadLocalMap;

    public FastThreadLocalThread() {
        this.cleanupFastThreadLocals = false;
    }

    public FastThreadLocalThread(Runnable target) {
        super(FastThreadLocalRunnable.wrap(target));
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(ThreadGroup group, Runnable target) {
        super(group, FastThreadLocalRunnable.wrap(target));
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(String name) {
        super(name);
        this.cleanupFastThreadLocals = false;
    }

    public FastThreadLocalThread(ThreadGroup group, String name) {
        super(group, name);
        this.cleanupFastThreadLocals = false;
    }

    public FastThreadLocalThread(Runnable target, String name) {
        super(FastThreadLocalRunnable.wrap(target), name);
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(ThreadGroup group, Runnable target, String name) {
        super(group, FastThreadLocalRunnable.wrap(target), name);
        this.cleanupFastThreadLocals = true;
    }

    public FastThreadLocalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, FastThreadLocalRunnable.wrap(target), name, stackSize);
        this.cleanupFastThreadLocals = true;
    }

    public final InternalThreadLocalMap threadLocalMap() {
        if (this != Thread.currentThread() && logger.isWarnEnabled()) {
            logger.warn(new RuntimeException("It's not thread-safe to get 'threadLocalMap' which doesn't belong to the caller thread"));
        }
        return this.threadLocalMap;
    }

    public final void setThreadLocalMap(InternalThreadLocalMap threadLocalMap) {
        if (this != Thread.currentThread() && logger.isWarnEnabled()) {
            logger.warn(new RuntimeException("It's not thread-safe to set 'threadLocalMap' which doesn't belong to the caller thread"));
        }
        this.threadLocalMap = threadLocalMap;
    }

    @Deprecated
    public boolean willCleanupFastThreadLocals() {
        return this.cleanupFastThreadLocals;
    }

    @Deprecated
    public static boolean willCleanupFastThreadLocals(Thread thread) {
        return thread instanceof FastThreadLocalThread && ((FastThreadLocalThread)thread).willCleanupFastThreadLocals();
    }

    public static boolean currentThreadWillCleanupFastThreadLocals() {
        Thread currentThread = FastThreadLocalThread.currentThread();
        if (currentThread instanceof FastThreadLocalThread) {
            return ((FastThreadLocalThread)currentThread).willCleanupFastThreadLocals();
        }
        return FastThreadLocalThread.isFastThreadLocalVirtualThread();
    }

    public static boolean currentThreadHasFastThreadLocal() {
        return FastThreadLocalThread.currentThread() instanceof FastThreadLocalThread || FastThreadLocalThread.isFastThreadLocalVirtualThread();
    }

    private static boolean isFastThreadLocalVirtualThread() {
        return fallbackThreads.get().contains(FastThreadLocalThread.currentThread().getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void runWithFastThreadLocal(Runnable runnable) {
        Thread current = FastThreadLocalThread.currentThread();
        if (current instanceof FastThreadLocalThread) {
            throw new IllegalStateException("Caller is a real FastThreadLocalThread");
        }
        long id = current.getId();
        fallbackThreads.updateAndGet(set -> {
            if (set.contains(id)) {
                throw new IllegalStateException("Reentrant call to run()");
            }
            return set.add(id);
        });
        try {
            runnable.run();
        }
        finally {
            fallbackThreads.getAndUpdate(set -> set.remove(id));
            FastThreadLocal.removeAll();
        }
    }

    public boolean permitBlockingCalls() {
        return false;
    }

    private static final class FallbackThreadSet {
        static final FallbackThreadSet EMPTY = new FallbackThreadSet();
        private static final long EMPTY_VALUE = 0L;
        private final LongLongHashMap map;

        private FallbackThreadSet() {
            this.map = new LongLongHashMap(0L);
        }

        private FallbackThreadSet(LongLongHashMap map) {
            this.map = map;
        }

        public boolean contains(long threadId) {
            long key = threadId >>> 6;
            long bit = 1L << (int)(threadId & 0x3FL);
            long bitmap = this.map.get(key);
            return (bitmap & bit) != 0L;
        }

        public FallbackThreadSet add(long threadId) {
            long key = threadId >>> 6;
            long bit = 1L << (int)(threadId & 0x3FL);
            LongLongHashMap newMap = new LongLongHashMap(this.map);
            long oldBitmap = newMap.get(key);
            long newBitmap = oldBitmap | bit;
            newMap.put(key, newBitmap);
            return new FallbackThreadSet(newMap);
        }

        public FallbackThreadSet remove(long threadId) {
            long key = threadId >>> 6;
            long bit = 1L << (int)(threadId & 0x3FL);
            long oldBitmap = this.map.get(key);
            if ((oldBitmap & bit) == 0L) {
                return this;
            }
            LongLongHashMap newMap = new LongLongHashMap(this.map);
            long newBitmap = oldBitmap & (bit ^ 0xFFFFFFFFFFFFFFFFL);
            if (newBitmap != 0L) {
                newMap.put(key, newBitmap);
            } else {
                newMap.remove(key);
            }
            return new FallbackThreadSet(newMap);
        }
    }
}

