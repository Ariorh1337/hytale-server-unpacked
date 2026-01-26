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
import io.sentry.DateUtils;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEvents;
import io.sentry.SentryOptions;
import io.sentry.clientreport.AtomicClientReportStorage;
import io.sentry.clientreport.ClientReport;
import io.sentry.clientreport.ClientReportKey;
import io.sentry.clientreport.DiscardReason;
import io.sentry.clientreport.DiscardedEvent;
import io.sentry.clientreport.IClientReportRecorder;
import io.sentry.clientreport.IClientReportStorage;
import io.sentry.protocol.SentrySpan;
import io.sentry.protocol.SentryTransaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ClientReportRecorder
implements IClientReportRecorder {
    @NotNull
    private final IClientReportStorage storage;
    @NotNull
    private final SentryOptions options;

    public ClientReportRecorder(@NotNull SentryOptions options) {
        this.options = options;
        this.storage = new AtomicClientReportStorage();
    }

    @Override
    @NotNull
    public SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope envelope) {
        @Nullable ClientReport clientReport = this.resetCountsAndGenerateClientReport();
        if (clientReport == null) {
            return envelope;
        }
        try {
            this.options.getLogger().log(SentryLevel.DEBUG, "Attaching client report to envelope.", new Object[0]);
            ArrayList<SentryEnvelopeItem> items = new ArrayList<SentryEnvelopeItem>();
            for (SentryEnvelopeItem item : envelope.getItems()) {
                items.add(item);
            }
            items.add(SentryEnvelopeItem.fromClientReport(this.options.getSerializer(), clientReport));
            return new SentryEnvelope(envelope.getHeader(), items);
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "Unable to attach client report to envelope.", new Object[0]);
            return envelope;
        }
    }

    @Override
    public void recordLostEnvelope(@NotNull DiscardReason reason, @Nullable SentryEnvelope envelope) {
        if (envelope == null) {
            return;
        }
        try {
            for (SentryEnvelopeItem item : envelope.getItems()) {
                this.recordLostEnvelopeItem(reason, item);
            }
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "Unable to record lost envelope.", new Object[0]);
        }
    }

    @Override
    public void recordLostEnvelopeItem(@NotNull DiscardReason reason, @Nullable SentryEnvelopeItem envelopeItem) {
        if (envelopeItem == null) {
            return;
        }
        try {
            @NotNull SentryItemType itemType = envelopeItem.getHeader().getType();
            if (SentryItemType.ClientReport.equals(itemType)) {
                try {
                    ClientReport clientReport = envelopeItem.getClientReport(this.options.getSerializer());
                    this.restoreCountsFromClientReport(clientReport);
                }
                catch (Exception e) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Unable to restore counts from previous client report.", new Object[0]);
                }
            } else {
                @NotNull DataCategory itemCategory = this.categoryFromItemType(itemType);
                if (itemCategory.equals((Object)DataCategory.Transaction)) {
                    @Nullable SentryTransaction transaction = envelopeItem.getTransaction(this.options.getSerializer());
                    if (transaction != null) {
                        @NotNull List<SentrySpan> spans = transaction.getSpans();
                        this.recordLostEventInternal(reason.getReason(), DataCategory.Span.getCategory(), (long)spans.size() + 1L);
                        this.executeOnDiscard(reason, DataCategory.Span, (long)spans.size() + 1L);
                    }
                    this.recordLostEventInternal(reason.getReason(), itemCategory.getCategory(), 1L);
                    this.executeOnDiscard(reason, itemCategory, 1L);
                } else if (itemCategory.equals((Object)DataCategory.LogItem)) {
                    @Nullable SentryLogEvents logs = envelopeItem.getLogs(this.options.getSerializer());
                    if (logs != null) {
                        @NotNull List<SentryLogEvent> items = logs.getItems();
                        long count = items.size();
                        this.recordLostEventInternal(reason.getReason(), itemCategory.getCategory(), count);
                        long logBytes = envelopeItem.getData().length;
                        this.recordLostEventInternal(reason.getReason(), DataCategory.LogByte.getCategory(), logBytes);
                        this.executeOnDiscard(reason, itemCategory, count);
                    } else {
                        this.options.getLogger().log(SentryLevel.ERROR, "Unable to parse lost logs envelope item.", new Object[0]);
                    }
                } else {
                    this.recordLostEventInternal(reason.getReason(), itemCategory.getCategory(), 1L);
                    this.executeOnDiscard(reason, itemCategory, 1L);
                }
            }
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "Unable to record lost envelope item.", new Object[0]);
        }
    }

    @Override
    public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category) {
        this.recordLostEvent(reason, category, 1L);
    }

    @Override
    public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category, long count) {
        try {
            this.recordLostEventInternal(reason.getReason(), category.getCategory(), count);
            this.executeOnDiscard(reason, category, count);
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "Unable to record lost event.", new Object[0]);
        }
    }

    private void executeOnDiscard(@NotNull DiscardReason reason, @NotNull DataCategory category, @NotNull Long countToAdd) {
        if (this.options.getOnDiscard() != null) {
            try {
                this.options.getOnDiscard().execute(reason, category, countToAdd);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The onDiscard callback threw an exception.", e);
            }
        }
    }

    private void recordLostEventInternal(@NotNull String reason, @NotNull String category, @NotNull Long countToAdd) {
        ClientReportKey key = new ClientReportKey(reason, category);
        this.storage.addCount(key, countToAdd);
    }

    @Nullable
    ClientReport resetCountsAndGenerateClientReport() {
        Date currentDate = DateUtils.getCurrentDateTime();
        List<DiscardedEvent> discardedEvents = this.storage.resetCountsAndGet();
        if (discardedEvents.isEmpty()) {
            return null;
        }
        return new ClientReport(currentDate, discardedEvents);
    }

    private void restoreCountsFromClientReport(@Nullable ClientReport clientReport) {
        if (clientReport == null) {
            return;
        }
        for (DiscardedEvent discardedEvent : clientReport.getDiscardedEvents()) {
            this.recordLostEventInternal(discardedEvent.getReason(), discardedEvent.getCategory(), discardedEvent.getQuantity());
        }
    }

    private DataCategory categoryFromItemType(SentryItemType itemType) {
        if (SentryItemType.Event.equals(itemType)) {
            return DataCategory.Error;
        }
        if (SentryItemType.Session.equals(itemType)) {
            return DataCategory.Session;
        }
        if (SentryItemType.Transaction.equals(itemType)) {
            return DataCategory.Transaction;
        }
        if (SentryItemType.UserFeedback.equals(itemType)) {
            return DataCategory.UserReport;
        }
        if (SentryItemType.Feedback.equals(itemType)) {
            return DataCategory.Feedback;
        }
        if (SentryItemType.Profile.equals(itemType)) {
            return DataCategory.Profile;
        }
        if (SentryItemType.ProfileChunk.equals(itemType)) {
            return DataCategory.ProfileChunkUi;
        }
        if (SentryItemType.Attachment.equals(itemType)) {
            return DataCategory.Attachment;
        }
        if (SentryItemType.CheckIn.equals(itemType)) {
            return DataCategory.Monitor;
        }
        if (SentryItemType.ReplayVideo.equals(itemType)) {
            return DataCategory.Replay;
        }
        if (SentryItemType.Log.equals(itemType)) {
            return DataCategory.LogItem;
        }
        if (SentryItemType.Span.equals(itemType)) {
            return DataCategory.Span;
        }
        if (SentryItemType.TraceMetric.equals(itemType)) {
            return DataCategory.TraceMetric;
        }
        return DataCategory.Default;
    }
}

