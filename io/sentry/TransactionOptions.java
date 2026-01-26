/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.CustomSamplingContext;
import io.sentry.ISpanFactory;
import io.sentry.ScopeBindingMode;
import io.sentry.SpanOptions;
import io.sentry.TransactionFinishedCallback;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TransactionOptions
extends SpanOptions {
    @ApiStatus.Internal
    public static final long DEFAULT_DEADLINE_TIMEOUT_AUTO_TRANSACTION = 30000L;
    @Nullable
    private CustomSamplingContext customSamplingContext = null;
    private boolean isAppStartTransaction = false;
    private boolean waitForChildren = false;
    @Nullable
    private Long idleTimeout = null;
    @Nullable
    private Long deadlineTimeout = null;
    @Nullable
    private TransactionFinishedCallback transactionFinishedCallback = null;
    @ApiStatus.Internal
    @Nullable
    private ISpanFactory spanFactory = null;

    @Nullable
    public CustomSamplingContext getCustomSamplingContext() {
        return this.customSamplingContext;
    }

    public void setCustomSamplingContext(@Nullable CustomSamplingContext customSamplingContext) {
        this.customSamplingContext = customSamplingContext;
    }

    public boolean isBindToScope() {
        return ScopeBindingMode.ON == this.getScopeBindingMode();
    }

    public void setBindToScope(boolean bindToScope) {
        this.setScopeBindingMode(bindToScope ? ScopeBindingMode.ON : ScopeBindingMode.OFF);
    }

    public boolean isWaitForChildren() {
        return this.waitForChildren;
    }

    public void setWaitForChildren(boolean waitForChildren) {
        this.waitForChildren = waitForChildren;
    }

    @Nullable
    public Long getIdleTimeout() {
        return this.idleTimeout;
    }

    @ApiStatus.Internal
    public void setDeadlineTimeout(@Nullable Long deadlineTimeoutMs) {
        this.deadlineTimeout = deadlineTimeoutMs;
    }

    @ApiStatus.Internal
    @Nullable
    public Long getDeadlineTimeout() {
        return this.deadlineTimeout;
    }

    public void setIdleTimeout(@Nullable Long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @Nullable
    public TransactionFinishedCallback getTransactionFinishedCallback() {
        return this.transactionFinishedCallback;
    }

    public void setTransactionFinishedCallback(@Nullable TransactionFinishedCallback transactionFinishedCallback) {
        this.transactionFinishedCallback = transactionFinishedCallback;
    }

    @ApiStatus.Internal
    public void setAppStartTransaction(boolean appStartTransaction) {
        this.isAppStartTransaction = appStartTransaction;
    }

    @ApiStatus.Internal
    public boolean isAppStartTransaction() {
        return this.isAppStartTransaction;
    }

    @ApiStatus.Internal
    @Nullable
    public ISpanFactory getSpanFactory() {
        return this.spanFactory;
    }

    @ApiStatus.Internal
    public void setSpanFactory(@NotNull ISpanFactory spanFactory) {
        this.spanFactory = spanFactory;
    }
}

