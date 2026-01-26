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
import io.sentry.ISentryClient;
import io.sentry.ProfileChunk;
import io.sentry.ProfilingTraceData;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEvent;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEvents;
import io.sentry.SentryReplayEvent;
import io.sentry.Session;
import io.sentry.TraceContext;
import io.sentry.UserFeedback;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.RateLimiter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NoOpSentryClient
implements ISentryClient {
    private static final NoOpSentryClient instance = new NoOpSentryClient();

    private NoOpSentryClient() {
    }

    public static NoOpSentryClient getInstance() {
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable IScope scope, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    public void close(boolean isRestarting) {
    }

    @Override
    public void close() {
    }

    @Override
    public void flush(long timeoutMillis) {
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @NotNull IScope scope) {
        return SentryId.EMPTY_ID;
    }

    @Override
    public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
    }

    @Override
    public void captureSession(@NotNull Session session, @Nullable Hint hint) {
    }

    @Override
    public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable IScope scope, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk, @Nullable IScope scope) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn checkIn, @Nullable IScope scope, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureReplayEvent(@NotNull SentryReplayEvent event, @Nullable IScope scope, @Nullable Hint hint) {
        return SentryId.EMPTY_ID;
    }

    @Override
    public void captureLog(@NotNull SentryLogEvent logEvent, @Nullable IScope scope) {
    }

    @Override
    @ApiStatus.Internal
    public void captureBatchedLogEvents(@NotNull SentryLogEvents logEvents) {
    }

    @Override
    @Nullable
    public RateLimiter getRateLimiter() {
        return null;
    }
}

