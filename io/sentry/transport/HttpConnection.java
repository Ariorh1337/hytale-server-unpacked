/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry.transport;

import io.sentry.RequestDetails;
import io.sentry.SentryEnvelope;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.transport.AuthenticatorWrapper;
import io.sentry.transport.ProxyAuthenticator;
import io.sentry.transport.RateLimiter;
import io.sentry.transport.TransportResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

final class HttpConnection {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    @Nullable
    private final Proxy proxy;
    @NotNull
    private final RequestDetails requestDetails;
    @NotNull
    private final SentryOptions options;
    @NotNull
    private final RateLimiter rateLimiter;

    public HttpConnection(@NotNull SentryOptions options, @NotNull RequestDetails requestDetails, @NotNull RateLimiter rateLimiter) {
        this(options, requestDetails, AuthenticatorWrapper.getInstance(), rateLimiter);
    }

    HttpConnection(@NotNull SentryOptions options, @NotNull RequestDetails requestDetails, @NotNull AuthenticatorWrapper authenticatorWrapper, @NotNull RateLimiter rateLimiter) {
        this.requestDetails = requestDetails;
        this.options = options;
        this.rateLimiter = rateLimiter;
        this.proxy = this.resolveProxy(options.getProxy());
        if (this.proxy != null && options.getProxy() != null) {
            String proxyUser = options.getProxy().getUser();
            String proxyPassword = options.getProxy().getPass();
            if (proxyUser != null && proxyPassword != null) {
                authenticatorWrapper.setDefault(new ProxyAuthenticator(proxyUser, proxyPassword));
            }
        }
    }

    @Nullable
    private Proxy resolveProxy(@Nullable SentryOptions.Proxy optionsProxy) {
        Proxy proxy = null;
        if (optionsProxy != null) {
            String port = optionsProxy.getPort();
            String host = optionsProxy.getHost();
            if (port != null && host != null) {
                try {
                    @NotNull Proxy.Type type = optionsProxy.getType() != null ? optionsProxy.getType() : Proxy.Type.HTTP;
                    InetSocketAddress proxyAddr = new InetSocketAddress(host, Integer.parseInt(port));
                    proxy = new Proxy(type, proxyAddr);
                }
                catch (NumberFormatException e) {
                    this.options.getLogger().log(SentryLevel.ERROR, e, "Failed to parse Sentry Proxy port: " + optionsProxy.getPort() + ". Proxy is ignored", new Object[0]);
                }
            }
        }
        return proxy;
    }

    @NotNull
    HttpURLConnection open() throws IOException {
        return (HttpURLConnection)(this.proxy == null ? this.requestDetails.getUrl().openConnection() : this.requestDetails.getUrl().openConnection(this.proxy));
    }

    @NotNull
    private HttpURLConnection createConnection() throws IOException {
        HttpURLConnection connection = this.open();
        for (Map.Entry<String, String> header : this.requestDetails.getHeaders().entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Encoding", "gzip");
        connection.setRequestProperty("Content-Type", "application/x-sentry-envelope");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Connection", "close");
        connection.setConnectTimeout(this.options.getConnectionTimeoutMillis());
        connection.setReadTimeout(this.options.getReadTimeoutMillis());
        SSLSocketFactory sslSocketFactory = this.options.getSslSocketFactory();
        if (connection instanceof HttpsURLConnection && sslSocketFactory != null) {
            ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
        }
        connection.connect();
        return connection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public TransportResult send(@NotNull SentryEnvelope envelope) throws IOException {
        TransportResult result;
        this.options.getSocketTagger().tagSockets();
        HttpURLConnection connection = this.createConnection();
        try (OutputStream outputStream = connection.getOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(outputStream);){
            this.options.getSerializer().serialize(envelope, gzip);
        }
        catch (Throwable e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "An exception occurred while submitting the envelope to the Sentry server.", new Object[0]);
        }
        finally {
            result = this.readAndLog(connection);
            this.options.getSocketTagger().untagSockets();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    private TransportResult readAndLog(@NotNull HttpURLConnection connection) {
        try {
            int responseCode = connection.getResponseCode();
            this.updateRetryAfterLimits(connection, responseCode);
            if (!this.isSuccessfulResponseCode(responseCode)) {
                this.options.getLogger().log(SentryLevel.ERROR, "Request failed, API returned %s", responseCode);
                if (this.options.isDebug()) {
                    @NotNull String errorMessage = this.getErrorMessageFromStream(connection);
                    this.options.getLogger().log(SentryLevel.ERROR, "%s", errorMessage);
                }
                TransportResult transportResult = TransportResult.error(responseCode);
                return transportResult;
            }
            this.options.getLogger().log(SentryLevel.DEBUG, "Envelope sent successfully.", new Object[0]);
            TransportResult transportResult = TransportResult.success();
            return transportResult;
        }
        catch (IOException e) {
            this.options.getLogger().log(SentryLevel.ERROR, e, "Error reading and logging the response stream", new Object[0]);
        }
        finally {
            this.closeAndDisconnect(connection);
        }
        return TransportResult.error();
    }

    public void updateRetryAfterLimits(@NotNull HttpURLConnection connection, int responseCode) {
        String retryAfterHeader = connection.getHeaderField("Retry-After");
        String sentryRateLimitHeader = connection.getHeaderField("X-Sentry-Rate-Limits");
        this.rateLimiter.updateRetryAfterLimits(sentryRateLimitHeader, retryAfterHeader, responseCode);
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

    /*
     * Enabled aggressive exception aggregation
     */
    @NotNull
    private String getErrorMessageFromStream(@NotNull HttpURLConnection connection) {
        try (InputStream errorStream = connection.getErrorStream();){
            String string;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, UTF_8));){
                String line;
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (!first) {
                        sb.append("\n");
                    }
                    sb.append(line);
                    first = false;
                }
                string = sb.toString();
            }
            return string;
        }
        catch (IOException e) {
            return "Failed to obtain error message while analyzing send failure.";
        }
    }

    private boolean isSuccessfulResponseCode(int responseCode) {
        return responseCode == 200;
    }

    @TestOnly
    @Nullable
    Proxy getProxy() {
        return this.proxy;
    }
}

