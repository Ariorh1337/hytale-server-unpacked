/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import java.util.Set;

public interface CriticalHeaderParamsAware {
    public Set<String> getProcessedCriticalHeaderParams();

    public Set<String> getDeferredCriticalHeaderParams();
}

