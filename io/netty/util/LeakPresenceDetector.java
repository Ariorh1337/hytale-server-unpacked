/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.SystemPropertyUtil;
import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

public class LeakPresenceDetector<T>
extends ResourceLeakDetector<T> {
    private static final String TRACK_CREATION_STACK_PROPERTY = "io.netty.util.LeakPresenceDetector.trackCreationStack";
    private static final boolean TRACK_CREATION_STACK = SystemPropertyUtil.getBoolean("io.netty.util.LeakPresenceDetector.trackCreationStack", false);
    private static final ResourceScope GLOBAL = new ResourceScope("global");
    private static int staticInitializerCount;

    private static boolean inStaticInitializerSlow(StackTraceElement[] stackTrace) {
        for (StackTraceElement element : stackTrace) {
            if (!element.getMethodName().equals("<clinit>")) continue;
            return true;
        }
        return false;
    }

    private static boolean inStaticInitializerFast() {
        return staticInitializerCount != 0 && LeakPresenceDetector.inStaticInitializerSlow(Thread.currentThread().getStackTrace());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <R> R staticInitializer(Supplier<R> supplier) {
        if (!LeakPresenceDetector.inStaticInitializerSlow(Thread.currentThread().getStackTrace())) {
            throw new IllegalStateException("Not in static initializer.");
        }
        Class clazz = LeakPresenceDetector.class;
        synchronized (LeakPresenceDetector.class) {
            ++staticInitializerCount;
            // ** MonitorExit[var1_1 /* !! */ ] (shouldn't be in output)
            try {
                clazz = supplier.get();
                return (R)clazz;
            }
            finally {
                Class<LeakPresenceDetector> clazz2 = LeakPresenceDetector.class;
                synchronized (LeakPresenceDetector.class) {
                    --staticInitializerCount;
                    // ** MonitorExit[var2_3] (shouldn't be in output)
                }
            }
        }
    }

    public LeakPresenceDetector(Class<?> resourceType) {
        super(resourceType, 0);
    }

    @Deprecated
    public LeakPresenceDetector(Class<?> resourceType, int samplingInterval) {
        this(resourceType);
    }

    public LeakPresenceDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
        this(resourceType);
    }

    protected ResourceScope currentScope() throws AllocationProhibitedException {
        return GLOBAL;
    }

    @Override
    public final ResourceLeakTracker<T> track(T obj) {
        if (LeakPresenceDetector.inStaticInitializerFast()) {
            return null;
        }
        return this.trackForcibly(obj);
    }

    @Override
    public final ResourceLeakTracker<T> trackForcibly(T obj) {
        return new PresenceTracker(this.currentScope());
    }

    @Override
    public final boolean isRecordEnabled() {
        return false;
    }

    public static void check() {
        ResourceLeakDetector<Object> detector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(Object.class);
        if (!(detector instanceof LeakPresenceDetector)) {
            throw new IllegalStateException("LeakPresenceDetector not in use. Please register it using -Dio.netty.customResourceLeakDetector=" + LeakPresenceDetector.class.getName());
        }
        ((LeakPresenceDetector)detector).currentScope().check();
    }

    public static final class AllocationProhibitedException
    extends IllegalStateException {
        public AllocationProhibitedException(String s) {
            super(s);
        }
    }

    private static final class LeakCreation
    extends Throwable {
        final Thread thread = Thread.currentThread();
        String message;

        private LeakCreation() {
        }

        @Override
        public synchronized String getMessage() {
            if (this.message == null) {
                this.message = LeakPresenceDetector.inStaticInitializerSlow(this.getStackTrace()) ? "Resource created in static initializer. Please wrap the static initializer in LeakPresenceDetector.staticInitializer so that this resource is excluded." : "Resource created outside static initializer on thread '" + this.thread.getName() + "' (" + (Object)((Object)this.thread.getState()) + "), likely leak.";
            }
            return this.message;
        }
    }

    public static final class ResourceScope
    implements Closeable {
        final String name;
        final LongAdder openResourceCounter = new LongAdder();
        final Map<PresenceTracker<?>, Throwable> creationStacks = LeakPresenceDetector.access$000() ? new ConcurrentHashMap() : null;
        boolean closed;

        public ResourceScope(String name) {
            this.name = name;
        }

        void checkOpen() {
            if (this.closed) {
                throw new AllocationProhibitedException("Resource scope '" + this.name + "' already closed");
            }
        }

        void check() {
            long n = this.openResourceCounter.sumThenReset();
            if (n != 0L) {
                StringBuilder msg = new StringBuilder("Possible memory leak detected for resource scope '").append(this.name).append("'. ");
                if (n < 0L) {
                    msg.append("Resource count was negative: A resource previously reported as a leak was released after all. Please ensure that that resource is released before its test finishes.");
                    throw new IllegalStateException(msg.toString());
                }
                if (TRACK_CREATION_STACK) {
                    msg.append("Creation stack traces:");
                    IllegalStateException ise = new IllegalStateException(msg.toString());
                    int i = 0;
                    for (Throwable t : this.creationStacks.values()) {
                        ise.addSuppressed(t);
                        if (i++ <= 5) continue;
                        break;
                    }
                    this.creationStacks.clear();
                    throw ise;
                }
                msg.append("Please use paranoid leak detection to get more information, or set -Dio.netty.util.LeakPresenceDetector.trackCreationStack=true");
                throw new IllegalStateException(msg.toString());
            }
        }

        public boolean hasOpenResources() {
            return this.openResourceCounter.sum() > 0L;
        }

        @Override
        public void close() {
            this.closed = true;
            this.check();
        }
    }

    private static final class PresenceTracker<T>
    extends AtomicBoolean
    implements ResourceLeakTracker<T> {
        private final ResourceScope scope;

        PresenceTracker(ResourceScope scope) {
            super(false);
            this.scope = scope;
            scope.checkOpen();
            scope.openResourceCounter.increment();
            if (TRACK_CREATION_STACK) {
                scope.creationStacks.put(this, new LeakCreation());
            }
        }

        @Override
        public void record() {
        }

        @Override
        public void record(Object hint) {
        }

        @Override
        public boolean close(Object trackedObject) {
            if (this.compareAndSet(false, true)) {
                this.scope.openResourceCounter.decrement();
                if (TRACK_CREATION_STACK) {
                    this.scope.creationStacks.remove(this);
                }
                this.scope.checkOpen();
                return true;
            }
            return false;
        }
    }
}

