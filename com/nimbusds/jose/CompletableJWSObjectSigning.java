/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;
import java.security.Signature;

public interface CompletableJWSObjectSigning {
    public Signature getInitializedSignature();

    public Base64URL complete() throws JOSEException;
}

