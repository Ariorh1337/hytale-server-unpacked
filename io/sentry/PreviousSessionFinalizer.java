/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DateUtils;
import io.sentry.IScopes;
import io.sentry.ISerializer;
import io.sentry.SentryEnvelope;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.cache.EnvelopeCache;
import io.sentry.cache.IEnvelopeCache;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PreviousSessionFinalizer
implements Runnable {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    @NotNull
    private final SentryOptions options;
    @NotNull
    private final IScopes scopes;

    PreviousSessionFinalizer(@NotNull SentryOptions options, @NotNull IScopes scopes) {
        this.options = options;
        this.scopes = scopes;
    }

    @Override
    public void run() {
        String cacheDirPath = this.options.getCacheDirPath();
        if (cacheDirPath == null) {
            this.options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, not finalizing the previous session.", new Object[0]);
            return;
        }
        if (!this.options.isEnableAutoSessionTracking()) {
            this.options.getLogger().log(SentryLevel.DEBUG, "Session tracking is disabled, bailing from previous session finalizer.", new Object[0]);
            return;
        }
        IEnvelopeCache cache = this.options.getEnvelopeDiskCache();
        if (cache instanceof EnvelopeCache && !((EnvelopeCache)cache).waitPreviousSessionFlush()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Timed out waiting to flush previous session to its own file in session finalizer.", new Object[0]);
            return;
        }
        File previousSessionFile = EnvelopeCache.getPreviousSessionFile(cacheDirPath);
        ISerializer serializer = this.options.getSerializer();
        if (previousSessionFile.exists()) {
            this.options.getLogger().log(SentryLevel.WARNING, "Current session is not ended, we'd need to end it.", new Object[0]);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(previousSessionFile), UTF_8));){
                Session session = serializer.deserialize(reader, Session.class);
                if (session == null) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Stream from path %s resulted in a null envelope.", previousSessionFile.getAbsolutePath());
                } else {
                    Date timestamp = null;
                    File crashMarkerFile = new File(this.options.getCacheDirPath(), ".sentry-native/last_crash");
                    if (crashMarkerFile.exists()) {
                        this.options.getLogger().log(SentryLevel.INFO, "Crash marker file exists, last Session is gonna be Crashed.", new Object[0]);
                        timestamp = this.getTimestampFromCrashMarkerFile(crashMarkerFile);
                        if (!crashMarkerFile.delete()) {
                            this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete the crash marker file. %s.", crashMarkerFile.getAbsolutePath());
                        }
                        session.update(Session.State.Crashed, null, true);
                    }
                    if (session.getAbnormalMechanism() == null) {
                        session.end(timestamp);
                    }
                    SentryEnvelope fromSession = SentryEnvelope.from(serializer, session, this.options.getSdkVersion());
                    this.scopes.captureEnvelope(fromSession);
                }
            }
            catch (Throwable e) {
                this.options.getLogger().log(SentryLevel.ERROR, "Error processing previous session.", e);
            }
            if (!previousSessionFile.delete()) {
                this.options.getLogger().log(SentryLevel.WARNING, "Failed to delete the previous session file.", new Object[0]);
            }
        }
    }

    @Nullable
    private Date getTimestampFromCrashMarkerFile(@NotNull File markerFile) {
        block8: {
            Date date;
            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(markerFile), UTF_8));
            try {
                String timestamp = reader.readLine();
                this.options.getLogger().log(SentryLevel.DEBUG, "Crash marker file has %s timestamp.", timestamp);
                date = DateUtils.getDateTime(timestamp);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        reader.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    this.options.getLogger().log(SentryLevel.ERROR, "Error reading the crash marker file.", e);
                    break block8;
                }
                catch (IllegalArgumentException e) {
                    this.options.getLogger().log(SentryLevel.ERROR, e, "Error converting the crash timestamp.", new Object[0]);
                }
            }
            reader.close();
            return date;
        }
        return null;
    }
}

