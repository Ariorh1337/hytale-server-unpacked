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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface IClientReportRecorder {
    public void recordLostEnvelope(@NotNull DiscardReason var1, @Nullable SentryEnvelope var2);

    public void recordLostEnvelopeItem(@NotNull DiscardReason var1, @Nullable SentryEnvelopeItem var2);

    public void recordLostEvent(@NotNull DiscardReason var1, @NotNull DataCategory var2);

    public void recordLostEvent(@NotNull DiscardReason var1, @NotNull DataCategory var2, long var3);

    @NotNull
    public SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope var1);
}

