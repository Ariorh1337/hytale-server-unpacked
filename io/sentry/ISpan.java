/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.BaggageHeader;
import io.sentry.ISentryLifecycleToken;
import io.sentry.Instrumenter;
import io.sentry.MeasurementUnit;
import io.sentry.SentryDate;
import io.sentry.SentryTraceHeader;
import io.sentry.SpanContext;
import io.sentry.SpanOptions;
import io.sentry.SpanStatus;
import io.sentry.TraceContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.protocol.Contexts;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISpan {
    @NotNull
    public ISpan startChild(@NotNull String var1);

    @ApiStatus.Internal
    @NotNull
    public ISpan startChild(@NotNull String var1, @Nullable String var2, @NotNull SpanOptions var3);

    @ApiStatus.Internal
    @NotNull
    public ISpan startChild(@NotNull SpanContext var1, @NotNull SpanOptions var2);

    @ApiStatus.Internal
    @NotNull
    public ISpan startChild(@NotNull String var1, @Nullable String var2, @Nullable SentryDate var3, @NotNull Instrumenter var4);

    @ApiStatus.Internal
    @NotNull
    public ISpan startChild(@NotNull String var1, @Nullable String var2, @Nullable SentryDate var3, @NotNull Instrumenter var4, @NotNull SpanOptions var5);

    @NotNull
    public ISpan startChild(@NotNull String var1, @Nullable String var2);

    @NotNull
    public SentryTraceHeader toSentryTrace();

    @Nullable
    @ApiStatus.Experimental
    public TraceContext traceContext();

    @Nullable
    @ApiStatus.Experimental
    public BaggageHeader toBaggageHeader(@Nullable List<String> var1);

    public void finish();

    public void finish(@Nullable SpanStatus var1);

    public void finish(@Nullable SpanStatus var1, @Nullable SentryDate var2);

    public void setOperation(@NotNull String var1);

    @NotNull
    public String getOperation();

    public void setDescription(@Nullable String var1);

    @Nullable
    public String getDescription();

    public void setStatus(@Nullable SpanStatus var1);

    @Nullable
    public SpanStatus getStatus();

    public void setThrowable(@Nullable Throwable var1);

    @Nullable
    public Throwable getThrowable();

    @NotNull
    public SpanContext getSpanContext();

    public void setTag(@Nullable String var1, @Nullable String var2);

    @Nullable
    public String getTag(@Nullable String var1);

    public boolean isFinished();

    public void setData(@Nullable String var1, @Nullable Object var2);

    @Nullable
    public Object getData(@Nullable String var1);

    public void setMeasurement(@NotNull String var1, @NotNull Number var2);

    public void setMeasurement(@NotNull String var1, @NotNull Number var2, @NotNull MeasurementUnit var3);

    @ApiStatus.Internal
    public boolean updateEndDate(@NotNull SentryDate var1);

    @ApiStatus.Internal
    @NotNull
    public SentryDate getStartDate();

    @ApiStatus.Internal
    @Nullable
    public SentryDate getFinishDate();

    @ApiStatus.Internal
    public boolean isNoOp();

    public void setContext(@Nullable String var1, @Nullable Object var2);

    @NotNull
    public Contexts getContexts();

    @Nullable
    public Boolean isSampled();

    @Nullable
    public TracesSamplingDecision getSamplingDecision();

    @ApiStatus.Internal
    @NotNull
    public ISentryLifecycleToken makeCurrent();

    public void addFeatureFlag(@Nullable String var1, @Nullable Boolean var2);
}

