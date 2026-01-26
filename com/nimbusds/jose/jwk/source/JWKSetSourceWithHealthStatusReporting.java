/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSetCacheRefreshEvaluator;
import com.nimbusds.jose.jwk.source.JWKSetSource;
import com.nimbusds.jose.jwk.source.JWKSetSourceWrapper;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.health.HealthReport;
import com.nimbusds.jose.util.health.HealthReportListener;
import com.nimbusds.jose.util.health.HealthStatus;
import java.util.Objects;

@ThreadSafe
public class JWKSetSourceWithHealthStatusReporting<C extends SecurityContext>
extends JWKSetSourceWrapper<C> {
    private final HealthReportListener<JWKSetSourceWithHealthStatusReporting<C>, C> healthReportListener;

    public JWKSetSourceWithHealthStatusReporting(JWKSetSource<C> source, HealthReportListener<JWKSetSourceWithHealthStatusReporting<C>, C> healthReportListener) {
        super(source);
        Objects.requireNonNull(healthReportListener);
        this.healthReportListener = healthReportListener;
    }

    @Override
    public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
        JWKSet jwkSet;
        try {
            jwkSet = this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
            this.healthReportListener.notify(new HealthReport<JWKSetSourceWithHealthStatusReporting, C>(this, HealthStatus.HEALTHY, currentTime, context));
        }
        catch (Exception e) {
            this.healthReportListener.notify(new HealthReport<JWKSetSourceWithHealthStatusReporting, C>(this, HealthStatus.NOT_HEALTHY, e, currentTime, context));
            throw e;
        }
        return jwkSet;
    }
}

