/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.BaggageHeader;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.Instrumenter;
import io.sentry.MeasurementUnit;
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.SentryDate;
import io.sentry.SentryNanotimeDate;
import io.sentry.SentryTraceHeader;
import io.sentry.SpanContext;
import io.sentry.SpanId;
import io.sentry.SpanOptions;
import io.sentry.SpanStatus;
import io.sentry.TraceContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpSpan
implements ISpan {
    private static final NoOpSpan instance = new NoOpSpan();

    private NoOpSpan() {
    }

    public static NoOpSpan getInstance() {
        return instance;
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation) {
        return NoOpSpan.getInstance();
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @NotNull SpanOptions spanOptions) {
        return NoOpSpan.getInstance();
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull SpanContext spanContext, @NotNull SpanOptions spanOptions) {
        return NoOpSpan.getInstance();
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter) {
        return NoOpSpan.getInstance();
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp, @NotNull Instrumenter instrumenter, @NotNull SpanOptions spanOptions) {
        return NoOpSpan.getInstance();
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description) {
        return NoOpSpan.getInstance();
    }

    @Override
    @NotNull
    public SentryTraceHeader toSentryTrace() {
        return new SentryTraceHeader(SentryId.EMPTY_ID, SpanId.EMPTY_ID, false);
    }

    @Override
    @NotNull
    public TraceContext traceContext() {
        return new TraceContext(SentryId.EMPTY_ID, "");
    }

    @Override
    @Nullable
    public BaggageHeader toBaggageHeader(@Nullable List<String> thirdPartyBaggageHeaders) {
        return null;
    }

    @Override
    public void finish() {
    }

    @Override
    public void finish(@Nullable SpanStatus status) {
    }

    @Override
    public void finish(@Nullable SpanStatus status, @Nullable SentryDate timestamp) {
    }

    @Override
    public void setOperation(@NotNull String operation) {
    }

    @Override
    @NotNull
    public String getOperation() {
        return "";
    }

    @Override
    public void setDescription(@Nullable String description) {
    }

    @Override
    @Nullable
    public String getDescription() {
        return null;
    }

    @Override
    public void setStatus(@Nullable SpanStatus status) {
    }

    @Override
    @Nullable
    public SpanStatus getStatus() {
        return null;
    }

    @Override
    public void setThrowable(@Nullable Throwable throwable) {
    }

    @Override
    @Nullable
    public Throwable getThrowable() {
        return null;
    }

    @Override
    @NotNull
    public SpanContext getSpanContext() {
        return new SpanContext(SentryId.EMPTY_ID, SpanId.EMPTY_ID, "op", null, null);
    }

    @Override
    public void setTag(@Nullable String key, @Nullable String value) {
    }

    @Override
    @Nullable
    public String getTag(@Nullable String key) {
        return null;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void setData(@Nullable String key, @Nullable Object value) {
    }

    @Override
    @Nullable
    public Object getData(@Nullable String key) {
        return null;
    }

    @Override
    public void setMeasurement(@NotNull String name, @NotNull Number value) {
    }

    @Override
    public void setMeasurement(@NotNull String name, @NotNull Number value, @NotNull MeasurementUnit unit) {
    }

    @Override
    public boolean updateEndDate(@NotNull SentryDate date) {
        return false;
    }

    @Override
    @NotNull
    public SentryDate getStartDate() {
        return new SentryNanotimeDate();
    }

    @Override
    @NotNull
    public SentryDate getFinishDate() {
        return new SentryNanotimeDate();
    }

    @Override
    public boolean isNoOp() {
        return true;
    }

    @Override
    public void setContext(@Nullable String key, @Nullable Object context) {
    }

    @Override
    @NotNull
    public Contexts getContexts() {
        return new Contexts();
    }

    @Override
    @Nullable
    public Boolean isSampled() {
        return null;
    }

    @Override
    @Nullable
    public TracesSamplingDecision getSamplingDecision() {
        return null;
    }

    @Override
    @NotNull
    public ISentryLifecycleToken makeCurrent() {
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Override
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
    }
}

