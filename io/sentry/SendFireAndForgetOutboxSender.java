/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.IScopes;
import io.sentry.OutboxSender;
import io.sentry.SendCachedEnvelopeFireAndForgetIntegration;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SendFireAndForgetOutboxSender
implements SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetFactory {
    @NotNull
    private final SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath;

    public SendFireAndForgetOutboxSender(@NotNull SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath) {
        this.sendFireAndForgetDirPath = Objects.requireNonNull(sendFireAndForgetDirPath, "SendFireAndForgetDirPath is required");
    }

    @Override
    @Nullable
    public SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForget create(@NotNull IScopes scopes, @NotNull SentryOptions options) {
        Objects.requireNonNull(scopes, "Scopes are required");
        Objects.requireNonNull(options, "SentryOptions is required");
        String dirPath = this.sendFireAndForgetDirPath.getDirPath();
        if (dirPath == null || !this.hasValidPath(dirPath, options.getLogger())) {
            options.getLogger().log(SentryLevel.ERROR, "No outbox dir path is defined in options.", new Object[0]);
            return null;
        }
        OutboxSender outboxSender = new OutboxSender(scopes, options.getEnvelopeReader(), options.getSerializer(), options.getLogger(), options.getFlushTimeoutMillis(), options.getMaxQueueSize());
        return this.processDir(outboxSender, dirPath, options.getLogger());
    }
}

