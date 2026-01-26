/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSProvider;
import com.nimbusds.jose.util.Base64URL;

public interface JWSSigner
extends JWSProvider {
    public Base64URL sign(JWSHeader var1, byte[] var2) throws JOSEException;
}

