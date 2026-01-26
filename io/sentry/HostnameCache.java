/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ISentryLifecycleToken;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.Objects;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class HostnameCache {
    private static final long HOSTNAME_CACHE_DURATION = TimeUnit.HOURS.toMillis(5L);
    private static final long GET_HOSTNAME_TIMEOUT = TimeUnit.SECONDS.toMillis(1L);
    @Nullable
    private static volatile HostnameCache INSTANCE;
    @NotNull
    private static final AutoClosableReentrantLock staticLock;
    private final long cacheDuration;
    @Nullable
    private volatile String hostname;
    private volatile long expirationTimestamp;
    @NotNull
    private final AtomicBoolean updateRunning = new AtomicBoolean(false);
    @NotNull
    private final Callable<InetAddress> getLocalhost;
    @NotNull
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new HostnameCacheThreadFactory());

    @NotNull
    public static HostnameCache getInstance() {
        if (INSTANCE == null) {
            try (@NotNull ISentryLifecycleToken ignored = staticLock.acquire();){
                if (INSTANCE == null) {
                    INSTANCE = new HostnameCache();
                }
            }
        }
        return INSTANCE;
    }

    private HostnameCache() {
        this(HOSTNAME_CACHE_DURATION);
    }

    HostnameCache(long cacheDuration) {
        this(cacheDuration, () -> InetAddress.getLocalHost());
    }

    HostnameCache(long cacheDuration, @NotNull Callable<InetAddress> getLocalhost) {
        this.cacheDuration = cacheDuration;
        this.getLocalhost = Objects.requireNonNull(getLocalhost, "getLocalhost is required");
        this.updateCache();
    }

    void close() {
        this.executorService.shutdown();
    }

    boolean isClosed() {
        return this.executorService.isShutdown();
    }

    @Nullable
    public String getHostname() {
        if (this.expirationTimestamp < System.currentTimeMillis() && this.updateRunning.compareAndSet(false, true)) {
            this.updateCache();
        }
        return this.hostname;
    }

    private void updateCache() {
        Callable<Void> hostRetriever = () -> {
            try {
                this.hostname = this.getLocalhost.call().getCanonicalHostName();
                this.expirationTimestamp = System.currentTimeMillis() + this.cacheDuration;
            }
            finally {
                this.updateRunning.set(false);
            }
            return null;
        };
        try {
            Future<Void> futureTask = this.executorService.submit(hostRetriever);
            futureTask.get(GET_HOSTNAME_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.handleCacheUpdateFailure();
        }
        catch (RuntimeException | ExecutionException | TimeoutException e) {
            this.handleCacheUpdateFailure();
        }
    }

    private void handleCacheUpdateFailure() {
        this.expirationTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1L);
    }

    static {
        staticLock = new AutoClosableReentrantLock();
    }

    private static final class HostnameCacheThreadFactory
    implements ThreadFactory {
        private int cnt;

        private HostnameCacheThreadFactory() {
        }

        @Override
        @NotNull
        public Thread newThread(@NotNull Runnable r) {
            Thread ret = new Thread(r, "SentryHostnameCache-" + this.cnt++);
            ret.setDaemon(true);
            return ret;
        }
    }
}

