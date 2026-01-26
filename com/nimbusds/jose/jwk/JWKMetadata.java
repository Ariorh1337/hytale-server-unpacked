/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyRevocation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;
import com.nimbusds.jwt.util.DateUtils;
import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class JWKMetadata {
    JWKMetadata() {
    }

    static KeyType parseKeyType(Map<String, Object> o) throws ParseException {
        try {
            return KeyType.parse(JSONObjectUtils.getString(o, "kty"));
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    static KeyUse parseKeyUse(Map<String, Object> o) throws ParseException {
        return KeyUse.parse(JSONObjectUtils.getString(o, "use"));
    }

    static Set<KeyOperation> parseKeyOperations(Map<String, Object> o) throws ParseException {
        return KeyOperation.parse(JSONObjectUtils.getStringList(o, "key_ops"));
    }

    static Algorithm parseAlgorithm(Map<String, Object> o) throws ParseException {
        return Algorithm.parse(JSONObjectUtils.getString(o, "alg"));
    }

    static String parseKeyID(Map<String, Object> o) throws ParseException {
        return JSONObjectUtils.getString(o, "kid");
    }

    static URI parseX509CertURL(Map<String, Object> o) throws ParseException {
        return JSONObjectUtils.getURI(o, "x5u");
    }

    static Base64URL parseX509CertThumbprint(Map<String, Object> o) throws ParseException {
        return JSONObjectUtils.getBase64URL(o, "x5t");
    }

    static Base64URL parseX509CertSHA256Thumbprint(Map<String, Object> o) throws ParseException {
        return JSONObjectUtils.getBase64URL(o, "x5t#S256");
    }

    static List<Base64> parseX509CertChain(Map<String, Object> o) throws ParseException {
        List<Base64> chain = X509CertChainUtils.toBase64List(JSONObjectUtils.getJSONArray(o, "x5c"));
        if (chain == null || !chain.isEmpty()) {
            return chain;
        }
        return null;
    }

    static Date parseExpirationTime(Map<String, Object> o) throws ParseException {
        if (o.get("exp") == null) {
            return null;
        }
        return DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(o, "exp"));
    }

    static Date parseNotBeforeTime(Map<String, Object> o) throws ParseException {
        if (o.get("nbf") == null) {
            return null;
        }
        return DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(o, "nbf"));
    }

    static Date parseIssueTime(Map<String, Object> o) throws ParseException {
        if (o.get("iat") == null) {
            return null;
        }
        return DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(o, "iat"));
    }

    static KeyRevocation parseKeyRevocation(Map<String, Object> o) throws ParseException {
        if (o.get("revoked") == null) {
            return null;
        }
        return KeyRevocation.parse(JSONObjectUtils.getJSONObject(o, "revoked"));
    }
}

