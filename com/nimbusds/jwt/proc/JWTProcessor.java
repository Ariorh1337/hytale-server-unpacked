/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;

public interface JWTProcessor<C extends SecurityContext> {
    public JWTClaimsSet process(String var1, C var2) throws ParseException, BadJOSEException, JOSEException;

    public JWTClaimsSet process(JWT var1, C var2) throws BadJOSEException, JOSEException;

    public JWTClaimsSet process(PlainJWT var1, C var2) throws BadJOSEException, JOSEException;

    public JWTClaimsSet process(SignedJWT var1, C var2) throws BadJOSEException, JOSEException;

    public JWTClaimsSet process(EncryptedJWT var1, C var2) throws BadJOSEException, JOSEException;
}

