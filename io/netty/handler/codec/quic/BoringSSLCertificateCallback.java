/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSL;
import io.netty.handler.codec.quic.BoringSSLKeylessPrivateKey;
import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicSslEngineMap;
import io.netty.util.CharsetUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;
import org.jetbrains.annotations.Nullable;

final class BoringSSLCertificateCallback {
    private static final byte[] BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
    private static final byte[] END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
    private static final byte TLS_CT_RSA_SIGN = 1;
    private static final byte TLS_CT_DSS_SIGN = 2;
    private static final byte TLS_CT_RSA_FIXED_DH = 3;
    private static final byte TLS_CT_DSS_FIXED_DH = 4;
    private static final byte TLS_CT_ECDSA_SIGN = 64;
    private static final byte TLS_CT_RSA_FIXED_ECDH = 65;
    private static final byte TLS_CT_ECDSA_FIXED_ECDH = 66;
    static final String KEY_TYPE_RSA = "RSA";
    static final String KEY_TYPE_DH_RSA = "DH_RSA";
    static final String KEY_TYPE_EC = "EC";
    static final String KEY_TYPE_EC_EC = "EC_EC";
    static final String KEY_TYPE_EC_RSA = "EC_RSA";
    private static final Map<String, String> DEFAULT_SERVER_KEY_TYPES = new HashMap<String, String>();
    private static final Set<String> DEFAULT_CLIENT_KEY_TYPES;
    private static final long[] NO_KEY_MATERIAL_CLIENT_SIDE;
    private final QuicheQuicSslEngineMap engineMap;
    private final X509ExtendedKeyManager keyManager;
    private final String password;
    private final Map<String, String> serverKeyTypes;
    private final Set<String> clientKeyTypes;

    BoringSSLCertificateCallback(QuicheQuicSslEngineMap engineMap, @Nullable X509ExtendedKeyManager keyManager, String password, Map<String, String> serverKeyTypes, Set<String> clientKeyTypes) {
        this.engineMap = engineMap;
        this.keyManager = keyManager;
        this.password = password;
        this.serverKeyTypes = serverKeyTypes != null ? serverKeyTypes : DEFAULT_SERVER_KEY_TYPES;
        this.clientKeyTypes = clientKeyTypes != null ? clientKeyTypes : DEFAULT_CLIENT_KEY_TYPES;
    }

    long @Nullable [] handle(long ssl, byte[] keyTypeBytes, byte @Nullable [][] asn1DerEncodedPrincipals, String[] authMethods) {
        QuicheQuicSslEngine engine = this.engineMap.get(ssl);
        if (engine == null) {
            return null;
        }
        try {
            if (this.keyManager == null) {
                if (engine.getUseClientMode()) {
                    return NO_KEY_MATERIAL_CLIENT_SIDE;
                }
                return null;
            }
            if (engine.getUseClientMode()) {
                X500Principal[] issuers;
                Set<String> keyTypesSet = this.supportedClientKeyTypes(keyTypeBytes);
                String[] keyTypes = keyTypesSet.toArray(new String[0]);
                if (asn1DerEncodedPrincipals == null) {
                    issuers = null;
                } else {
                    issuers = new X500Principal[asn1DerEncodedPrincipals.length];
                    for (int i = 0; i < asn1DerEncodedPrincipals.length; ++i) {
                        issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
                    }
                }
                return this.removeMappingIfNeeded(ssl, this.selectKeyMaterialClientSide(ssl, engine, keyTypes, issuers));
            }
            return this.removeMappingIfNeeded(ssl, this.selectKeyMaterialServerSide(ssl, engine, authMethods));
        }
        catch (SSLException e) {
            return null;
        }
        finally {
            this.engineMap.remove(ssl);
        }
    }

    private long @Nullable [] removeMappingIfNeeded(long ssl, long @Nullable [] result) {
        if (result == null) {
            this.engineMap.remove(ssl);
        }
        return result;
    }

    private long @Nullable [] selectKeyMaterialServerSide(long ssl, QuicheQuicSslEngine engine, String[] authMethods) throws SSLException {
        if (authMethods.length == 0) {
            throw new SSLHandshakeException("Unable to find key material");
        }
        HashSet<String> typeSet = new HashSet<String>(this.serverKeyTypes.size());
        for (String authMethod : authMethods) {
            String alias;
            String type = this.serverKeyTypes.get(authMethod);
            if (type == null || !typeSet.add(type) || (alias = this.chooseServerAlias(engine, type)) == null) continue;
            return this.selectMaterial(ssl, engine, alias);
        }
        throw new SSLHandshakeException("Unable to find key material for auth method(s): " + Arrays.toString(authMethods));
    }

