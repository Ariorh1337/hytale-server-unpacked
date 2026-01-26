/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.IScopes;
import io.sentry.ISentryExecutorService;
import io.sentry.Integration;
import io.sentry.NoOpLogger;
import io.sentry.NoOpSentryExecutorService;
import io.sentry.SentryEnvelope;
import io.sentry.SentryExecutorService;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.IntegrationUtils;
import io.sentry.util.Platform;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.RejectedExecutionException;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class SpotlightIntegration
implements Integration,
SentryOptions.BeforeEnvelopeCallback,
Closeable {
    @Nullable
    private SentryOptions options;
    @NotNull
    private ILogger logger = NoOpLogger.getInstance();
    @NotNull
    private ISentryExecutorService executorService = NoOpSentryExecutorService.getInstance();

    @Override
    public void register(@NotNull IScopes scopes, @NotNull SentryOptions options) {
        this.options = options;
        this.logger = options.getLogger();
        if (options.getBeforeEnvelopeCallback() == null && options.isEnableSpotlight()) {
            this.executorService = new SentryExecutorService(options);
            options.setBeforeEnvelopeCallback(this);
            this.logger.log(SentryLevel.DEBUG, "SpotlightIntegration enabled.", new Object[0]);
            IntegrationUtils.addIntegrationToSdkVersion("Spotlight");
        } else {
            this.logger.log(SentryLevel.DEBUG, "SpotlightIntegration is not enabled. BeforeEnvelopeCallback is already set or spotlight is not enabled.", new Object[0]);
        }
    }

    @Override
    public void execute(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
        try {
            this.executorService.submit(() -> this.sendEnvelope(envelope));
        }
        catch (RejectedExecutionException e) {
            this.logger.log(SentryLevel.WARNING, "Spotlight envelope submission rejected.", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendEnvelope(@NotNull SentryEnvelope envelope) {
        block19: {
            try {
                if (this.options == null) {
                    throw new IllegalArgumentException("SentryOptions are required to send envelopes.");
                }
                String spotlightConnectionUrl = this.getSpotlightConnectionUrl();
                HttpURLConnection connection = this.createConnection(spotlightConnectionUrl);
                try (OutputStream outputStream = connection.getOutputStream();
                     GZIPOutputStream gzip = new GZIPOutputStream(outputStream);){
                    this.options.getSerializer().serialize(envelope, gzip);
                }
                catch (Throwable e) {
                    try {
                        this.logger.log(SentryLevel.ERROR, "An exception occurred while submitting the envelope to the Sentry server.", e);
                    }
                    catch (Throwable throwable) {
                        int responseCode = connection.getResponseCode();
                        this.logger.log(SentryLevel.DEBUG, "Envelope sent to spotlight: %d", responseCode);
                        this.closeAndDisconnect(connection);
                        throw throwable;
                    }
                    int responseCode = connection.getResponseCode();
                    this.logger.log(SentryLevel.DEBUG, "Envelope sent to spotlight: %d", responseCode);
                    this.closeAndDisconnect(connection);
                    break block19;
                }
                int responseCode = connection.getResponseCode();
                this.logger.log(SentryLevel.DEBUG, "Envelope sent to spotlight: %d", responseCode);
                this.closeAndDisconnect(connection);
            }
            catch (Exception e) {
                this.logger.log(SentryLevel.ERROR, "An exception occurred while creating the connection to spotlight.", e);
            }
        }
    }

    @TestOnly
    public String getSpotlightConnectionUrl() {
        if (this.options != null && this.options.getSpotlightConnectionUrl() != null) {
            return this.options.getSpotlightConnectionUrl();
        }
        if (Platform.isAndroid()) {
            return "http://10.0.2.2:8969/stream";
        }
        return "http://localhost:8969/stream";
    }

    @NotNull
    private HttpURLConnection createConnection(@NotNull String url) throws Exception {
        @NotNull HttpURLConnection connection = (HttpURLConnection)URI.create(url).toURL().openConnection();
        connection.setReadTimeout(1000);
        connection.setConnectTimeout(1000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Encoding", "gzip");
        connection.setRequestProperty("Content-Type", "application/x-sentry-envelope");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Connection", "close");
        connection.connect();
        return connection;
    }

    private void closeAndDisconnect(@NotNull HttpURLConnection connection) {
        try {
            connection.getInputStream().close();
        }
        catch (IOException iOException) {
        }
        finally {
            connection.disconnect();
        }
    }

    @Override
    public void close() throws IOException {
        this.executorService.close(0L);
        if (this.options != null && this.options.getBeforeEnvelopeCallback() == this) {
            this.options.setBeforeEnvelopeCallback(null);
        }
    }
}

