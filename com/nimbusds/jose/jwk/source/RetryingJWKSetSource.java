/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.AbstractJWKSetSourceEvent;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;
import com.nimbusds.jose.jwk.source.JWKSetSource;
import com.nimbusds.jose.jwk.source.JWKSetSourceWrapper;
import com.nimbusds.jose.jwk.source.JWKSetUnavailableException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.events.EventListener;
import java.util.Objects;

@ThreadSafe
public class RetryingJWKSetSource<C extends SecurityContext>
extends JWKSetSourceWrapper<C> {
    private final EventListener<RetryingJWKSetSource<C>, C> eventListener;

    public RetryingJWKSetSource(JWKSetSource<C> source, EventListener<RetryingJWKSetSource<C>, C> eventListener) {
        super(source);
        this.eventListener = eventListener;
    }

    @Override
    public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
        try {
            return this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
        }
        catch (JWKSetUnavailableException e) {
            if (this.eventListener != null) {
                this.eventListener.notify(new RetrialEvent(this, e, (SecurityContext)context, null));
            }
            return this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
        }
    }

    public static class RetrialEvent<C extends SecurityContext>
    extends AbstractJWKSetSourceEvent<RetryingJWKSetSource<C>, C> {
        private final Exception exception;

        private RetrialEvent(RetryingJWKSetSource<C> source, Exception exception, C securityContext) {
            super(source, securityContext);
            Objects.requireNonNull(exception);
            this.exception = exception;
        }

        public Exception getException() {
            return this.exception;
        }

        /* synthetic */ RetrialEvent(RetryingJWKSetSource x0, Exception x1, SecurityContext x2, 1 x3) {
            this(x0, x1, x2);
        }
    }
}

