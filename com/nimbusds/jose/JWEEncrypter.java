/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEProvider;

public interface JWEEncrypter
extends JWEProvider {
    public JWECryptoParts encrypt(JWEHeader var1, byte[] var2, byte[] var3) throws JOSEException;
}

