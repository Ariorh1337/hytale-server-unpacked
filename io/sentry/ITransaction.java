/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.Hint;
import io.sentry.ISpan;
import io.sentry.SentryDate;
import io.sentry.Span;
import io.sentry.SpanStatus;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public interface ITransaction
extends ISpan {
    public void setName(@NotNull String var1);

    @ApiStatus.Internal
    public void setName(@NotNull String var1, @NotNull TransactionNameSource var2);

    @NotNull
    public String getName();

    @NotNull
    public TransactionNameSource getTransactionNameSource();

    @NotNull
    @TestOnly
    public List<Span> getSpans();

    @NotNull
    public ISpan startChild(@NotNull String var1, @Nullable String var2, @Nullable SentryDate var3);

    @Nullable
    public Boolean isProfileSampled();

    @Nullable
    public ISpan getLatestActiveSpan();

    public void scheduleFinish();

    @ApiStatus.Internal
    public void forceFinish(@NotNull SpanStatus var1, boolean var2, @Nullable Hint var3);

    @ApiStatus.Internal
    public void finish(@Nullable SpanStatus var1, @Nullable SentryDate var2, boolean var3, @Nullable Hint var4);

    @NotNull
    public SentryId getEventId();
}

