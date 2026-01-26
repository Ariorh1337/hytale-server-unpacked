/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.AbstractCachingJWKSetSource;
import com.nimbusds.jose.jwk.source.AbstractJWKSetSourceEvent;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;
import com.nimbusds.jose.jwk.source.JWKSetSource;
import com.nimbusds.jose.jwk.source.JWKSetUnavailableException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.cache.CachedObject;
import com.nimbusds.jose.util.events.EventListener;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class CachingJWKSetSource<C extends SecurityContext>
extends AbstractCachingJWKSetSource<C> {
    private final ReentrantLock lock = new ReentrantLock();
    private final long cacheRefreshTimeout;
    private final EventListener<CachingJWKSetSource<C>, C> eventListener;

    public CachingJWKSetSource(JWKSetSource<C> source, long timeToLive, long cacheRefreshTimeout, EventListener<CachingJWKSetSource<C>, C> eventListener) {
        super(source, timeToLive);
        this.cacheRefreshTimeout = cacheRefreshTimeout;
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
        return cache.get();
    }

    public long getCacheRefreshTimeout() {
        return this.cacheRefreshTimeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    JWKSet loadJWKSetBlocking(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
        try {
            CachedObject<JWKSet> cache;
            block19: {
                if (this.lock.tryLock()) {
                    try {
                        CachedObject<JWKSet> cachedJWKSet = this.getCachedJWKSet();
                        if (cachedJWKSet == null || refreshEvaluator.requiresRefresh(cachedJWKSet.get())) {
                            if (this.eventListener != null) {
                                this.eventListener.notify(new RefreshInitiatedEvent(this, this.lock.getQueueLength(), (SecurityContext)context, null));
                            }
                            CachedObject<JWKSet> result = this.loadJWKSetNotThreadSafe(refreshEvaluator, currentTime, context);
                            if (this.eventListener != null) {
                                this.eventListener.notify(new RefreshCompletedEvent(this, result.get(), this.lock.getQueueLength(), (SecurityContext)context, null));
                            }
                            cache = result;
                        }
                        cache = cachedJWKSet;
                    }
                    finally {
                        this.lock.unlock();
                    }
                } else {
                    if (this.eventListener != null) {
                        this.eventListener.notify(new WaitingForRefreshEvent(this, this.lock.getQueueLength(), (SecurityContext)context, null));
                    }
                    if (!this.lock.tryLock(this.getCacheRefreshTimeout(), TimeUnit.MILLISECONDS)) {
                        if (this.eventListener == null) throw new JWKSetUnavailableException("Timeout while waiting for cache refresh (" + this.cacheRefreshTimeout + "ms exceeded)");
                        this.eventListener.notify(new RefreshTimedOutEvent(this, this.lock.getQueueLength(), (SecurityContext)context, null));
                        throw new JWKSetUnavailableException("Timeout while waiting for cache refresh (" + this.cacheRefreshTimeout + "ms exceeded)");
                    }
                    try {
                        CachedObject<JWKSet> cachedJWKSet = this.getCachedJWKSet();
                        if (cachedJWKSet == null || refreshEvaluator.requiresRefresh(cachedJWKSet.get())) {
                            if (this.eventListener != null) {
                                this.eventListener.notify(new RefreshInitiatedEvent(this, this.lock.getQueueLength(), (SecurityContext)context, null));
                            }
                            cache = this.loadJWKSetNotThreadSafe(refreshEvaluator, currentTime, context);
                            if (this.eventListener != null) {
                                this.eventListener.notify(new RefreshCompletedEvent(this, cache.get(), this.lock.getQueueLength(), (SecurityContext)context, null));
                            }
                            break block19;
                        }
                        cache = cachedJWKSet;
                    }
                    finally {
                        this.lock.unlock();
                    }
                }
            }
            if (cache != null && cache.isValid(currentTime)) {
                return cache.get();
            }
            if (this.eventListener == null) throw new JWKSetUnavailableException("Unable to refresh cache");
            this.eventListener.notify(new UnableToRefreshEvent(this, (SecurityContext)context, null));
            throw new JWKSetUnavailableException("Unable to refresh cache");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JWKSetUnavailableException("Interrupted while waiting for cache refresh", e);
        }
    }

    CachedObject<JWKSet> loadJWKSetNotThreadSafe(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
        JWKSet jwkSet = this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
        return this.cacheJWKSet(jwkSet, currentTime);
    }

    ReentrantLock getLock() {
        return this.lock;
    }

    public static class RefreshInitiatedEvent<C extends SecurityContext>
    extends AbstractCachingJWKSetSourceEvent<C> {
        private RefreshInitiatedEvent(CachingJWKSetSource<C> source, int queueLength, C context) {
            super(source, queueLength, context);
        }

        /* synthetic */ RefreshInitiatedEvent(CachingJWKSetSource x0, int x1, SecurityContext x2, 1 x3) {
            this(x0, x1, x2);
        }
    }

    public static class RefreshCompletedEvent<C extends SecurityContext>
    extends AbstractCachingJWKSetSourceEvent<C> {
        private final JWKSet jwkSet;

        private RefreshCompletedEvent(CachingJWKSetSource<C> source, JWKSet jwkSet, int queueLength, C context) {
            super(source, queueLength, context);
            Objects.requireNonNull(jwkSet);
            this.jwkSet = jwkSet;
        }

        public JWKSet getJWKSet() {
            return this.jwkSet;
        }

        /* synthetic */ RefreshCompletedEvent(CachingJWKSetSource x0, JWKSet x1, int x2, SecurityContext x3, 1 x4) {
            this(x0, x1, x2, x3);
        }
    }

    public static class WaitingForRefreshEvent<C extends SecurityContext>
    extends AbstractCachingJWKSetSourceEvent<C> {
        private WaitingForRefreshEvent(CachingJWKSetSource<C> source, int queueLength, C context) {
            super(source, queueLength, context);
        }

        /* synthetic */ WaitingForRefreshEvent(CachingJWKSetSource x0, int x1, SecurityContext x2, 1 x3) {
            this(x0, x1, x2);
        }
    }

    public static class RefreshTimedOutEvent<C extends SecurityContext>
    extends AbstractCachingJWKSetSourceEvent<C> {
        private RefreshTimedOutEvent(CachingJWKSetSource<C> source, int queueLength, C context) {
            super(source, queueLength, context);
        }

        /* synthetic */ RefreshTimedOutEvent(CachingJWKSetSource x0, int x1, SecurityContext x2, 1 x3) {
            this(x0, x1, x2);
        }
    }

    public static class UnableToRefreshEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        private UnableToRefreshEvent(CachingJWKSetSource<C> source, C context) {
            super(source, context);
        }

        /* synthetic */ UnableToRefreshEvent(CachingJWKSetSource x0, SecurityContext x1, 1 x2) {
            this(x0, x1);
        }
    }

    static class AbstractCachingJWKSetSourceEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<CachingJWKSetSource<C>, C> {
        private final int threadQueueLength;

        public AbstractCachingJWKSetSourceEvent(CachingJWKSetSource<C> source, int threadQueueLength, C context) {
            super(source, context);
            this.threadQueueLength = threadQueueLength;
        }

        public int getThreadQueueLength() {
            return this.threadQueueLength;
        }
    }
}

