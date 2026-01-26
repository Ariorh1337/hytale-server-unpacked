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
import io.sentry.ISpanFactory;
import io.sentry.ITransaction;
import io.sentry.NoOpSpan;
import io.sentry.NoOpTransaction;
import io.sentry.SpanContext;
import io.sentry.SpanOptions;
import io.sentry.TransactionContext;
import io.sentry.TransactionOptions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class NoOpSpanFactory
implements ISpanFactory {
    private static final NoOpSpanFactory instance = new NoOpSpanFactory();

    private NoOpSpanFactory() {
    }

    public static NoOpSpanFactory getInstance() {
        return instance;
    }

    @Override
    @NotNull
    public ITransaction createTransaction(@NotNull TransactionContext context, @NotNull IScopes scopes, @NotNull TransactionOptions transactionOptions, @Nullable CompositePerformanceCollector compositePerformanceCollector) {
        return NoOpTransaction.getInstance();
    }

    @Override
    @NotNull
    public ISpan createSpan(@NotNull IScopes scopes, @NotNull SpanOptions spanOptions, @NotNull SpanContext spanContext, @Nullable ISpan parentSpan) {
        return NoOpSpan.getInstance();
    }
}

