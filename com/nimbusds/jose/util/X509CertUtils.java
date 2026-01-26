/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

public class X509CertUtils {
    public static final String PEM_BEGIN_MARKER = "-----BEGIN CERTIFICATE-----";
    public static final String PEM_END_MARKER = "-----END CERTIFICATE-----";
    private static Provider jcaProvider;

    public static Provider getProvider() {
        return jcaProvider;
    }

    public static void setProvider(Provider provider) {
        jcaProvider = provider;
    }

    public static X509Certificate parse(byte[] derEncodedCert) {
        try {
            return X509CertUtils.parseWithException(derEncodedCert);
        }
        catch (CertificateException e) {
            return null;
        }
    }

    public static X509Certificate parseWithException(byte[] derEncodedCert) throws CertificateException {
        if (derEncodedCert == null || derEncodedCert.length == 0) {
            return null;
        }
        CertificateFactory cf = jcaProvider != null ? CertificateFactory.getInstance("X.509", jcaProvider) : CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(new ByteArrayInputStream(derEncodedCert));
        if (!(cert instanceof X509Certificate)) {
            throw new CertificateException("Not a X.509 certificate: " + cert.getType());
        }
        return (X509Certificate)cert;
    }

    public static X509Certificate parse(String pemEncodedCert) {
        if (pemEncodedCert == null || pemEncodedCert.isEmpty()) {
            return null;
        }
        int markerStart = pemEncodedCert.indexOf(PEM_BEGIN_MARKER);
        if (markerStart < 0) {
            return null;
        }
        String buf = pemEncodedCert.substring(markerStart + PEM_BEGIN_MARKER.length());
        int markerEnd = buf.indexOf(PEM_END_MARKER);
        if (markerEnd < 0) {
            return null;
        }
        buf = buf.substring(0, markerEnd);
        buf = buf.replaceAll("\\s", "");
        return X509CertUtils.parse(new Base64(buf).decode());
    }

    public static X509Certificate parseWithException(String pemEncodedCert) throws CertificateException {
        if (pemEncodedCert == null || pemEncodedCert.isEmpty()) {
            return null;
        }
        int markerStart = pemEncodedCert.indexOf(PEM_BEGIN_MARKER);
        if (markerStart < 0) {
            throw new CertificateException("PEM begin marker not found");
        }
        String buf = pemEncodedCert.substring(markerStart + PEM_BEGIN_MARKER.length());
        int markerEnd = buf.indexOf(PEM_END_MARKER);
        if (markerEnd < 0) {
            throw new CertificateException("PEM end marker not found");
        }
        buf = buf.substring(0, markerEnd);
        buf = buf.replaceAll("\\s", "");
        return X509CertUtils.parseWithException(new Base64(buf).decode());
    }

    public static String toPEMString(X509Certificate cert) {
        return X509CertUtils.toPEMString(cert, true);
    }

    public static String toPEMString(X509Certificate cert, boolean withLineBreaks) {
        StringBuilder sb = new StringBuilder();
        sb.append(PEM_BEGIN_MARKER);
        if (withLineBreaks) {
            sb.append('\n');
        }
        try {
            sb.append(Base64.encode(cert.getEncoded()));
        }
        catch (CertificateEncodingException e) {
            return null;
        }
        if (withLineBreaks) {
            sb.append('\n');
        }
        sb.append(PEM_END_MARKER);
        return sb.toString();
    }

    public static Base64URL computeSHA256Thumbprint(X509Certificate cert) {
        try {
            byte[] derEncodedCert = cert.getEncoded();
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return Base64URL.encode(sha256.digest(derEncodedCert));
        }
        catch (NoSuchAlgorithmException | CertificateEncodingException e) {
            return null;
        }
    }

    public static UUID store(KeyStore keyStore, PrivateKey privateKey, char[] keyPassword, X509Certificate cert) throws KeyStoreException {
        UUID alias = UUID.randomUUID();
        keyStore.setKeyEntry(alias.toString(), privateKey, keyPassword, new Certificate[]{cert});
        return alias;
    }
}

