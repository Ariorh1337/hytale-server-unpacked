/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.AsyncHttpTransportFactory;
import io.sentry.Attachment;
import io.sentry.BackfillingEventProcessor;
import io.sentry.Baggage;
import io.sentry.Breadcrumb;
import io.sentry.CheckIn;
import io.sentry.DataCategory;
import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.IScope;
import io.sentry.ISentryClient;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.ITransportFactory;
import io.sentry.NoOpTransportFactory;
import io.sentry.ProfileChunk;
import io.sentry.ProfilingTraceData;
import io.sentry.PropagationContext;
import io.sentry.ReplayRecording;
import io.sentry.RequestDetailsResolver;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeHeader;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryEvent;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEvents;
import io.sentry.SentryOptions;
import io.sentry.SentryReplayEvent;
import io.sentry.Session;
import io.sentry.SpanStatus;
import io.sentry.TraceContext;
import io.sentry.TransactionContext;
import io.sentry.UserFeedback;
import io.sentry.clientreport.DiscardReason;
import io.sentry.exception.SentryEnvelopeException;
import io.sentry.hints.AbnormalExit;
import io.sentry.hints.ApplyScopeData;
import io.sentry.hints.Backfillable;
import io.sentry.hints.Cached;
import io.sentry.hints.DiskFlushNotification;
import io.sentry.hints.TransactionEnd;
import io.sentry.logger.ILoggerBatchProcessor;
import io.sentry.logger.NoOpLoggerBatchProcessor;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.ITransport;
import io.sentry.transport.RateLimiter;
import io.sentry.util.CheckInUtils;
import io.sentry.util.ErrorUtils;
import io.sentry.util.EventSizeLimitingUtils;
import io.sentry.util.ExceptionUtils;
import io.sentry.util.HintUtils;
import io.sentry.util.JsonSerializationUtils;
import io.sentry.util.Objects;
import io.sentry.util.Random;
import io.sentry.util.SentryRandom;
import io.sentry.util.TracingUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public final class SentryClient
implements ISentryClient {
    static final String SENTRY_PROTOCOL_VERSION = "7";
    private boolean enabled;
    @NotNull
    private final SentryOptions options;
    @NotNull
    private final ITransport transport;
    @NotNull
    private final SortBreadcrumbsByDate sortBreadcrumbsByDate = new SortBreadcrumbsByDate();
    @NotNull
    private final ILoggerBatchProcessor loggerBatchProcessor;

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public SentryClient(@NotNull SentryOptions options) {
        this.options = Objects.requireNonNull(options, "SentryOptions is required.");
        this.enabled = true;
        ITransportFactory transportFactory = options.getTransportFactory();
        if (transportFactory instanceof NoOpTransportFactory) {
            transportFactory = new AsyncHttpTransportFactory();
            options.setTransportFactory(transportFactory);
        }
        RequestDetailsResolver requestDetailsResolver = new RequestDetailsResolver(options);
        this.transport = transportFactory.create(options, requestDetailsResolver.resolve());
        this.loggerBatchProcessor = options.getLogs().isEnabled() ? options.getLogs().getLoggerBatchProcessorFactory().create(options, this) : NoOpLoggerBatchProcessor.getInstance();
    }

    private boolean shouldApplyScopeData(@NotNull SentryBaseEvent event, @NotNull Hint hint) {
        if (HintUtils.shouldApplyScopeData(hint)) {
            return true;
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Event was cached so not applying scope: %s", event.getEventId());
        return false;
    }

    private boolean shouldApplyScopeData(@NotNull CheckIn event, @NotNull Hint hint) {
        if (HintUtils.shouldApplyScopeData(hint)) {
            return true;
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Check-in was cached so not applying scope: %s", event.getCheckInId());
        return false;
    }

    @Override
    @NotNull
    public SentryId captureEvent(@NotNull SentryEvent event, @Nullable IScope scope, @Nullable Hint hint) {
        boolean isCached;
        Objects.requireNonNull(event, "SentryEvent is required.");
        if (hint == null) {
            hint = new Hint();
        }
        if (this.shouldApplyScopeData(event, hint)) {
            this.addScopeAttachmentsToHint(scope, hint);
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Capturing event: %s", event.getEventId());
        if (event != null) {
            Throwable eventThrowable = event.getThrowable();
            if (eventThrowable != null && ExceptionUtils.isIgnored(this.options.getIgnoredExceptionsForType(), eventThrowable)) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped as the exception %s is ignored", eventThrowable.getClass());
                this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Error);
                return SentryId.EMPTY_ID;
            }
            if (ErrorUtils.isIgnored(this.options.getIgnoredErrors(), event)) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped as it matched a string/pattern in ignoredErrors", event.getMessage());
                this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Error);
                return SentryId.EMPTY_ID;
            }
        }
        if (this.shouldApplyScopeData(event, hint) && (event = this.applyScope(event, scope, hint)) == null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by applyScope", new Object[0]);
            return SentryId.EMPTY_ID;
        }
        if ((event = this.processEvent(event, hint, this.options.getEventProcessors())) != null && (event = this.executeBeforeSend(event, hint)) == null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by beforeSend", new Object[0]);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Error);
        }
        if (event != null) {
            event = EventSizeLimitingUtils.limitEventSize(event, hint, this.options);
        }
        if (event == null) {
            return SentryId.EMPTY_ID;
        }
        Session sessionBeforeUpdate = scope != null ? scope.withSession(session -> {}) : null;
        Session session2 = null;
        if (event != null) {
            if (sessionBeforeUpdate == null || !sessionBeforeUpdate.isTerminated()) {
                session2 = this.updateSessionData(event, hint, scope);
            }
            if (!this.sample()) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Event %s was dropped due to sampling decision.", event.getEventId());
                this.options.getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Error);
                event = null;
            }
        }
        boolean shouldSendSessionUpdate = this.shouldSendSessionUpdateForDroppedEvent(sessionBeforeUpdate, session2);
        if (event == null && !shouldSendSessionUpdate) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Not sending session update for dropped event as it did not cause the session health to change.", new Object[0]);
            return SentryId.EMPTY_ID;
        }
        SentryId sentryId = SentryId.EMPTY_ID;
        if (event != null && event.getEventId() != null) {
            sentryId = event.getEventId();
        }
        boolean isBackfillable = HintUtils.hasType(hint, Backfillable.class);
        boolean bl = isCached = HintUtils.hasType(hint, Cached.class) && !HintUtils.hasType(hint, ApplyScopeData.class);
        if (event != null && !isBackfillable && !isCached && (event.isErrored() || event.isCrashed())) {
            this.options.getReplayController().captureReplay(event.isCrashed());
        }
        try {
            @Nullable TraceContext traceContext = this.getTraceContext(scope, hint, event);
            boolean shouldSendAttachments = event != null;
            List<Attachment> attachments = shouldSendAttachments ? this.getAttachments(hint) : null;
            @Nullable SentryEnvelope envelope = this.buildEnvelope(event, attachments, session2, traceContext, null);
            hint.clear();
            if (envelope != null) {
                sentryId = this.sendEnvelope(envelope, hint);
            }
        }
        catch (SentryEnvelopeException | IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing event %s failed.", sentryId);
            sentryId = SentryId.EMPTY_ID;
        }
        if (scope != null) {
            this.finalizeTransaction(scope, hint);
        }
        return sentryId;
    }

    private void finalizeTransaction(@NotNull IScope scope, @NotNull Hint hint) {
        @Nullable ITransaction transaction = scope.getTransaction();
        if (transaction != null && HintUtils.hasType(hint, TransactionEnd.class)) {
            Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
            if (sentrySdkHint instanceof DiskFlushNotification) {
                ((DiskFlushNotification)sentrySdkHint).setFlushable(transaction.getEventId());
                transaction.forceFinish(SpanStatus.ABORTED, false, hint);
            } else {
                transaction.forceFinish(SpanStatus.ABORTED, false, null);
            }
        }
    }

    @Override
    @NotNull
    public SentryId captureReplayEvent(@NotNull SentryReplayEvent event, @Nullable IScope scope, @Nullable Hint hint) {
        Objects.requireNonNull(event, "SessionReplay is required.");
        if (hint == null) {
            hint = new Hint();
        }
        if (this.shouldApplyScopeData(event, hint)) {
            this.applyScope(event, scope);
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Capturing session replay: %s", event.getEventId());
        SentryId sentryId = SentryId.EMPTY_ID;
        if (event.getEventId() != null) {
            sentryId = event.getEventId();
        }
        if ((event = this.processReplayEvent(event, hint, this.options.getEventProcessors())) != null && (event = this.executeBeforeSendReplay(event, hint)) == null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by beforeSendReplay", new Object[0]);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Replay);
        }
        if (event == null) {
            return SentryId.EMPTY_ID;
        }
        try {
            @Nullable TraceContext traceContext = this.getTraceContext(scope, hint, event, null);
            boolean cleanupReplayFolder = HintUtils.hasType(hint, Backfillable.class);
            SentryEnvelope envelope = this.buildEnvelope(event, hint.getReplayRecording(), traceContext, cleanupReplayFolder);
            hint.clear();
            this.transport.send(envelope, hint);
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing event %s failed.", sentryId);
            sentryId = SentryId.EMPTY_ID;
        }
        return sentryId;
    }

    private void addScopeAttachmentsToHint(@Nullable IScope scope, @NotNull Hint hint) {
        if (scope != null) {
            hint.addAttachments(scope.getAttachments());
        }
    }

    private boolean shouldSendSessionUpdateForDroppedEvent(@Nullable Session sessionBeforeUpdate, @Nullable Session sessionAfterUpdate) {
        boolean didSessionMoveToErroredState;
        boolean didSessionMoveToCrashedState;
        if (sessionAfterUpdate == null) {
            return false;
        }
        if (sessionBeforeUpdate == null) {
            return true;
        }
        boolean bl = didSessionMoveToCrashedState = sessionAfterUpdate.getStatus() == Session.State.Crashed && sessionBeforeUpdate.getStatus() != Session.State.Crashed;
        if (didSessionMoveToCrashedState) {
            return true;
        }
        boolean bl2 = didSessionMoveToErroredState = sessionAfterUpdate.errorCount() > 0 && sessionBeforeUpdate.errorCount() <= 0;
        return didSessionMoveToErroredState;
    }

    @Nullable
    private List<Attachment> getAttachments(@NotNull Hint hint) {
        Attachment threadDump;
        Attachment viewHierarchy;
        @NotNull List<Attachment> attachments = hint.getAttachments();
        @Nullable Attachment screenshot = hint.getScreenshot();
        if (screenshot != null) {
            attachments.add(screenshot);
        }
        if ((viewHierarchy = hint.getViewHierarchy()) != null) {
            attachments.add(viewHierarchy);
        }
        if ((threadDump = hint.getThreadDump()) != null) {
            attachments.add(threadDump);
        }
        return attachments;
    }

    @Nullable
    private SentryEnvelope buildEnvelope(@Nullable SentryBaseEvent event, @Nullable List<Attachment> attachments, @Nullable Session session, @Nullable TraceContext traceContext, @Nullable ProfilingTraceData profilingTraceData) throws IOException, SentryEnvelopeException {
        SentryId sentryId = null;
        ArrayList<SentryEnvelopeItem> envelopeItems = new ArrayList<SentryEnvelopeItem>();
        if (event != null) {
            SentryEnvelopeItem eventItem = SentryEnvelopeItem.fromEvent(this.options.getSerializer(), event);
            envelopeItems.add(eventItem);
            sentryId = event.getEventId();
        }
        if (session != null) {
            SentryEnvelopeItem sessionItem = SentryEnvelopeItem.fromSession(this.options.getSerializer(), session);
            envelopeItems.add(sessionItem);
        }
        if (profilingTraceData != null) {
            SentryEnvelopeItem profilingTraceItem = SentryEnvelopeItem.fromProfilingTrace(profilingTraceData, this.options.getMaxTraceFileSize(), this.options.getSerializer());
            envelopeItems.add(profilingTraceItem);
            if (sentryId == null) {
                sentryId = new SentryId(profilingTraceData.getProfileId());
            }
        }
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                SentryEnvelopeItem attachmentItem = SentryEnvelopeItem.fromAttachment(this.options.getSerializer(), this.options.getLogger(), attachment, this.options.getMaxAttachmentSize());
                envelopeItems.add(attachmentItem);
            }
        }
        if (!envelopeItems.isEmpty()) {
            SentryEnvelopeHeader envelopeHeader = new SentryEnvelopeHeader(sentryId, this.options.getSdkVersion(), traceContext);
            return new SentryEnvelope(envelopeHeader, envelopeItems);
        }
        return null;
    }

    @Nullable
    private SentryEvent processEvent(@NotNull SentryEvent event, @NotNull Hint hint, @NotNull List<EventProcessor> eventProcessors) {
        for (EventProcessor processor : eventProcessors) {
            try {
                boolean isBackfillingProcessor = processor instanceof BackfillingEventProcessor;
                boolean isBackfillable = HintUtils.hasType(hint, Backfillable.class);
                if (isBackfillable && isBackfillingProcessor) {
                    event = processor.process(event, hint);
                } else if (!isBackfillable && !isBackfillingProcessor) {
                    event = processor.process(event, hint);
                }
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, e, "An exception occurred while processing event by processor: %s", processor.getClass().getName());
            }
            if (event != null) continue;
            this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by a processor: %s", processor.getClass().getName());
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Error);
            break;
        }
        return event;
    }

    @Nullable
    private SentryLogEvent processLogEvent(@NotNull SentryLogEvent event, @NotNull List<EventProcessor> eventProcessors) {
        for (EventProcessor processor : eventProcessors) {
            try {
                event = processor.process(event);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, e, "An exception occurred while processing log event by processor: %s", processor.getClass().getName());
            }
            if (event != null) continue;
            this.options.getLogger().log(SentryLevel.DEBUG, "Log event was dropped by a processor: %s", processor.getClass().getName());
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.LogItem);
            break;
        }
        return event;
    }

    @Nullable
    private SentryTransaction processTransaction(@NotNull SentryTransaction transaction, @NotNull Hint hint, @NotNull List<EventProcessor> eventProcessors) {
        for (EventProcessor processor : eventProcessors) {
            int spanCountAfterProcessor;
            int spanCountBeforeProcessor = transaction.getSpans().size();
            try {
                transaction = processor.process(transaction, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, e, "An exception occurred while processing transaction by processor: %s", processor.getClass().getName());
            }
            int n = spanCountAfterProcessor = transaction == null ? 0 : transaction.getSpans().size();
            if (transaction == null) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by a processor: %s", processor.getClass().getName());
                this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Transaction);
                this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Span, spanCountBeforeProcessor + 1);
                break;
            }
            if (spanCountAfterProcessor >= spanCountBeforeProcessor) continue;
            int droppedSpanCount = spanCountBeforeProcessor - spanCountAfterProcessor;
            this.options.getLogger().log(SentryLevel.DEBUG, "%d spans were dropped by a processor: %s", droppedSpanCount, processor.getClass().getName());
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Span, droppedSpanCount);
        }
        return transaction;
    }

    @Nullable
    private SentryReplayEvent processReplayEvent(@NotNull SentryReplayEvent replayEvent, @NotNull Hint hint, @NotNull List<EventProcessor> eventProcessors) {
        for (EventProcessor processor : eventProcessors) {
            try {
                replayEvent = processor.process(replayEvent, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, e, "An exception occurred while processing replay event by processor: %s", processor.getClass().getName());
            }
            if (replayEvent != null) continue;
            this.options.getLogger().log(SentryLevel.DEBUG, "Replay event was dropped by a processor: %s", processor.getClass().getName());
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Replay);
            break;
        }
        return replayEvent;
    }

    @Nullable
    private SentryEvent processFeedbackEvent(@NotNull SentryEvent feedbackEvent, @NotNull Hint hint, @NotNull List<EventProcessor> eventProcessors) {
        for (EventProcessor processor : eventProcessors) {
            try {
                feedbackEvent = processor.process(feedbackEvent, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, e, "An exception occurred while processing feedback event by processor: %s", processor.getClass().getName());
            }
            if (feedbackEvent != null) continue;
            this.options.getLogger().log(SentryLevel.DEBUG, "Feedback event was dropped by a processor: %s", processor.getClass().getName());
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Feedback);
            break;
        }
        return feedbackEvent;
    }

    @Override
    public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
        Objects.requireNonNull(userFeedback, "SentryEvent is required.");
        if (SentryId.EMPTY_ID.equals(userFeedback.getEventId())) {
            this.options.getLogger().log(SentryLevel.WARNING, "Capturing userFeedback without a Sentry Id.", new Object[0]);
            return;
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Capturing userFeedback: %s", userFeedback.getEventId());
        try {
            @NotNull SentryEnvelope envelope = this.buildEnvelope(userFeedback);
            this.sendEnvelope(envelope, null);
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing user feedback %s failed.", userFeedback.getEventId());
        }
    }

    @NotNull
    private SentryEnvelope buildEnvelope(@NotNull UserFeedback userFeedback) {
        ArrayList<SentryEnvelopeItem> envelopeItems = new ArrayList<SentryEnvelopeItem>();
        SentryEnvelopeItem userFeedbackItem = SentryEnvelopeItem.fromUserFeedback(this.options.getSerializer(), userFeedback);
        envelopeItems.add(userFeedbackItem);
        SentryEnvelopeHeader envelopeHeader = new SentryEnvelopeHeader(userFeedback.getEventId(), this.options.getSdkVersion());
        return new SentryEnvelope(envelopeHeader, envelopeItems);
    }

    @NotNull
    private SentryEnvelope buildEnvelope(@NotNull CheckIn checkIn, @Nullable TraceContext traceContext) {
        ArrayList<SentryEnvelopeItem> envelopeItems = new ArrayList<SentryEnvelopeItem>();
        SentryEnvelopeItem checkInItem = SentryEnvelopeItem.fromCheckIn(this.options.getSerializer(), checkIn);
        envelopeItems.add(checkInItem);
        SentryEnvelopeHeader envelopeHeader = new SentryEnvelopeHeader(checkIn.getCheckInId(), this.options.getSdkVersion(), traceContext);
        return new SentryEnvelope(envelopeHeader, envelopeItems);
    }

    @NotNull
    private SentryEnvelope buildEnvelope(@NotNull SentryLogEvents logEvents) {
        ArrayList<SentryEnvelopeItem> envelopeItems = new ArrayList<SentryEnvelopeItem>();
        SentryEnvelopeItem logItem = SentryEnvelopeItem.fromLogs(this.options.getSerializer(), logEvents);
        envelopeItems.add(logItem);
        SentryEnvelopeHeader envelopeHeader = new SentryEnvelopeHeader(null, this.options.getSdkVersion(), null);
        return new SentryEnvelope(envelopeHeader, envelopeItems);
    }

    @NotNull
    private SentryEnvelope buildEnvelope(@NotNull SentryReplayEvent event, @Nullable ReplayRecording replayRecording, @Nullable TraceContext traceContext, boolean cleanupReplayFolder) {
        ArrayList<SentryEnvelopeItem> envelopeItems = new ArrayList<SentryEnvelopeItem>();
        SentryEnvelopeItem replayItem = SentryEnvelopeItem.fromReplay(this.options.getSerializer(), this.options.getLogger(), event, replayRecording, cleanupReplayFolder);
        envelopeItems.add(replayItem);
        SentryId sentryId = event.getEventId();
        SentryEnvelopeHeader envelopeHeader = new SentryEnvelopeHeader(sentryId, this.options.getSessionReplay().getSdkVersion(), traceContext);
        return new SentryEnvelope(envelopeHeader, envelopeItems);
    }

    @TestOnly
    @Nullable
    Session updateSessionData(@NotNull SentryEvent event, @NotNull Hint hint, @Nullable IScope scope) {
        Session clonedSession = null;
        if (HintUtils.shouldApplyScopeData(hint)) {
            if (scope != null) {
                clonedSession = scope.withSession(session -> {
                    if (session != null) {
                        Session.State status = null;
                        if (event.isCrashed()) {
                            status = Session.State.Crashed;
                        }
                        boolean crashedOrErrored = false;
                        if (Session.State.Crashed == status || event.isErrored()) {
                            crashedOrErrored = true;
                        }
                        String userAgent = null;
                        if (event.getRequest() != null && event.getRequest().getHeaders() != null && event.getRequest().getHeaders().containsKey("user-agent")) {
                            userAgent = event.getRequest().getHeaders().get("user-agent");
                        }
                        Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
                        String abnormalMechanism = null;
                        if (sentrySdkHint instanceof AbnormalExit) {
                            abnormalMechanism = ((AbnormalExit)sentrySdkHint).mechanism();
                            status = Session.State.Abnormal;
                        }
                        if (session.update(status, userAgent, crashedOrErrored, abnormalMechanism) && session.isTerminated()) {
                            session.end();
                        }
                    } else {
                        this.options.getLogger().log(SentryLevel.INFO, "Session is null on scope.withSession", new Object[0]);
                    }
                });
            } else {
                this.options.getLogger().log(SentryLevel.INFO, "Scope is null on client.captureEvent", new Object[0]);
            }
        }
        return clonedSession;
    }

    @Override
    @ApiStatus.Internal
    public void captureSession(@NotNull Session session, @Nullable Hint hint) {
        SentryEnvelope envelope;
        Objects.requireNonNull(session, "Session is required.");
        if (session.getRelease() == null || session.getRelease().isEmpty()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Sessions can't be captured without setting a release.", new Object[0]);
            return;
        }
        try {
            envelope = SentryEnvelope.from(this.options.getSerializer(), session, this.options.getSdkVersion());
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to capture session.", e);
            return;
        }
        this.captureEnvelope(envelope, hint);
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
        Objects.requireNonNull(envelope, "SentryEnvelope is required.");
        if (hint == null) {
            hint = new Hint();
        }
        try {
            hint.clear();
            return this.sendEnvelope(envelope, hint);
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to capture envelope.", e);
            return SentryId.EMPTY_ID;
        }
    }

    @NotNull
    private SentryId sendEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) throws IOException {
        @Nullable SentryOptions.BeforeEnvelopeCallback beforeEnvelopeCallback = this.options.getBeforeEnvelopeCallback();
        if (beforeEnvelopeCallback != null) {
            try {
                beforeEnvelopeCallback.execute(envelope, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The BeforeEnvelope callback threw an exception.", e);
            }
        }
        SentryIntegrationPackageStorage.getInstance().checkForMixedVersions(this.options.getLogger());
        if (hint == null) {
            this.transport.send(envelope);
        } else {
            this.transport.send(envelope, hint);
        }
        @Nullable SentryId id = envelope.getHeader().getEventId();
        return id != null ? id : SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable IScope scope, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData) {
        int spanCountAfterCallback;
        Objects.requireNonNull(transaction, "Transaction is required.");
        if (hint == null) {
            hint = new Hint();
        }
        if (this.shouldApplyScopeData(transaction, hint)) {
            this.addScopeAttachmentsToHint(scope, hint);
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Capturing transaction: %s", transaction.getEventId());
        if (TracingUtils.isIgnored(this.options.getIgnoredTransactions(), transaction.getTransaction())) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped as transaction name %s is ignored", transaction.getTransaction());
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Transaction);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Span, transaction.getSpans().size() + 1);
            return SentryId.EMPTY_ID;
        }
        SentryId sentryId = SentryId.EMPTY_ID;
        if (transaction.getEventId() != null) {
            sentryId = transaction.getEventId();
        }
        if (this.shouldApplyScopeData(transaction, hint)) {
            if ((transaction = this.applyScope(transaction, scope)) != null && scope != null) {
                transaction = this.processTransaction(transaction, hint, scope.getEventProcessors());
            }
            if (transaction == null) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by applyScope", new Object[0]);
            }
        }
        if (transaction != null) {
            transaction = this.processTransaction(transaction, hint, this.options.getEventProcessors());
        }
        if (transaction == null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by Event processors.", new Object[0]);
            return SentryId.EMPTY_ID;
        }
        int spanCountBeforeCallback = transaction.getSpans().size();
        int n = spanCountAfterCallback = (transaction = this.executeBeforeSendTransaction(transaction, hint)) == null ? 0 : transaction.getSpans().size();
        if (transaction == null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Transaction was dropped by beforeSendTransaction.", new Object[0]);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Transaction);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Span, spanCountBeforeCallback + 1);
            return SentryId.EMPTY_ID;
        }
        if (spanCountAfterCallback < spanCountBeforeCallback) {
            int droppedSpanCount = spanCountBeforeCallback - spanCountAfterCallback;
            this.options.getLogger().log(SentryLevel.DEBUG, "%d spans were dropped by beforeSendTransaction.", droppedSpanCount);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Span, droppedSpanCount);
        }
        try {
            SentryEnvelope envelope = this.buildEnvelope(transaction, this.filterForTransaction(this.getAttachments(hint)), null, traceContext, profilingTraceData);
            hint.clear();
            if (envelope != null) {
                sentryId = this.sendEnvelope(envelope, hint);
            }
        }
        catch (SentryEnvelopeException | IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing transaction %s failed.", sentryId);
            sentryId = SentryId.EMPTY_ID;
        }
        return sentryId;
    }

    @Override
    @ApiStatus.Internal
    @NotNull
    public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk, @Nullable IScope scope) {
        Objects.requireNonNull(profileChunk, "profileChunk is required.");
        this.options.getLogger().log(SentryLevel.DEBUG, "Capturing profile chunk: %s", profileChunk.getChunkId());
        @NotNull SentryId sentryId = profileChunk.getChunkId();
        DebugMeta debugMeta = DebugMeta.buildDebugMeta(profileChunk.getDebugMeta(), this.options);
        if (debugMeta != null) {
            profileChunk.setDebugMeta(debugMeta);
        }
        try {
            @NotNull SentryEnvelope envelope = new SentryEnvelope(new SentryEnvelopeHeader(sentryId, this.options.getSdkVersion(), null), Collections.singletonList(SentryEnvelopeItem.fromProfileChunk(profileChunk, this.options.getSerializer(), this.options.getProfilerConverter())));
            sentryId = this.sendEnvelope(envelope, null);
        }
        catch (SentryEnvelopeException | IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing profile chunk %s failed.", sentryId);
            sentryId = SentryId.EMPTY_ID;
        }
        return sentryId;
    }

    @Override
    @NotNull
    public SentryId captureCheckIn(@NotNull CheckIn checkIn, @Nullable IScope scope, @Nullable Hint hint) {
        if (hint == null) {
            hint = new Hint();
        }
        if (checkIn.getEnvironment() == null) {
            checkIn.setEnvironment(this.options.getEnvironment());
        }
        if (checkIn.getRelease() == null) {
            checkIn.setRelease(this.options.getRelease());
        }
        if (this.shouldApplyScopeData(checkIn, hint)) {
            checkIn = this.applyScope(checkIn, scope);
        }
        if (CheckInUtils.isIgnored(this.options.getIgnoredCheckIns(), checkIn.getMonitorSlug())) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Check-in was dropped as slug %s is ignored", checkIn.getMonitorSlug());
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.EVENT_PROCESSOR, DataCategory.Monitor);
            return SentryId.EMPTY_ID;
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Capturing check-in: %s", checkIn.getCheckInId());
        SentryId sentryId = checkIn.getCheckInId();
        try {
            @Nullable TraceContext traceContext = this.getTraceContext(scope, hint, null);
            @NotNull SentryEnvelope envelope = this.buildEnvelope(checkIn, traceContext);
            hint.clear();
            sentryId = this.sendEnvelope(envelope, hint);
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing check-in %s failed.", sentryId);
            sentryId = SentryId.EMPTY_ID;
        }
        return sentryId;
    }

    @Override
    @NotNull
    public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @NotNull IScope scope) {
        SentryEvent event = new SentryEvent();
        event.getContexts().setFeedback(feedback);
        if (hint == null) {
            hint = new Hint();
        }
        if (feedback.getUrl() == null) {
            feedback.setUrl(scope.getScreen());
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Capturing feedback: %s", event.getEventId());
        if (this.shouldApplyScopeData(event, hint) && (event = this.applyFeedbackScope(event, scope, hint)) == null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Feedback was dropped by applyScope", new Object[0]);
            return SentryId.EMPTY_ID;
        }
        if ((event = this.processFeedbackEvent(event, hint, this.options.getEventProcessors())) != null && (event = this.executeBeforeSendFeedback(event, hint)) == null) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Event was dropped by beforeSend", new Object[0]);
            this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.Feedback);
        }
        if (event == null) {
            return SentryId.EMPTY_ID;
        }
        SentryId sentryId = SentryId.EMPTY_ID;
        if (event.getEventId() != null) {
            sentryId = event.getEventId();
        }
        if (feedback.getReplayId() == null) {
            this.options.getReplayController().captureReplay(false);
            @NotNull SentryId replayId = scope.getReplayId();
            if (!replayId.equals(SentryId.EMPTY_ID)) {
                feedback.setReplayId(replayId);
            }
        }
        try {
            @Nullable TraceContext traceContext = this.getTraceContext(scope, hint, event);
            List<Attachment> attachments = this.getAttachments(hint);
            @Nullable SentryEnvelope envelope = this.buildEnvelope(event, attachments, null, traceContext, null);
            hint.clear();
            if (envelope != null) {
                sentryId = this.sendEnvelope(envelope, hint);
            }
        }
        catch (SentryEnvelopeException | IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing feedback %s failed.", sentryId);
            sentryId = SentryId.EMPTY_ID;
        }
        return sentryId;
    }

    @Nullable
    private TraceContext getTraceContext(@Nullable IScope scope, @NotNull Hint hint, @Nullable SentryEvent event) {
        return this.getTraceContext(scope, hint, event, event != null ? event.getTransaction() : null);
    }

    @Nullable
    private TraceContext getTraceContext(@Nullable IScope scope, @NotNull Hint hint, @Nullable SentryBaseEvent event, @Nullable String txn) {
        TraceContext traceContext = null;
        boolean isBackfillable = HintUtils.hasType(hint, Backfillable.class);
        if (isBackfillable) {
            if (event != null) {
                Baggage baggage = Baggage.fromEvent(event, txn, this.options);
                traceContext = baggage.toTraceContext();
            }
        } else if (scope != null) {
            @Nullable ITransaction transaction = scope.getTransaction();
            if (transaction != null) {
                traceContext = transaction.traceContext();
            } else {
                @NotNull PropagationContext propagationContext = TracingUtils.maybeUpdateBaggage(scope, this.options);
                traceContext = propagationContext.traceContext();
            }
        }
        return traceContext;
    }

    @Override
    @ApiStatus.Experimental
    public void captureLog(@Nullable SentryLogEvent logEvent, @Nullable IScope scope) {
        if (logEvent != null && scope != null && (logEvent = this.processLogEvent(logEvent, scope.getEventProcessors())) == null) {
            return;
        }
        if (logEvent != null && (logEvent = this.processLogEvent(logEvent, this.options.getEventProcessors())) == null) {
            return;
        }
        if (logEvent != null) {
            @NotNull SentryLogEvent tmpLogEvent = logEvent;
            if ((logEvent = this.executeBeforeSendLog(logEvent)) == null) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Log Event was dropped by beforeSendLog", new Object[0]);
                this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.LogItem);
                @NotNull long logEventNumberOfBytes = JsonSerializationUtils.byteSizeOf(this.options.getSerializer(), this.options.getLogger(), tmpLogEvent);
                this.options.getClientReportRecorder().recordLostEvent(DiscardReason.BEFORE_SEND, DataCategory.LogByte, logEventNumberOfBytes);
                return;
            }
            this.loggerBatchProcessor.add(logEvent);
        }
    }

    @Override
    @ApiStatus.Internal
    public void captureBatchedLogEvents(@NotNull SentryLogEvents logEvents) {
        try {
            @NotNull SentryEnvelope envelope = this.buildEnvelope(logEvents);
            this.sendEnvelope(envelope, null);
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, e, "Capturing log failed.", new Object[0]);
        }
    }

    @Nullable
    private List<Attachment> filterForTransaction(@Nullable List<Attachment> attachments) {
        if (attachments == null) {
            return null;
        }
        ArrayList<Attachment> attachmentsToSend = new ArrayList<Attachment>();
        for (Attachment attachment : attachments) {
            if (!attachment.isAddToTransactions()) continue;
            attachmentsToSend.add(attachment);
        }
        return attachmentsToSend;
    }

    @Nullable
    private SentryEvent applyScope(@NotNull SentryEvent event, @Nullable IScope scope, @NotNull Hint hint) {
        if (scope != null) {
            FeatureFlags featureFlags;
            this.applyScope(event, scope);
            if (event.getTransaction() == null) {
                event.setTransaction(scope.getTransactionName());
            }
            if (event.getFingerprints() == null) {
                event.setFingerprints(scope.getFingerprint());
            }
            if (scope.getLevel() != null) {
                event.setLevel(scope.getLevel());
            }
            ISpan span = scope.getSpan();
            if (event.getContexts().getTrace() == null) {
                if (span == null) {
                    event.getContexts().setTrace(TransactionContext.fromPropagationContext(scope.getPropagationContext()));
                } else {
                    event.getContexts().setTrace(span.getSpanContext());
                }
            }
            if (event.getContexts().getFeatureFlags() == null && (featureFlags = scope.getFeatureFlags()) != null) {
                event.getContexts().setFeatureFlags(featureFlags);
            }
            event = this.processEvent(event, hint, scope.getEventProcessors());
        }
        return event;
    }

    @Nullable
    private SentryEvent applyFeedbackScope(@NotNull SentryEvent event, @NotNull IScope scope, @NotNull Hint hint) {
        if (event.getUser() == null) {
            event.setUser(scope.getUser());
        }
        if (event.getTags() == null) {
            event.setTags(new HashMap<String, String>(scope.getTags()));
        } else {
            for (Map.Entry<String, String> entry : scope.getTags().entrySet()) {
                if (event.getTags().containsKey(entry.getKey())) continue;
                event.getTags().put(entry.getKey(), entry.getValue());
            }
        }
        Contexts contexts = event.getContexts();
        for (Map.Entry<String, Object> entry : new Contexts(scope.getContexts()).entrySet()) {
            if (contexts.containsKey(entry.getKey())) continue;
            contexts.put(entry.getKey(), entry.getValue());
        }
        ISpan iSpan = scope.getSpan();
        if (event.getContexts().getTrace() == null) {
            if (iSpan == null) {
                event.getContexts().setTrace(TransactionContext.fromPropagationContext(scope.getPropagationContext()));
            } else {
                event.getContexts().setTrace(iSpan.getSpanContext());
            }
        }
        event = this.processFeedbackEvent(event, hint, scope.getEventProcessors());
        return event;
    }

    @NotNull
    private CheckIn applyScope(@NotNull CheckIn checkIn, @Nullable IScope scope) {
        if (scope != null) {
            ISpan span = scope.getSpan();
            if (checkIn.getContexts().getTrace() == null) {
                if (span == null) {
                    checkIn.getContexts().setTrace(TransactionContext.fromPropagationContext(scope.getPropagationContext()));
                } else {
                    checkIn.getContexts().setTrace(span.getSpanContext());
                }
            }
        }
        return checkIn;
    }

    @NotNull
    private SentryReplayEvent applyScope(@NotNull SentryReplayEvent replayEvent, @Nullable IScope scope) {
        if (scope != null) {
            if (replayEvent.getRequest() == null) {
                replayEvent.setRequest(scope.getRequest());
            }
            if (replayEvent.getUser() == null) {
                replayEvent.setUser(scope.getUser());
            }
            if (replayEvent.getTags() == null) {
                replayEvent.setTags(new HashMap<String, String>(scope.getTags()));
            } else {
                for (Map.Entry<String, String> entry : scope.getTags().entrySet()) {
                    if (replayEvent.getTags().containsKey(entry.getKey())) continue;
                    replayEvent.getTags().put(entry.getKey(), entry.getValue());
                }
            }
            Contexts contexts = replayEvent.getContexts();
            for (Map.Entry<String, Object> entry : new Contexts(scope.getContexts()).entrySet()) {
                if (contexts.containsKey(entry.getKey())) continue;
                contexts.put(entry.getKey(), entry.getValue());
            }
            ISpan iSpan = scope.getSpan();
            if (replayEvent.getContexts().getTrace() == null) {
                if (iSpan == null) {
                    replayEvent.getContexts().setTrace(TransactionContext.fromPropagationContext(scope.getPropagationContext()));
                } else {
                    replayEvent.getContexts().setTrace(iSpan.getSpanContext());
                }
            }
        }
        return replayEvent;
    }

    @NotNull
    private <T extends SentryBaseEvent> T applyScope(@NotNull T sentryBaseEvent, @Nullable IScope scope) {
        if (scope != null) {
            if (sentryBaseEvent.getRequest() == null) {
                sentryBaseEvent.setRequest(scope.getRequest());
            }
            if (sentryBaseEvent.getUser() == null) {
                sentryBaseEvent.setUser(scope.getUser());
            }
            if (sentryBaseEvent.getTags() == null) {
                sentryBaseEvent.setTags(new HashMap<String, String>(scope.getTags()));
            } else {
                for (Map.Entry<String, String> entry : scope.getTags().entrySet()) {
                    if (sentryBaseEvent.getTags().containsKey(entry.getKey())) continue;
                    sentryBaseEvent.getTags().put(entry.getKey(), entry.getValue());
                }
            }
            if (sentryBaseEvent.getBreadcrumbs() == null) {
                sentryBaseEvent.setBreadcrumbs(new ArrayList<Breadcrumb>(scope.getBreadcrumbs()));
            } else {
                this.sortBreadcrumbsByDate(sentryBaseEvent, scope.getBreadcrumbs());
            }
            if (sentryBaseEvent.getExtras() == null) {
                sentryBaseEvent.setExtras(new HashMap<String, Object>(scope.getExtras()));
            } else {
                for (Map.Entry<String, Object> entry : scope.getExtras().entrySet()) {
                    if (sentryBaseEvent.getExtras().containsKey(entry.getKey())) continue;
                    sentryBaseEvent.getExtras().put(entry.getKey(), entry.getValue());
                }
            }
            Contexts contexts = sentryBaseEvent.getContexts();
            for (Map.Entry<String, Object> entry : new Contexts(scope.getContexts()).entrySet()) {
                if (contexts.containsKey(entry.getKey())) continue;
                contexts.put(entry.getKey(), entry.getValue());
            }
        }
        return sentryBaseEvent;
    }

    private void sortBreadcrumbsByDate(@NotNull SentryBaseEvent event, @NotNull Collection<Breadcrumb> breadcrumbs) {
        List<Breadcrumb> sortedBreadcrumbs = event.getBreadcrumbs();
        if (sortedBreadcrumbs != null && !breadcrumbs.isEmpty()) {
            sortedBreadcrumbs.addAll(breadcrumbs);
            Collections.sort(sortedBreadcrumbs, this.sortBreadcrumbsByDate);
        }
    }

    @Nullable
    private SentryEvent executeBeforeSend(@NotNull SentryEvent event, @NotNull Hint hint) {
        SentryOptions.BeforeSendCallback beforeSend = this.options.getBeforeSend();
        if (beforeSend != null) {
            try {
                event = beforeSend.execute(event, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The BeforeSend callback threw an exception. It will be added as breadcrumb and continue.", e);
                event = null;
            }
        }
        return event;
    }

    @Nullable
    private SentryTransaction executeBeforeSendTransaction(@NotNull SentryTransaction transaction, @NotNull Hint hint) {
        SentryOptions.BeforeSendTransactionCallback beforeSendTransaction = this.options.getBeforeSendTransaction();
        if (beforeSendTransaction != null) {
            try {
                transaction = beforeSendTransaction.execute(transaction, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The BeforeSendTransaction callback threw an exception. It will be added as breadcrumb and continue.", e);
                transaction = null;
            }
        }
        return transaction;
    }

    @Nullable
    private SentryEvent executeBeforeSendFeedback(@NotNull SentryEvent event, @NotNull Hint hint) {
        SentryOptions.BeforeSendCallback beforeSendFeedback = this.options.getBeforeSendFeedback();
        if (beforeSendFeedback != null) {
            try {
                event = beforeSendFeedback.execute(event, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The BeforeSendFeedback callback threw an exception.", e);
                event = null;
            }
        }
        return event;
    }

    @Nullable
    private SentryReplayEvent executeBeforeSendReplay(@NotNull SentryReplayEvent event, @NotNull Hint hint) {
        SentryOptions.BeforeSendReplayCallback beforeSendReplay = this.options.getBeforeSendReplay();
        if (beforeSendReplay != null) {
            try {
                event = beforeSendReplay.execute(event, hint);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The BeforeSendReplay callback threw an exception. It will be added as breadcrumb and continue.", e);
                event = null;
            }
        }
        return event;
    }

    @Nullable
    private SentryLogEvent executeBeforeSendLog(@NotNull SentryLogEvent event) {
        SentryOptions.Logs.BeforeSendLogCallback beforeSendLog = this.options.getLogs().getBeforeSend();
        if (beforeSendLog != null) {
            try {
                event = beforeSendLog.execute(event);
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "The BeforeSendLog callback threw an exception. Dropping log event.", e);
                event = null;
            }
        }
        return event;
    }

    @Override
    public void close() {
        this.close(false);
    }

    @Override
    public void close(boolean isRestarting) {
        this.options.getLogger().log(SentryLevel.INFO, "Closing SentryClient.", new Object[0]);
        try {
            this.flush(isRestarting ? 0L : this.options.getShutdownTimeoutMillis());
            this.loggerBatchProcessor.close(isRestarting);
            this.transport.close(isRestarting);
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.WARNING, "Failed to close the connection to the Sentry Server.", e);
        }
        for (EventProcessor eventProcessor : this.options.getEventProcessors()) {
            if (!(eventProcessor instanceof Closeable)) continue;
            try {
                ((Closeable)((Object)eventProcessor)).close();
            }
            catch (IOException e) {
                this.options.getLogger().log(SentryLevel.WARNING, "Failed to close the event processor {}.", eventProcessor, e);
            }
        }
        this.enabled = false;
    }

    @Override
    public void flush(long timeoutMillis) {
        this.loggerBatchProcessor.flush(timeoutMillis);
        this.transport.flush(timeoutMillis);
    }

    @Override
    @Nullable
    public RateLimiter getRateLimiter() {
        return this.transport.getRateLimiter();
    }

    @Override
    public boolean isHealthy() {
        return this.transport.isHealthy();
    }

    private boolean sample() {
        Random random;
        Random random2 = random = this.options.getSampleRate() == null ? null : SentryRandom.current();
        if (this.options.getSampleRate() != null && random != null) {
            double sampling = this.options.getSampleRate();
            return !(sampling < random.nextDouble());
        }
        return true;
    }

    private static final class SortBreadcrumbsByDate
    implements Comparator<Breadcrumb> {
        private SortBreadcrumbsByDate() {
        }

        @Override
        public int compare(@NotNull Breadcrumb b1, @NotNull Breadcrumb b2) {
            return b1.getTimestamp().compareTo(b2.getTimestamp());
        }
    }
}

