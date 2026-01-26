/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.internal.KeyCreator;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.LegacyProtoParameters;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class MutableKeyCreationRegistry {
    private final Map<Class<? extends Parameters>, KeyCreator<? extends Parameters>> creators = new HashMap<Class<? extends Parameters>, KeyCreator<? extends Parameters>>();
    private static final KeyCreator<LegacyProtoParameters> LEGACY_PROTO_KEY_CREATOR = MutableKeyCreationRegistry::createProtoKeyFromProtoParameters;
    private static final MutableKeyCreationRegistry globalInstance = MutableKeyCreationRegistry.newRegistryWithLegacyFallback();

    private static LegacyProtoKey createProtoKeyFromProtoParameters(LegacyProtoParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
        KeyTemplate keyTemplate = parameters.getSerialization().getKeyTemplate();
        KeyManager<?> manager = KeyManagerRegistry.globalInstance().getUntypedKeyManager(keyTemplate.getTypeUrl());
        if (!KeyManagerRegistry.globalInstance().isNewKeyAllowed(keyTemplate.getTypeUrl())) {
            throw new GeneralSecurityException("Creating new keys is not allowed.");
        }
        KeyData keyData = manager.newKeyData(keyTemplate.getValue());
        ProtoKeySerialization protoSerialization = ProtoKeySerialization.create(keyData.getTypeUrl(), keyData.getValue(), keyData.getKeyMaterialType(), keyTemplate.getOutputPrefixType(), idRequirement);
        return new LegacyProtoKey(protoSerialization, InsecureSecretKeyAccess.get());
    }

    private static MutableKeyCreationRegistry newRegistryWithLegacyFallback() {
        MutableKeyCreationRegistry registry = new MutableKeyCreationRegistry();
        try {
            registry.add(LEGACY_PROTO_KEY_CREATOR, LegacyProtoParameters.class);
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException("unexpected error.", e);
        }
        return registry;
    }

    public static MutableKeyCreationRegistry globalInstance() {
        return globalInstance;
    }

    public synchronized <ParametersT extends Parameters> void add(KeyCreator<ParametersT> creator, Class<ParametersT> parametersClass) throws GeneralSecurityException {
        KeyCreator<? extends Parameters> existingCreator = this.creators.get(parametersClass);
        if (existingCreator != null && !existingCreator.equals(creator)) {
            throw new GeneralSecurityException("Different key creator for parameters class " + parametersClass + " already inserted");
        }
        this.creators.put(parametersClass, creator);
    }

    public Key createKey(Parameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
        return this.createKeyTyped(parameters, idRequirement);
    }

    private synchronized <ParametersT extends Parameters> Key createKeyTyped(ParametersT parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
        Class<?> parametersClass = parameters.getClass();
        KeyCreator<? extends Parameters> creator = this.creators.get(parametersClass);
        if (creator == null) {
            throw new GeneralSecurityException("Cannot create a new key for parameters " + parameters + ": no key creator for this class was registered.");
        }
        KeyCreator<? extends Parameters> castCreator = creator;
        return castCreator.createKey(parameters, idRequirement);
    }
}

