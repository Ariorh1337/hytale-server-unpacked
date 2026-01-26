/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache;

import io.sentry.ISerializer;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.clientreport.DiscardReason;
import io.sentry.util.LazyEvaluator;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class CacheStrategy {
    protected static final Charset UTF_8 = Charset.forName("UTF-8");
    @NotNull
    protected SentryOptions options;
    @NotNull
    protected final LazyEvaluator<ISerializer> serializer = new LazyEvaluator<ISerializer>(() -> this.options.getSerializer());
    @NotNull
    protected final File directory;
    private final int maxSize;

    CacheStrategy(@NotNull SentryOptions options, @NotNull String directoryPath, int maxSize) {
        Objects.requireNonNull(directoryPath, "Directory is required.");
        this.options = Objects.requireNonNull(options, "SentryOptions is required.");
        this.directory = new File(directoryPath);
        this.maxSize = maxSize;
    }

    protected boolean isDirectoryValid() {
        if (!(this.directory.isDirectory() && this.directory.canWrite() && this.directory.canRead())) {
            this.options.getLogger().log(SentryLevel.ERROR, "The directory for caching files is inaccessible.: %s", this.directory.getAbsolutePath());
            return false;
        }
        return true;
    }

    private void sortFilesOldestToNewest(@NotNull File[] files) {
        if (files.length > 1) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
        }
    }

    protected void rotateCacheIfNeeded(@NotNull File[] files) {
        int length = files.length;
        if (length >= this.maxSize) {
            this.options.getLogger().log(SentryLevel.WARNING, "Cache folder if full (respecting maxSize). Rotating files", new Object[0]);
            int totalToBeDeleted = length - this.maxSize + 1;
            this.sortFilesOldestToNewest(files);
            File[] notDeletedFiles = Arrays.copyOfRange(files, totalToBeDeleted, length);
            for (int i = 0; i < totalToBeDeleted; ++i) {
                File file = files[i];
                this.moveInitFlagIfNecessary(file, notDeletedFiles);
                if (file.delete()) continue;
                this.options.getLogger().log(SentryLevel.WARNING, "File can't be deleted: %s", file.getAbsolutePath());
            }
        }
    }

    private void moveInitFlagIfNecessary(@NotNull File currentFile, @NotNull File[] notDeletedFiles) {
        SentryEnvelope currentEnvelope = this.readEnvelope(currentFile);
        if (currentEnvelope == null || !this.isValidEnvelope(currentEnvelope)) {
            return;
        }
        this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.CACHE_OVERFLOW, currentEnvelope);
        Session currentSession = this.getFirstSession(currentEnvelope);
        if (currentSession == null || !this.isValidSession(currentSession)) {
            return;
        }
        Boolean currentSessionInit = currentSession.getInit();
        if (currentSessionInit == null || !currentSessionInit.booleanValue()) {
            return;
        }
        for (File notDeletedFile : notDeletedFiles) {
            SentryEnvelope envelope = this.readEnvelope(notDeletedFile);
            if (envelope == null || !this.isValidEnvelope(envelope)) continue;
            SentryEnvelopeItem newSessionItem = null;
            Iterator<SentryEnvelopeItem> itemsIterator = envelope.getItems().iterator();
            while (itemsIterator.hasNext()) {
                Session session;
                SentryEnvelopeItem envelopeItem = itemsIterator.next();
                if (!this.isSessionType(envelopeItem) || (session = this.readSession(envelopeItem)) == null || !this.isValidSession(session)) continue;
                Boolean init = session.getInit();
                if (init != null && init.booleanValue()) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Session %s has 2 times the init flag.", currentSession.getSessionId());
                    return;
                }
                if (currentSession.getSessionId() == null || !currentSession.getSessionId().equals(session.getSessionId())) continue;
                session.setInitAsTrue();
                try {
                    newSessionItem = SentryEnvelopeItem.fromSession(this.serializer.getValue(), session);
                    itemsIterator.remove();
                }
                catch (IOException e) {
                    this.options.getLogger().log(SentryLevel.ERROR, e, "Failed to create new envelope item for the session %s", currentSession.getSessionId());
                }
                break;
            }
            if (newSessionItem == null) continue;
            SentryEnvelope newEnvelope = this.buildNewEnvelope(envelope, newSessionItem);
            long notDeletedFileTimestamp = notDeletedFile.lastModified();
            if (!notDeletedFile.delete()) {
                this.options.getLogger().log(SentryLevel.WARNING, "File can't be deleted: %s", notDeletedFile.getAbsolutePath());
            }
            this.saveNewEnvelope(newEnvelope, notDeletedFile, notDeletedFileTimestamp);
            break;
        }
    }

    @Nullable
    private SentryEnvelope readEnvelope(@NotNull File file) {
        SentryEnvelope sentryEnvelope;
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            sentryEnvelope = this.serializer.getValue().deserializeEnvelope(inputStream);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)inputStream).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                this.options.getLogger().log(SentryLevel.ERROR, "Failed to deserialize the envelope.", e);
                return null;
            }
        }
        ((InputStream)inputStream).close();
        return sentryEnvelope;
    }

    @Nullable
    private Session getFirstSession(@NotNull SentryEnvelope envelope) {
        for (SentryEnvelopeItem item : envelope.getItems()) {
            if (!this.isSessionType(item)) continue;
            return this.readSession(item);
        }
        return null;
    }

    private boolean isValidSession(@NotNull Session session) {
        if (!session.getStatus().equals((Object)Session.State.Ok)) {
            return false;
        }
        String sessionId = session.getSessionId();
        return sessionId != null;
    }

    private boolean isSessionType(@Nullable SentryEnvelopeItem item) {
        if (item == null) {
            return false;
        }
        return item.getHeader().getType().equals(SentryItemType.Session);
    }

    @Nullable
    private Session readSession(@NotNull SentryEnvelopeItem item) {
        Session session;
        BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(item.getData()), UTF_8));
        try {
            session = this.serializer.getValue().deserialize(reader, Session.class);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((Reader)reader).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "Failed to deserialize the session.", e);
                return null;
            }
        }
        ((Reader)reader).close();
        return session;
    }

    private void saveNewEnvelope(@NotNull SentryEnvelope envelope, @NotNull File file, long timestamp) {
        try (FileOutputStream outputStream = new FileOutputStream(file);){
            this.serializer.getValue().serialize(envelope, outputStream);
            file.setLastModified(timestamp);
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to serialize the new envelope to the disk.", e);
        }
    }

    @NotNull
    private SentryEnvelope buildNewEnvelope(@NotNull SentryEnvelope envelope, @NotNull SentryEnvelopeItem sessionItem) {
        ArrayList<SentryEnvelopeItem> newEnvelopeItems = new ArrayList<SentryEnvelopeItem>();
        for (SentryEnvelopeItem newEnvelopeItem : envelope.getItems()) {
            newEnvelopeItems.add(newEnvelopeItem);
        }
        newEnvelopeItems.add(sessionItem);
        return new SentryEnvelope(envelope.getHeader(), newEnvelopeItems);
    }

    private boolean isValidEnvelope(@NotNull SentryEnvelope envelope) {
        return envelope.getItems().iterator().hasNext();
    }
}

