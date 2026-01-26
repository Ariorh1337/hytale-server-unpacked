/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.api.client.http.GenericUrl
 *  com.google.api.client.http.HttpHeaders
 *  com.google.api.client.http.HttpRequest
 *  com.google.api.client.http.HttpResponse
 *  com.google.api.client.http.HttpTransport
 *  com.google.api.client.http.javanet.NetHttpTransport
 *  com.google.api.client.http.javanet.NetHttpTransport$Builder
 */
package com.google.crypto.tink.util;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.concurrent.GuardedBy;

@Deprecated
public class KeysDownloader {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final NetHttpTransport DEFAULT_HTTP_TRANSPORT = new NetHttpTransport.Builder().build();
    private static final Executor DEFAULT_BACKGROUND_EXECUTOR = Executors.newCachedThreadPool();
    private static final Pattern MAX_AGE_PATTERN = Pattern.compile("\\s*max-age\\s*=\\s*(\\d+)\\s*");
    private final Executor backgroundExecutor;
    private final HttpTransport httpTransport;
    private final Object fetchDataLock;
    private final Object instanceStateLock;
    private final String url;
    @GuardedBy(value="instanceStateLock")
    private Runnable pendingRefreshRunnable;
    @GuardedBy(value="instanceStateLock")
    private String cachedData;
    @GuardedBy(value="instanceStateLock")
    private long cachedTimeInMillis;
    @GuardedBy(value="instanceStateLock")
    private long cacheExpirationDurationInMillis;

