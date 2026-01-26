/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DirectoryProcessor;
import io.sentry.Hint;
import io.sentry.IEnvelopeReader;
import io.sentry.IEnvelopeSender;
import io.sentry.ILogger;
import io.sentry.IScopes;
import io.sentry.ISerializer;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryEvent;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.TraceContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.hints.Flushable;
import io.sentry.hints.Resettable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.util.CollectionUtils;
import io.sentry.util.HintUtils;
import io.sentry.util.LogUtils;
import io.sentry.util.Objects;
import io.sentry.util.SampleRateUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class OutboxSender
extends DirectoryProcessor
implements IEnvelopeSender {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    @NotNull
    private final IScopes scopes;
    @NotNull
    private final IEnvelopeReader envelopeReader;
    @NotNull
    private final ISerializer serializer;
    @NotNull
    private final ILogger logger;

    public OutboxSender(@NotNull IScopes scopes, @NotNull IEnvelopeReader envelopeReader, @NotNull ISerializer serializer, @NotNull ILogger logger, long flushTimeoutMillis, int maxQueueSize) {
        super(scopes, logger, flushTimeoutMillis, maxQueueSize);
        this.scopes = Objects.requireNonNull(scopes, "Scopes are required.");
        this.envelopeReader = Objects.requireNonNull(envelopeReader, "Envelope reader is required.");
        this.serializer = Objects.requireNonNull(serializer, "Serializer is required.");
        this.logger = Objects.requireNonNull(logger, "Logger is required.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void processFile(@NotNull File file, @NotNull Hint hint) {
        Objects.requireNonNull(file, "File is required.");
        if (!this.isRelevantFileName(file.getName())) {
            this.logger.log(SentryLevel.DEBUG, "File '%s' should be ignored.", file.getAbsolutePath());
            return;
        }
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));){
            SentryEnvelope envelope = this.envelopeReader.read(stream);
            if (envelope == null) {
                this.logger.log(SentryLevel.ERROR, "Stream from path %s resulted in a null envelope.", file.getAbsolutePath());
            } else {
                this.processEnvelope(envelope, hint);
                this.logger.log(SentryLevel.DEBUG, "File '%s' is done.", file.getAbsolutePath());
            }
        }
        catch (IOException e) {
            this.logger.log(SentryLevel.ERROR, "Error processing envelope.", e);
        }
        finally {
            HintUtils.runIfHasTypeLogIfNot(hint, Retryable.class, this.logger, retryable -> {
                if (!retryable.isRetry()) {
                    try {
                        if (!file.delete()) {
                            this.logger.log(SentryLevel.ERROR, "Failed to delete: %s", file.getAbsolutePath());
                        }
                    }
                    catch (RuntimeException e) {
                        this.logger.log(SentryLevel.ERROR, e, "Failed to delete: %s", file.getAbsolutePath());
                    }
                }
            });
        }
    }

    @Override
    protected boolean isRelevantFileName(@Nullable String fileName) {
        return fileName != null && !fileName.startsWith("session") && !fileName.startsWith("previous_session") && !fileName.startsWith("startup_crash");
    }

    @Override
    public void processEnvelopeFile(@NotNull String path, @NotNull Hint hint) {
        Objects.requireNonNull(path, "Path is required.");
        this.processFile(new File(path), hint);
    }

    private void processEnvelope(@NotNull SentryEnvelope envelope, @NotNull Hint hint) throws IOException {
        this.logger.log(SentryLevel.DEBUG, "Processing Envelope with %d item(s)", CollectionUtils.size(envelope.getItems()));
        int currentItem = 0;
        for (SentryEnvelopeItem item : envelope.getItems()) {
            block32: {
                ++currentItem;
                if (item.getHeader() == null) {
                    this.logger.log(SentryLevel.ERROR, "Item %d has no header", currentItem);
                    continue;
                }
                if (SentryItemType.Event.equals(item.getHeader().getType())) {
                    try {
                        BufferedReader eventReader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(item.getData()), UTF_8));
                        try {
                            SentryEvent event = this.serializer.deserialize(eventReader, SentryEvent.class);
                            if (event == null) {
                                this.logEnvelopeItemNull(item, currentItem);
                            } else {
                                if (event.getSdk() != null) {
                                    HintUtils.setIsFromHybridSdk(hint, event.getSdk().getName());
                                }
                                if (envelope.getHeader().getEventId() != null && !envelope.getHeader().getEventId().equals(event.getEventId())) {
                                    this.logUnexpectedEventId(envelope, event.getEventId(), currentItem);
                                    continue;
                                }
                                this.scopes.captureEvent(event, hint);
                                this.logItemCaptured(currentItem);
                                if (!this.waitFlush(hint)) {
                                    this.logTimeout(event.getEventId());
                                    break;
                                }
                            }
                            break block32;
                        }
                        finally {
                            ((Reader)eventReader).close();
                            continue;
                        }
                    }
                    catch (Throwable e) {
                        this.logger.log(SentryLevel.ERROR, "Item failed to process.", e);
                        break block32;
                    }
                }
                if (SentryItemType.Transaction.equals(item.getHeader().getType())) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(item.getData()), UTF_8));
                        try {
                            SentryTransaction transaction = this.serializer.deserialize(reader, SentryTransaction.class);
                            if (transaction == null) {
                                this.logEnvelopeItemNull(item, currentItem);
                            } else {
                                if (envelope.getHeader().getEventId() != null && !envelope.getHeader().getEventId().equals(transaction.getEventId())) {
                                    this.logUnexpectedEventId(envelope, transaction.getEventId(), currentItem);
                                    continue;
                                }
                                @Nullable TraceContext traceContext = envelope.getHeader().getTraceContext();
                                if (transaction.getContexts().getTrace() != null) {
                                    transaction.getContexts().getTrace().setSamplingDecision(this.extractSamplingDecision(traceContext));
                                }
                                this.scopes.captureTransaction(transaction, traceContext, hint);
                                this.logItemCaptured(currentItem);
                                if (!this.waitFlush(hint)) {
                                    this.logTimeout(transaction.getEventId());
                                    break;
                                }
                            }
                            break block32;
                        }
                        finally {
                            ((Reader)reader).close();
                            continue;
                        }
                    }
                    catch (Throwable e) {
                        this.logger.log(SentryLevel.ERROR, "Item failed to process.", e);
                        break block32;
                    }
                }
                SentryEnvelope newEnvelope = new SentryEnvelope(envelope.getHeader().getEventId(), envelope.getHeader().getSdkVersion(), item);
                this.scopes.captureEnvelope(newEnvelope, hint);
                this.logger.log(SentryLevel.DEBUG, "%s item %d is being captured.", item.getHeader().getType().getItemType(), currentItem);
                if (!this.waitFlush(hint)) {
                    this.logger.log(SentryLevel.WARNING, "Timed out waiting for item type submission: %s", item.getHeader().getType().getItemType());
                    break;
                }
            }
            Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
            if (sentrySdkHint instanceof SubmissionResult && !((SubmissionResult)sentrySdkHint).isSuccess()) {
                this.logger.log(SentryLevel.WARNING, "Envelope had a failed capture at item %d. No more items will be sent.", currentItem);
                break;
            }
            HintUtils.runIfHasType(hint, Resettable.class, resettable -> resettable.reset());
        }
    }

    @NotNull
    private TracesSamplingDecision extractSamplingDecision(@Nullable TraceContext traceContext) {
        String sampleRateString;
        if (traceContext != null && (sampleRateString = traceContext.getSampleRate()) != null) {
            try {
                Double sampleRate = Double.parseDouble(sampleRateString);
                if (SampleRateUtils.isValidTracesSampleRate(sampleRate, false)) {
                    Double sampleRand;
                    @Nullable String sampleRandString = traceContext.getSampleRand();
                    if (sampleRandString != null && SampleRateUtils.isValidTracesSampleRate(sampleRand = Double.valueOf(Double.parseDouble(sampleRandString)), false)) {
                        return new TracesSamplingDecision(true, sampleRate, sampleRand);
                    }
                    return SampleRateUtils.backfilledSampleRand(new TracesSamplingDecision(true, sampleRate));
                }
                this.logger.log(SentryLevel.ERROR, "Invalid sample rate parsed from TraceContext: %s", sampleRateString);
            }
            catch (Exception e) {
                this.logger.log(SentryLevel.ERROR, "Unable to parse sample rate from TraceContext: %s", sampleRateString);
            }
        }
        return new TracesSamplingDecision(true);
    }

    private void logEnvelopeItemNull(@NotNull SentryEnvelopeItem item, int itemIndex) {
        this.logger.log(SentryLevel.ERROR, "Item %d of type %s returned null by the parser.", itemIndex, item.getHeader().getType());
    }

    private void logUnexpectedEventId(@NotNull SentryEnvelope envelope, @Nullable SentryId eventId, int itemIndex) {
        this.logger.log(SentryLevel.ERROR, "Item %d of has a different event id (%s) to the envelope header (%s)", itemIndex, envelope.getHeader().getEventId(), eventId);
    }

    private void logItemCaptured(int itemIndex) {
        this.logger.log(SentryLevel.DEBUG, "Item %d is being captured.", itemIndex);
    }

    private void logTimeout(@Nullable SentryId eventId) {
        this.logger.log(SentryLevel.WARNING, "Timed out waiting for event id submission: %s", eventId);
    }

    private boolean waitFlush(@NotNull Hint hint) {
        @Nullable Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
        if (sentrySdkHint instanceof Flushable) {
            return ((Flushable)sentrySdkHint).waitFlush();
        }
        LogUtils.logNotInstanceOf(Flushable.class, sentrySdkHint, this.logger);
        return true;
    }
}

