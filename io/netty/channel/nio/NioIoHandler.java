/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.IoHandle;
import io.netty.channel.IoHandler;
import io.netty.channel.IoHandlerContext;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoOps;
import io.netty.channel.IoRegistration;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.nio.NioIoHandle;
import io.netty.channel.nio.NioIoOps;
import io.netty.channel.nio.SelectedSelectionKeySet;
import io.netty.channel.nio.SelectedSelectionKeySetSelector;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.ThreadAwareExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioIoHandler
implements IoHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioIoHandler.class);
    private static final int CLEANUP_INTERVAL = 256;
    private static final boolean DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
    private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
    private final IntSupplier selectNowSupplier = new IntSupplier(){

        @Override
        public int get() throws Exception {
            return NioIoHandler.this.selectNow();
        }
    };
    private Selector selector;
    private Selector unwrappedSelector;
    private SelectedSelectionKeySet selectedKeys;
    private final SelectorProvider provider;
    private final AtomicBoolean wakenUp = new AtomicBoolean();
    private final SelectStrategy selectStrategy;
    private final ThreadAwareExecutor executor;
    private int cancelledKeys;
    private boolean needsToSelectAgain;

    private NioIoHandler(ThreadAwareExecutor executor, SelectorProvider selectorProvider, SelectStrategy strategy) {
        this.executor = ObjectUtil.checkNotNull(executor, "executionContext");
        this.provider = ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "selectStrategy");
        SelectorTuple selectorTuple = this.openSelector();
        this.selector = selectorTuple.selector;
        this.unwrappedSelector = selectorTuple.unwrappedSelector;
    }

    private SelectorTuple openSelector() {
        AbstractSelector unwrappedSelector;
        try {
            unwrappedSelector = this.provider.openSelector();
        }
        catch (IOException e) {
            throw new ChannelException("failed to open a new selector", e);
        }
        if (DISABLE_KEY_SET_OPTIMIZATION) {
            return new SelectorTuple(unwrappedSelector);
        }
        Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
                }
                catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (!(maybeSelectorImplClass instanceof Class) || !((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
            if (maybeSelectorImplClass instanceof Throwable) {
                Throwable t = (Throwable)maybeSelectorImplClass;
                logger.trace("failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)t);
            }
            return new SelectorTuple(unwrappedSelector);
        }
        final Class selectorImplClass = (Class)maybeSelectorImplClass;
        final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
        Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    Throwable cause;
                    Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
                    Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
                    if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                        long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset(selectedKeysField);
                        long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset(publicSelectedKeysField);
                        if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
                            PlatformDependent.putObject(unwrappedSelector, selectedKeysFieldOffset, selectedKeySet);
                            PlatformDependent.putObject(unwrappedSelector, publicSelectedKeysFieldOffset, selectedKeySet);
                            return null;
                        }
                    }
                    if ((cause = ReflectionUtil.trySetAccessible(selectedKeysField, true)) != null) {
                        return cause;
                    }
                    cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField, true);
                    if (cause != null) {
                        return cause;
                    }
                    selectedKeysField.set(unwrappedSelector, selectedKeySet);
                    publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
                    return null;
                }
                catch (IllegalAccessException | NoSuchFieldException e) {
                    return e;
                }
            }
        });
        if (maybeException instanceof Exception) {
            this.selectedKeys = null;
            Exception e = (Exception)maybeException;
            logger.trace("failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)e);
            return new SelectorTuple(unwrappedSelector);
        }
        this.selectedKeys = selectedKeySet;
        logger.trace("instrumented a special java.util.Set into: {}", (Object)unwrappedSelector);
        return new SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
    }

    public SelectorProvider selectorProvider() {
        return this.provider;
    }

    Selector selector() {
        return this.selector;
    }

    int numRegistered() {
        return this.selector().keys().size() - this.cancelledKeys;
    }

    Set<SelectionKey> registeredSet() {
        return this.selector().keys();
    }

    void rebuildSelector0() {
        int nChannels;
        block9: {
            SelectorTuple newSelectorTuple;
            Selector oldSelector = this.selector;
            if (oldSelector == null) {
                return;
            }
            try {
                newSelectorTuple = this.openSelector();
            }
            catch (Exception e) {
                logger.warn("Failed to create a new Selector.", e);
                return;
            }
            nChannels = 0;
            for (SelectionKey key : oldSelector.keys()) {
                DefaultNioRegistration handle = (DefaultNioRegistration)key.attachment();
                try {
                    if (!key.isValid() || key.channel().keyFor(newSelectorTuple.unwrappedSelector) != null) continue;
                    handle.register(newSelectorTuple.unwrappedSelector);
                    ++nChannels;
                }
                catch (Exception e) {
                    logger.warn("Failed to re-register a NioHandle to the new Selector.", e);
                    handle.cancel();
                }
            }
            this.selector = newSelectorTuple.selector;
            this.unwrappedSelector = newSelectorTuple.unwrappedSelector;
            try {
                oldSelector.close();
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block9;
                logger.warn("Failed to close the old Selector.", t);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
        }
    }

    private static NioIoHandle nioHandle(IoHandle handle) {
        if (handle instanceof NioIoHandle) {
            return (NioIoHandle)handle;
        }
        throw new IllegalArgumentException("IoHandle of type " + StringUtil.simpleClassName(handle) + " not supported");
    }

    private static NioIoOps cast(IoOps ops) {
        if (ops instanceof NioIoOps) {
            return (NioIoOps)ops;
        }
        throw new IllegalArgumentException("IoOps of type " + StringUtil.simpleClassName(ops) + " not supported");
    }

    @Override
    public IoRegistration register(IoHandle handle) throws Exception {
        NioIoHandle nioHandle = NioIoHandler.nioHandle(handle);
        NioIoOps ops = NioIoOps.NONE;
        boolean selected = false;
        while (true) {
            try {
                DefaultNioRegistration registration = new DefaultNioRegistration(this.executor, nioHandle, ops, this.unwrappedSelector());
                handle.registered();
                return registration;
            }
            catch (CancelledKeyException e) {
                if (!selected) {
                    this.selectNow();
                    selected = true;
                    continue;
                }
                throw e;
            }
            break;
        }
    }

    @Override
    public int run(IoHandlerContext context) {
        int handled = 0;
        try {
            try {
                switch (this.selectStrategy.calculateStrategy(this.selectNowSupplier, !context.canBlock())) {
                    case -2: {
                        if (context.shouldReportActiveIoTime()) {
                            context.reportActiveIoTime(0L);
                        }
                        return 0;
                    }
                    case -3: 
                    case -1: {
                        this.select(context, this.wakenUp.getAndSet(false));
                        if (!this.wakenUp.get()) break;
                        this.selector.wakeup();
                    }
                }
            }
            catch (IOException e) {
                this.rebuildSelector0();
                NioIoHandler.handleLoopException(e);
                return 0;
            }
            this.cancelledKeys = 0;
            this.needsToSelectAgain = false;
            if (context.shouldReportActiveIoTime()) {
                long activeIoStartTimeNanos = System.nanoTime();
                handled = this.processSelectedKeys();
                long activeIoEndTimeNanos = System.nanoTime();
                context.reportActiveIoTime(activeIoEndTimeNanos - activeIoStartTimeNanos);
            } else {
                handled = this.processSelectedKeys();
            }
        }
        catch (Error e) {
            throw e;
        }
        catch (Throwable t) {
            NioIoHandler.handleLoopException(t);
        }
        return handled;
    }

    private static void handleLoopException(Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private int processSelectedKeys() {
        if (this.selectedKeys != null) {
            return this.processSelectedKeysOptimized();
        }
        return this.processSelectedKeysPlain(this.selector.selectedKeys());
    }

    @Override
    public void destroy() {
        try {
            this.selector.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a selector.", e);
        }
    }

    private int processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return 0;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        int handled = 0;
        while (true) {
            SelectionKey k = i.next();
            i.remove();
            this.processSelectedKey(k);
            ++handled;
            if (!i.hasNext()) break;
            if (!this.needsToSelectAgain) continue;
            this.selectAgain();
            selectedKeys = this.selector.selectedKeys();
            if (selectedKeys.isEmpty()) break;
            i = selectedKeys.iterator();
        }
        return handled;
    }

    private int processSelectedKeysOptimized() {
        int handled = 0;
        for (int i = 0; i < this.selectedKeys.size; ++i) {
            SelectionKey k = this.selectedKeys.keys[i];
            this.selectedKeys.keys[i] = null;
            this.processSelectedKey(k);
            ++handled;
            if (!this.needsToSelectAgain) continue;
            this.selectedKeys.reset(i + 1);
            this.selectAgain();
            i = -1;
        }
        return handled;
    }

    private void processSelectedKey(SelectionKey k) {
        DefaultNioRegistration registration = (DefaultNioRegistration)k.attachment();
        if (!registration.isValid()) {
            try {
                registration.handle.close();
            }
            catch (Exception e) {
                logger.debug("Exception during closing " + registration.handle, e);
            }
            return;
        }
        registration.handle(k.readyOps());
    }

    @Override
    public void prepareToDestroy() {
        this.selectAgain();
        Set<SelectionKey> keys = this.selector.keys();
        ArrayList<DefaultNioRegistration> registrations = new ArrayList<DefaultNioRegistration>(keys.size());
        for (SelectionKey k : keys) {
            DefaultNioRegistration handle = (DefaultNioRegistration)k.attachment();
            registrations.add(handle);
        }
        for (DefaultNioRegistration reg : registrations) {
            reg.close();
        }
    }

    @Override
    public void wakeup() {
        if (!this.executor.isExecutorThread(Thread.currentThread()) && this.wakenUp.compareAndSet(false, true)) {
            this.selector.wakeup();
        }
    }

    @Override
    public boolean isCompatible(Class<? extends IoHandle> handleType) {
        return NioIoHandle.class.isAssignableFrom(handleType);
    }

    Selector unwrappedSelector() {
        return this.unwrappedSelector;
    }

    private void select(IoHandlerContext runner, boolean oldWakenUp) throws IOException {
        block14: {
            Selector selector = this.selector;
            try {
                int selectCnt = 0;
                long currentTimeNanos = System.nanoTime();
                long delayNanos = runner.delayNanos(currentTimeNanos);
                long selectDeadLineNanos = Long.MAX_VALUE;
                if (delayNanos != Long.MAX_VALUE) {
                    selectDeadLineNanos = currentTimeNanos + runner.delayNanos(currentTimeNanos);
                }
                while (true) {
                    long timeoutMillis;
                    if (delayNanos != Long.MAX_VALUE) {
                        long millisBeforeDeadline = NioIoHandler.millisBeforeDeadline(selectDeadLineNanos, currentTimeNanos);
                        if (millisBeforeDeadline <= 0L) {
                            if (selectCnt != 0) break;
                            selector.selectNow();
                            selectCnt = 1;
                            break;
                        }
                        timeoutMillis = millisBeforeDeadline;
                    } else {
                        timeoutMillis = 0L;
                    }
                    if (!runner.canBlock() && this.wakenUp.compareAndSet(false, true)) {
                        selector.selectNow();
                        selectCnt = 1;
                        break;
                    }
                    int selectedKeys = selector.select(timeoutMillis);
                    ++selectCnt;
                    if (selectedKeys != 0 || oldWakenUp || this.wakenUp.get() || !runner.canBlock()) break;
                    if (Thread.interrupted()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioHandler.shutdownGracefully() to shutdown the NioHandler.");
                        }
                        selectCnt = 1;
                        break;
                    }
                    long time = System.nanoTime();
                    if (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
                        selectCnt = 1;
                    } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                        selector = this.selectRebuildSelector(selectCnt);
                        selectCnt = 1;
                        break;
                    }
                    currentTimeNanos = time;
                }
                if (selectCnt > 3 && logger.isDebugEnabled()) {
                    logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.", (Object)(selectCnt - 1), (Object)selector);
                }
            }
            catch (CancelledKeyException e) {
                if (!logger.isDebugEnabled()) break block14;
                logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?", (Object)selector, (Object)e);
            }
        }
    }

    private static long millisBeforeDeadline(long selectDeadLineNanos, long currentTimeNanos) {
        assert (selectDeadLineNanos != Long.MAX_VALUE);
        long nanosBeforeDeadline = selectDeadLineNanos - currentTimeNanos;
        if (nanosBeforeDeadline >= 9223372036854275807L) {
            return 9223372036854L;
        }
        return (nanosBeforeDeadline + 500000L) / 1000000L;
    }

    int selectNow() throws IOException {
        try {
            int n = this.selector.selectNow();
            return n;
        }
        finally {
            if (this.wakenUp.get()) {
                this.selector.wakeup();
            }
        }
    }

    private Selector selectRebuildSelector(int selectCnt) throws IOException {
        logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", (Object)selectCnt, (Object)this.selector);
        this.rebuildSelector0();
        Selector selector = this.selector;
        selector.selectNow();
        return selector;
    }

    private void selectAgain() {
        this.needsToSelectAgain = false;
        try {
            this.selector.selectNow();
        }
        catch (Throwable t) {
            logger.warn("Failed to update SelectionKeys.", t);
        }
    }

    public static IoHandlerFactory newFactory() {
        return NioIoHandler.newFactory(SelectorProvider.provider(), DefaultSelectStrategyFactory.INSTANCE);
    }

    public static IoHandlerFactory newFactory(SelectorProvider selectorProvider) {
        return NioIoHandler.newFactory(selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
    }

    public static IoHandlerFactory newFactory(final SelectorProvider selectorProvider, final SelectStrategyFactory selectStrategyFactory) {
        ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
        ObjectUtil.checkNotNull(selectStrategyFactory, "selectStrategyFactory");
        return new IoHandlerFactory(){

            @Override
            public IoHandler newHandler(ThreadAwareExecutor executor) {
                return new NioIoHandler(executor, selectorProvider, selectStrategyFactory.newSelectStrategy());
            }

            @Override
            public boolean isChangingThreadSupported() {
                return true;
            }
        };
    }

    static {
        int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
        if (selectorAutoRebuildThreshold < 3) {
            selectorAutoRebuildThreshold = 0;
        }
        SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object)DISABLE_KEY_SET_OPTIMIZATION);
            logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)SELECTOR_AUTO_REBUILD_THRESHOLD);
        }
    }

    final class DefaultNioRegistration
    implements IoRegistration {
        private final AtomicBoolean canceled = new AtomicBoolean();
        private final NioIoHandle handle;
        private volatile SelectionKey key;

        DefaultNioRegistration(ThreadAwareExecutor executor, NioIoHandle handle, NioIoOps initialOps, Selector selector) throws IOException {
            this.handle = handle;
            this.key = handle.selectableChannel().register(selector, initialOps.value, this);
        }

        NioIoHandle handle() {
            return this.handle;
        }

        void register(Selector selector) throws IOException {
            SelectionKey newKey = this.handle.selectableChannel().register(selector, this.key.interestOps(), this);
            this.key.cancel();
            this.key = newKey;
        }

        @Override
        public <T> T attachment() {
            return (T)this.key;
        }

        @Override
        public boolean isValid() {
            return !this.canceled.get() && this.key.isValid();
        }

        @Override
        public long submit(IoOps ops) {
            if (!this.isValid()) {
                return -1L;
            }
            int v = NioIoHandler.cast((IoOps)ops).value;
            this.key.interestOps(v);
            return v;
        }

        @Override
        public boolean cancel() {
            if (!this.canceled.compareAndSet(false, true)) {
                return false;
            }
            this.key.cancel();
            NioIoHandler.this.cancelledKeys++;
            if (NioIoHandler.this.cancelledKeys >= 256) {
                NioIoHandler.this.cancelledKeys = 0;
                NioIoHandler.this.needsToSelectAgain = true;
            }
            this.handle.unregistered();
            return true;
        }

        void close() {
            this.cancel();
            try {
                this.handle.close();
            }
            catch (Exception e) {
                logger.debug("Exception during closing " + this.handle, e);
            }
        }

        void handle(int ready) {
            if (!this.isValid()) {
                return;
            }
            this.handle.handle(this, NioIoOps.eventOf(ready));
        }
    }

    private static final class SelectorTuple {
        final Selector unwrappedSelector;
        final Selector selector;

        SelectorTuple(Selector unwrappedSelector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = unwrappedSelector;
        }

        SelectorTuple(Selector unwrappedSelector, Selector selector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = selector;
        }
    }
}

