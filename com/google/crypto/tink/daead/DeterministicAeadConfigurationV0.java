/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.daead;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.daead.AesSivKey;
import com.google.crypto.tink.daead.DeterministicAeadWrapper;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.subtle.AesSiv;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;

class DeterministicAeadConfigurationV0 {
    private static final InternalConfiguration INTERNAL_CONFIGURATION = DeterministicAeadConfigurationV0.create();
    private static final int KEY_SIZE_IN_BYTES = 64;

    private DeterministicAeadConfigurationV0() {
    }

    private static InternalConfiguration create() {
        try {
            PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
            DeterministicAeadWrapper.registerToInternalPrimitiveRegistry(builder);
            builder.registerPrimitiveConstructor(PrimitiveConstructor.create(DeterministicAeadConfigurationV0::createAesSiv, AesSivKey.class, DeterministicAead.class));
            return InternalConfiguration.createFromPrimitiveRegistry(builder.allowReparsingLegacyKeys().build());
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Configuration get() throws GeneralSecurityException {
        if (TinkFipsUtil.useOnlyFips()) {
            throw new GeneralSecurityException("Cannot use non-FIPS-compliant DeterministicAeadConfigurationV0 in FIPS mode");
        }
        return INTERNAL_CONFIGURATION;
    }

    private static DeterministicAead createAesSiv(AesSivKey key) throws GeneralSecurityException {
        if (key.getParameters().getKeySizeBytes() != 64) {
            throw new InvalidAlgorithmParameterException("invalid key size: " + key.getParameters().getKeySizeBytes() + ". Valid keys must have " + 64 + " bytes.");
        }
        return AesSiv.create(key);
    }
}