    public KeysDownloader(Executor backgroundExecutor, HttpTransport httpTransport, String url) {
        KeysDownloader.validate(url);
        this.backgroundExecutor = backgroundExecutor;
        this.httpTransport = httpTransport;
        this.instanceStateLock = new Object();
        this.fetchDataLock = new Object();
        this.url = url;
        this.cachedTimeInMillis = Long.MIN_VALUE;
        this.cacheExpirationDurationInMillis = 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String download() throws IOException {
        Object object = this.instanceStateLock;
        synchronized (object) {
            if (this.hasNonExpiredDataCached()) {
                if (this.shouldProactivelyRefreshDataInBackground()) {
                    this.refreshInBackground();
                }
                return this.cachedData;
            }
        }
        object = this.fetchDataLock;
        synchronized (object) {
            Object object2 = this.instanceStateLock;
            synchronized (object2) {
                if (this.hasNonExpiredDataCached()) {
                    return this.cachedData;
                }
            }
            return this.fetchAndCacheData();
        }
    }

    public HttpTransport getHttpTransport() {
        return this.httpTransport;
    }

    public String getUrl() {
        return this.url;
    }

    @GuardedBy(value="instanceStateLock")
    private boolean hasNonExpiredDataCached() {
        long currentTimeInMillis = this.getCurrentTimeInMillis();
        boolean cachedInFuture = this.cachedTimeInMillis > currentTimeInMillis;
        boolean cacheExpired = this.cachedTimeInMillis + this.cacheExpirationDurationInMillis <= currentTimeInMillis;
        return !cacheExpired && !cachedInFuture;
    }

    @GuardedBy(value="instanceStateLock")
    private boolean shouldProactivelyRefreshDataInBackground() {
        return this.cachedTimeInMillis + this.cacheExpirationDurationInMillis / 2L <= this.getCurrentTimeInMillis();
    }

    long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @GuardedBy(value="fetchDataLock")
    @CanIgnoreReturnValue
    private String fetchAndCacheData() throws IOException {
        String data;
        long currentTimeInMillis = this.getCurrentTimeInMillis();
        HttpRequest httpRequest = this.httpTransport.createRequestFactory().buildGetRequest(new GenericUrl(this.url));
        HttpResponse httpResponse = httpRequest.execute();
        if (httpResponse.getStatusCode() != 200) {
            throw new IOException("Unexpected status code = " + httpResponse.getStatusCode());
        }
        try (InputStream contentStream = httpResponse.getContent();){
            InputStreamReader reader = new InputStreamReader(contentStream, UTF_8);
            data = KeysDownloader.readerToString(reader);
        }
        Object object = this.instanceStateLock;
        synchronized (object) {
            this.cachedTimeInMillis = currentTimeInMillis;
            this.cacheExpirationDurationInMillis = this.getExpirationDurationInSeconds(httpResponse.getHeaders()) * 1000L;
            this.cachedData = data;
        }
        return data;
    }

    private static String readerToString(Reader reader) throws IOException {
        int c;
        reader = new BufferedReader(reader);
        StringBuilder stringBuilder = new StringBuilder();
        while ((c = reader.read()) != -1) {
            stringBuilder.append((char)c);
        }
        return stringBuilder.toString();
    }

    long getExpirationDurationInSeconds(HttpHeaders httpHeaders) {
        long expirationDurationInSeconds = 0L;
        if (httpHeaders.getCacheControl() != null) {
            for (String arg : httpHeaders.getCacheControl().split(",")) {
                Matcher m = MAX_AGE_PATTERN.matcher(arg);
                if (!m.matches()) continue;
                expirationDurationInSeconds = Long.valueOf(m.group(1));
                break;
            }
        }
        if (httpHeaders.getAge() != null) {
            expirationDurationInSeconds -= httpHeaders.getAge().longValue();
        }
        return Math.max(0L, expirationDurationInSeconds);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void refreshInBackground() {
        Runnable refreshRunnable = this.newRefreshRunnable();
        Object object = this.instanceStateLock;
        synchronized (object) {
            if (this.pendingRefreshRunnable != null) {
                return;
            }
            this.pendingRefreshRunnable = refreshRunnable;
        }
        try {
            this.backgroundExecutor.execute(refreshRunnable);
        }
        catch (Throwable e) {
            Object object2 = this.instanceStateLock;
            synchronized (object2) {
                if (this.pendingRefreshRunnable == refreshRunnable) {
                    this.pendingRefreshRunnable = null;
                }
            }
            throw e;
        }
    }

    private Runnable newRefreshRunnable() {
        return new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                Object object = KeysDownloader.this.fetchDataLock;
                synchronized (object) {
                    try {
                        KeysDownloader.this.fetchAndCacheData();
                    }
                    catch (IOException iOException) {
                        Object object2 = KeysDownloader.this.instanceStateLock;
                        synchronized (object2) {
                            if (KeysDownloader.this.pendingRefreshRunnable == this) {
                                KeysDownloader.this.pendingRefreshRunnable = null;
                            }
                        }
                    }
                    finally {
                        Object object3 = KeysDownloader.this.instanceStateLock;
                        synchronized (object3) {
                            if (KeysDownloader.this.pendingRefreshRunnable == this) {
                                KeysDownloader.this.pendingRefreshRunnable = null;
                            }
                        }
                    }
                }
            }
        };
    }

    private static void validate(String url) {
        try {
            URL tmp = new URL(url);
            if (!tmp.getProtocol().toLowerCase(Locale.US).equals("https")) {
                throw new IllegalArgumentException("url must point to a HTTPS server");
            }
        }
        catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    static /* synthetic */ NetHttpTransport access$400() {
        return DEFAULT_HTTP_TRANSPORT;
    }

    static /* synthetic */ Executor access$500() {
        return DEFAULT_BACKGROUND_EXECUTOR;
    }

    public static class Builder {
        private HttpTransport httpTransport = KeysDownloader.access$400();
        private Executor executor = KeysDownloader.access$500();
        private String url;

        @CanIgnoreReturnValue
        public Builder setUrl(String val) {
            this.url = val;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setExecutor(Executor val) {
            this.executor = val;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHttpTransport(HttpTransport httpTransport) {
            this.httpTransport = httpTransport;
            return this;
        }

        public KeysDownloader build() {
            if (this.url == null) {
                throw new IllegalArgumentException("must provide a url with {#setUrl}");
            }
            return new KeysDownloader(this.executor, this.httpTransport, this.url);
        }
    }
}

