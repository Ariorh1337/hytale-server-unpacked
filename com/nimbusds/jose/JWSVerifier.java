/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSProvider;
import com.nimbusds.jose.util.Base64URL;

public interface JWSVerifier
extends JWSProvider {
    public boolean verify(JWSHeader var1, byte[] var2, Base64URL var3) throws JOSEException;
}

