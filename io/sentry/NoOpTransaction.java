/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.BaggageHeader;
import io.sentry.Hint;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Instrumenter;
import io.sentry.MeasurementUnit;
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.NoOpSpan;
import io.sentry.SentryDate;
import io.sentry.SentryNanotimeDate;
import io.sentry.SentryTraceHeader;
import io.sentry.Span;
import io.sentry.SpanContext;
import io.sentry.SpanId;
import io.sentry.SpanOptions;
import io.sentry.SpanStatus;
import io.sentry.TraceContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpTransaction
implements ITransaction {
    private static final NoOpTransaction instance = new NoOpTransaction();

    private NoOpTransaction() {
    }

    public static NoOpTransaction getInstance() {
        return instance;
    }

    @Override
    public void setName(@NotNull String name) {
    }

    @Override
    @ApiStatus.Internal
    public void setName(@NotNull String name, @NotNull TransactionNameSource transactionNameSource) {
    }

    @Override
    @NotNull
    public String getName() {
        return "";
    }

    @Override
    @NotNull
    public TransactionNameSource getTransactionNameSource() {
        return TransactionNameSource.CUSTOM;
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
    @Nullable
    public String getDescription() {
        return null;
    }

    @Override
    @NotNull
    public List<Span> getSpans() {
        return Collections.emptyList();
    }

    @Override
    @NotNull
    public ISpan startChild(@NotNull String operation, @Nullable String description, @Nullable SentryDate timestamp) {
        return NoOpSpan.getInstance();
    }

    @Override
    @Nullable
    public ISpan getLatestActiveSpan() {
        return null;
    }

    @Override
    @NotNull
    public SentryId getEventId() {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public ISentryLifecycleToken makeCurrent() {
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Override
    public void scheduleFinish() {
    }

    @Override
    public void forceFinish(@NotNull SpanStatus status, boolean dropIfNoChildren, @Nullable Hint hint) {
    }

    @Override
    public void finish(@Nullable SpanStatus status, @Nullable SentryDate timestamp, boolean dropIfNoChildren, @Nullable Hint hint) {
    }

    @Override
    public boolean isFinished() {
        return true;
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
    @Nullable
    public Boolean isSampled() {
        return null;
    }

    @Override
    @Nullable
    public Boolean isProfileSampled() {
        return null;
    }

    @Override
    @Nullable
    public TracesSamplingDecision getSamplingDecision() {
        return null;
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
    @ApiStatus.Internal
    public void setContext(@Nullable String key, @Nullable Object context) {
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public Contexts getContexts() {
        return new Contexts();
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
    public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
    }
}

