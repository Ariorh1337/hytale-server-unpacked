/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.proc.JOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWEDecrypterFactory;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jose.proc.SecurityContext;

public interface JOSEProcessorConfiguration<C extends SecurityContext> {
    public JOSEObjectTypeVerifier<C> getJWSTypeVerifier();

    public void setJWSTypeVerifier(JOSEObjectTypeVerifier<C> var1);

    public JWSKeySelector<C> getJWSKeySelector();

    public void setJWSKeySelector(JWSKeySelector<C> var1);

    public JOSEObjectTypeVerifier<C> getJWETypeVerifier();

    public void setJWETypeVerifier(JOSEObjectTypeVerifier<C> var1);

    public JWEKeySelector<C> getJWEKeySelector();

    public void setJWEKeySelector(JWEKeySelector<C> var1);

    public JWSVerifierFactory getJWSVerifierFactory();

    public void setJWSVerifierFactory(JWSVerifierFactory var1);

    public JWEDecrypterFactory getJWEDecrypterFactory();

    public void setJWEDecrypterFactory(JWEDecrypterFactory var1);
}

