/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoHandlerContext;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoOps;
import io.netty.channel.IoRegistration;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventArray;
import io.netty.channel.epoll.EpollIoHandle;
import io.netty.channel.epoll.EpollIoOps;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeArrays;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.ThreadAwareExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class EpollIoHandler
implements IoHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollIoHandler.class);
    private static final long EPOLL_WAIT_MILLIS_THRESHOLD = SystemPropertyUtil.getLong("io.netty.channel.epoll.epollWaitThreshold", 10L);
    private long prevDeadlineNanos;
    private FileDescriptor epollFd;
    private FileDescriptor eventFd;
    private FileDescriptor timerFd;
    private final IntObjectMap<DefaultEpollIoRegistration> registrations;
    private final boolean allowGrowing;
    private final EpollEventArray events;
    private final NativeArrays nativeArrays;
    private final SelectStrategy selectStrategy;
    private final IntSupplier selectNowSupplier;
    private final ThreadAwareExecutor executor;
    private static final long AWAKE = -1L;
    private static final long NONE = Long.MAX_VALUE;
    private final AtomicLong nextWakeupNanos;
    private boolean pendingWakeup;
    private int numChannels;
    private static final long MAX_SCHEDULED_TIMERFD_NS = 999999999L;

    public static IoHandlerFactory newFactory() {
        return EpollIoHandler.newFactory(0, DefaultSelectStrategyFactory.INSTANCE);
    }

    public static IoHandlerFactory newFactory(final int maxEvents, final SelectStrategyFactory selectStrategyFactory) {
        Epoll.ensureAvailability();
        ObjectUtil.checkPositiveOrZero(maxEvents, "maxEvents");
        ObjectUtil.checkNotNull(selectStrategyFactory, "selectStrategyFactory");
        return new IoHandlerFactory(){

            @Override
            public IoHandler newHandler(ThreadAwareExecutor executor) {
                return new EpollIoHandler(executor, maxEvents, selectStrategyFactory.newSelectStrategy());
            }

            @Override
            public boolean isChangingThreadSupported() {
                return true;
            }
        };
    }

    EpollIoHandler(ThreadAwareExecutor executor, int maxEvents, SelectStrategy strategy) {
        Epoll.ensureAvailability();
        this.prevDeadlineNanos = Long.MAX_VALUE;
        this.registrations = new IntObjectHashMap<DefaultEpollIoRegistration>(4096);
        this.selectNowSupplier = new IntSupplier(){

            @Override
            public int get() throws Exception {
                return EpollIoHandler.this.epollWaitNow();
            }
        };
        this.nextWakeupNanos = new AtomicLong(-1L);
        this.executor = ObjectUtil.checkNotNull(executor, "executor");
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
        if (maxEvents == 0) {
            this.allowGrowing = true;
            this.events = new EpollEventArray(4096);
        } else {
            this.allowGrowing = false;
            this.events = new EpollEventArray(maxEvents);
        }
        this.nativeArrays = new NativeArrays();
        this.openFileDescriptors();
    }

    private static EpollIoHandle cast(IoHandle handle) {
        if (handle instanceof EpollIoHandle) {
            return (EpollIoHandle)handle;
        }
        throw new IllegalArgumentException("IoHandle of type " + StringUtil.simpleClassName(handle) + " not supported");
    }

    private static EpollIoOps cast(IoOps ops) {
        if (ops instanceof EpollIoOps) {
            return (EpollIoOps)ops;
        }
        throw new IllegalArgumentException("IoOps of type " + StringUtil.simpleClassName(ops) + " not supported");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void openFileDescriptors() {
        boolean success = false;
        FileDescriptor epollFd = null;
        FileDescriptor eventFd = null;
        FileDescriptor timerFd = null;
        try {
            this.epollFd = epollFd = Native.newEpollCreate();
            this.eventFd = eventFd = Native.newEventFd();
            try {
                Native.epollCtlAdd(epollFd.intValue(), eventFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            }
            catch (IOException e) {
                throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", e);
            }
            this.timerFd = timerFd = Native.newTimerFd();
            try {
                Native.epollCtlAdd(epollFd.intValue(), timerFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
                return;
            }
            catch (IOException e) {
                throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", e);
            }
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            EpollIoHandler.closeFileDescriptor(epollFd);
            EpollIoHandler.closeFileDescriptor(eventFd);
            EpollIoHandler.closeFileDescriptor(timerFd);
            throw throwable;
        }
    }

    private static void closeFileDescriptor(FileDescriptor fd) {
        if (fd != null) {
            try {
                fd.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    public void wakeup() {
        if (!this.executor.isExecutorThread(Thread.currentThread()) && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
            Native.eventFdWrite(this.eventFd.intValue(), 1L);
        }
    }

    @Override
    public void prepareToDestroy() {
        DefaultEpollIoRegistration[] copy;
        for (DefaultEpollIoRegistration reg : copy = this.registrations.values().toArray(new DefaultEpollIoRegistration[0])) {
            reg.close();
        }
    }

    @Override
    public void destroy() {
        try {
            this.closeFileDescriptors();
        }
        finally {
            this.nativeArrays.free();
            this.events.free();
        }
    }

    @Override
    public IoRegistration register(IoHandle handle) throws Exception {
        EpollIoHandle epollHandle = EpollIoHandler.cast(handle);
        DefaultEpollIoRegistration registration = new DefaultEpollIoRegistration(this.executor, epollHandle);
        int fd = epollHandle.fd().intValue();
        Native.epollCtlAdd(this.epollFd.intValue(), fd, EpollIoOps.EPOLLERR.value);
        DefaultEpollIoRegistration old = this.registrations.put(fd, registration);
        assert (old == null || !old.isValid());
        if (epollHandle instanceof AbstractEpollChannel.AbstractEpollUnsafe) {
            ++this.numChannels;
        }
        handle.registered();
        return registration;
    }

    @Override
    public boolean isCompatible(Class<? extends IoHandle> handleType) {
        return EpollIoHandle.class.isAssignableFrom(handleType);
    }

    int numRegisteredChannels() {
        return this.numChannels;
    }

    List<Channel> registeredChannelsList() {
        IntObjectMap<DefaultEpollIoRegistration> ch = this.registrations;
        if (ch.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Channel> channels = new ArrayList<Channel>(ch.size());
        for (DefaultEpollIoRegistration registration : ch.values()) {
            if (!(registration.handle instanceof AbstractEpollChannel.AbstractEpollUnsafe)) continue;
            channels.add(((AbstractEpollChannel.AbstractEpollUnsafe)registration.handle).channel());
        }
        return Collections.unmodifiableList(channels);
    }

    private long epollWait(IoHandlerContext context, long deadlineNanos) throws IOException {
        if (deadlineNanos == Long.MAX_VALUE) {
            return Native.epollWait(this.epollFd, this.events, this.timerFd, Integer.MAX_VALUE, 0, EPOLL_WAIT_MILLIS_THRESHOLD);
        }
        long totalDelay = context.delayNanos(System.nanoTime());
        int delaySeconds = (int)Math.min(totalDelay / 1000000000L, Integer.MAX_VALUE);
        int delayNanos = (int)Math.min(totalDelay - (long)delaySeconds * 1000000000L, 999999999L);
        return Native.epollWait(this.epollFd, this.events, this.timerFd, delaySeconds, delayNanos, EPOLL_WAIT_MILLIS_THRESHOLD);
    }

    private int epollWaitNoTimerChange() throws IOException {
        return Native.epollWait(this.epollFd, this.events, false);
    }

    private int epollWaitNow() throws IOException {
        return Native.epollWait(this.epollFd, this.events, true);
    }

    private int epollBusyWait() throws IOException {
        return Native.epollBusyWait(this.epollFd, this.events);
    }

    private int epollWaitTimeboxed() throws IOException {
        return Native.epollWait(this.epollFd, this.events, 1000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int run(IoHandlerContext context) {
        int handled = 0;
        try {
            int strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, !context.canBlock());
            switch (strategy) {
                case -2: {
                    if (context.shouldReportActiveIoTime()) {
                        context.reportActiveIoTime(0L);
                    }
                    return 0;
                }
                case -3: {
                    strategy = this.epollBusyWait();
                    break;
                }
                case -1: {
                    long curDeadlineNanos;
                    if (this.pendingWakeup) {
                        strategy = this.epollWaitTimeboxed();
                        if (strategy != 0) break;
                        logger.warn("Missed eventfd write (not seen after > 1 second)");
                        this.pendingWakeup = false;
                        if (!context.canBlock()) break;
                    }
                    if ((curDeadlineNanos = context.deadlineNanos()) == -1L) {
                        curDeadlineNanos = Long.MAX_VALUE;
                    }
                    this.nextWakeupNanos.set(curDeadlineNanos);
                    try {
                        if (context.canBlock()) {
                            if (curDeadlineNanos == this.prevDeadlineNanos) {
                                strategy = this.epollWaitNoTimerChange();
                            } else {
                                long result = this.epollWait(context, curDeadlineNanos);
                                strategy = Native.epollReady(result);
                                long l = this.prevDeadlineNanos = Native.epollTimerWasUsed(result) ? curDeadlineNanos : Long.MAX_VALUE;
                            }
                        }
                        if (this.nextWakeupNanos.get() != -1L && this.nextWakeupNanos.getAndSet(-1L) != -1L) break;
                        this.pendingWakeup = true;
                        break;
                    }
                    catch (Throwable throwable) {
                        if (this.nextWakeupNanos.get() == -1L || this.nextWakeupNanos.getAndSet(-1L) == -1L) {
                            this.pendingWakeup = true;
                        }
                        throw throwable;
                    }
                }
            }
            if (strategy > 0) {
                handled = strategy;
                if (context.shouldReportActiveIoTime()) {
                    long activeIoStartTimeNanos = System.nanoTime();
                    if (this.processReady(this.events, strategy)) {
                        this.prevDeadlineNanos = Long.MAX_VALUE;
                    }
                    long activeIoEndTimeNanos = System.nanoTime();
                    context.reportActiveIoTime(activeIoEndTimeNanos - activeIoStartTimeNanos);
                } else if (this.processReady(this.events, strategy)) {
                    this.prevDeadlineNanos = Long.MAX_VALUE;
                }
            } else if (context.shouldReportActiveIoTime()) {
                context.reportActiveIoTime(0L);
            }
            if (this.allowGrowing && strategy == this.events.length()) {
                this.events.increase();
            }
        }
        catch (Error e) {
            throw e;
        }
        catch (Throwable t) {
            this.handleLoopException(t);
        }
        return handled;
    }

    void handleLoopException(Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private boolean processReady(EpollEventArray events, int ready) {
        boolean timerFired = false;
        for (int i = 0; i < ready; ++i) {
            int fd = events.fd(i);
            if (fd == this.eventFd.intValue()) {
                this.pendingWakeup = false;
                continue;
            }
            if (fd == this.timerFd.intValue()) {
                timerFired = true;
                continue;
            }
            long ev = events.events(i);
            DefaultEpollIoRegistration registration = this.registrations.get(fd);
            if (registration != null) {
                registration.handle(ev);
                continue;
            }
            try {
                Native.epollCtlDel(this.epollFd.intValue(), fd);
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return timerFired;
    }

    public void closeFileDescriptors() {
        block8: while (this.pendingWakeup) {
            try {
                int count = this.epollWaitTimeboxed();
                if (count == 0) break;
                for (int i = 0; i < count; ++i) {
                    if (this.events.fd(i) != this.eventFd.intValue()) continue;
                    this.pendingWakeup = false;
                    continue block8;
                }
            }
            catch (IOException count) {
            }
        }
        try {
            this.eventFd.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close the event fd.", e);
        }
        try {
            this.timerFd.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close the timer fd.", e);
        }
        try {
            this.epollFd.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close the epoll fd.", e);
        }
    }

    private final class DefaultEpollIoRegistration
    implements IoRegistration {
        private final ThreadAwareExecutor executor;
        private final AtomicBoolean canceled = new AtomicBoolean();
        final EpollIoHandle handle;

        DefaultEpollIoRegistration(ThreadAwareExecutor executor, EpollIoHandle handle) {
            this.executor = executor;
            this.handle = handle;
        }

        @Override
        public <T> T attachment() {
            return (T)EpollIoHandler.this.nativeArrays;
        }

        @Override
        public long submit(IoOps ops) {
            EpollIoOps epollIoOps = EpollIoHandler.cast(ops);
            try {
                if (!this.isValid()) {
                    return -1L;
                }
                Native.epollCtlMod(EpollIoHandler.this.epollFd.intValue(), this.handle.fd().intValue(), epollIoOps.value);
                return epollIoOps.value;
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public boolean isValid() {
            return !this.canceled.get();
        }

        @Override
        public boolean cancel() {
            if (!this.canceled.compareAndSet(false, true)) {
                return false;
            }
            if (this.executor.isExecutorThread(Thread.currentThread())) {
                this.cancel0();
            } else {
                this.executor.execute(this::cancel0);
            }
            return true;
        }

        private void cancel0() {
            int fd = this.handle.fd().intValue();
            DefaultEpollIoRegistration old = (DefaultEpollIoRegistration)EpollIoHandler.this.registrations.remove(fd);
            if (old != null) {
                if (old != this) {
                    EpollIoHandler.this.registrations.put(fd, old);
                    return;
                }
                if (old.handle instanceof AbstractEpollChannel.AbstractEpollUnsafe) {
                    EpollIoHandler.this.numChannels--;
                }
                if (this.handle.fd().isOpen()) {
                    try {
                        Native.epollCtlDel(EpollIoHandler.this.epollFd.intValue(), fd);
                    }
                    catch (IOException e) {
                        logger.debug("Unable to remove fd {} from epoll {}", (Object)fd, (Object)EpollIoHandler.this.epollFd.intValue());
                    }
                }
                this.handle.unregistered();
            }
        }

        void close() {
            try {
                this.cancel();
            }
            catch (Exception e) {
                logger.debug("Exception during canceling " + this, e);
            }
            try {
                this.handle.close();
            }
            catch (Exception e) {
                logger.debug("Exception during closing " + this.handle, e);
            }
        }

        void handle(long ev) {
            this.handle.handle(this, EpollIoOps.eventOf((int)ev));
        }
    }
}

