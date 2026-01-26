/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.transport;

import io.sentry.DateUtils;
import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.RequestDetails;
import io.sentry.SentryDate;
import io.sentry.SentryDateProvider;
import io.sentry.SentryEnvelope;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.Cached;
import io.sentry.hints.DiskFlushNotification;
import io.sentry.hints.Enqueable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.transport.HttpConnection;
import io.sentry.transport.ITransport;
import io.sentry.transport.ITransportGate;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.transport.QueuedThreadPoolExecutor;
import io.sentry.transport.RateLimiter;
import io.sentry.transport.TransportResult;
import io.sentry.util.HintUtils;
import io.sentry.util.LogUtils;
import io.sentry.util.Objects;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AsyncHttpTransport
implements ITransport {
    @NotNull
    private final QueuedThreadPoolExecutor executor;
    @NotNull
    private final IEnvelopeCache envelopeCache;
    @NotNull
    private final SentryOptions options;
    @NotNull
    private final RateLimiter rateLimiter;
    @NotNull
    private final ITransportGate transportGate;
    @NotNull
    private final HttpConnection connection;
    @Nullable
    private volatile Runnable currentRunnable = null;

    public AsyncHttpTransport(@NotNull SentryOptions options, @NotNull RateLimiter rateLimiter, @NotNull ITransportGate transportGate, @NotNull RequestDetails requestDetails) {
        this(AsyncHttpTransport.initExecutor(options.getMaxQueueSize(), options.getEnvelopeDiskCache(), options.getLogger(), options.getDateProvider()), options, rateLimiter, transportGate, new HttpConnection(options, requestDetails, rateLimiter));
    }

    public AsyncHttpTransport(@NotNull QueuedThreadPoolExecutor executor, @NotNull SentryOptions options, @NotNull RateLimiter rateLimiter, @NotNull ITransportGate transportGate, @NotNull HttpConnection httpConnection) {
        this.executor = Objects.requireNonNull(executor, "executor is required");
        this.envelopeCache = Objects.requireNonNull(options.getEnvelopeDiskCache(), "envelopeCache is required");
        this.options = Objects.requireNonNull(options, "options is required");
        this.rateLimiter = Objects.requireNonNull(rateLimiter, "rateLimiter is required");
        this.transportGate = Objects.requireNonNull(transportGate, "transportGate is required");
        this.connection = Objects.requireNonNull(httpConnection, "httpConnection is required");
    }

    @Override
    public void send(@NotNull SentryEnvelope envelope, @NotNull Hint hint) throws IOException {
        SentryEnvelope filteredEnvelope;
        IEnvelopeCache currentEnvelopeCache = this.envelopeCache;
        boolean cached = false;
        if (HintUtils.hasType(hint, Cached.class)) {
            currentEnvelopeCache = NoOpEnvelopeCache.getInstance();
            cached = true;
            this.options.getLogger().log(SentryLevel.DEBUG, "Captured Envelope is already cached", new Object[0]);
        }
        if ((filteredEnvelope = this.rateLimiter.filter(envelope, hint)) == null) {
            if (cached) {
                this.envelopeCache.discard(envelope);
            }
        } else {
            SentryEnvelope envelopeThatMayIncludeClientReport = HintUtils.hasType(hint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class) ? this.options.getClientReportRecorder().attachReportToEnvelope(filteredEnvelope) : filteredEnvelope;
            Future<?> future = this.executor.submit(new EnvelopeSender(envelopeThatMayIncludeClientReport, hint, currentEnvelopeCache));
            if (future != null && future.isCancelled()) {
                this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.QUEUE_OVERFLOW, envelopeThatMayIncludeClientReport);
            } else {
                HintUtils.runIfHasType(hint, Enqueable.class, enqueable -> {
                    enqueable.markEnqueued();
                    this.options.getLogger().log(SentryLevel.DEBUG, "Envelope enqueued", new Object[0]);
                });
            }
        }
    }

    @Override
    public void flush(long timeoutMillis) {
        this.executor.waitTillIdle(timeoutMillis);
    }

    private static QueuedThreadPoolExecutor initExecutor(int maxQueueSize, @NotNull IEnvelopeCache envelopeCache, @NotNull ILogger logger, @NotNull SentryDateProvider dateProvider) {
        RejectedExecutionHandler storeEvents = (r, executor) -> {
            if (r instanceof EnvelopeSender) {
                EnvelopeSender envelopeSender = (EnvelopeSender)r;
                if (!HintUtils.hasType(envelopeSender.hint, Cached.class)) {
                    envelopeCache.storeEnvelope(envelopeSender.envelope, envelopeSender.hint);
                }
                AsyncHttpTransport.markHintWhenSendingFailed(envelopeSender.hint, true);
                logger.log(SentryLevel.WARNING, "Envelope rejected", new Object[0]);
            }
        };
        return new QueuedThreadPoolExecutor(1, maxQueueSize, new AsyncConnectionThreadFactory(), storeEvents, logger, dateProvider);
    }

    @Override
    @NotNull
    public RateLimiter getRateLimiter() {
        return this.rateLimiter;
    }

    @Override
    public boolean isHealthy() {
        boolean anyRateLimitActive = this.rateLimiter.isAnyRateLimitActive();
        boolean didRejectRecently = this.executor.didRejectRecently();
        return !anyRateLimitActive && !didRejectRecently;
    }

    @Override
    public void close() throws IOException {
        this.close(false);
    }

    @Override
    public void close(boolean isRestarting) throws IOException {
        this.rateLimiter.close();
        this.executor.shutdown();
        this.options.getLogger().log(SentryLevel.DEBUG, "Shutting down", new Object[0]);
        try {
            long timeout;
            if (!isRestarting && !this.executor.awaitTermination(timeout = this.options.getFlushTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                this.options.getLogger().log(SentryLevel.WARNING, "Failed to shutdown the async connection async sender  within " + timeout + " ms. Trying to force it now.", new Object[0]);
                this.executor.shutdownNow();
                if (this.currentRunnable != null) {
                    this.executor.getRejectedExecutionHandler().rejectedExecution(this.currentRunnable, this.executor);
                }
            }
        }
        catch (InterruptedException e) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Thread interrupted while closing the connection.", new Object[0]);
            Thread.currentThread().interrupt();
        }
    }

    private static void markHintWhenSendingFailed(@NotNull Hint hint, boolean retry) {
        HintUtils.runIfHasType(hint, SubmissionResult.class, result -> result.setResult(false));
        HintUtils.runIfHasType(hint, Retryable.class, retryable -> retryable.setRetry(retry));
    }

    private final class EnvelopeSender
    implements Runnable {
        @NotNull
        private final SentryEnvelope envelope;
        @NotNull
        private final Hint hint;
        @NotNull
        private final IEnvelopeCache envelopeCache;
        private final TransportResult failedResult = TransportResult.error();

        EnvelopeSender(@NotNull SentryEnvelope envelope, @NotNull Hint hint, IEnvelopeCache envelopeCache) {
            this.envelope = Objects.requireNonNull(envelope, "Envelope is required.");
            this.hint = hint;
            this.envelopeCache = Objects.requireNonNull(envelopeCache, "EnvelopeCache is required.");
        }

        @Override
        public void run() {
            AsyncHttpTransport.this.currentRunnable = this;
            TransportResult result = this.failedResult;
            try {
                result = this.flush();
                AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Envelope flushed", new Object[0]);
            }
            catch (Throwable e) {
                AsyncHttpTransport.this.options.getLogger().log(SentryLevel.ERROR, e, "Envelope submission failed", new Object[0]);
                throw e;
            }
            finally {
                TransportResult finalResult = result;
                HintUtils.runIfHasType(this.hint, SubmissionResult.class, submissionResult -> {
                    AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Marking envelope submission result: %s", finalResult.isSuccess());
                    submissionResult.setResult(finalResult.isSuccess());
                });
                AsyncHttpTransport.this.currentRunnable = null;
            }
        }

        @NotNull
        private TransportResult flush() {
            TransportResult result = this.failedResult;
            this.envelope.getHeader().setSentAt(null);
            boolean cached = this.envelopeCache.storeEnvelope(this.envelope, this.hint);
            HintUtils.runIfHasType(this.hint, DiskFlushNotification.class, diskFlushNotification -> {
                if (diskFlushNotification.isFlushable(this.envelope.getHeader().getEventId())) {
                    diskFlushNotification.markFlushed();
                    AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Disk flush envelope fired", new Object[0]);
                } else {
                    AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Not firing envelope flush as there's an ongoing transaction", new Object[0]);
                }
            });
            if (AsyncHttpTransport.this.transportGate.isConnected()) {
                SentryEnvelope envelopeWithClientReport = AsyncHttpTransport.this.options.getClientReportRecorder().attachReportToEnvelope(this.envelope);
                try {
                    @NotNull SentryDate now = AsyncHttpTransport.this.options.getDateProvider().now();
                    envelopeWithClientReport.getHeader().setSentAt(DateUtils.nanosToDate(now.nanoTimestamp()));
                    result = AsyncHttpTransport.this.connection.send(envelopeWithClientReport);
                    if (!result.isSuccess()) {
                        String message = "The transport failed to send the envelope with response code " + result.getResponseCode();
                        AsyncHttpTransport.this.options.getLogger().log(SentryLevel.ERROR, message, new Object[0]);
                        if (result.getResponseCode() >= 400 && result.getResponseCode() != 429 && !cached) {
                            HintUtils.runIfDoesNotHaveType(this.hint, Retryable.class, hint -> AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, envelopeWithClientReport));
                        }
                        throw new IllegalStateException(message);
                    }
                    this.envelopeCache.discard(this.envelope);
                }
                catch (IOException e) {
                    HintUtils.runIfHasType(this.hint, Retryable.class, retryable -> retryable.setRetry(true), (hint, clazz) -> {
                        if (!cached) {
                            LogUtils.logNotInstanceOf(clazz, hint, AsyncHttpTransport.this.options.getLogger());
                            AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, envelopeWithClientReport);
                        }
                    });
                    throw new IllegalStateException("Sending the event failed.", e);
                }
            } else {
                HintUtils.runIfHasType(this.hint, Retryable.class, retryable -> retryable.setRetry(true), (hint, clazz) -> {
                    if (!cached) {
                        LogUtils.logNotInstanceOf(clazz, hint, AsyncHttpTransport.this.options.getLogger());
                        AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, this.envelope);
                    }
                });
            }
            return result;
        }
    }

    private static final class AsyncConnectionThreadFactory
    implements ThreadFactory {
        private int cnt;

        private AsyncConnectionThreadFactory() {
        }

        @Override
        @NotNull
        public Thread newThread(@NotNull Runnable r) {
            Thread ret = new Thread(r, "SentryAsyncConnection-" + this.cnt++);
            ret.setDaemon(true);
            return ret;
        }
    }
}

