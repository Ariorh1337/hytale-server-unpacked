/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.SecretJWK;
import java.security.Key;
import java.security.KeyPair;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class KeyConverter {
    public static List<Key> toJavaKeys(List<JWK> jwkList) {
        if (jwkList == null) {
            return Collections.emptyList();
        }
        LinkedList<Key> out = new LinkedList<Key>();
        for (JWK jwk : jwkList) {
            try {
                if (jwk instanceof AsymmetricJWK) {
                    KeyPair keyPair = ((AsymmetricJWK)((Object)jwk)).toKeyPair();
                    out.add(keyPair.getPublic());
                    if (keyPair.getPrivate() == null) continue;
                    out.add(keyPair.getPrivate());
                    continue;
                }
                if (!(jwk instanceof SecretJWK)) continue;
                out.add(((SecretJWK)((Object)jwk)).toSecretKey());
            }
            catch (JOSEException jOSEException) {}
        }
        return out;
    }
}

