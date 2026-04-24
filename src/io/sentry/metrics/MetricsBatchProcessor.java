package io.sentry.metrics;

import io.sentry.DataCategory;
import io.sentry.ISentryClient;
import io.sentry.ISentryExecutorService;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryExecutorService;
import io.sentry.SentryLevel;
import io.sentry.SentryMetricsEvent;
import io.sentry.SentryMetricsEvents;
import io.sentry.SentryOptions;
import io.sentry.clientreport.DiscardReason;
import io.sentry.transport.ReusableCountLatch;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetricsBatchProcessor implements IMetricsBatchProcessor {
   public static final int FLUSH_AFTER_MS = 5000;
   public static final int MAX_BATCH_SIZE = 1000;
   public static final int MAX_QUEUE_SIZE = 10000;
   @NotNull
   protected final SentryOptions options;
   @NotNull
   private final ISentryClient client;
   @NotNull
   private final Queue<SentryMetricsEvent> queue;
   @NotNull
   private final ISentryExecutorService executorService;
   @Nullable
   private volatile Future<?> scheduledFlush;
   @NotNull
   private final AutoClosableReentrantLock scheduleLock = new AutoClosableReentrantLock();
   private volatile boolean hasScheduled = false;
   private volatile boolean isShuttingDown = false;
   @NotNull
   private final ReusableCountLatch pendingCount = new ReusableCountLatch();

   public MetricsBatchProcessor(@NotNull SentryOptions options, @NotNull ISentryClient client) {
      this.options = options;
      this.client = client;
      this.queue = new ConcurrentLinkedQueue<>();
      this.executorService = new SentryExecutorService(options);
   }

   @Override
   public void add(@NotNull SentryMetricsEvent metricsEvent) {
      if (!this.isShuttingDown) {
         if (this.pendingCount.getCount() >= 10000) {
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.QUEUE_OVERFLOW, DataCategory.TraceMetric);
         } else {
            this.pendingCount.increment();
            this.queue.offer(metricsEvent);
            this.maybeSchedule(false, false);
         }
      }
   }

   @Override
   public void close(boolean isRestarting) {
      this.isShuttingDown = true;
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
      if (!this.hasScheduled || forceSchedule) {
         ISentryLifecycleToken ignored = this.scheduleLock.acquire();

         try {
            Future<?> latestScheduledFlush = this.scheduledFlush;
            if (forceSchedule || latestScheduledFlush == null || latestScheduledFlush.isDone() || latestScheduledFlush.isCancelled()) {
               this.hasScheduled = true;
               int flushAfterMs = immediately ? 0 : 5000;

               try {
                  this.scheduledFlush = this.executorService.schedule(new MetricsBatchProcessor.BatchRunnable(), flushAfterMs);
               } catch (RejectedExecutionException e) {
                  this.hasScheduled = false;
                  this.options.getLogger().log(SentryLevel.WARNING, "Metrics batch processor flush task rejected", e);
               }
            }
         } catch (Throwable var9) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (ignored != null) {
            ignored.close();
         }
      }
   }

   @Override
   public void flush(long timeoutMillis) {
      this.maybeSchedule(true, true);

      try {
         this.pendingCount.waitTillZero(timeoutMillis, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
         this.options.getLogger().log(SentryLevel.ERROR, "Failed to flush metrics events", e);
         Thread.currentThread().interrupt();
      }
   }

   private void flush() {
      this.flushInternal();
      ISentryLifecycleToken ignored = this.scheduleLock.acquire();

      try {
         if (!this.queue.isEmpty()) {
            this.maybeSchedule(true, false);
         } else {
            this.hasScheduled = false;
         }
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   private void flushInternal() {
      do {
         this.flushBatch();
      } while (this.queue.size() >= 1000);
   }

   private void flushBatch() {
      List<SentryMetricsEvent> metricsEvents = new ArrayList<>(1000);

      do {
         SentryMetricsEvent metricsEvent = this.queue.poll();
         if (metricsEvent != null) {
            metricsEvents.add(metricsEvent);
         }
      } while (!this.queue.isEmpty() && metricsEvents.size() < 1000);

      if (!metricsEvents.isEmpty()) {
         this.client.captureBatchedMetricsEvents(new SentryMetricsEvents(metricsEvents));

         for (int i = 0; i < metricsEvents.size(); i++) {
            this.pendingCount.decrement();
         }
      }
   }

   private class BatchRunnable implements Runnable {
      private BatchRunnable() {
      }

      @Override
      public void run() {
         MetricsBatchProcessor.this.flush();
      }
   }
}