    private long @Nullable [] selectKeyMaterialClientSide(long ssl, QuicheQuicSslEngine engine, String[] keyTypes, X500Principal @Nullable [] issuer) {
        String alias = this.chooseClientAlias(engine, keyTypes, issuer);
        if (alias != null) {
            return this.selectMaterial(ssl, engine, alias);
        }
        return NO_KEY_MATERIAL_CLIENT_SIDE;
    }

    private long @Nullable [] selectMaterial(long ssl, QuicheQuicSslEngine engine, String alias) {
        long key;
        Certificate[] certificates = this.keyManager.getCertificateChain(alias);
        if (certificates == null || certificates.length == 0) {
            return null;
        }
        byte[][] certs = new byte[certificates.length][];
        for (int i = 0; i < certificates.length; ++i) {
            try {
                certs[i] = certificates[i].getEncoded();
                continue;
            }
            catch (CertificateEncodingException e) {
                return null;
            }
        }
        PrivateKey privateKey = this.keyManager.getPrivateKey(alias);
        if (privateKey == BoringSSLKeylessPrivateKey.INSTANCE) {
            key = 0L;
        } else {
            byte[] pemKey = BoringSSLCertificateCallback.toPemEncoded(privateKey);
            if (pemKey == null) {
                return null;
            }
            key = BoringSSL.EVP_PKEY_parse(pemKey, this.password);
        }
        long chain = BoringSSL.CRYPTO_BUFFER_stack_new(ssl, certs);
        engine.setLocalCertificateChain(certificates);
        return new long[]{key, chain};
    }

    private static byte @Nullable [] toPemEncoded(PrivateKey key) {
        byte[] byArray;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(BEGIN_PRIVATE_KEY);
            out.write(Base64.getEncoder().encode(key.getEncoded()));
            out.write(END_PRIVATE_KEY);
            byArray = out.toByteArray();
        }
        catch (Throwable throwable) {
            try {
                try {
                    out.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                return null;
            }
        }
        out.close();
        return byArray;
    }

    @Nullable
    private String chooseClientAlias(QuicheQuicSslEngine engine, String[] keyTypes, X500Principal @Nullable [] issuer) {
        return this.keyManager.chooseEngineClientAlias(keyTypes, issuer, engine);
    }

    @Nullable
    private String chooseServerAlias(QuicheQuicSslEngine engine, String type) {
        return this.keyManager.chooseEngineServerAlias(type, null, engine);
    }

    private Set<String> supportedClientKeyTypes(byte @Nullable [] clientCertificateTypes) {
        if (clientCertificateTypes == null) {
            return this.clientKeyTypes;
        }
        HashSet<String> result = new HashSet<String>(clientCertificateTypes.length);
        for (byte keyTypeCode : clientCertificateTypes) {
            String keyType = BoringSSLCertificateCallback.clientKeyType(keyTypeCode);
            if (keyType == null) continue;
            result.add(keyType);
        }
        return result;
    }

    @Nullable
    private static String clientKeyType(byte clientCertificateType) {
        switch (clientCertificateType) {
            case 1: {
                return KEY_TYPE_RSA;
            }
            case 3: {
                return KEY_TYPE_DH_RSA;
            }
            case 64: {
                return KEY_TYPE_EC;
            }
            case 65: {
                return KEY_TYPE_EC_RSA;
            }
            case 66: {
                return KEY_TYPE_EC_EC;
            }
        }
        return null;
    }

    static {
        DEFAULT_SERVER_KEY_TYPES.put(KEY_TYPE_RSA, KEY_TYPE_RSA);
        DEFAULT_SERVER_KEY_TYPES.put("DHE_RSA", KEY_TYPE_RSA);
        DEFAULT_SERVER_KEY_TYPES.put("ECDHE_RSA", KEY_TYPE_RSA);
        DEFAULT_SERVER_KEY_TYPES.put("ECDHE_ECDSA", KEY_TYPE_EC);
        DEFAULT_SERVER_KEY_TYPES.put("ECDH_RSA", KEY_TYPE_EC_RSA);
        DEFAULT_SERVER_KEY_TYPES.put("ECDH_ECDSA", KEY_TYPE_EC_EC);
        DEFAULT_SERVER_KEY_TYPES.put(KEY_TYPE_DH_RSA, KEY_TYPE_DH_RSA);
        DEFAULT_CLIENT_KEY_TYPES = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(KEY_TYPE_RSA, KEY_TYPE_DH_RSA, KEY_TYPE_EC, KEY_TYPE_EC_RSA, KEY_TYPE_EC_EC)));
        NO_KEY_MATERIAL_CLIENT_SIDE = new long[]{0L, 0L};
    }
}

