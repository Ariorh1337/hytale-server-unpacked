/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEProvider;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jca.JCAAware;
import com.nimbusds.jose.jca.JCAContext;
import java.util.Set;

public interface JWSProvider
extends JOSEProvider,
JCAAware<JCAContext> {
    public Set<JWSAlgorithm> supportedJWSAlgorithms();
}

