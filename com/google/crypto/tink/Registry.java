/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.Catalogue;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.PrivateKeyManager;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.TinkProtoParametersFormat;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.prf.Prf;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public final class Registry {
    private static final Logger logger = Logger.getLogger(Registry.class.getName());
    private static final ConcurrentMap<String, Catalogue<?>> catalogueMap = new ConcurrentHashMap();
    private static final Set<Class<?>> ALLOWED_PRIMITIVES = Collections.unmodifiableSet(Registry.createAllowedPrimitives());

    static synchronized void reset() {
        KeyManagerRegistry.resetGlobalInstanceTestOnly();
        MutablePrimitiveRegistry.resetGlobalInstanceTestOnly();
        catalogueMap.clear();
    }

    @Deprecated
    public static synchronized void addCatalogue(String catalogueName, Catalogue<?> catalogue) throws GeneralSecurityException {
        if (catalogueName == null) {
            throw new IllegalArgumentException("catalogueName must be non-null.");
        }
        if (catalogue == null) {
            throw new IllegalArgumentException("catalogue must be non-null.");
        }
        if (catalogueMap.containsKey(catalogueName.toLowerCase(Locale.US))) {
            Catalogue existing = (Catalogue)catalogueMap.get(catalogueName.toLowerCase(Locale.US));
            if (!catalogue.getClass().getName().equals(existing.getClass().getName())) {
                logger.warning("Attempted overwrite of a catalogueName catalogue for name " + catalogueName);
                throw new GeneralSecurityException("catalogue for name " + catalogueName + " has been already registered");
            }
        }
        catalogueMap.put(catalogueName.toLowerCase(Locale.US), catalogue);
    }

    @Deprecated
    public static Catalogue<?> getCatalogue(String catalogueName) throws GeneralSecurityException {
        if (catalogueName == null) {
            throw new IllegalArgumentException("catalogueName must be non-null.");
        }
        Catalogue catalogue = (Catalogue)catalogueMap.get(catalogueName.toLowerCase(Locale.US));
        if (catalogue == null) {
            String error = String.format("no catalogue found for %s. ", catalogueName);
            if (catalogueName.toLowerCase(Locale.US).startsWith("tinkaead")) {
                error = error + "Maybe call AeadConfig.register().";
            }
            if (catalogueName.toLowerCase(Locale.US).startsWith("tinkdeterministicaead")) {
                error = error + "Maybe call DeterministicAeadConfig.register().";
            } else if (catalogueName.toLowerCase(Locale.US).startsWith("tinkstreamingaead")) {
                error = error + "Maybe call StreamingAeadConfig.register().";
            } else if (catalogueName.toLowerCase(Locale.US).startsWith("tinkhybriddecrypt") || catalogueName.toLowerCase(Locale.US).startsWith("tinkhybridencrypt")) {
                error = error + "Maybe call HybridConfig.register().";
            } else if (catalogueName.toLowerCase(Locale.US).startsWith("tinkmac")) {
                error = error + "Maybe call MacConfig.register().";
            } else if (catalogueName.toLowerCase(Locale.US).startsWith("tinkpublickeysign") || catalogueName.toLowerCase(Locale.US).startsWith("tinkpublickeyverify")) {
                error = error + "Maybe call SignatureConfig.register().";
            } else if (catalogueName.toLowerCase(Locale.US).startsWith("tink")) {
                error = error + "Maybe call TinkConfig.register().";
            }
            throw new GeneralSecurityException(error);
        }
        return catalogue;
    }

    public static synchronized <P> void registerKeyManager(KeyManager<P> manager) throws GeneralSecurityException {
        Registry.registerKeyManager(manager, true);
    }

    private static Set<Class<?>> createAllowedPrimitives() {
        HashSet result = new HashSet();
        result.add(Aead.class);
        result.add(DeterministicAead.class);
        result.add(StreamingAead.class);
        result.add(HybridEncrypt.class);
        result.add(HybridDecrypt.class);
        result.add(Mac.class);
        result.add(Prf.class);
        result.add(PublicKeySign.class);
        result.add(PublicKeyVerify.class);
        return result;
    }

    public static synchronized <P> void registerKeyManager(KeyManager<P> manager, boolean newKeyAllowed) throws GeneralSecurityException {
        if (manager == null) {
            throw new IllegalArgumentException("key manager must be non-null.");
        }
        if (!ALLOWED_PRIMITIVES.contains(manager.getPrimitiveClass())) {
            throw new GeneralSecurityException("Registration of key managers for class " + manager.getPrimitiveClass() + " has been disabled. Please file an issue on https://github.com/tink-crypto/tink-java");
        }
        if (!TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS.isCompatible()) {
            throw new GeneralSecurityException("Registering key managers is not supported in FIPS mode");
        }
        KeyManagerRegistry.globalInstance().registerKeyManager(manager, newKeyAllowed);
    }

    @Deprecated
    public static synchronized <P> void registerKeyManager(String typeUrl, KeyManager<P> manager) throws GeneralSecurityException {
        Registry.registerKeyManager(typeUrl, manager, true);
    }

    @Deprecated
    public static synchronized <P> void registerKeyManager(String typeUrl, KeyManager<P> manager, boolean newKeyAllowed) throws GeneralSecurityException {
        if (manager == null) {
            throw new IllegalArgumentException("key manager must be non-null.");
        }
        if (!typeUrl.equals(manager.getKeyType())) {
            throw new GeneralSecurityException("Manager does not support key type " + typeUrl + ".");
        }
        Registry.registerKeyManager(manager, newKeyAllowed);
    }

    @Deprecated
    public static <P> KeyManager<P> getKeyManager(String typeUrl, Class<P> primitiveClass) throws GeneralSecurityException {
        return KeyManagerRegistry.globalInstance().getKeyManager(typeUrl, primitiveClass);
    }

    @Deprecated
    public static KeyManager<?> getUntypedKeyManager(String typeUrl) throws GeneralSecurityException {
        return KeyManagerRegistry.globalInstance().getUntypedKeyManager(typeUrl);
    }

    @Deprecated
    public static synchronized KeyData newKeyData(KeyTemplate keyTemplate) throws GeneralSecurityException {
        KeyManager<?> manager = KeyManagerRegistry.globalInstance().getUntypedKeyManager(keyTemplate.getTypeUrl());
        if (KeyManagerRegistry.globalInstance().isNewKeyAllowed(keyTemplate.getTypeUrl())) {
            return manager.newKeyData(keyTemplate.getValue());
        }
        throw new GeneralSecurityException("newKey-operation not permitted for key type " + keyTemplate.getTypeUrl());
    }

    @Deprecated
    public static synchronized KeyData newKeyData(com.google.crypto.tink.KeyTemplate keyTemplate) throws GeneralSecurityException {
        byte[] serializedKeyTemplate = TinkProtoParametersFormat.serialize(keyTemplate.toParameters());
        try {
            return Registry.newKeyData(KeyTemplate.parseFrom(serializedKeyTemplate, ExtensionRegistryLite.getEmptyRegistry()));
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Failed to parse serialized parameters", e);
        }
    }

    @Deprecated
    public static synchronized MessageLite newKey(KeyTemplate keyTemplate) throws GeneralSecurityException {
        KeyManager<?> manager = Registry.getUntypedKeyManager(keyTemplate.getTypeUrl());
        if (KeyManagerRegistry.globalInstance().isNewKeyAllowed(keyTemplate.getTypeUrl())) {
            return manager.newKey(keyTemplate.getValue());
        }
        throw new GeneralSecurityException("newKey-operation not permitted for key type " + keyTemplate.getTypeUrl());
    }

    @Deprecated
    public static synchronized MessageLite newKey(String typeUrl, MessageLite format) throws GeneralSecurityException {
        KeyManager<?> manager = Registry.getUntypedKeyManager(typeUrl);
        if (KeyManagerRegistry.globalInstance().isNewKeyAllowed(typeUrl)) {
            return manager.newKey(format);
        }
        throw new GeneralSecurityException("newKey-operation not permitted for key type " + typeUrl);
    }

    @Deprecated
    public static KeyData getPublicKeyData(String typeUrl, ByteString serializedPrivateKey) throws GeneralSecurityException {
        KeyManager<?> manager = Registry.getUntypedKeyManager(typeUrl);
        if (!(manager instanceof PrivateKeyManager)) {
            throw new GeneralSecurityException("manager for key type " + typeUrl + " is not a PrivateKeyManager");
        }
        return ((PrivateKeyManager)manager).getPublicKeyData(serializedPrivateKey);
    }

    @Deprecated
    public static <P> P getPrimitive(String typeUrl, MessageLite key, Class<P> primitiveClass) throws GeneralSecurityException {
        KeyManager<P> manager = KeyManagerRegistry.globalInstance().getKeyManager(typeUrl, primitiveClass);
        return manager.getPrimitive(key.toByteString());
    }

    @Deprecated
    public static <P> P getPrimitive(String typeUrl, ByteString serializedKey, Class<P> primitiveClass) throws GeneralSecurityException {
        KeyManager<P> manager = KeyManagerRegistry.globalInstance().getKeyManager(typeUrl, primitiveClass);
        return manager.getPrimitive(serializedKey);
    }

    @Deprecated
    public static <P> P getPrimitive(String typeUrl, byte[] serializedKey, Class<P> primitiveClass) throws GeneralSecurityException {
        return Registry.getPrimitive(typeUrl, ByteString.copyFrom(serializedKey), primitiveClass);
    }

    @Deprecated
    public static <P> P getPrimitive(KeyData keyData, Class<P> primitiveClass) throws GeneralSecurityException {
        return Registry.getPrimitive(keyData.getTypeUrl(), keyData.getValue(), primitiveClass);
    }

    static <KeyT extends Key, P> P getFullPrimitive(KeyT key, Class<P> primitiveClass) throws GeneralSecurityException {
        return MutablePrimitiveRegistry.globalInstance().getPrimitive(key, primitiveClass);
    }

    public static synchronized List<String> keyTemplates() {
        return MutableParametersRegistry.globalInstance().getNames();
    }

    public static synchronized void restrictToFipsIfEmpty() throws GeneralSecurityException {
        KeyManagerRegistry.globalInstance().restrictToFipsIfEmptyAndGlobalInstance();
    }

    private Registry() {
    }
}

