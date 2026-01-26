/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import java.security.GeneralSecurityException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public final class KeyManagerRegistry {
    private static final Logger logger = Logger.getLogger(KeyManagerRegistry.class.getName());
    private ConcurrentMap<String, KeyManager<?>> keyManagerMap;
    private ConcurrentMap<String, Boolean> newKeyAllowedMap;
    private static final KeyManagerRegistry GLOBAL_INSTANCE = new KeyManagerRegistry();

    public static KeyManagerRegistry globalInstance() {
        return GLOBAL_INSTANCE;
    }

    public static void resetGlobalInstanceTestOnly() {
        KeyManagerRegistry.GLOBAL_INSTANCE.keyManagerMap = new ConcurrentHashMap();
        KeyManagerRegistry.GLOBAL_INSTANCE.newKeyAllowedMap = new ConcurrentHashMap<String, Boolean>();
    }

    public KeyManagerRegistry(KeyManagerRegistry original) {
        this.keyManagerMap = new ConcurrentHashMap(original.keyManagerMap);
        this.newKeyAllowedMap = new ConcurrentHashMap<String, Boolean>(original.newKeyAllowedMap);
    }

    public KeyManagerRegistry() {
        this.keyManagerMap = new ConcurrentHashMap();
        this.newKeyAllowedMap = new ConcurrentHashMap<String, Boolean>();
    }

    private synchronized KeyManager<?> getKeyManagerOrThrow(String typeUrl) throws GeneralSecurityException {
        if (!this.keyManagerMap.containsKey(typeUrl)) {
            throw new GeneralSecurityException("No key manager found for key type " + typeUrl + ", see https://developers.google.com/tink/faq/registration_errors");
        }
        return (KeyManager)this.keyManagerMap.get(typeUrl);
    }

    private synchronized void insertKeyManager(KeyManager<?> manager, boolean forceOverwrite, boolean newKeyAllowed) throws GeneralSecurityException {
        String typeUrl = manager.getKeyType();
        if (newKeyAllowed && this.newKeyAllowedMap.containsKey(typeUrl) && !((Boolean)this.newKeyAllowedMap.get(typeUrl)).booleanValue()) {
            throw new GeneralSecurityException("New keys are already disallowed for key type " + typeUrl);
        }
        KeyManager existing = (KeyManager)this.keyManagerMap.get(typeUrl);
        if (existing != null && !existing.getClass().equals(manager.getClass())) {
            logger.warning("Attempted overwrite of a registered key manager for key type " + typeUrl);
            throw new GeneralSecurityException(String.format("typeUrl (%s) is already registered with %s, cannot be re-registered with %s", typeUrl, existing.getClass().getName(), manager.getClass().getName()));
        }
        if (!forceOverwrite) {
            this.keyManagerMap.putIfAbsent(typeUrl, manager);
        } else {
            this.keyManagerMap.put(typeUrl, manager);
        }
        this.newKeyAllowedMap.put(typeUrl, newKeyAllowed);
    }

    public synchronized <P> void registerKeyManager(KeyManager<P> manager, boolean newKeyAllowed) throws GeneralSecurityException {
        this.registerKeyManagerWithFipsCompatibility(manager, TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS, newKeyAllowed);
    }

    public synchronized <P> void registerKeyManagerWithFipsCompatibility(KeyManager<P> manager, TinkFipsUtil.AlgorithmFipsCompatibility compatibility, boolean newKeyAllowed) throws GeneralSecurityException {
        if (!compatibility.isCompatible()) {
            throw new GeneralSecurityException("Cannot register key manager: FIPS compatibility insufficient");
        }
        this.insertKeyManager(manager, false, newKeyAllowed);
    }

    public boolean typeUrlExists(String typeUrl) {
        return this.keyManagerMap.containsKey(typeUrl);
    }

    public <P> KeyManager<P> getKeyManager(String typeUrl, Class<P> primitiveClass) throws GeneralSecurityException {
        KeyManager<?> manager = this.getKeyManagerOrThrow(typeUrl);
        if (manager.getPrimitiveClass().equals(primitiveClass)) {
            return manager;
        }
        throw new GeneralSecurityException("Primitive type " + primitiveClass.getName() + " not supported by key manager of type " + manager.getClass() + ", which only supports: " + manager.getPrimitiveClass());
    }

    public KeyManager<?> getUntypedKeyManager(String typeUrl) throws GeneralSecurityException {
        return this.getKeyManagerOrThrow(typeUrl);
    }

    public boolean isNewKeyAllowed(String typeUrl) {
        return (Boolean)this.newKeyAllowedMap.get(typeUrl);
    }

    public boolean isEmpty() {
        return this.keyManagerMap.isEmpty();
    }

    public synchronized void restrictToFipsIfEmptyAndGlobalInstance() throws GeneralSecurityException {
        if (this != KeyManagerRegistry.globalInstance()) {
            throw new GeneralSecurityException("Only the global instance can be restricted to FIPS.");
        }
        if (TinkFipsUtil.useOnlyFips()) {
            return;
        }
        if (!this.isEmpty()) {
            throw new GeneralSecurityException("Could not enable FIPS mode as Registry is not empty.");
        }
        TinkFipsUtil.setFipsRestricted();
    }
}

