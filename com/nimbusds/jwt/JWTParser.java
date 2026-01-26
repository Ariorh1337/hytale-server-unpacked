/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Map;

public final class JWTParser {
    public static JWT parse(String s) throws ParseException {
        Map<String, Object> jsonObject;
        int firstDotPos = s.indexOf(".");
        if (firstDotPos == -1) {
            throw new ParseException("Invalid JWT serialization: Missing dot delimiter(s)", 0);
        }
        Base64URL header = new Base64URL(s.substring(0, firstDotPos));
        try {
            jsonObject = JSONObjectUtils.parse(header.decodeToString());
        }
        catch (ParseException e) {
            throw new ParseException("Invalid unsecured/JWS/JWE header: " + e.getMessage(), 0);
        }
        Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (alg.equals(Algorithm.NONE)) {
            return PlainJWT.parse(s);
        }
        if (alg instanceof JWSAlgorithm) {
            return SignedJWT.parse(s);
        }
        if (alg instanceof JWEAlgorithm) {
            return EncryptedJWT.parse(s);
        }
        throw new AssertionError((Object)("Unexpected algorithm type: " + alg));
    }

    private JWTParser() {
    }
}

