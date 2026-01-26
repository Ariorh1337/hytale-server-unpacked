/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

public class HashedWheelTimer
implements Timer {
    static final InternalLogger logger = InternalLoggerFactory.getInstance(HashedWheelTimer.class);
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();
    private static final AtomicBoolean WARNED_TOO_MANY_INSTANCES = new AtomicBoolean();
    private static final int INSTANCE_COUNT_LIMIT = 64;
    private static final long MILLISECOND_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
    private static final ResourceLeakDetector<HashedWheelTimer> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(HashedWheelTimer.class, 1);
    private static final AtomicIntegerFieldUpdater<HashedWheelTimer> WORKER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimer.class, "workerState");
    private final ResourceLeakTracker<HashedWheelTimer> leak;
    private final Worker worker = new Worker();
    private final Thread workerThread;
    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;
    private volatile int workerState;
    private final long tickDuration;
    private final HashedWheelBucket[] wheel;
    private final int mask;
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);
    private final Queue<HashedWheelTimeout> timeouts = PlatformDependent.newMpscQueue();
    private final Queue<HashedWheelTimeout> cancelledTimeouts = PlatformDependent.newMpscQueue();
    private final AtomicLong pendingTimeouts = new AtomicLong(0L);
    private final long maxPendingTimeouts;
    private final Executor taskExecutor;
    private volatile long startTime;

    public HashedWheelTimer() {
        this(Executors.defaultThreadFactory());
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit) {
        this(Executors.defaultThreadFactory(), tickDuration, unit);
    }

    public HashedWheelTimer(long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
    }

    public HashedWheelTimer(ThreadFactory threadFactory) {
        this(threadFactory, 100L, TimeUnit.MILLISECONDS);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
        this(threadFactory, tickDuration, unit, 512);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel) {
        this(threadFactory, tickDuration, unit, ticksPerWheel, true);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection) {
        this(threadFactory, tickDuration, unit, ticksPerWheel, leakDetection, -1L);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection, long maxPendingTimeouts) {
        this(threadFactory, tickDuration, unit, ticksPerWheel, leakDetection, maxPendingTimeouts, ImmediateExecutor.INSTANCE);
    }

    public HashedWheelTimer(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection, long maxPendingTimeouts, Executor taskExecutor) {
        ObjectUtil.checkNotNull(threadFactory, "threadFactory");
        ObjectUtil.checkNotNull(unit, "unit");
        ObjectUtil.checkPositive(tickDuration, "tickDuration");
        ObjectUtil.checkPositive(ticksPerWheel, "ticksPerWheel");
        this.taskExecutor = ObjectUtil.checkNotNull(taskExecutor, "taskExecutor");
        this.wheel = HashedWheelTimer.createWheel(ticksPerWheel);
        this.mask = this.wheel.length - 1;
        long duration = unit.toNanos(tickDuration);
        if (duration >= Long.MAX_VALUE / (long)this.wheel.length) {
            throw new IllegalArgumentException(String.format("tickDuration: %d (expected: 0 < tickDuration in nanos < %d", tickDuration, Long.MAX_VALUE / (long)this.wheel.length));
        }
        if (duration < MILLISECOND_NANOS) {
            logger.warn("Configured tickDuration {} smaller than {}, using 1ms.", (Object)tickDuration, (Object)MILLISECOND_NANOS);
            this.tickDuration = MILLISECOND_NANOS;
        } else {
            this.tickDuration = duration;
        }
        this.workerThread = threadFactory.newThread(this.worker);
        this.leak = leakDetection || !this.workerThread.isDaemon() ? leakDetector.track(this) : null;
        this.maxPendingTimeouts = maxPendingTimeouts;
        if (INSTANCE_COUNTER.incrementAndGet() > 64 && WARNED_TOO_MANY_INSTANCES.compareAndSet(false, true)) {
            HashedWheelTimer.reportTooManyInstances();
        }
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
        }
        finally {
            if (WORKER_STATE_UPDATER.getAndSet(this, 2) != 2) {
                INSTANCE_COUNTER.decrementAndGet();
            }
        }
    }

    private static HashedWheelBucket[] createWheel(int ticksPerWheel) {
        ticksPerWheel = MathUtil.findNextPositivePowerOfTwo(ticksPerWheel);
        HashedWheelBucket[] wheel = new HashedWheelBucket[ticksPerWheel];
        for (int i = 0; i < wheel.length; ++i) {
            wheel[i] = new HashedWheelBucket();
        }
        return wheel;
    }

    public void start() {
        int state = WORKER_STATE_UPDATER.get(this);
        switch (state) {
            case 0: {
                if (!WORKER_STATE_UPDATER.compareAndSet(this, 0, 1)) break;
                this.workerThread.start();
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                throw new IllegalStateException("cannot be started once stopped");
            }
            default: {
                throw new Error("Invalid WorkerState: " + state);
            }
        }
        while (this.startTime == 0L) {
            try {
                this.startTimeInitialized.await();
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    @Override
    public Set<Timeout> stop() {
        if (Thread.currentThread() == this.workerThread) {
            throw new IllegalStateException(HashedWheelTimer.class.getSimpleName() + ".stop() cannot be called from " + TimerTask.class.getSimpleName());
        }
        if (!WORKER_STATE_UPDATER.compareAndSet(this, 1, 2)) {
            if (WORKER_STATE_UPDATER.getAndSet(this, 2) != 2) {
                INSTANCE_COUNTER.decrementAndGet();
                if (this.leak != null) {
                    boolean closed = this.leak.close(this);
                    assert (closed);
                }
            }
            return Collections.emptySet();
        }
        try {
            boolean interrupted = false;
            while (this.workerThread.isAlive()) {
                this.workerThread.interrupt();
                try {
                    this.workerThread.join(100L);
                }
                catch (InterruptedException ignored) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        finally {
            INSTANCE_COUNTER.decrementAndGet();
            if (this.leak != null) {
                boolean closed = this.leak.close(this);
                assert (closed);
            }
        }
        Set<Timeout> unprocessed = this.worker.unprocessedTimeouts();
        HashSet<Timeout> cancelled = new HashSet<Timeout>(unprocessed.size());
        for (Timeout timeout : unprocessed) {
            if (!timeout.cancel()) continue;
            cancelled.add(timeout);
        }
        return cancelled;
    }

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(task, "task");
        ObjectUtil.checkNotNull(unit, "unit");
        long pendingTimeoutsCount = this.pendingTimeouts.incrementAndGet();
        if (this.maxPendingTimeouts > 0L && pendingTimeoutsCount > this.maxPendingTimeouts) {
            this.pendingTimeouts.decrementAndGet();
            throw new RejectedExecutionException("Number of pending timeouts (" + pendingTimeoutsCount + ") is greater than or equal to maximum allowed pending timeouts (" + this.maxPendingTimeouts + ")");
        }
        this.start();
        long deadline = System.nanoTime() + unit.toNanos(delay) - this.startTime;
        if (delay > 0L && deadline < 0L) {
            deadline = Long.MAX_VALUE;
        }
        HashedWheelTimeout timeout = new HashedWheelTimeout(this, task, deadline);
        this.timeouts.add(timeout);
        return timeout;
    }

    public long pendingTimeouts() {
        return this.pendingTimeouts.get();
    }

    private static void reportTooManyInstances() {
        if (logger.isErrorEnabled()) {
            String resourceType = StringUtil.simpleClassName(HashedWheelTimer.class);
            logger.error("You are creating too many " + resourceType + " instances. " + resourceType + " is a shared resource that must be reused across the JVM, so that only a few instances are created.");
        }
    }

    private static final class HashedWheelBucket {
        private HashedWheelTimeout head;
        private HashedWheelTimeout tail;

        private HashedWheelBucket() {
        }

        public void addTimeout(HashedWheelTimeout timeout) {
            assert (timeout.bucket == null);
            timeout.bucket = this;
            if (this.head == null) {
                this.head = this.tail = timeout;
            } else {
                this.tail.next = timeout;
                timeout.prev = this.tail;
                this.tail = timeout;
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public void expireTimeouts(long deadline) {
            HashedWheelTimeout timeout = this.head;
            while (timeout != null) {
                HashedWheelTimeout next = timeout.next;
                if (timeout.remainingRounds <= 0L) {
                    if (timeout.deadline > deadline) throw new IllegalStateException(String.format("timeout.deadline (%d) > deadline (%d)", timeout.deadline, deadline));
                    timeout.expire();
                } else if (!timeout.isCancelled()) {
                    --timeout.remainingRounds;
                }
                timeout = next;
            }
        }

        public HashedWheelTimeout remove(HashedWheelTimeout timeout) {
            HashedWheelTimeout prev = timeout.prev;
            HashedWheelTimeout next = timeout.next;
            if (prev != null) {
                prev.next = next;
            }
            if (next != null) {
                next.prev = prev;
            }
            if (timeout == this.head) {
                this.head = next;
            }
            if (timeout == this.tail) {
                this.tail = prev;
            }
            timeout.prev = null;
            timeout.next = null;
            timeout.bucket = null;
            return next;
        }

        public void clearTimeouts(Set<Timeout> set) {
            HashedWheelTimeout timeout;
            while ((timeout = this.pollTimeout()) != null) {
                if (timeout.isExpired() || timeout.isCancelled()) continue;
                set.add(timeout);
            }
            return;
        }

        private HashedWheelTimeout pollTimeout() {
            HashedWheelTimeout head = this.head;
            if (head == null) {
                return null;
            }
            HashedWheelTimeout next = head.next;
            if (next == null) {
                this.head = null;
                this.tail = null;
            } else {
                this.head = next;
                next.prev = null;
            }
            head.next = null;
            head.prev = null;
            head.bucket = null;
            return head;
        }
    }

    private static final class HashedWheelTimeout
    implements Timeout,
    Runnable {
        private static final int ST_INIT = 0;
        private static final int ST_CANCELLED = 1;
        private static final int ST_EXPIRED = 2;
        private static final AtomicIntegerFieldUpdater<HashedWheelTimeout> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(HashedWheelTimeout.class, "state");
        private final HashedWheelTimer timer;
        private final TimerTask task;
        private final long deadline;
        private volatile int state = 0;
        long remainingRounds;
        HashedWheelTimeout next;
        HashedWheelTimeout prev;
        HashedWheelBucket bucket;

        HashedWheelTimeout(HashedWheelTimer timer, TimerTask task, long deadline) {
            this.timer = timer;
            this.task = task;
            this.deadline = deadline;
        }

        @Override
        public Timer timer() {
            return this.timer;
        }

        @Override
        public TimerTask task() {
            return this.task;
        }

        @Override
        public boolean cancel() {
            if (!this.compareAndSetState(0, 1)) {
                return false;
            }
            this.timer.cancelledTimeouts.add(this);
            return true;
        }

        private void remove() {
            HashedWheelBucket bucket = this.bucket;
            if (bucket != null) {
                bucket.remove(this);
            }
            this.timer.pendingTimeouts.decrementAndGet();
        }

        void removeAfterCancellation() {
            this.remove();
            this.task.cancelled(this);
        }

        public boolean compareAndSetState(int expected, int state) {
            return STATE_UPDATER.compareAndSet(this, expected, state);
        }

        public int state() {
            return this.state;
        }

        @Override
        public boolean isCancelled() {
            return this.state() == 1;
        }

        @Override
        public boolean isExpired() {
            return this.state() == 2;
        }

        public void expire() {
            block3: {
                if (!this.compareAndSetState(0, 2)) {
                    return;
                }
                try {
                    this.remove();
                    this.timer.taskExecutor.execute(this);
                }
                catch (Throwable t) {
                    if (!logger.isWarnEnabled()) break block3;
                    logger.warn("An exception was thrown while submit " + TimerTask.class.getSimpleName() + " for execution.", t);
                }
            }
        }

        @Override
        public void run() {
            block2: {
                try {
                    this.task.run(this);
                }
                catch (Throwable t) {
                    if (!logger.isWarnEnabled()) break block2;
                    logger.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.', t);
                }
            }
        }

        public String toString() {
            long currentTime = System.nanoTime();
            long remaining = this.deadline - currentTime + this.timer.startTime;
            StringBuilder buf = new StringBuilder(192).append(StringUtil.simpleClassName(this)).append('(').append("deadline: ");
            if (remaining > 0L) {
                buf.append(remaining).append(" ns later");
            } else if (remaining < 0L) {
                buf.append(-remaining).append(" ns ago");
            } else {
                buf.append("now");
            }
            if (this.isCancelled()) {
                buf.append(", cancelled");
            }
            return buf.append(", task: ").append(this.task()).append(')').toString();
        }
    }

    private final class Worker
    implements Runnable {
        private final Set<Timeout> unprocessedTimeouts = new HashSet<Timeout>();
        private long tick;

        private Worker() {
        }

        @Override
        public void run() {
            HashedWheelTimeout timeout;
            HashedWheelTimer.this.startTime = System.nanoTime();
            if (HashedWheelTimer.this.startTime == 0L) {
                HashedWheelTimer.this.startTime = 1L;
            }
            HashedWheelTimer.this.startTimeInitialized.countDown();
            do {
                long deadline;
                if ((deadline = this.waitForNextTick()) <= 0L) continue;
                int idx = (int)(this.tick & (long)HashedWheelTimer.this.mask);
                this.processCancelledTasks();
                HashedWheelBucket bucket = HashedWheelTimer.this.wheel[idx];
                this.transferTimeoutsToBuckets();
                bucket.expireTimeouts(deadline);
                ++this.tick;
            } while (WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == 1);
            for (HashedWheelBucket bucket : HashedWheelTimer.this.wheel) {
                bucket.clearTimeouts(this.unprocessedTimeouts);
            }
            while ((timeout = (HashedWheelTimeout)HashedWheelTimer.this.timeouts.poll()) != null) {
                if (timeout.isCancelled()) continue;
                this.unprocessedTimeouts.add(timeout);
            }
            this.processCancelledTasks();
        }

        private void transferTimeoutsToBuckets() {
            HashedWheelTimeout timeout;
            for (int i = 0; i < 100000 && (timeout = (HashedWheelTimeout)HashedWheelTimer.this.timeouts.poll()) != null; ++i) {
                if (timeout.state() == 1) continue;
                long calculated = timeout.deadline / HashedWheelTimer.this.tickDuration;
                timeout.remainingRounds = (calculated - this.tick) / (long)HashedWheelTimer.this.wheel.length;
                long ticks = Math.max(calculated, this.tick);
                int stopIndex = (int)(ticks & (long)HashedWheelTimer.this.mask);
                HashedWheelBucket bucket = HashedWheelTimer.this.wheel[stopIndex];
                bucket.addTimeout(timeout);
            }
        }

        private void processCancelledTasks() {
            HashedWheelTimeout timeout;
            while ((timeout = (HashedWheelTimeout)HashedWheelTimer.this.cancelledTimeouts.poll()) != null) {
                try {
                    timeout.removeAfterCancellation();
                }
                catch (Throwable t) {
                    if (!logger.isWarnEnabled()) continue;
                    logger.warn("An exception was thrown while process a cancellation task", t);
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private long waitForNextTick() {
            long deadline = HashedWheelTimer.this.tickDuration * (this.tick + 1L);
            while (true) {
                long currentTime;
                long sleepTimeMs;
                if ((sleepTimeMs = (deadline - (currentTime = System.nanoTime() - HashedWheelTimer.this.startTime) + 999999L) / 1000000L) <= 0L) {
                    if (currentTime != Long.MIN_VALUE) return currentTime;
                    return -9223372036854775807L;
                }
                if (PlatformDependent.isWindows() && (sleepTimeMs = sleepTimeMs / 10L * 10L) == 0L) {
                    sleepTimeMs = 1L;
                }
                try {
                    Thread.sleep(sleepTimeMs);
                    continue;
                }
                catch (InterruptedException ignored) {
                    if (WORKER_STATE_UPDATER.get(HashedWheelTimer.this) == 2) return Long.MIN_VALUE;
                    continue;
                }
                break;
            }
        }

        public Set<Timeout> unprocessedTimeouts() {
            return Collections.unmodifiableSet(this.unprocessedTimeouts);
        }
    }
}

