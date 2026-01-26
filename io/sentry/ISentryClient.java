/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.CheckIn;
import io.sentry.Hint;
import io.sentry.IScope;
import io.sentry.ProfileChunk;
import io.sentry.ProfilingTraceData;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEvents;
import io.sentry.SentryReplayEvent;
import io.sentry.Session;
import io.sentry.TraceContext;
import io.sentry.UserFeedback;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.Message;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.RateLimiter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISentryClient {
    public boolean isEnabled();

    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent var1, @Nullable IScope var2, @Nullable Hint var3);

    public void close();

    public void close(boolean var1);

    public void flush(long var1);

    @NotNull
    default public SentryId captureEvent(@NotNull SentryEvent event) {
        return this.captureEvent(event, null, null);
    }

    @NotNull
    default public SentryId captureEvent(@NotNull SentryEvent event, @Nullable IScope scope) {
        return this.captureEvent(event, scope, null);
    }

    @NotNull
    default public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
        return this.captureEvent(event, null, hint);
    }

    @NotNull
    public SentryId captureFeedback(@NotNull Feedback var1, @Nullable Hint var2, @NotNull IScope var3);

    @NotNull
    default public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @Nullable IScope scope) {
        SentryEvent event = new SentryEvent();
        Message sentryMessage = new Message();
        sentryMessage.setFormatted(message);
        event.setMessage(sentryMessage);
        event.setLevel(level);
        return this.captureEvent(event, scope);
    }

    @NotNull
    default public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
        return this.captureMessage(message, level, null);
    }

    @NotNull
    default public SentryId captureException(@NotNull Throwable throwable) {
        return this.captureException(throwable, null, null);
    }

    @NotNull
    default public SentryId captureException(@NotNull Throwable throwable, @Nullable IScope scope, @Nullable Hint hint) {
        SentryEvent event = new SentryEvent(throwable);
        return this.captureEvent(event, scope, hint);
    }

    @NotNull
    default public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
        return this.captureException(throwable, null, hint);
    }

    @NotNull
    default public SentryId captureException(@NotNull Throwable throwable, @Nullable IScope scope) {
        return this.captureException(throwable, scope, null);
    }

    @NotNull
    public SentryId captureReplayEvent(@NotNull SentryReplayEvent var1, @Nullable IScope var2, @Nullable Hint var3);

    public void captureUserFeedback(@NotNull UserFeedback var1);

    public void captureSession(@NotNull Session var1, @Nullable Hint var2);

    default public void captureSession(@NotNull Session session) {
        this.captureSession(session, null);
    }

    @Nullable
    public SentryId captureEnvelope(@NotNull SentryEnvelope var1, @Nullable Hint var2);

    @Nullable
    default public SentryId captureEnvelope(@NotNull SentryEnvelope envelope) {
        return this.captureEnvelope(envelope, null);
    }

    @NotNull
    default public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable IScope scope, @Nullable Hint hint) {
        return this.captureTransaction(transaction, null, scope, hint);
    }

    @NotNull
    default public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable IScope scope, @Nullable Hint hint) {
        return this.captureTransaction(transaction, traceContext, scope, hint, null);
    }

    @NotNull
    @ApiStatus.Internal
    public SentryId captureTransaction(@NotNull SentryTransaction var1, @Nullable TraceContext var2, @Nullable IScope var3, @Nullable Hint var4, @Nullable ProfilingTraceData var5);

    @ApiStatus.Internal
    @NotNull
    default public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext) {
        return this.captureTransaction(transaction, traceContext, null, null);
    }

    @NotNull
    default public SentryId captureTransaction(@NotNull SentryTransaction transaction) {
        return this.captureTransaction(transaction, null, null, null);
    }

    @ApiStatus.Internal
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk var1, @Nullable IScope var2);

    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn var1, @Nullable IScope var2, @Nullable Hint var3);

    public void captureLog(@NotNull SentryLogEvent var1, @Nullable IScope var2);

    @ApiStatus.Internal
    public void captureBatchedLogEvents(@NotNull SentryLogEvents var1);

    @ApiStatus.Internal
    @Nullable
    public RateLimiter getRateLimiter();

    @ApiStatus.Internal
    default public boolean isHealthy() {
        return true;
    }
}

