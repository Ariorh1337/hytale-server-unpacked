/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Async$Schedule
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.concurrent.ThreadProperties;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.Async;

public abstract class SingleThreadEventExecutor
extends AbstractScheduledEventExecutor
implements OrderedEventExecutor {
    static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", Integer.MAX_VALUE));
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_SUSPENDING = 2;
    private static final int ST_SUSPENDED = 3;
    private static final int ST_STARTED = 4;
    private static final int ST_SHUTTING_DOWN = 5;
    private static final int ST_SHUTDOWN = 6;
    private static final int ST_TERMINATED = 7;
    private static final Runnable NOOP_TASK = new Runnable(){

        @Override
        public void run() {
        }
    };
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");
    private static final AtomicReferenceFieldUpdater<SingleThreadEventExecutor, ThreadProperties> PROPERTIES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(SingleThreadEventExecutor.class, ThreadProperties.class, "threadProperties");
    private static final AtomicLongFieldUpdater<SingleThreadEventExecutor> ACCUMULATED_ACTIVE_TIME_NANOS_UPDATER = AtomicLongFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "accumulatedActiveTimeNanos");
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> CONSECUTIVE_IDLE_CYCLES_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "consecutiveIdleCycles");
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> CONSECUTIVE_BUSY_CYCLES_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "consecutiveBusyCycles");
    private final Queue<Runnable> taskQueue;
    private volatile Thread thread;
    private volatile ThreadProperties threadProperties;
    private final Executor executor;
    private volatile boolean interrupted;
    private final Lock processingLock = new ReentrantLock();
    private final CountDownLatch threadLock = new CountDownLatch(1);
    private final Set<Runnable> shutdownHooks = new LinkedHashSet<Runnable>();
    private final boolean addTaskWakesUp;
    private final int maxPendingTasks;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private final boolean supportSuspension;
    private volatile long accumulatedActiveTimeNanos;
    private volatile long lastActivityTimeNanos;
    private volatile int consecutiveIdleCycles;
    private volatile int consecutiveBusyCycles;
    private long lastExecutionTime;
    private volatile int state = 1;
    private volatile long gracefulShutdownQuietPeriod;
    private volatile long gracefulShutdownTimeout;
    private long gracefulShutdownStartTime;
    private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
    private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        this(parent, new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        this(parent, (Executor)new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp, maxPendingTasks, rejectedHandler);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, boolean supportSuspension, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        this(parent, (Executor)new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp, supportSuspension, maxPendingTasks, rejectedHandler);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp) {
        this(parent, executor, addTaskWakesUp, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        this(parent, executor, addTaskWakesUp, false, maxPendingTasks, rejectedHandler);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, boolean supportSuspension, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        super(parent);
        this.addTaskWakesUp = addTaskWakesUp;
        this.supportSuspension = supportSuspension;
        this.maxPendingTasks = Math.max(16, maxPendingTasks);
        this.executor = ThreadExecutorMap.apply(executor, (EventExecutor)this);
        this.taskQueue = this.newTaskQueue(this.maxPendingTasks);
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
        this.lastActivityTimeNanos = this.ticker().nanoTime();
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, RejectedExecutionHandler rejectedHandler) {
        this(parent, executor, addTaskWakesUp, false, taskQueue, rejectedHandler);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, boolean supportSuspension, Queue<Runnable> taskQueue, RejectedExecutionHandler rejectedHandler) {
        super(parent);
        this.addTaskWakesUp = addTaskWakesUp;
        this.supportSuspension = supportSuspension;
        this.maxPendingTasks = DEFAULT_MAX_PENDING_EXECUTOR_TASKS;
        this.executor = ThreadExecutorMap.apply(executor, (EventExecutor)this);
        this.taskQueue = ObjectUtil.checkNotNull(taskQueue, "taskQueue");
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
    }

    @Deprecated
    protected Queue<Runnable> newTaskQueue() {
        return this.newTaskQueue(this.maxPendingTasks);
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<Runnable>(maxPendingTasks);
    }

    protected void interruptThread() {
        Thread currentThread = this.thread;
        if (currentThread == null) {
            this.interrupted = true;
        } else {
            currentThread.interrupt();
        }
    }

    protected Runnable pollTask() {
        assert (this.inEventLoop());
        return SingleThreadEventExecutor.pollTaskFrom(this.taskQueue);
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        Runnable task;
        while ((task = taskQueue.poll()) == WAKEUP_TASK) {
        }
        return task;
    }

    protected Runnable takeTask() {
        Runnable task;
        assert (this.inEventLoop());
        if (!(this.taskQueue instanceof BlockingQueue)) {
            throw new UnsupportedOperationException();
        }
        BlockingQueue taskQueue = (BlockingQueue)this.taskQueue;
        do {
            ScheduledFutureTask<?> scheduledTask;
            if ((scheduledTask = this.peekScheduledTask()) == null) {
                Runnable task2 = null;
                try {
                    task2 = (Runnable)taskQueue.take();
                    if (task2 == WAKEUP_TASK) {
                        task2 = null;
                    }
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return task2;
            }
            long delayNanos = scheduledTask.delayNanos();
            task = null;
            if (delayNanos > 0L) {
                try {
                    task = (Runnable)taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e) {
                    return null;
                }
            }
            if (task != null) continue;
            this.fetchFromScheduledTaskQueue();
            task = (Runnable)taskQueue.poll();
        } while (task == null);
        if (task == WAKEUP_TASK) {
            return null;
        }
        return task;
    }

    private boolean fetchFromScheduledTaskQueue() {
        return this.fetchFromScheduledTaskQueue(this.taskQueue);
    }

    private boolean executeExpiredScheduledTasks() {
        if (this.scheduledTaskQueue == null || this.scheduledTaskQueue.isEmpty()) {
            return false;
        }
        long nanoTime = this.getCurrentTimeNanos();
        Runnable scheduledTask = this.pollScheduledTask(nanoTime);
        if (scheduledTask == null) {
            return false;
        }
        do {
            SingleThreadEventExecutor.safeExecute(scheduledTask);
        } while ((scheduledTask = this.pollScheduledTask(nanoTime)) != null);
        return true;
    }

    protected Runnable peekTask() {
        assert (this.inEventLoop());
        return this.taskQueue.peek();
    }

    protected boolean hasTasks() {
        assert (this.inEventLoop());
        return !this.taskQueue.isEmpty();
    }

    public int pendingTasks() {
        return this.taskQueue.size();
    }

    protected void addTask(Runnable task) {
        ObjectUtil.checkNotNull(task, "task");
        if (!this.offerTask(task)) {
            this.reject(task);
        }
    }

    final boolean offerTask(Runnable task) {
        if (this.isShutdown()) {
            SingleThreadEventExecutor.reject();
        }
        return this.taskQueue.offer(task);
    }

    protected boolean removeTask(Runnable task) {
        return this.taskQueue.remove(ObjectUtil.checkNotNull(task, "task"));
    }

    protected boolean runAllTasks() {
        boolean fetchedAll;
        assert (this.inEventLoop());
        boolean ranAtLeastOne = false;
        do {
            fetchedAll = this.fetchFromScheduledTaskQueue(this.taskQueue);
            if (!this.runAllTasksFrom(this.taskQueue)) continue;
            ranAtLeastOne = true;
        } while (!fetchedAll);
        if (ranAtLeastOne) {
            this.lastExecutionTime = this.getCurrentTimeNanos();
        }
        this.afterRunningAllTasks();
        return ranAtLeastOne;
    }

    protected final boolean runScheduledAndExecutorTasks(int maxDrainAttempts) {
        boolean ranAtLeastOneTask;
        assert (this.inEventLoop());
        int drainAttempt = 0;
        while ((ranAtLeastOneTask = this.runExistingTasksFrom(this.taskQueue) | this.executeExpiredScheduledTasks()) && ++drainAttempt < maxDrainAttempts) {
        }
        if (drainAttempt > 0) {
            this.lastExecutionTime = this.getCurrentTimeNanos();
        }
        this.afterRunningAllTasks();
        return drainAttempt > 0;
    }

    protected final boolean runAllTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = SingleThreadEventExecutor.pollTaskFrom(taskQueue);
        if (task == null) {
            return false;
        }
        do {
            SingleThreadEventExecutor.safeExecute(task);
        } while ((task = SingleThreadEventExecutor.pollTaskFrom(taskQueue)) != null);
        return true;
    }

    private boolean runExistingTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = SingleThreadEventExecutor.pollTaskFrom(taskQueue);
        if (task == null) {
            return false;
        }
        int remaining = Math.min(this.maxPendingTasks, taskQueue.size());
        SingleThreadEventExecutor.safeExecute(task);
        while (remaining-- > 0 && (task = taskQueue.poll()) != null) {
            SingleThreadEventExecutor.safeExecute(task);
        }
        return true;
    }

    protected boolean runAllTasks(long timeoutNanos) {
        long lastExecutionTime;
        long workStartTime;
        block2: {
            this.fetchFromScheduledTaskQueue(this.taskQueue);
            Runnable task = this.pollTask();
            if (task == null) {
                this.afterRunningAllTasks();
                return false;
            }
            long deadline = timeoutNanos > 0L ? this.getCurrentTimeNanos() + timeoutNanos : 0L;
            long runTasks = 0L;
            workStartTime = this.ticker().nanoTime();
            do {
                SingleThreadEventExecutor.safeExecute(task);
                if ((++runTasks & 0x3FL) == 0L && (lastExecutionTime = this.getCurrentTimeNanos()) >= deadline) break block2;
            } while ((task = this.pollTask()) != null);
            lastExecutionTime = this.getCurrentTimeNanos();
        }
        long workEndTime = this.ticker().nanoTime();
        this.accumulatedActiveTimeNanos += workEndTime - workStartTime;
        this.lastActivityTimeNanos = workEndTime;
        this.afterRunningAllTasks();
        this.lastExecutionTime = lastExecutionTime;
        return true;
    }

    protected void afterRunningAllTasks() {
    }

    protected long delayNanos(long currentTimeNanos) {
        currentTimeNanos -= this.ticker().initialNanoTime();
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null) {
            return SCHEDULE_PURGE_INTERVAL;
        }
        return scheduledTask.delayNanos(currentTimeNanos);
    }

    protected long deadlineNanos() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null) {
            return this.getCurrentTimeNanos() + SCHEDULE_PURGE_INTERVAL;
        }
        return scheduledTask.deadlineNanos();
    }

    protected void updateLastExecutionTime() {
        long now;
        this.lastExecutionTime = now = this.getCurrentTimeNanos();
        this.lastActivityTimeNanos = now;
    }

    protected int getNumOfRegisteredChannels() {
        return -1;
    }

    protected void reportActiveIoTime(long nanos) {
        assert (this.inEventLoop());
        if (nanos > 0L) {
            this.accumulatedActiveTimeNanos += nanos;
            this.lastActivityTimeNanos = this.ticker().nanoTime();
        }
    }

    protected long getAndResetAccumulatedActiveTimeNanos() {
        return ACCUMULATED_ACTIVE_TIME_NANOS_UPDATER.getAndSet(this, 0L);
    }

    protected long getLastActivityTimeNanos() {
        return this.lastActivityTimeNanos;
    }

    protected int getAndIncrementIdleCycles() {
        return CONSECUTIVE_IDLE_CYCLES_UPDATER.getAndIncrement(this);
    }

    protected void resetIdleCycles() {
        CONSECUTIVE_IDLE_CYCLES_UPDATER.set(this, 0);
    }

    protected int getAndIncrementBusyCycles() {
        return CONSECUTIVE_BUSY_CYCLES_UPDATER.getAndIncrement(this);
    }

    protected void resetBusyCycles() {
        CONSECUTIVE_BUSY_CYCLES_UPDATER.set(this, 0);
    }

    protected boolean isSuspensionSupported() {
        return this.supportSuspension;
    }

    protected abstract void run();

    protected void cleanup() {
    }

    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop) {
            this.taskQueue.offer(WAKEUP_TASK);
        }
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    public void addShutdownHook(final Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.add(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.add(task);
                }
            });
        }
    }

    public void removeShutdownHook(final Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.remove(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.remove(task);
                }
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean runShutdownHooks() {
        boolean ran = false;
        while (!this.shutdownHooks.isEmpty()) {
            ArrayList<Runnable> copy = new ArrayList<Runnable>(this.shutdownHooks);
            this.shutdownHooks.clear();
            for (Runnable task : copy) {
                try {
                    SingleThreadEventExecutor.runTask(task);
                }
                catch (Throwable t) {
                    logger.warn("Shutdown hook raised an exception.", t);
                }
                finally {
                    ran = true;
                }
            }
        }
        if (ran) {
            this.lastExecutionTime = this.getCurrentTimeNanos();
        }
        return ran;
    }

    private void shutdown0(long quietPeriod, long timeout, int shutdownState) {
        boolean wakeup;
        int newState;
        int oldState;
        if (this.isShuttingDown()) {
            return;
        }
        boolean inEventLoop = this.inEventLoop();
        do {
            if (this.isShuttingDown()) {
                return;
            }
            wakeup = true;
            oldState = this.state;
            if (inEventLoop) {
                newState = shutdownState;
                continue;
            }
            switch (oldState) {
                case 1: 
                case 2: 
                case 3: 
                case 4: {
                    newState = shutdownState;
                    break;
                }
                default: {
                    newState = oldState;
                    wakeup = false;
                }
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
        if (quietPeriod != -1L) {
            this.gracefulShutdownQuietPeriod = quietPeriod;
        }
        if (timeout != -1L) {
            this.gracefulShutdownTimeout = timeout;
        }
        if (this.ensureThreadStarted(oldState)) {
            return;
        }
        if (wakeup) {
            this.taskQueue.offer(WAKEUP_TASK);
            if (!this.addTaskWakesUp) {
                this.wakeup(inEventLoop);
            }
        }
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        ObjectUtil.checkPositiveOrZero(quietPeriod, "quietPeriod");
        if (timeout < quietPeriod) {
            throw new IllegalArgumentException("timeout: " + timeout + " (expected >= quietPeriod (" + quietPeriod + "))");
        }
        ObjectUtil.checkNotNull(unit, "unit");
        this.shutdown0(unit.toNanos(quietPeriod), unit.toNanos(timeout), 5);
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Override
    @Deprecated
    public void shutdown() {
        this.shutdown0(-1L, -1L, 6);
    }

    @Override
    public boolean isShuttingDown() {
        return this.state >= 5;
    }

    @Override
    public boolean isShutdown() {
        return this.state >= 6;
    }

    @Override
    public boolean isTerminated() {
        return this.state == 7;
    }

    @Override
    public boolean isSuspended() {
        int currentState = this.state;
        return currentState == 3 || currentState == 2;
    }

    @Override
    public boolean trySuspend() {
        if (this.supportSuspension) {
            if (STATE_UPDATER.compareAndSet(this, 4, 2)) {
                this.wakeup(this.inEventLoop());
                return true;
            }
            if (STATE_UPDATER.compareAndSet(this, 1, 3)) {
                return true;
            }
            int currentState = this.state;
            return currentState == 3 || currentState == 2;
        }
        return false;
    }

    protected boolean canSuspend() {
        return this.canSuspend(this.state);
    }

    protected boolean canSuspend(int state) {
        assert (this.inEventLoop());
        return this.supportSuspension && (state == 3 || state == 2) && !this.hasTasks() && this.nextScheduledTaskDeadlineNanos() == -1L;
    }

    protected boolean confirmShutdown() {
        if (!this.isShuttingDown()) {
            return false;
        }
        if (!this.inEventLoop()) {
            throw new IllegalStateException("must be invoked from an event loop");
        }
        this.cancelScheduledTasks();
        if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = this.getCurrentTimeNanos();
        }
        if (this.runAllTasks() || this.runShutdownHooks()) {
            if (this.isShutdown()) {
                return true;
            }
            if (this.gracefulShutdownQuietPeriod == 0L) {
                return true;
            }
            this.taskQueue.offer(WAKEUP_TASK);
            return false;
        }
        long nanoTime = this.getCurrentTimeNanos();
        if (this.isShutdown() || nanoTime - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout) {
            return true;
        }
        if (nanoTime - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
            this.taskQueue.offer(WAKEUP_TASK);
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        ObjectUtil.checkNotNull(unit, "unit");
        if (this.inEventLoop()) {
            throw new IllegalStateException("cannot await termination of the current thread");
        }
        this.threadLock.await(timeout, unit);
        return this.isTerminated();
    }

    @Override
    public void execute(Runnable task) {
        this.execute0(task);
    }

    @Override
    public void lazyExecute(Runnable task) {
        this.lazyExecute0(task);
    }

    private void execute0(@Async.Schedule Runnable task) {
        ObjectUtil.checkNotNull(task, "task");
        this.execute(task, this.wakesUpForTask(task));
    }

    private void lazyExecute0(@Async.Schedule Runnable task) {
        this.execute(ObjectUtil.checkNotNull(task, "task"), false);
    }

    @Override
    void scheduleRemoveScheduled(final ScheduledFutureTask<?> task) {
        ObjectUtil.checkNotNull(task, "task");
        int currentState = this.state;
        if (this.supportSuspension && currentState == 3) {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    task.run();
                    if (SingleThreadEventExecutor.this.canSuspend(3)) {
                        SingleThreadEventExecutor.this.trySuspend();
                    }
                }
            }, true);
        } else {
            this.execute(task, false);
        }
    }

    private void execute(Runnable task, boolean immediate) {
        boolean inEventLoop = this.inEventLoop();
        this.addTask(task);
        if (!inEventLoop) {
            this.startThread();
            if (this.isShutdown()) {
                boolean reject = false;
                try {
                    if (this.removeTask(task)) {
                        reject = true;
                    }
                }
                catch (UnsupportedOperationException unsupportedOperationException) {
                    // empty catch block
                }
                if (reject) {
                    SingleThreadEventExecutor.reject();
                }
            }
        }
        if (!this.addTaskWakesUp && immediate) {
            this.wakeup(inEventLoop);
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        this.throwIfInEventLoop("invokeAny");
        return super.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.throwIfInEventLoop("invokeAny");
        return super.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        this.throwIfInEventLoop("invokeAll");
        return super.invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        this.throwIfInEventLoop("invokeAll");
        return super.invokeAll(tasks, timeout, unit);
    }

    private void throwIfInEventLoop(String method) {
        if (this.inEventLoop()) {
            throw new RejectedExecutionException("Calling " + method + " from within the EventLoop is not allowed");
        }
    }

    public final ThreadProperties threadProperties() {
        ThreadProperties threadProperties = this.threadProperties;
        if (threadProperties == null) {
            Thread thread = this.thread;
            if (thread == null) {
                assert (!this.inEventLoop());
                this.submit(NOOP_TASK).syncUninterruptibly();
                thread = this.thread;
                assert (thread != null);
            }
            if (!PROPERTIES_UPDATER.compareAndSet(this, null, threadProperties = new DefaultThreadProperties(thread))) {
                threadProperties = this.threadProperties;
            }
        }
        return threadProperties;
    }

    protected boolean wakesUpForTask(Runnable task) {
        return true;
    }

    protected static void reject() {
        throw new RejectedExecutionException("event executor terminated");
    }

    protected final void reject(Runnable task) {
        this.rejectedExecutionHandler.rejected(task, this);
    }

    private void startThread() {
        int currentState = this.state;
        if ((currentState == 1 || currentState == 3) && STATE_UPDATER.compareAndSet(this, currentState, 4)) {
            this.resetIdleCycles();
            this.resetBusyCycles();
            boolean success = false;
            try {
                this.doStartThread();
                success = true;
            }
            finally {
                if (!success) {
                    STATE_UPDATER.compareAndSet(this, 4, 1);
                }
            }
        }
    }

    private boolean ensureThreadStarted(int oldState) {
        if (oldState == 1 || oldState == 3) {
            try {
                this.doStartThread();
            }
            catch (Throwable cause) {
                STATE_UPDATER.set(this, 7);
                this.terminationFuture.tryFailure(cause);
                if (!(cause instanceof Exception)) {
                    PlatformDependent.throwException(cause);
                }
                return true;
            }
        }
        return false;
    }

    private void doStartThread() {
        this.executor.execute(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Loose catch block
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void run() {
                boolean shutdown;
                boolean suspend;
                Throwable unexpectedException;
                boolean success;
                block73: {
                    SingleThreadEventExecutor.this.processingLock.lock();
                    assert (SingleThreadEventExecutor.this.thread == null);
                    SingleThreadEventExecutor.this.thread = Thread.currentThread();
                    if (SingleThreadEventExecutor.this.interrupted) {
                        SingleThreadEventExecutor.this.thread.interrupt();
                        SingleThreadEventExecutor.this.interrupted = false;
                    }
                    success = false;
                    unexpectedException = null;
                    SingleThreadEventExecutor.this.updateLastExecutionTime();
                    suspend = false;
                    do {
                        SingleThreadEventExecutor.this.run();
                        success = true;
                        int currentState = SingleThreadEventExecutor.this.state;
                        if (!SingleThreadEventExecutor.this.canSuspend(currentState)) break block73;
                    } while (!STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, 2, 3) || !SingleThreadEventExecutor.this.canSuspend(3) && STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, 3, 4));
                    suspend = true;
                }
                boolean bl = shutdown = !suspend;
                if (shutdown) {
                    int oldState;
                    while ((oldState = SingleThreadEventExecutor.this.state) < 5 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 5)) {
                    }
                    if (success && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L && logger.isErrorEnabled()) {
                        logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates.");
                    }
                }
                try {
                    int currentState;
                    if (!shutdown) return;
                    while (!SingleThreadEventExecutor.this.confirmShutdown()) {
                    }
                    while ((currentState = SingleThreadEventExecutor.this.state) < 6 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, currentState, 6)) {
                    }
                    SingleThreadEventExecutor.this.confirmShutdown();
                    return;
                }
                finally {
                    block74: {
                        try {
                            if (shutdown) {
                                try {
                                    SingleThreadEventExecutor.this.cleanup();
                                    break block74;
                                }
                                finally {
                                    FastThreadLocal.removeAll();
                                    STATE_UPDATER.set(SingleThreadEventExecutor.this, 7);
                                    SingleThreadEventExecutor.this.threadLock.countDown();
                                    int numUserTasks = SingleThreadEventExecutor.this.drainTasks();
                                    if (numUserTasks > 0 && logger.isWarnEnabled()) {
                                        logger.warn("An event executor terminated with non-empty task queue (" + numUserTasks + ')');
                                    }
                                    if (unexpectedException == null) {
                                        SingleThreadEventExecutor.this.terminationFuture.setSuccess(null);
                                    } else {
                                        SingleThreadEventExecutor.this.terminationFuture.setFailure(unexpectedException);
                                    }
                                }
                            }
                            FastThreadLocal.removeAll();
                            SingleThreadEventExecutor.this.threadProperties = null;
                        }
                        finally {
                            SingleThreadEventExecutor.this.thread = null;
                            SingleThreadEventExecutor.this.processingLock.unlock();
                        }
                    }
                }
                catch (Throwable t) {
                    unexpectedException = t;
                    logger.warn("Unexpected exception from an event executor: ", t);
                    boolean bl2 = shutdown = !suspend;
                    if (shutdown) {
                        int oldState;
                        while ((oldState = SingleThreadEventExecutor.this.state) < 5 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 5)) {
                        }
                        if (success && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L && logger.isErrorEnabled()) {
                            logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates.");
                        }
                    }
                    try {
                        int currentState;
                        if (!shutdown) return;
                        while (!SingleThreadEventExecutor.this.confirmShutdown()) {
                        }
                        while ((currentState = SingleThreadEventExecutor.this.state) < 6 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, currentState, 6)) {
                        }
                        SingleThreadEventExecutor.this.confirmShutdown();
                        return;
                    }
                    finally {
                        block75: {
                            try {
                                if (shutdown) {
                                    try {
                                        SingleThreadEventExecutor.this.cleanup();
                                        break block75;
                                    }
                                    finally {
                                        FastThreadLocal.removeAll();
                                        STATE_UPDATER.set(SingleThreadEventExecutor.this, 7);
                                        SingleThreadEventExecutor.this.threadLock.countDown();
                                        int numUserTasks = SingleThreadEventExecutor.this.drainTasks();
                                        if (numUserTasks > 0 && logger.isWarnEnabled()) {
                                            logger.warn("An event executor terminated with non-empty task queue (" + numUserTasks + ')');
                                        }
                                        if (unexpectedException == null) {
                                            SingleThreadEventExecutor.this.terminationFuture.setSuccess(null);
                                        } else {
                                            SingleThreadEventExecutor.this.terminationFuture.setFailure(unexpectedException);
                                        }
                                    }
                                }
                                FastThreadLocal.removeAll();
                                SingleThreadEventExecutor.this.threadProperties = null;
                            }
                            finally {
                                SingleThreadEventExecutor.this.thread = null;
                                SingleThreadEventExecutor.this.processingLock.unlock();
                            }
                        }
                    }
                    catch (Throwable throwable) {
                        boolean shutdown2;
                        boolean bl3 = shutdown2 = !suspend;
                        if (shutdown2) {
                            int oldState;
                            while ((oldState = SingleThreadEventExecutor.this.state) < 5 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 5)) {
                            }
                            if (success && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L && logger.isErrorEnabled()) {
                                logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates.");
                            }
                        }
                        try {
                            int currentState;
                            if (!shutdown2) throw throwable;
                            while (!SingleThreadEventExecutor.this.confirmShutdown()) {
                            }
                            while ((currentState = SingleThreadEventExecutor.this.state) < 6 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, currentState, 6)) {
                            }
                            SingleThreadEventExecutor.this.confirmShutdown();
                            throw throwable;
                        }
                        finally {
                            block76: {
                                try {
                                    if (shutdown2) {
                                        try {
                                            SingleThreadEventExecutor.this.cleanup();
                                            break block76;
                                        }
                                        finally {
                                            FastThreadLocal.removeAll();
                                            STATE_UPDATER.set(SingleThreadEventExecutor.this, 7);
                                            SingleThreadEventExecutor.this.threadLock.countDown();
                                            int numUserTasks = SingleThreadEventExecutor.this.drainTasks();
                                            if (numUserTasks > 0 && logger.isWarnEnabled()) {
                                                logger.warn("An event executor terminated with non-empty task queue (" + numUserTasks + ')');
                                            }
                                            if (unexpectedException == null) {
                                                SingleThreadEventExecutor.this.terminationFuture.setSuccess(null);
                                            } else {
                                                SingleThreadEventExecutor.this.terminationFuture.setFailure(unexpectedException);
                                            }
                                        }
                                    }
                                    FastThreadLocal.removeAll();
                                    SingleThreadEventExecutor.this.threadProperties = null;
                                }
                                finally {
                                    SingleThreadEventExecutor.this.thread = null;
                                    SingleThreadEventExecutor.this.processingLock.unlock();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    final int drainTasks() {
        Runnable runnable;
        int numTasks = 0;
        while ((runnable = this.taskQueue.poll()) != null) {
            if (WAKEUP_TASK == runnable) continue;
            ++numTasks;
        }
        return numTasks;
    }

    private static final class DefaultThreadProperties
    implements ThreadProperties {
        private final Thread t;

        DefaultThreadProperties(Thread t) {
            this.t = t;
        }

        @Override
        public Thread.State state() {
            return this.t.getState();
        }

        @Override
        public int priority() {
            return this.t.getPriority();
        }

        @Override
        public boolean isInterrupted() {
            return this.t.isInterrupted();
        }

        @Override
        public boolean isDaemon() {
            return this.t.isDaemon();
        }

        @Override
        public String name() {
            return this.t.getName();
        }

        @Override
        public long id() {
            return this.t.getId();
        }

        @Override
        public StackTraceElement[] stackTrace() {
            return this.t.getStackTrace();
        }

        @Override
        public boolean isAlive() {
            return this.t.isAlive();
        }
    }

    @Deprecated
    protected static interface NonWakeupRunnable
    extends AbstractEventExecutor.LazyRunnable {
    }
}

