/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultFutureListeners;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultPromise<V>
extends AbstractFuture<V>
implements Promise<V> {
    public static final String PROPERTY_MAX_LISTENER_STACK_DEPTH = "io.netty.defaultPromise.maxListenerStackDepth";
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);
    private static final InternalLogger rejectedExecutionLogger = InternalLoggerFactory.getInstance(DefaultPromise.class.getName() + ".rejectedExecution");
    private static final int MAX_LISTENER_STACK_DEPTH = Math.min(8, SystemPropertyUtil.getInt("io.netty.defaultPromise.maxListenerStackDepth", 8));
    private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> RESULT_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultPromise.class, Object.class, "result");
    private static final Object SUCCESS = new Object();
    private static final Object UNCANCELLABLE = new Object();
    private static final CauseHolder CANCELLATION_CAUSE_HOLDER = new CauseHolder(StacklessCancellationException.newInstance(DefaultPromise.class, "cancel(...)"));
    private static final StackTraceElement[] CANCELLATION_STACK = DefaultPromise.CANCELLATION_CAUSE_HOLDER.cause.getStackTrace();
    private volatile Object result;
    private final EventExecutor executor;
    private GenericFutureListener<? extends Future<?>> listener;
    private DefaultFutureListeners listeners;
    private short waiters;
    private boolean notifyingListeners;

    public DefaultPromise(EventExecutor executor) {
        this.executor = ObjectUtil.checkNotNull(executor, "executor");
    }

    protected DefaultPromise() {
        this.executor = null;
    }

    @Override
    public Promise<V> setSuccess(V result) {
        if (this.setSuccess0(result)) {
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    @Override
    public boolean trySuccess(V result) {
        return this.setSuccess0(result);
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        if (this.setFailure0(cause)) {
            return this;
        }
        throw new IllegalStateException("complete already: " + this, cause);
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return this.setFailure0(cause);
    }

    @Override
    public boolean setUncancellable() {
        if (RESULT_UPDATER.compareAndSet(this, null, UNCANCELLABLE)) {
            return true;
        }
        Object result = this.result;
        return !DefaultPromise.isDone0(result) || !DefaultPromise.isCancelled0(result);
    }

    @Override
    public boolean isSuccess() {
        Object result = this.result;
        return result != null && result != UNCANCELLABLE && !(result instanceof CauseHolder);
    }

    @Override
    public boolean isCancellable() {
        return this.result == null;
    }

    @Override
    public Throwable cause() {
        return this.cause0(this.result);
    }

    private Throwable cause0(Object result) {
        if (!(result instanceof CauseHolder)) {
            return null;
        }
        if (result == CANCELLATION_CAUSE_HOLDER) {
            LeanCancellationException ce = new LeanCancellationException();
            if (RESULT_UPDATER.compareAndSet(this, CANCELLATION_CAUSE_HOLDER, new CauseHolder(ce))) {
                return ce;
            }
            result = this.result;
        }
        return ((CauseHolder)result).cause;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener) {
        ObjectUtil.checkNotNull(listener, "listener");
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            this.addListener0(listener);
        }
        if (this.isDone()) {
            this.notifyListeners();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        ObjectUtil.checkNotNull(listeners, "listeners");
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            for (GenericFutureListener<? extends Future<? super V>> listener : listeners) {
                if (listener == null) break;
                this.addListener0(listener);
            }
        }
        if (this.isDone()) {
            this.notifyListeners();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener) {
        ObjectUtil.checkNotNull(listener, "listener");
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            this.removeListener0(listener);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>> ... listeners) {
        ObjectUtil.checkNotNull(listeners, "listeners");
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            for (GenericFutureListener<? extends Future<? super V>> listener : listeners) {
                if (listener == null) break;
                this.removeListener0(listener);
            }
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> await() throws InterruptedException {
        if (this.isDone()) {
            return this;
        }
        if (Thread.interrupted()) {
            throw new InterruptedException(this.toString());
        }
        this.checkDeadLock();
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            while (!this.isDone()) {
                this.incWaiters();
                try {
                    this.wait();
                }
                finally {
                    this.decWaiters();
                }
            }
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Promise<V> awaitUninterruptibly() {
        if (this.isDone()) {
            return this;
        }
        this.checkDeadLock();
        boolean interrupted = false;
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            while (!this.isDone()) {
                this.incWaiters();
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
                finally {
                    this.decWaiters();
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.await0(unit.toNanos(timeout), true);
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), true);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        try {
            return this.await0(unit.toNanos(timeout), false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    @Override
    public V getNow() {
        Object result = this.result;
        if (result instanceof CauseHolder || result == SUCCESS || result == UNCANCELLABLE) {
            return null;
        }
        return (V)result;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        Object result = this.result;
        if (!DefaultPromise.isDone0(result)) {
            this.await();
            result = this.result;
        }
        if (result == SUCCESS || result == UNCANCELLABLE) {
            return null;
        }
        Throwable cause = this.cause0(result);
        if (cause == null) {
            return (V)result;
        }
        if (cause instanceof CancellationException) {
            throw (CancellationException)cause;
        }
        throw new ExecutionException(cause);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Object result = this.result;
        if (!DefaultPromise.isDone0(result)) {
            if (!this.await(timeout, unit)) {
                throw new TimeoutException("timeout after " + timeout + " " + unit.name().toLowerCase(Locale.ENGLISH));
            }
            result = this.result;
        }
        if (result == SUCCESS || result == UNCANCELLABLE) {
            return null;
        }
        Throwable cause = this.cause0(result);
        if (cause == null) {
            return (V)result;
        }
        if (cause instanceof CancellationException) {
            throw (CancellationException)cause;
        }
        throw new ExecutionException(cause);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (RESULT_UPDATER.compareAndSet(this, null, CANCELLATION_CAUSE_HOLDER)) {
            if (this.checkNotifyWaiters()) {
                this.notifyListeners();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return DefaultPromise.isCancelled0(this.result);
    }

    @Override
    public boolean isDone() {
        return DefaultPromise.isDone0(this.result);
    }

    @Override
    public Promise<V> sync() throws InterruptedException {
        this.await();
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public Promise<V> syncUninterruptibly() {
        this.awaitUninterruptibly();
        this.rethrowIfFailed();
        return this;
    }

    public String toString() {
        return this.toStringBuilder().toString();
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder buf = new StringBuilder(64).append(StringUtil.simpleClassName(this)).append('@').append(Integer.toHexString(this.hashCode()));
        Object result = this.result;
        if (result == SUCCESS) {
            buf.append("(success)");
        } else if (result == UNCANCELLABLE) {
            buf.append("(uncancellable)");
        } else if (result instanceof CauseHolder) {
            buf.append("(failure: ").append(((CauseHolder)result).cause).append(')');
        } else if (result != null) {
            buf.append("(success: ").append(result).append(')');
        } else {
            buf.append("(incomplete)");
        }
        return buf;
    }

    protected EventExecutor executor() {
        return this.executor;
    }

    protected void checkDeadLock() {
        EventExecutor e = this.executor();
        if (e != null && e.inEventLoop()) {
            throw new BlockingOperationException(this.toString());
        }
    }

    protected static void notifyListener(EventExecutor eventExecutor, Future<?> future, GenericFutureListener<?> listener) {
        DefaultPromise.notifyListenerWithStackOverFlowProtection(ObjectUtil.checkNotNull(eventExecutor, "eventExecutor"), ObjectUtil.checkNotNull(future, "future"), ObjectUtil.checkNotNull(listener, "listener"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyListeners() {
        InternalThreadLocalMap threadLocals;
        int stackDepth;
        EventExecutor executor = this.executor();
        if (executor.inEventLoop() && (stackDepth = (threadLocals = InternalThreadLocalMap.get()).futureListenerStackDepth()) < MAX_LISTENER_STACK_DEPTH) {
            threadLocals.setFutureListenerStackDepth(stackDepth + 1);
            try {
                this.notifyListenersNow();
            }
            finally {
                threadLocals.setFutureListenerStackDepth(stackDepth);
            }
            return;
        }
        DefaultPromise.safeExecute(executor, new Runnable(){

            @Override
            public void run() {
                DefaultPromise.this.notifyListenersNow();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void notifyListenerWithStackOverFlowProtection(EventExecutor executor, final Future<?> future, final GenericFutureListener<?> listener) {
        InternalThreadLocalMap threadLocals;
        int stackDepth;
        if (executor.inEventLoop() && (stackDepth = (threadLocals = InternalThreadLocalMap.get()).futureListenerStackDepth()) < MAX_LISTENER_STACK_DEPTH) {
            threadLocals.setFutureListenerStackDepth(stackDepth + 1);
            try {
                DefaultPromise.notifyListener0(future, listener);
            }
            finally {
                threadLocals.setFutureListenerStackDepth(stackDepth);
            }
            return;
        }
        DefaultPromise.safeExecute(executor, new Runnable(){

            @Override
            public void run() {
                DefaultPromise.notifyListener0(future, listener);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void notifyListenersNow() {
        DefaultFutureListeners listeners;
        GenericFutureListener<? extends Future<?>> listener;
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            listener = this.listener;
            listeners = this.listeners;
            if (this.notifyingListeners || listener == null && listeners == null) {
                return;
            }
            this.notifyingListeners = true;
            if (listener != null) {
                this.listener = null;
            } else {
                this.listeners = null;
            }
        }
        while (true) {
            if (listener != null) {
                DefaultPromise.notifyListener0(this, listener);
            } else {
                this.notifyListeners0(listeners);
            }
            defaultPromise = this;
            synchronized (defaultPromise) {
                if (this.listener == null && this.listeners == null) {
                    this.notifyingListeners = false;
                    return;
                }
                listener = this.listener;
                listeners = this.listeners;
                if (listener != null) {
                    this.listener = null;
                } else {
                    this.listeners = null;
                }
            }
        }
    }

    private void notifyListeners0(DefaultFutureListeners listeners) {
        GenericFutureListener<? extends Future<?>>[] a = listeners.listeners();
        int size = listeners.size();
        for (int i = 0; i < size; ++i) {
            DefaultPromise.notifyListener0(this, a[i]);
        }
    }

    private static void notifyListener0(Future future, GenericFutureListener l) {
        block2: {
            try {
                l.operationComplete(future);
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationComplete()", t);
            }
        }
    }

    private void addListener0(GenericFutureListener<? extends Future<? super V>> listener) {
        if (this.listener == null) {
            if (this.listeners == null) {
                this.listener = listener;
            } else {
                this.listeners.add(listener);
            }
        } else {
            assert (this.listeners == null);
            this.listeners = new DefaultFutureListeners(this.listener, listener);
            this.listener = null;
        }
    }

    private void removeListener0(GenericFutureListener<? extends Future<? super V>> toRemove) {
        if (this.listener == toRemove) {
            this.listener = null;
        } else if (this.listeners != null) {
            this.listeners.remove(toRemove);
            if (this.listeners.size() == 0) {
                this.listeners = null;
            }
        }
    }

    private boolean setSuccess0(V result) {
        return this.setValue0(result == null ? SUCCESS : result);
    }

    private boolean setFailure0(Throwable cause) {
        return this.setValue0(new CauseHolder(ObjectUtil.checkNotNull(cause, "cause")));
    }

    private boolean setValue0(Object objResult) {
        if (RESULT_UPDATER.compareAndSet(this, null, objResult) || RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, objResult)) {
            if (this.checkNotifyWaiters()) {
                this.notifyListeners();
            }
            return true;
        }
        return false;
    }

    private synchronized boolean checkNotifyWaiters() {
        if (this.waiters > 0) {
            this.notifyAll();
        }
        return this.listener != null || this.listeners != null;
    }

    private void incWaiters() {
        if (this.waiters == Short.MAX_VALUE) {
            throw new IllegalStateException("too many waiters: " + this);
        }
        this.waiters = (short)(this.waiters + 1);
    }

    private void decWaiters() {
        this.waiters = (short)(this.waiters - 1);
    }

    private void rethrowIfFailed() {
        Throwable cause = this.cause();
        if (cause == null) {
            return;
        }
        if (!(cause instanceof CancellationException) && cause.getSuppressed().length == 0) {
            cause.addSuppressed(new CompletionException("Rethrowing promise failure cause", null));
        }
        PlatformDependent.throwException(cause);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
        if (this.isDone()) {
            return true;
        }
        if (timeoutNanos <= 0L) {
            return this.isDone();
        }
        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException(this.toString());
        }
        this.checkDeadLock();
        long startTime = System.nanoTime();
        DefaultPromise defaultPromise = this;
        synchronized (defaultPromise) {
            boolean interrupted = false;
            try {
                long waitTime = timeoutNanos;
                while (!this.isDone() && waitTime > 0L) {
                    this.incWaiters();
                    try {
                        this.wait(waitTime / 1000000L, (int)(waitTime % 1000000L));
                    }
                    catch (InterruptedException e) {
                        if (interruptable) {
                            throw e;
                        }
                        interrupted = true;
                    }
                    finally {
                        this.decWaiters();
                    }
                    if (this.isDone()) {
                        boolean bl = true;
                        return bl;
                    }
                    waitTime = timeoutNanos - (System.nanoTime() - startTime);
                }
                boolean bl = this.isDone();
                return bl;
            }
            finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    void notifyProgressiveListeners(final long progress, final long total) {
        Object listeners = this.progressiveListeners();
        if (listeners == null) {
            return;
        }
        final ProgressiveFuture self = (ProgressiveFuture)((Object)this);
        EventExecutor executor = this.executor();
        if (executor.inEventLoop()) {
            if (listeners instanceof GenericProgressiveFutureListener[]) {
                DefaultPromise.notifyProgressiveListeners0(self, (GenericProgressiveFutureListener[])listeners, progress, total);
            } else {
                DefaultPromise.notifyProgressiveListener0(self, (GenericProgressiveFutureListener)listeners, progress, total);
            }
        } else if (listeners instanceof GenericProgressiveFutureListener[]) {
            final GenericProgressiveFutureListener[] array = (GenericProgressiveFutureListener[])listeners;
            DefaultPromise.safeExecute(executor, new Runnable(){

                @Override
                public void run() {
                    DefaultPromise.notifyProgressiveListeners0(self, array, progress, total);
                }
            });
        } else {
            final GenericProgressiveFutureListener l = (GenericProgressiveFutureListener)listeners;
            DefaultPromise.safeExecute(executor, new Runnable(){

                @Override
                public void run() {
                    DefaultPromise.notifyProgressiveListener0(self, l, progress, total);
                }
            });
        }
    }

    /*
     * WARNING - void declaration
     */
    private synchronized Object progressiveListeners() {
        GenericFutureListener<? extends Future<?>> listener = this.listener;
        DefaultFutureListeners listeners = this.listeners;
        if (listener == null && listeners == null) {
            return null;
        }
        if (listeners != null) {
            void var8_13;
            DefaultFutureListeners dfl = listeners;
            int progressiveSize = dfl.progressiveSize();
            switch (progressiveSize) {
                case 0: {
                    return null;
                }
                case 1: {
                    for (GenericFutureListener<Future<?>> genericFutureListener : dfl.listeners()) {
                        if (!(genericFutureListener instanceof GenericProgressiveFutureListener)) continue;
                        return genericFutureListener;
                    }
                    return null;
                }
            }
            GenericFutureListener<? extends Future<?>>[] array = dfl.listeners();
            GenericProgressiveFutureListener[] copy = new GenericProgressiveFutureListener[progressiveSize];
            int i = 0;
            boolean bl = false;
            while (var8_13 < progressiveSize) {
                GenericFutureListener<Future<?>> l = array[i];
                if (l instanceof GenericProgressiveFutureListener) {
                    copy[++var8_13] = (GenericProgressiveFutureListener)l;
                }
                ++i;
            }
            return copy;
        }
        if (listener instanceof GenericProgressiveFutureListener) {
            return listener;
        }
        return null;
    }

    private static void notifyProgressiveListeners0(ProgressiveFuture<?> future, GenericProgressiveFutureListener<?>[] listeners, long progress, long total) {
        for (GenericProgressiveFutureListener<?> l : listeners) {
            if (l == null) break;
            DefaultPromise.notifyProgressiveListener0(future, l, progress, total);
        }
    }

    private static void notifyProgressiveListener0(ProgressiveFuture future, GenericProgressiveFutureListener l, long progress, long total) {
        block2: {
            try {
                l.operationProgressed(future, progress, total);
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationProgressed()", t);
            }
        }
    }

    private static boolean isCancelled0(Object result) {
        return result instanceof CauseHolder && ((CauseHolder)result).cause instanceof CancellationException;
    }

    private static boolean isDone0(Object result) {
        return result != null && result != UNCANCELLABLE;
    }

    private static void safeExecute(EventExecutor executor, Runnable task) {
        try {
            executor.execute(task);
        }
        catch (Throwable t) {
            rejectedExecutionLogger.error("Failed to submit a listener notification task. Event loop shut down?", t);
        }
    }

    private static final class StacklessCancellationException
    extends CancellationException {
        private static final long serialVersionUID = -2974906711413716191L;

        private StacklessCancellationException() {
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }

        static StacklessCancellationException newInstance(Class<?> clazz, String method) {
            return ThrowableUtil.unknownStackTrace(new StacklessCancellationException(), clazz, method);
        }
    }

    private static final class CauseHolder {
        final Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }

    private static final class LeanCancellationException
    extends CancellationException {
        private static final long serialVersionUID = 2794674970981187807L;

        private LeanCancellationException() {
        }

        @Override
        public Throwable fillInStackTrace() {
            this.setStackTrace(CANCELLATION_STACK);
            return this;
        }

        @Override
        public String toString() {
            return CancellationException.class.getName();
        }
    }
}

