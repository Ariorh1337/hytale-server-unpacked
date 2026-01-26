/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.CompositePerformanceCollector;
import io.sentry.IPerformanceCollector;
import io.sentry.IPerformanceContinuousCollector;
import io.sentry.IPerformanceSnapshotCollector;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.PerformanceCollectionData;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class DefaultCompositePerformanceCollector
implements CompositePerformanceCollector {
    private static final long TRANSACTION_COLLECTION_INTERVAL_MILLIS = 100L;
    private static final long TRANSACTION_COLLECTION_TIMEOUT_MILLIS = 30000L;
    @NotNull
    private final AutoClosableReentrantLock timerLock = new AutoClosableReentrantLock();
    @Nullable
    private volatile Timer timer = null;
    @NotNull
    private final Map<String, CompositeData> compositeDataMap = new ConcurrentHashMap<String, CompositeData>();
    @NotNull
    private final List<IPerformanceSnapshotCollector> snapshotCollectors;
    @NotNull
    private final List<IPerformanceContinuousCollector> continuousCollectors;
    private final boolean hasNoCollectors;
    @NotNull
    private final SentryOptions options;
    @NotNull
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private long lastCollectionTimestamp = 0L;

    public DefaultCompositePerformanceCollector(@NotNull SentryOptions options) {
        this.options = Objects.requireNonNull(options, "The options object is required.");
        this.snapshotCollectors = new ArrayList<IPerformanceSnapshotCollector>();
        this.continuousCollectors = new ArrayList<IPerformanceContinuousCollector>();
        @NotNull List<IPerformanceCollector> performanceCollectors = options.getPerformanceCollectors();
        for (IPerformanceCollector performanceCollector : performanceCollectors) {
            if (performanceCollector instanceof IPerformanceSnapshotCollector) {
                this.snapshotCollectors.add((IPerformanceSnapshotCollector)performanceCollector);
            }
            if (!(performanceCollector instanceof IPerformanceContinuousCollector)) continue;
            this.continuousCollectors.add((IPerformanceContinuousCollector)performanceCollector);
        }
        this.hasNoCollectors = this.snapshotCollectors.isEmpty() && this.continuousCollectors.isEmpty();
    }

    @Override
    public void start(@NotNull ITransaction transaction) {
        if (this.hasNoCollectors) {
            this.options.getLogger().log(SentryLevel.INFO, "No collector found. Performance stats will not be captured during transactions.", new Object[0]);
            return;
        }
        for (IPerformanceContinuousCollector collector : this.continuousCollectors) {
            collector.onSpanStarted(transaction);
        }
        @NotNull String id = transaction.getEventId().toString();
        if (!this.compositeDataMap.containsKey(id)) {
            this.compositeDataMap.put(id, new CompositeData(transaction));
        }
        this.start(id);
    }

    @Override
    public void start(@NotNull String id) {
        if (this.hasNoCollectors) {
            this.options.getLogger().log(SentryLevel.INFO, "No collector found. Performance stats will not be captured during transactions.", new Object[0]);
            return;
        }
        if (!this.compositeDataMap.containsKey(id)) {
            this.compositeDataMap.put(id, new CompositeData(null));
        }
        if (!this.isStarted.getAndSet(true)) {
            try (@NotNull ISentryLifecycleToken ignored = this.timerLock.acquire();){
                if (this.timer == null) {
                    this.timer = new Timer(true);
                }
                this.timer.schedule(new TimerTask(){

                    @Override
                    public void run() {
                        for (IPerformanceSnapshotCollector collector : DefaultCompositePerformanceCollector.this.snapshotCollectors) {
                            collector.setup();
                        }
                    }
                }, 0L);
                final @NotNull ArrayList<E> timedOutTransactions = new ArrayList();
                TimerTask timerTask = new TimerTask(){

                    @Override
                    public void run() {
                        long now = System.currentTimeMillis();
                        if (now - DefaultCompositePerformanceCollector.this.lastCollectionTimestamp <= 10L) {
                            return;
                        }
                        timedOutTransactions.clear();
                        DefaultCompositePerformanceCollector.this.lastCollectionTimestamp = now;
                        @NotNull PerformanceCollectionData tempData = new PerformanceCollectionData(DefaultCompositePerformanceCollector.this.options.getDateProvider().now().nanoTimestamp());
                        for (IPerformanceSnapshotCollector collector : DefaultCompositePerformanceCollector.this.snapshotCollectors) {
                            collector.collect(tempData);
                        }
                        for (CompositeData data : DefaultCompositePerformanceCollector.this.compositeDataMap.values()) {
                            if (!data.addDataAndCheckTimeout(tempData) || data.transaction == null) continue;
                            timedOutTransactions.add(data.transaction);
                        }
                        for (ITransaction t : timedOutTransactions) {
                            DefaultCompositePerformanceCollector.this.stop(t);
                        }
                    }
                };
                this.timer.scheduleAtFixedRate(timerTask, 100L, 100L);
            }
        }
    }

    @Override
    public void onSpanStarted(@NotNull ISpan span) {
        for (IPerformanceContinuousCollector collector : this.continuousCollectors) {
            collector.onSpanStarted(span);
        }
    }

    @Override
    public void onSpanFinished(@NotNull ISpan span) {
        for (IPerformanceContinuousCollector collector : this.continuousCollectors) {
            collector.onSpanFinished(span);
        }
    }

    @Override
    @Nullable
    public List<PerformanceCollectionData> stop(@NotNull ITransaction transaction) {
        this.options.getLogger().log(SentryLevel.DEBUG, "stop collecting performance info for transactions %s (%s)", transaction.getName(), transaction.getSpanContext().getTraceId().toString());
        for (IPerformanceContinuousCollector collector : this.continuousCollectors) {
            collector.onSpanFinished(transaction);
        }
        return this.stop(transaction.getEventId().toString());
    }

    @Override
    @Nullable
    public List<PerformanceCollectionData> stop(@NotNull String id) {
        @Nullable CompositeData data = this.compositeDataMap.remove(id);
        this.options.getLogger().log(SentryLevel.DEBUG, "stop collecting performance info for " + id, new Object[0]);
        if (this.compositeDataMap.isEmpty()) {
            this.close();
        }
        return data != null ? data.dataList : null;
    }

    @Override
    public void close() {
        this.options.getLogger().log(SentryLevel.DEBUG, "stop collecting all performance info for transactions", new Object[0]);
        this.compositeDataMap.clear();
        for (IPerformanceContinuousCollector collector : this.continuousCollectors) {
            collector.clear();
        }
        if (this.isStarted.getAndSet(false)) {
            try (@NotNull ISentryLifecycleToken ignored = this.timerLock.acquire();){
                if (this.timer != null) {
                    this.timer.cancel();
                    this.timer = null;
                }
            }
        }
    }

    private class CompositeData {
        @NotNull
        private final List<PerformanceCollectionData> dataList = new ArrayList<PerformanceCollectionData>();
        @Nullable
        private final ITransaction transaction;
        private final long startTimestamp;

        private CompositeData(ITransaction transaction) {
            this.transaction = transaction;
            this.startTimestamp = DefaultCompositePerformanceCollector.this.options.getDateProvider().now().nanoTimestamp();
        }

        boolean addDataAndCheckTimeout(@NotNull PerformanceCollectionData data) {
            this.dataList.add(data);
            return this.transaction != null && DefaultCompositePerformanceCollector.this.options.getDateProvider().now().nanoTimestamp() > this.startTimestamp + TimeUnit.MILLISECONDS.toNanos(30000L);
        }
    }
}

