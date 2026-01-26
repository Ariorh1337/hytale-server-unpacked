/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.cache;

import io.sentry.DateUtils;
import io.sentry.Hint;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISerializer;
import io.sentry.SentryCrashLastRunState;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SentryUUID;
import io.sentry.Session;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.cache.CacheStrategy;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.hints.AbnormalExit;
import io.sentry.hints.SessionEnd;
import io.sentry.hints.SessionStart;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class EnvelopeCache
extends CacheStrategy
implements IEnvelopeCache {
    public static final String SUFFIX_ENVELOPE_FILE = ".envelope";
    public static final String PREFIX_CURRENT_SESSION_FILE = "session";
    public static final String PREFIX_PREVIOUS_SESSION_FILE = "previous_session";
    static final String SUFFIX_SESSION_FILE = ".json";
    public static final String CRASH_MARKER_FILE = "last_crash";
    public static final String NATIVE_CRASH_MARKER_FILE = ".sentry-native/last_crash";
    public static final String STARTUP_CRASH_MARKER_FILE = "startup_crash";
    private final CountDownLatch previousSessionLatch;
    @NotNull
    private final Map<SentryEnvelope, String> fileNameMap = new WeakHashMap<SentryEnvelope, String>();
    @NotNull
    protected final AutoClosableReentrantLock cacheLock = new AutoClosableReentrantLock();
    @NotNull
    protected final AutoClosableReentrantLock sessionLock = new AutoClosableReentrantLock();

    @NotNull
    public static IEnvelopeCache create(@NotNull SentryOptions options) {
        String cacheDirPath = options.getCacheDirPath();
        int maxCacheItems = options.getMaxCacheItems();
        if (cacheDirPath == null) {
            options.getLogger().log(SentryLevel.WARNING, "cacheDirPath is null, returning NoOpEnvelopeCache", new Object[0]);
            return NoOpEnvelopeCache.getInstance();
        }
        return new EnvelopeCache(options, cacheDirPath, maxCacheItems);
    }

    public EnvelopeCache(@NotNull SentryOptions options, @NotNull String cacheDirPath, int maxCacheItems) {
        super(options, cacheDirPath, maxCacheItems);
        this.previousSessionLatch = new CountDownLatch(1);
    }

    @Override
    public void store(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
        this.storeInternal(envelope, hint);
    }

    @Override
    public boolean storeEnvelope(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
        return this.storeInternal(envelope, hint);
    }

    private boolean storeInternal(@NotNull SentryEnvelope envelope, @NotNull Hint hint) {
        File envelopeFile;
        Objects.requireNonNull(envelope, "Envelope is required.");
        this.rotateCacheIfNeeded(this.allEnvelopeFiles());
        File currentSessionFile = EnvelopeCache.getCurrentSessionFile(this.directory.getAbsolutePath());
        File previousSessionFile = EnvelopeCache.getPreviousSessionFile(this.directory.getAbsolutePath());
        if (HintUtils.hasType(hint, SessionEnd.class) && !currentSessionFile.delete()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Current envelope doesn't exist.", new Object[0]);
        }
        if (HintUtils.hasType(hint, AbnormalExit.class)) {
            this.tryEndPreviousSession(hint);
        }
        if (HintUtils.hasType(hint, SessionStart.class)) {
            File javaCrashMarkerFile;
            this.movePreviousSession(currentSessionFile, previousSessionFile);
            this.updateCurrentSession(currentSessionFile, envelope);
            boolean crashedLastRun = false;
            File crashMarkerFile = new File(this.options.getCacheDirPath(), NATIVE_CRASH_MARKER_FILE);
            if (crashMarkerFile.exists()) {
                crashedLastRun = true;
            }
            if (!crashedLastRun && (javaCrashMarkerFile = new File(this.options.getCacheDirPath(), CRASH_MARKER_FILE)).exists()) {
                this.options.getLogger().log(SentryLevel.INFO, "Crash marker file exists, crashedLastRun will return true.", new Object[0]);
                crashedLastRun = true;
                if (!javaCrashMarkerFile.delete()) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete the crash marker file. %s.", javaCrashMarkerFile.getAbsolutePath());
                }
            }
            SentryCrashLastRunState.getInstance().setCrashedLastRun(crashedLastRun);
            this.flushPreviousSession();
        }
        if ((envelopeFile = this.getEnvelopeFile(envelope)).exists()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Not adding Envelope to offline storage because it already exists: %s", envelopeFile.getAbsolutePath());
            return true;
        }
        this.options.getLogger().log(SentryLevel.DEBUG, "Adding Envelope to offline storage: %s", envelopeFile.getAbsolutePath());
        boolean didWriteToDisk = this.writeEnvelopeToDisk(envelopeFile, envelope);
        if (HintUtils.hasType(hint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class)) {
            this.writeCrashMarkerFile();
        }
        return didWriteToDisk;
    }

    private void tryEndPreviousSession(@NotNull Hint hint) {
        Object sdkHint = HintUtils.getSentrySdkHint(hint);
        if (sdkHint instanceof AbnormalExit) {
            File previousSessionFile = EnvelopeCache.getPreviousSessionFile(this.directory.getAbsolutePath());
            if (previousSessionFile.exists()) {
                this.options.getLogger().log(SentryLevel.WARNING, "Previous session is not ended, we'd need to end it.", new Object[0]);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(previousSessionFile), UTF_8));){
                    Session session = ((ISerializer)this.serializer.getValue()).deserialize(reader, Session.class);
                    if (session != null) {
                        AbnormalExit abnormalHint = (AbnormalExit)sdkHint;
                        @Nullable Long abnormalExitTimestamp = abnormalHint.timestamp();
                        Date timestamp = null;
                        if (abnormalExitTimestamp != null) {
                            timestamp = DateUtils.getDateTime(abnormalExitTimestamp);
                            Date sessionStart = session.getStarted();
                            if (sessionStart == null || timestamp.before(sessionStart)) {
                                this.options.getLogger().log(SentryLevel.WARNING, "Abnormal exit happened before previous session start, not ending the session.", new Object[0]);
                                return;
                            }
                        }
                        String abnormalMechanism = abnormalHint.mechanism();
                        session.update(Session.State.Abnormal, null, true, abnormalMechanism);
                        session.end(timestamp);
                        this.writeSessionToDisk(previousSessionFile, session);
                    }
                }
                catch (Throwable e) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Error processing previous session.", e);
                }
            } else {
                this.options.getLogger().log(SentryLevel.DEBUG, "No previous session file to end.", new Object[0]);
            }
        }
    }

    private void writeCrashMarkerFile() {
        File crashMarkerFile = new File(this.options.getCacheDirPath(), CRASH_MARKER_FILE);
        try (FileOutputStream outputStream = new FileOutputStream(crashMarkerFile);){
            String timestamp = DateUtils.getTimestamp(DateUtils.getCurrentDateTime());
            ((OutputStream)outputStream).write(timestamp.getBytes(UTF_8));
            outputStream.flush();
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, "Error writing the crash marker file to the disk", e);
        }
    }

    private void updateCurrentSession(@NotNull File currentSessionFile, @NotNull SentryEnvelope envelope) {
        Iterable<SentryEnvelopeItem> items = envelope.getItems();
        if (items.iterator().hasNext()) {
            SentryEnvelopeItem item = items.iterator().next();
            if (SentryItemType.Session.equals(item.getHeader().getType())) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(item.getData()), UTF_8));){
                    Session session = ((ISerializer)this.serializer.getValue()).deserialize(reader, Session.class);
                    if (session == null) {
                        this.options.getLogger().log(SentryLevel.ERROR, "Item of type %s returned null by the parser.", item.getHeader().getType());
                    } else {
                        this.writeSessionToDisk(currentSessionFile, session);
                    }
                }
                catch (Throwable e) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Item failed to process.", e);
                }
            } else {
                this.options.getLogger().log(SentryLevel.INFO, "Current envelope has a different envelope type %s", item.getHeader().getType());
            }
        } else {
            this.options.getLogger().log(SentryLevel.INFO, "Current envelope %s is empty", currentSessionFile.getAbsolutePath());
        }
    }

    private boolean writeEnvelopeToDisk(@NotNull File file, @NotNull SentryEnvelope envelope) {
        if (file.exists()) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Overwriting envelope to offline storage: %s", file.getAbsolutePath());
            if (!file.delete()) {
                this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete: %s", file.getAbsolutePath());
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(file);){
            ((ISerializer)this.serializer.getValue()).serialize(envelope, outputStream);
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "Error writing Envelope %s to offline storage", file.getAbsolutePath());
            return false;
        }
        return true;
    }

    private void writeSessionToDisk(@NotNull File file, @NotNull Session session) {
        try (FileOutputStream outputStream = new FileOutputStream(file);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)outputStream, UTF_8));){
            this.options.getLogger().log(SentryLevel.DEBUG, "Overwriting session to offline storage: %s", session.getSessionId());
            ((ISerializer)this.serializer.getValue()).serialize(session, writer);
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "Error writing Session to offline storage: %s", session.getSessionId());
        }
    }

    @Override
    public void discard(@NotNull SentryEnvelope envelope) {
        Objects.requireNonNull(envelope, "Envelope is required.");
        File envelopeFile = this.getEnvelopeFile(envelope);
        if (envelopeFile.delete()) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Discarding envelope from cache: %s", envelopeFile.getAbsolutePath());
        } else {
            this.options.getLogger().log(SentryLevel.DEBUG, "Envelope was not cached or could not be deleted: %s", envelopeFile.getAbsolutePath());
        }
    }

    @NotNull
    private File getEnvelopeFile(@NotNull SentryEnvelope envelope) {
        try (@NotNull ISentryLifecycleToken ignored = this.cacheLock.acquire();){
            String fileName;
            if (this.fileNameMap.containsKey(envelope)) {
                fileName = this.fileNameMap.get(envelope);
            } else {
                fileName = SentryUUID.generateSentryId() + SUFFIX_ENVELOPE_FILE;
                this.fileNameMap.put(envelope, fileName);
            }
            File file = new File(this.directory.getAbsolutePath(), fileName);
            return file;
        }
    }

    @NotNull
    public static File getCurrentSessionFile(@NotNull String cacheDirPath) {
        return new File(cacheDirPath, "session.json");
    }

    @NotNull
    public static File getPreviousSessionFile(@NotNull String cacheDirPath) {
        return new File(cacheDirPath, "previous_session.json");
    }

    @Override
    @NotNull
    public Iterator<SentryEnvelope> iterator() {
        File[] allCachedEnvelopes = this.allEnvelopeFiles();
        ArrayList<SentryEnvelope> ret = new ArrayList<SentryEnvelope>(allCachedEnvelopes.length);
        for (File file : allCachedEnvelopes) {
            try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));){
                ret.add(((ISerializer)this.serializer.getValue()).deserializeEnvelope(is));
            }
            catch (FileNotFoundException e) {
                this.options.getLogger().log(SentryLevel.DEBUG, "Envelope file '%s' disappeared while converting all cached files to envelopes.", file.getAbsolutePath());
            }
            catch (IOException e) {
                this.options.getLogger().log(SentryLevel.ERROR, String.format("Error while reading cached envelope from file %s", file.getAbsolutePath()), e);
            }
        }
        return ret.iterator();
    }

    @NotNull
    private File[] allEnvelopeFiles() {
        File[] files;
        if (this.isDirectoryValid() && (files = this.directory.listFiles((__, fileName) -> fileName.endsWith(SUFFIX_ENVELOPE_FILE))) != null) {
            return files;
        }
        return new File[0];
    }

    public boolean waitPreviousSessionFlush() {
        try {
            return this.previousSessionLatch.await(this.options.getSessionFlushTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.options.getLogger().log(SentryLevel.DEBUG, "Timed out waiting for previous session to flush.", new Object[0]);
            return false;
        }
    }

    public void flushPreviousSession() {
        this.previousSessionLatch.countDown();
    }

    public void movePreviousSession(@NotNull File currentSessionFile, @NotNull File previousSessionFile) {
        block11: {
            try (@NotNull ISentryLifecycleToken ignored = this.sessionLock.acquire();){
                if (previousSessionFile.exists()) {
                    this.options.getLogger().log(SentryLevel.DEBUG, "Previous session file already exists, deleting it.", new Object[0]);
                    if (!previousSessionFile.delete()) {
                        this.options.getLogger().log(SentryLevel.WARNING, "Unable to delete previous session file: %s", previousSessionFile);
                    }
                }
                if (!currentSessionFile.exists()) break block11;
                this.options.getLogger().log(SentryLevel.INFO, "Moving current session to previous session.", new Object[0]);
                try {
                    boolean renamed = currentSessionFile.renameTo(previousSessionFile);
                    if (!renamed) {
                        this.options.getLogger().log(SentryLevel.WARNING, "Unable to move current session to previous session.", new Object[0]);
                    }
                }
                catch (Throwable e) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Error moving current session to previous session.", e);
                }
            }
        }
    }
}

