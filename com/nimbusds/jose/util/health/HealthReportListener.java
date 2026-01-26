/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util.health;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.health.HealthReport;

public interface HealthReportListener<S, C extends SecurityContext> {
    public void notify(HealthReport<S, C> var1);
}

