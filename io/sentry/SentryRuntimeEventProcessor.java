/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryEvent;
import io.sentry.protocol.SentryRuntime;
import io.sentry.protocol.SentryTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SentryRuntimeEventProcessor
implements EventProcessor {
    @Nullable
    private final String javaVersion;
    @Nullable
    private final String javaVendor;

    public SentryRuntimeEventProcessor(@Nullable String javaVersion, @Nullable String javaVendor) {
        this.javaVersion = javaVersion;
        this.javaVendor = javaVendor;
    }

    public SentryRuntimeEventProcessor() {
        this(System.getProperty("java.version"), System.getProperty("java.vendor"));
    }

    @Override
    @NotNull
    public SentryEvent process(@NotNull SentryEvent event, @Nullable Hint hint) {
        return this.process(event);
    }

    @Override
    @NotNull
    public SentryTransaction process(@NotNull SentryTransaction transaction, @Nullable Hint hint) {
        return this.process(transaction);
    }

    @NotNull
    private <T extends SentryBaseEvent> T process(@NotNull T event) {
        SentryRuntime runtime;
        if (event.getContexts().getRuntime() == null) {
            event.getContexts().setRuntime(new SentryRuntime());
        }
        if ((runtime = event.getContexts().getRuntime()) != null && runtime.getName() == null && runtime.getVersion() == null) {
            runtime.setName(this.javaVendor);
            runtime.setVersion(this.javaVersion);
        }
        return event;
    }

    @Override
    @Nullable
    public Long getOrder() {
        return 2000L;
    }
}

