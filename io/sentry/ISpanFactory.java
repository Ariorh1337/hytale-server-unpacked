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
import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.SpanContext;
import io.sentry.SpanOptions;
import io.sentry.TransactionContext;
import io.sentry.TransactionOptions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface ISpanFactory {
    @NotNull
    public ITransaction createTransaction(@NotNull TransactionContext var1, @NotNull IScopes var2, @NotNull TransactionOptions var3, @Nullable CompositePerformanceCollector var4);

    @NotNull
    public ISpan createSpan(@NotNull IScopes var1, @NotNull SpanOptions var2, @NotNull SpanContext var3, @Nullable ISpan var4);
}

