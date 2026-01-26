/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.logger;

import io.sentry.DataCategory;
import io.sentry.ISentryClient;
import io.sentry.ISentryExecutorService;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryExecutorService;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEvents;
import io.sentry.SentryOptions;
import io.sentry.clientreport.DiscardReason;
import io.sentry.logger.ILoggerBatchProcessor;
import io.sentry.transport.ReusableCountLatch;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.JsonSerializationUtils;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoggerBatchProcessor
implements ILoggerBatchProcessor {
    public static final int FLUSH_AFTER_MS = 5000;
    public static final int MAX_BATCH_SIZE = 100;
    public static final int MAX_QUEUE_SIZE = 1000;
    @NotNull
    protected final SentryOptions options;
    @NotNull
    private final ISentryClient client;
    @NotNull
    private final Queue<SentryLogEvent> queue;
    @NotNull
    private final ISentryExecutorService executorService;
    @Nullable
    private volatile Future<?> scheduledFlush;
    @NotNull
    private static final AutoClosableReentrantLock scheduleLock = new AutoClosableReentrantLock();
    private volatile boolean hasScheduled = false;
    @NotNull
    private final ReusableCountLatch pendingCount = new ReusableCountLatch();

    public LoggerBatchProcessor(@NotNull SentryOptions options, @NotNull ISentryClient client) {
        this.options = options;
        this.client = client;
        this.queue = new ConcurrentLinkedQueue<SentryLogEvent>();
        this.executorService = new SentryExecutorService(options);
    }

    @Override
    public void add(@NotNull SentryLogEvent logEvent) {
        if (this.pendingCount.getCount() >= 1000) {
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.QUEUE_OVERFLOW, DataCategory.LogItem);
            long lostBytes = JsonSerializationUtils.byteSizeOf(this.options.getSerializer(), this.options.getLogger(), logEvent);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.QUEUE_OVERFLOW, DataCategory.Attachment, lostBytes);
            return;
        }
        this.pendingCount.increment();
        this.queue.offer(logEvent);
        this.maybeSchedule(false, false);
    }

    @Override
    public void close(boolean isRestarting) {
        if (isRestarting) {
            this.maybeSchedule(true, true);
            this.executorService.submit(() -> this.executorService.close(this.options.getShutdownTimeoutMillis()));
        } else {
            this.executorService.close(this.options.getShutdownTimeoutMillis());
            while (!this.queue.isEmpty()) {
                this.flushBatch();
            }
        }
    }

    private void maybeSchedule(boolean forceSchedule, boolean immediately) {
        if (this.hasScheduled && !forceSchedule) {
            return;
        }
        try (@NotNull ISentryLifecycleToken ignored = scheduleLock.acquire();){
            @Nullable Future<?> latestScheduledFlush = this.scheduledFlush;
            if (forceSchedule || latestScheduledFlush == null || latestScheduledFlush.isDone() || latestScheduledFlush.isCancelled()) {
                this.hasScheduled = true;
                int flushAfterMs = immediately ? 0 : 5000;
                try {
                    this.scheduledFlush = this.executorService.schedule(new BatchRunnable(), flushAfterMs);
                }
                catch (RejectedExecutionException e) {
                    this.hasScheduled = false;
                    this.options.getLogger().log(SentryLevel.WARNING, "Logs batch processor flush task rejected", e);
                }
            }
        }
    }

    @Override
    public void flush(long timeoutMillis) {
        this.maybeSchedule(true, true);
        try {
            this.pendingCount.waitTillZero(timeoutMillis, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to flush log events", e);
            Thread.currentThread().interrupt();
        }
    }

    private void flush() {
        this.flushInternal();
        try (@NotNull ISentryLifecycleToken ignored = scheduleLock.acquire();){
            if (!this.queue.isEmpty()) {
                this.maybeSchedule(true, false);
            } else {
                this.hasScheduled = false;
            }
        }
    }

    private void flushInternal() {
        do {
            this.flushBatch();
        } while (this.queue.size() >= 100);
    }

    private void flushBatch() {
        @NotNull ArrayList<SentryLogEvent> logEvents = new ArrayList<SentryLogEvent>(100);
        do {
            SentryLogEvent logEvent;
            if ((logEvent = this.queue.poll()) == null) continue;
            logEvents.add(logEvent);
        } while (!this.queue.isEmpty() && logEvents.size() < 100);
        if (!logEvents.isEmpty()) {
            this.client.captureBatchedLogEvents(new SentryLogEvents(logEvents));
            for (int i = 0; i < logEvents.size(); ++i) {
                this.pendingCount.decrement();
            }
        }
    }

    private class BatchRunnable
    implements Runnable {
        private BatchRunnable() {
        }

        @Override
        public void run() {
            LoggerBatchProcessor.this.flush();
        }
    }
}

