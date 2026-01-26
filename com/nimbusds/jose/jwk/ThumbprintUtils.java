/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

public final class ThumbprintUtils {
    public static Base64URL compute(JWK jwk) throws JOSEException {
        return ThumbprintUtils.compute("SHA-256", jwk);
    }

    public static Base64URL compute(String hashAlg, JWK jwk) throws JOSEException {
        LinkedHashMap<String, ?> orderedParams = jwk.getRequiredParams();
        return ThumbprintUtils.compute(hashAlg, orderedParams);
    }

    public static Base64URL compute(String hashAlg, LinkedHashMap<String, ?> params) throws JOSEException {
        MessageDigest md;
        String json = JSONObjectUtils.toJSONString(params);
        try {
            md = MessageDigest.getInstance(hashAlg);
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't compute JWK thumbprint: Unsupported hash algorithm: " + e.getMessage(), e);
        }
        md.update(json.getBytes(StandardCharset.UTF_8));
        return Base64URL.encode(md.digest());
    }
}

