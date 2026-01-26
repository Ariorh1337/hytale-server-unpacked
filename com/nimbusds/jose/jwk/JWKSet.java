/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.PasswordLookup;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.StandardCharset;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Immutable
public class JWKSet
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String MIME_TYPE = "application/jwk-set+json; charset=UTF-8";
    private final List<JWK> keys;
    private final Map<String, Object> customMembers;

    public JWKSet() {
        this(Collections.emptyList());
    }

    public JWKSet(JWK key) {
        this(Collections.singletonList(Objects.requireNonNull(key, "The JWK must not be null")));
    }

    public JWKSet(List<JWK> keys) {
        this(keys, Collections.emptyMap());
    }

    public JWKSet(List<JWK> keys, Map<String, Object> customMembers) {
        this.keys = Collections.unmodifiableList(Objects.requireNonNull(keys, "The JWK list must not be null"));
        this.customMembers = Collections.unmodifiableMap(customMembers);
    }

    public List<JWK> getKeys() {
        return this.keys;
    }

    public boolean isEmpty() {
        return this.keys.isEmpty();
    }

    public int size() {
        return this.keys.size();
    }

    public JWK getKeyByKeyId(String kid) {
        for (JWK key : this.getKeys()) {
            if (key.getKeyID() == null || !key.getKeyID().equals(kid)) continue;
            return key;
        }
        return null;
    }

    public boolean containsJWK(JWK jwk) throws JOSEException {
        Base64URL thumbprint = jwk.computeThumbprint();
        for (JWK k : this.getKeys()) {
            if (!thumbprint.equals(k.computeThumbprint())) continue;
            return true;
        }
        return false;
    }

    public Map<String, Object> getAdditionalMembers() {
        return this.customMembers;
    }

    public JWKSet toPublicJWKSet() {
        LinkedList<JWK> publicKeyList = new LinkedList<JWK>();
        for (JWK key : this.keys) {
            JWK publicKey = key.toPublicJWK();
            if (publicKey == null) continue;
            publicKeyList.add(publicKey);
        }
        return new JWKSet(publicKeyList, this.customMembers);
    }

    public JWKSet filter(JWKMatcher jwkMatcher) {
        LinkedList<JWK> matches = new LinkedList<JWK>();
        for (JWK key : this.keys) {
            if (!jwkMatcher.matches(key)) continue;
            matches.add(key);
        }
        return new JWKSet(matches, this.customMembers);
    }

    public boolean containsNonPublicKeys() {
        for (JWK jwk : this.getKeys()) {
            if (!jwk.isPrivate()) continue;
            return true;
        }
        return false;
    }

    public Map<String, Object> toJSONObject() {
        return this.toJSONObject(true);
    }

    public Map<String, Object> toJSONObject(boolean publicKeysOnly) {
        Map<String, Object> o = JSONObjectUtils.newJSONObject();
        o.putAll(this.customMembers);
        List<Object> a = JSONArrayUtils.newJSONArray();
        for (JWK key : this.keys) {
            if (publicKeysOnly) {
                JWK publicKey = key.toPublicJWK();
                if (publicKey == null) continue;
                a.add(publicKey.toJSONObject());
                continue;
            }
            a.add(key.toJSONObject());
        }
        o.put("keys", a);
        return o;
    }

    public String toString(boolean publicKeysOnly) {
        return JSONObjectUtils.toJSONString(this.toJSONObject(publicKeysOnly));
    }

    public String toString() {
        return this.toString(true);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JWKSet)) {
            return false;
        }
        JWKSet jwkSet = (JWKSet)o;
        return this.getKeys().equals(jwkSet.getKeys()) && this.customMembers.equals(jwkSet.customMembers);
    }

    public int hashCode() {
        return Objects.hash(this.getKeys(), this.customMembers);
    }

    public static JWKSet parse(String s) throws ParseException {
        return JWKSet.parse(JSONObjectUtils.parse(s));
    }

    public static JWKSet parse(Map<String, Object> json) throws ParseException {
        List<Object> keyArray = JSONObjectUtils.getJSONArray(json, "keys");
        if (keyArray == null) {
            throw new ParseException("Missing required \"keys\" member", 0);
        }
        LinkedList<JWK> keys = new LinkedList<JWK>();
        for (int i = 0; i < keyArray.size(); ++i) {
            try {
                Map keyJSONObject = (Map)keyArray.get(i);
                keys.add(JWK.parse(keyJSONObject));
                continue;
            }
            catch (ClassCastException e) {
                throw new ParseException("The \"keys\" JSON array must contain JSON objects only", 0);
            }
            catch (ParseException e) {
                if (e.getMessage() != null && e.getMessage().startsWith("Unsupported key type")) continue;
                throw new ParseException("Invalid JWK at position " + i + ": " + e.getMessage(), 0);
            }
        }
        HashMap<String, Object> additionalMembers = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            if (entry.getKey() == null || entry.getKey().equals("keys")) continue;
            additionalMembers.put(entry.getKey(), entry.getValue());
        }
        return new JWKSet(keys, additionalMembers);
    }

    public static JWKSet load(InputStream inputStream) throws IOException, ParseException {
        return JWKSet.parse(IOUtils.readInputStreamToString(inputStream, StandardCharset.UTF_8));
    }

    public static JWKSet load(File file) throws IOException, ParseException {
        return JWKSet.parse(IOUtils.readFileToString(file, StandardCharset.UTF_8));
    }

    public static JWKSet load(URL url, int connectTimeout, int readTimeout, int sizeLimit) throws IOException, ParseException {
        return JWKSet.load(url, connectTimeout, readTimeout, sizeLimit, null);
    }

    public static JWKSet load(URL url, int connectTimeout, int readTimeout, int sizeLimit, Proxy proxy) throws IOException, ParseException {
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectTimeout, readTimeout, sizeLimit);
        resourceRetriever.setProxy(proxy);
        Resource resource = resourceRetriever.retrieveResource(url);
        return JWKSet.parse(resource.getContent());
    }

    public static JWKSet load(URL url) throws IOException, ParseException {
        return JWKSet.load(url, 0, 0, 0);
    }

    public static JWKSet load(KeyStore keyStore, PasswordLookup pwLookup) throws KeyStoreException {
        char[] keyPassword;
        String keyAlias;
        LinkedList<JWK> jwks = new LinkedList<JWK>();
        Enumeration<String> keyAliases = keyStore.aliases();
        while (keyAliases.hasMoreElements()) {
            ECKey ecJWK;
            keyAlias = keyAliases.nextElement();
            char[] cArray = keyPassword = pwLookup == null ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);
            Certificate cert = keyStore.getCertificate(keyAlias);
            if (cert == null) continue;
            if (cert.getPublicKey() instanceof RSAPublicKey) {
                RSAKey rsaJWK;
                try {
                    rsaJWK = RSAKey.load(keyStore, keyAlias, keyPassword);
                }
                catch (JOSEException e) {
                    continue;
                }
                if (rsaJWK == null) continue;
                jwks.add(rsaJWK);
                continue;
            }
            if (!(cert.getPublicKey() instanceof ECPublicKey)) continue;
            try {
                ecJWK = ECKey.load(keyStore, keyAlias, keyPassword);
            }
            catch (JOSEException e) {
                continue;
            }
            if (ecJWK == null) continue;
            jwks.add(ecJWK);
        }
        keyAliases = keyStore.aliases();
        while (keyAliases.hasMoreElements()) {
            OctetSequenceKey octJWK;
            keyAlias = keyAliases.nextElement();
            keyPassword = pwLookup == null ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);
            try {
                octJWK = OctetSequenceKey.load(keyStore, keyAlias, keyPassword);
            }
            catch (JOSEException e) {
                continue;
            }
            if (octJWK == null) continue;
            jwks.add(octJWK);
        }
        return new JWKSet(jwks);
    }
}

