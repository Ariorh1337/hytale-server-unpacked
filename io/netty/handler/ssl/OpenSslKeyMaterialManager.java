/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import java.util.Arrays;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslKeyMaterialManager {
    static final String KEY_TYPE_RSA = "RSA";
    static final String KEY_TYPE_DH_RSA = "DH_RSA";
    static final String KEY_TYPE_EC = "EC";
    static final String KEY_TYPE_EC_EC = "EC_EC";
    static final String KEY_TYPE_EC_RSA = "EC_RSA";
    private static final int TYPE_RSA = 1;
    private static final int TYPE_DH_RSA = 2;
    private static final int TYPE_EC = 4;
    private static final int TYPE_EC_EC = 8;
    private static final int TYPE_EC_RSA = 16;
    private final OpenSslKeyMaterialProvider provider;
    private final boolean hasTmpDhKeys;

    OpenSslKeyMaterialManager(OpenSslKeyMaterialProvider provider, boolean hasTmpDhKeys) {
        this.provider = provider;
        this.hasTmpDhKeys = hasTmpDhKeys;
    }

    void setKeyMaterialServerSide(ReferenceCountedOpenSslEngine engine) throws SSLException {
        Object[] authMethods = engine.authMethods();
        if (authMethods.length == 0) {
            throw new SSLHandshakeException("Unable to find key material");
        }
        int seenTypes = 0;
        for (String string : authMethods) {
            int typeBit = OpenSslKeyMaterialManager.resolveKeyTypeBit(string);
            if (typeBit == 0 || (seenTypes & typeBit) != 0) continue;
            seenTypes |= typeBit;
            String keyType = OpenSslKeyMaterialManager.keyTypeString(typeBit);
            String alias = this.chooseServerAlias(engine, keyType);
            if (alias == null) continue;
            this.setKeyMaterial(engine, alias);
            return;
        }
        if (this.hasTmpDhKeys && authMethods.length == 1 && ("DH_anon".equals(authMethods[0]) || "ECDH_anon".equals(authMethods[0]))) {
            return;
        }
        throw new SSLHandshakeException("Unable to find key material for auth method(s): " + Arrays.toString(authMethods));
    }

    private static int resolveKeyTypeBit(String authMethod) {
        switch (authMethod) {
            case "RSA": 
            case "DHE_RSA": 
            case "ECDHE_RSA": {
                return 1;
            }
            case "DH_RSA": {
                return 2;
            }
            case "ECDHE_ECDSA": {
                return 4;
            }
            case "ECDH_ECDSA": {
                return 8;
            }
            case "ECDH_RSA": {
                return 16;
            }
        }
        return 0;
    }

    private static String keyTypeString(int typeBit) {
        switch (typeBit) {
            case 1: {
                return KEY_TYPE_RSA;
            }
            case 2: {
                return KEY_TYPE_DH_RSA;
            }
            case 4: {
                return KEY_TYPE_EC;
            }
            case 8: {
                return KEY_TYPE_EC_EC;
            }
            case 16: {
                return KEY_TYPE_EC_RSA;
            }
        }
        return null;
    }

    void setKeyMaterialClientSide(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) throws SSLException {
        String alias = this.chooseClientAlias(engine, keyTypes, issuer);
        if (alias != null) {
            this.setKeyMaterial(engine, alias);
        }
    }

    private void setKeyMaterial(ReferenceCountedOpenSslEngine engine, String alias) throws SSLException {
        OpenSslKeyMaterial keyMaterial = null;
        try {
            keyMaterial = this.provider.chooseKeyMaterial(engine.alloc, alias);
            if (keyMaterial == null) {
                return;
            }
            engine.setKeyMaterial(keyMaterial);
        }
        catch (SSLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SSLException(e);
        }
        finally {
            if (keyMaterial != null) {
                keyMaterial.release();
            }
        }
    }

    private String chooseClientAlias(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) {
        X509KeyManager manager = this.provider.keyManager();
        if (manager instanceof X509ExtendedKeyManager) {
            return ((X509ExtendedKeyManager)manager).chooseEngineClientAlias(keyTypes, issuer, engine);
        }
        return manager.chooseClientAlias(keyTypes, issuer, null);
    }

    private String chooseServerAlias(ReferenceCountedOpenSslEngine engine, String type) {
        X509KeyManager manager = this.provider.keyManager();
        if (manager instanceof X509ExtendedKeyManager) {
            return ((X509ExtendedKeyManager)manager).chooseEngineServerAlias(type, null, engine);
        }
        return manager.chooseServerAlias(type, null, null);
    }
}

