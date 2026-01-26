/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

class PEMEncodedKeyParser {
    private static final JcaPEMKeyConverter pemConverter = new JcaPEMKeyConverter();

    private PEMEncodedKeyParser() {
    }

    static List<KeyPair> parseKeys(String pemEncodedKeys) throws JOSEException {
        StringReader pemReader = new StringReader(pemEncodedKeys);
        PEMParser parser = new PEMParser(pemReader);
        ArrayList<KeyPair> keys = new ArrayList<KeyPair>();
        try {
            Object pemObj;
            do {
                if ((pemObj = parser.readObject()) instanceof SubjectPublicKeyInfo) {
                    keys.add(PEMEncodedKeyParser.toKeyPair((SubjectPublicKeyInfo)pemObj));
                    continue;
                }
                if (pemObj instanceof X509CertificateHolder) {
                    keys.add(PEMEncodedKeyParser.toKeyPair((X509CertificateHolder)pemObj));
                    continue;
                }
                if (pemObj instanceof PEMKeyPair) {
                    keys.add(PEMEncodedKeyParser.toKeyPair((PEMKeyPair)pemObj));
                    continue;
                }
                if (!(pemObj instanceof PrivateKeyInfo)) continue;
                keys.add(PEMEncodedKeyParser.toKeyPair((PrivateKeyInfo)pemObj));
            } while (pemObj != null);
            return keys;
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    private static KeyPair toKeyPair(SubjectPublicKeyInfo spki) throws PEMException {
        return new KeyPair(pemConverter.getPublicKey(spki), null);
    }

    private static KeyPair toKeyPair(X509CertificateHolder pemObj) throws PEMException {
        SubjectPublicKeyInfo spki = pemObj.getSubjectPublicKeyInfo();
        return new KeyPair(pemConverter.getPublicKey(spki), null);
    }

    private static KeyPair toKeyPair(PEMKeyPair pair) throws PEMException {
        return pemConverter.getKeyPair(pair);
    }

    private static KeyPair toKeyPair(PrivateKeyInfo pki) throws PEMException, NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKey = pemConverter.getPrivateKey(pki);
        if (privateKey instanceof RSAPrivateCrtKey) {
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(((RSAPrivateCrtKey)privateKey).getModulus(), ((RSAPrivateCrtKey)privateKey).getPublicExponent());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return new KeyPair(publicKey, privateKey);
        }
        return new KeyPair(null, privateKey);
    }
}

