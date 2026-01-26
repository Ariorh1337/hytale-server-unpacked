/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.AbstractJWKSetSourceEvent;
import com.nimbusds.jose.jwk.source.CachingJWKSetSource;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;
import com.nimbusds.jose.jwk.source.JWKSetSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.cache.CachedObject;
import com.nimbusds.jose.util.events.EventListener;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class RefreshAheadCachingJWKSetSource<C extends SecurityContext>
extends CachingJWKSetSource<C> {
    private final long refreshAheadTime;
    private final ReentrantLock lazyLock = new ReentrantLock();
    private final ExecutorService executorService;
    private final boolean shutdownExecutorOnClose;
    private final ScheduledExecutorService scheduledExecutorService;
    private final boolean shutdownScheduledExecutorOnClose;
    private volatile long cacheExpiration;
    private ScheduledFuture<?> scheduledRefreshFuture;
    private final EventListener<CachingJWKSetSource<C>, C> eventListener;

    public static ExecutorService createDefaultExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    public static ScheduledExecutorService createDefaultScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    public RefreshAheadCachingJWKSetSource(JWKSetSource<C> source, long timeToLive, long cacheRefreshTimeout, long refreshAheadTime, boolean scheduled, EventListener<CachingJWKSetSource<C>, C> eventListener) {
        this(source, timeToLive, cacheRefreshTimeout, refreshAheadTime, RefreshAheadCachingJWKSetSource.createDefaultExecutorService(), true, eventListener, scheduled ? RefreshAheadCachingJWKSetSource.createDefaultScheduledExecutorService() : null, scheduled);
    }

    public RefreshAheadCachingJWKSetSource(JWKSetSource<C> source, long timeToLive, long cacheRefreshTimeout, long refreshAheadTime, boolean scheduled, ExecutorService executorService, boolean shutdownExecutorOnClose, EventListener<CachingJWKSetSource<C>, C> eventListener) {
        this(source, timeToLive, cacheRefreshTimeout, refreshAheadTime, executorService, shutdownExecutorOnClose, eventListener, scheduled ? RefreshAheadCachingJWKSetSource.createDefaultScheduledExecutorService() : null, scheduled);
    }

    public RefreshAheadCachingJWKSetSource(JWKSetSource<C> source, long timeToLive, long cacheRefreshTimeout, long refreshAheadTime, ExecutorService executorService, boolean shutdownExecutorOnClose, EventListener<CachingJWKSetSource<C>, C> eventListener, ScheduledExecutorService scheduledExecutorService, boolean shutdownScheduledExecutorOnClose) {
        super(source, timeToLive, cacheRefreshTimeout, eventListener);
        if (refreshAheadTime + cacheRefreshTimeout > timeToLive) {
            throw new IllegalArgumentException("The sum of the refresh-ahead time (" + refreshAheadTime + "ms) and the cache refresh timeout (" + cacheRefreshTimeout + "ms) must not exceed the time-to-lived time (" + timeToLive + "ms)");
        }
        this.refreshAheadTime = refreshAheadTime;
        Objects.requireNonNull(executorService, "The executor service must not be null");
        this.executorService = executorService;
        this.shutdownExecutorOnClose = shutdownExecutorOnClose;
        this.shutdownScheduledExecutorOnClose = shutdownScheduledExecutorOnClose;
        this.scheduledExecutorService = scheduledExecutorService;
        this.eventListener = eventListener;
    }

    @Override
    public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
        CachedObject<JWKSet> cache = this.getCachedJWKSet();
        if (cache == null) {
            return this.loadJWKSetBlocking(JWKSetCacheRefreshEvaluator.noRefresh(), currentTime, context);
        }
        JWKSet jwkSet = cache.get();
        if (refreshEvaluator.requiresRefresh(jwkSet)) {
            return this.loadJWKSetBlocking(refreshEvaluator, currentTime, context);
        }
        if (cache.isExpired(currentTime)) {
            return this.loadJWKSetBlocking(JWKSetCacheRefreshEvaluator.referenceComparison(jwkSet), currentTime, context);
        }
        this.refreshAheadOfExpiration(cache, false, currentTime, context);
        return cache.get();
    }

    @Override
    CachedObject<JWKSet> loadJWKSetNotThreadSafe(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
        CachedObject<JWKSet> cache = super.loadJWKSetNotThreadSafe(refreshEvaluator, currentTime, context);
        if (this.scheduledExecutorService != null) {
            this.scheduleRefreshAheadOfExpiration(cache, currentTime, context);
        }
        return cache;
    }

    void scheduleRefreshAheadOfExpiration(final CachedObject<JWKSet> cache, long currentTime, C context) {
        long delay;
        if (this.scheduledRefreshFuture != null) {
            this.scheduledRefreshFuture.cancel(false);
        }
        if ((delay = cache.getExpirationTime() - currentTime - this.refreshAheadTime - this.getCacheRefreshTimeout()) > 0L) {
            RefreshAheadCachingJWKSetSource that = this;
            Runnable command = new Runnable(){
                final /* synthetic */ SecurityContext val$context;
                final /* synthetic */ RefreshAheadCachingJWKSetSource val$that;
                {
                    this.val$context = securityContext;
                    this.val$that = refreshAheadCachingJWKSetSource;
                }

                @Override
                public void run() {
                    block2: {
                        try {
                            RefreshAheadCachingJWKSetSource.this.refreshAheadOfExpiration(cache, true, System.currentTimeMillis(), this.val$context);
                        }
                        catch (Exception e) {
                            if (RefreshAheadCachingJWKSetSource.this.eventListener == null) break block2;
                            RefreshAheadCachingJWKSetSource.this.eventListener.notify(new ScheduledRefreshFailed<SecurityContext>(this.val$that, e, this.val$context));
                        }
                    }
                }
            };
            this.scheduledRefreshFuture = this.scheduledExecutorService.schedule(command, delay, TimeUnit.MILLISECONDS);
            if (this.eventListener != null) {
                this.eventListener.notify(new RefreshScheduledEvent<C>(this, context));
            }
        } else if (this.eventListener != null) {
            this.eventListener.notify(new RefreshNotScheduledEvent<C>(this, context));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void refreshAheadOfExpiration(CachedObject<JWKSet> cache, boolean forceRefresh, long currentTime, C context) {
        if ((cache.isExpired(currentTime + this.refreshAheadTime) || forceRefresh) && this.cacheExpiration < cache.getExpirationTime() && this.lazyLock.tryLock()) {
            try {
                this.lockedRefresh(cache, currentTime, context);
            }
            finally {
                this.lazyLock.unlock();
            }
        }
    }

    void lockedRefresh(CachedObject<JWKSet> cache, long currentTime, C context) {
        if (this.cacheExpiration < cache.getExpirationTime()) {
            this.cacheExpiration = cache.getExpirationTime();
            final RefreshAheadCachingJWKSetSource that = this;
            Runnable runnable = new Runnable(){
                final /* synthetic */ SecurityContext val$context;
                final /* synthetic */ long val$currentTime;
                {
                    this.val$context = securityContext;
                    this.val$currentTime = l;
                }

                @Override
                public void run() {
                    block4: {
                        try {
                            if (RefreshAheadCachingJWKSetSource.this.eventListener != null) {
                                RefreshAheadCachingJWKSetSource.this.eventListener.notify(new ScheduledRefreshInitiatedEvent(that, this.val$context, null));
                            }
                            JWKSet jwkSet = RefreshAheadCachingJWKSetSource.this.loadJWKSetBlocking(JWKSetCacheRefreshEvaluator.forceRefresh(), this.val$currentTime, this.val$context);
                            if (RefreshAheadCachingJWKSetSource.this.eventListener != null) {
                                RefreshAheadCachingJWKSetSource.this.eventListener.notify(new ScheduledRefreshCompletedEvent(that, jwkSet, this.val$context, null));
                            }
                        }
                        catch (Throwable e) {
                            RefreshAheadCachingJWKSetSource.this.cacheExpiration = -1L;
                            if (RefreshAheadCachingJWKSetSource.this.eventListener == null) break block4;
                            RefreshAheadCachingJWKSetSource.this.eventListener.notify(new UnableToRefreshAheadOfExpirationEvent<SecurityContext>(that, this.val$context));
                        }
                    }
                }
            };
            this.executorService.execute(runnable);
        }
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }

    ReentrantLock getLazyLock() {
        return this.lazyLock;
    }

    ScheduledFuture<?> getScheduledRefreshFuture() {
        return this.scheduledRefreshFuture;
    }

    @Override
    public void close() throws IOException {
        ScheduledFuture<?> currentScheduledRefreshFuture = this.scheduledRefreshFuture;
        if (currentScheduledRefreshFuture != null) {
            currentScheduledRefreshFuture.cancel(true);
        }
        super.close();
        if (this.shutdownExecutorOnClose) {
            this.executorService.shutdownNow();
            try {
                this.executorService.awaitTermination(this.getCacheRefreshTimeout(), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (this.scheduledExecutorService != null && this.shutdownScheduledExecutorOnClose) {
            this.scheduledExecutorService.shutdownNow();
            try {
                this.scheduledExecutorService.awaitTermination(this.getCacheRefreshTimeout(), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class RefreshScheduledEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        public RefreshScheduledEvent(RefreshAheadCachingJWKSetSource<C> source, C context) {
            super(source, context);
        }
    }

    public static class RefreshNotScheduledEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        public RefreshNotScheduledEvent(RefreshAheadCachingJWKSetSource<C> source, C context) {
            super(source, context);
        }
    }

    public static class UnableToRefreshAheadOfExpirationEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        public UnableToRefreshAheadOfExpirationEvent(CachingJWKSetSource<C> source, C context) {
            super(source, context);
        }
    }

    public static class ScheduledRefreshCompletedEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        private final JWKSet jwkSet;

        private ScheduledRefreshCompletedEvent(CachingJWKSetSource<C> source, JWKSet jwkSet, C context) {
            super(source, context);
            Objects.requireNonNull(jwkSet);
            this.jwkSet = jwkSet;
        }

        public JWKSet getJWKSet() {
            return this.jwkSet;
        }

        /* synthetic */ ScheduledRefreshCompletedEvent(CachingJWKSetSource x0, JWKSet x1, SecurityContext x2, 1 x3) {
            this(x0, x1, x2);
        }
    }

    public static class ScheduledRefreshInitiatedEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        private ScheduledRefreshInitiatedEvent(CachingJWKSetSource<C> source, C context) {
            super(source, context);
        }

        /* synthetic */ ScheduledRefreshInitiatedEvent(CachingJWKSetSource x0, SecurityContext x1, 1 x2) {
            this(x0, x1);
        }
    }

    public static class ScheduledRefreshFailed<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        private final Exception exception;

        public ScheduledRefreshFailed(CachingJWKSetSource<C> source, Exception exception, C context) {
            super(source, context);
            Objects.requireNonNull(exception);
            this.exception = exception;
        }

        public Exception getException() {
            return this.exception;
        }
    }
}

