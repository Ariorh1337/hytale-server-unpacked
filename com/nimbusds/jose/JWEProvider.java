/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEProvider;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jca.JCAAware;
import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;

public interface JWEProvider
extends JOSEProvider,
JCAAware<JWEJCAContext> {
    public Set<JWEAlgorithm> supportedJWEAlgorithms();

    public Set<EncryptionMethod> supportedEncryptionMethods();
}

