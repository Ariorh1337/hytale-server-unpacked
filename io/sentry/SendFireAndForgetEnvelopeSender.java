/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.EnvelopeSender;
import io.sentry.IScopes;
import io.sentry.SendCachedEnvelopeFireAndForgetIntegration;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SendFireAndForgetEnvelopeSender
implements SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetFactory {
    @NotNull
    private final SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath;

    public SendFireAndForgetEnvelopeSender(@NotNull SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForgetDirPath sendFireAndForgetDirPath) {
        this.sendFireAndForgetDirPath = Objects.requireNonNull(sendFireAndForgetDirPath, "SendFireAndForgetDirPath is required");
    }

    @Override
    @Nullable
    public SendCachedEnvelopeFireAndForgetIntegration.SendFireAndForget create(@NotNull IScopes scopes, @NotNull SentryOptions options) {
        Objects.requireNonNull(scopes, "Scopes are required");
        Objects.requireNonNull(options, "SentryOptions is required");
        String dirPath = this.sendFireAndForgetDirPath.getDirPath();
        if (dirPath == null || !this.hasValidPath(dirPath, options.getLogger())) {
            options.getLogger().log(SentryLevel.ERROR, "No cache dir path is defined in options.", new Object[0]);
            return null;
        }
        EnvelopeSender envelopeSender = new EnvelopeSender(scopes, options.getSerializer(), options.getLogger(), options.getFlushTimeoutMillis(), options.getMaxQueueSize());
        return this.processDir(envelopeSender, dirPath, options.getLogger());
    }
}

