/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

public abstract class JOSEObject
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String MIME_TYPE_COMPACT = "application/jose; charset=UTF-8";
    @Deprecated
    public static final String MIME_TYPE_JS = "application/jose+json; charset=UTF-8";
    private Payload payload;
    private Base64URL[] parsedParts;

    protected JOSEObject() {
        this.payload = null;
        this.parsedParts = null;
    }

    protected JOSEObject(Payload payload) {
        this.payload = payload;
    }

    public abstract Header getHeader();

    protected void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Payload getPayload() {
        return this.payload;
    }

    protected void setParsedParts(Base64URL ... parts) {
        this.parsedParts = parts;
    }

    public Base64URL[] getParsedParts() {
        return this.parsedParts;
    }

    public String getParsedString() {
        if (this.parsedParts == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Base64URL part : this.parsedParts) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            if (part == null) continue;
            sb.append(part);
        }
        return sb.toString();
    }

    public abstract String serialize();

    public static Base64URL[] split(String s) throws ParseException {
        String t = s.trim();
        int dot1 = t.indexOf(".");
        if (dot1 == -1) {
            throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Missing part delimiters", 0);
        }
        int dot2 = t.indexOf(".", dot1 + 1);
        if (dot2 == -1) {
            throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Missing second delimiter", 0);
        }
        int dot3 = t.indexOf(".", dot2 + 1);
        if (dot3 == -1) {
            Base64URL[] parts = new Base64URL[]{new Base64URL(t.substring(0, dot1)), new Base64URL(t.substring(dot1 + 1, dot2)), new Base64URL(t.substring(dot2 + 1))};
            return parts;
        }
        int dot4 = t.indexOf(".", dot3 + 1);
        if (dot4 == -1) {
            throw new ParseException("Invalid serialized JWE object: Missing fourth delimiter", 0);
        }
        if (dot4 != -1 && t.indexOf(".", dot4 + 1) != -1) {
            throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Too many part delimiters", 0);
        }
        Base64URL[] parts = new Base64URL[]{new Base64URL(t.substring(0, dot1)), new Base64URL(t.substring(dot1 + 1, dot2)), new Base64URL(t.substring(dot2 + 1, dot3)), new Base64URL(t.substring(dot3 + 1, dot4)), new Base64URL(t.substring(dot4 + 1))};
        return parts;
    }

    public static JOSEObject parse(String s) throws ParseException {
        Map<String, Object> jsonObject;
        Base64URL[] parts = JOSEObject.split(s);
        try {
            jsonObject = JSONObjectUtils.parse(parts[0].decodeToString());
        }
        catch (ParseException e) {
            throw new ParseException("Invalid unsecured/JWS/JWE header: " + e.getMessage(), 0);
        }
        Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (alg.equals(Algorithm.NONE)) {
            return PlainObject.parse(s);
        }
        if (alg instanceof JWSAlgorithm) {
            return JWSObject.parse(s);
        }
        if (alg instanceof JWEAlgorithm) {
            return JWEObject.parse(s);
        }
        throw new AssertionError((Object)("Unexpected algorithm type: " + alg));
    }
}

