/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import java.io.Serializable;
import java.text.ParseException;

public interface JWT
extends Serializable {
    public Header getHeader();

    public JWTClaimsSet getJWTClaimsSet() throws ParseException;

    public Base64URL[] getParsedParts();

    public String getParsedString();

    public String serialize();
}

