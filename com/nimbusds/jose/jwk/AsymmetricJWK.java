/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface AsymmetricJWK {
    public PublicKey toPublicKey() throws JOSEException;

    public PrivateKey toPrivateKey() throws JOSEException;

    public KeyPair toKeyPair() throws JOSEException;

    public boolean matches(X509Certificate var1);
}

