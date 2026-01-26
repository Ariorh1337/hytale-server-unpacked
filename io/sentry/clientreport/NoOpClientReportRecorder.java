/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.clientreport.DiscardReason;
import io.sentry.clientreport.IClientReportRecorder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class NoOpClientReportRecorder
implements IClientReportRecorder {
    @Override
    public void recordLostEnvelope(@NotNull DiscardReason reason, @Nullable SentryEnvelope envelope) {
    }

    @Override
    public void recordLostEnvelopeItem(@NotNull DiscardReason reason, @Nullable SentryEnvelopeItem envelopeItem) {
    }

    @Override
    public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category) {
    }

    @Override
    public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category, long count) {
    }

    @Override
    @NotNull
    public SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope envelope) {
        return envelope;
    }
}

