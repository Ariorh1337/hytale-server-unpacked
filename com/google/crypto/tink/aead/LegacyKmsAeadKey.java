/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.aead.AeadKey;
import com.google.crypto.tink.aead.LegacyKmsAeadParameters;
import com.google.crypto.tink.util.Bytes;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class LegacyKmsAeadKey
extends AeadKey {
    private final LegacyKmsAeadParameters parameters;
    private final Bytes outputPrefix;
    @Nullable
    private final Integer idRequirement;

    private LegacyKmsAeadKey(LegacyKmsAeadParameters parameters, Bytes outputPrefix, @Nullable Integer idRequirement) {
        this.parameters = parameters;
        this.outputPrefix = outputPrefix;
        this.idRequirement = idRequirement;
    }

    public static LegacyKmsAeadKey create(LegacyKmsAeadParameters parameters, @Nullable Integer idRequirement) throws GeneralSecurityException {
        Bytes outputPrefix;
        if (parameters.variant() == LegacyKmsAeadParameters.Variant.TINK) {
            if (idRequirement == null) {
                throw new GeneralSecurityException("For given Variant TINK the value of idRequirement must be non-null");
            }
            outputPrefix = Bytes.copyFrom(ByteBuffer.allocate(5).put((byte)1).putInt(idRequirement).array());
        } else if (parameters.variant() == LegacyKmsAeadParameters.Variant.NO_PREFIX) {
            if (idRequirement != null) {
                throw new GeneralSecurityException("For given Variant NO_PREFIX the value of idRequirement must be null");
            }
            outputPrefix = Bytes.copyFrom(new byte[0]);
        } else {
            throw new GeneralSecurityException("Unknown Variant: " + parameters.variant());
        }
        return new LegacyKmsAeadKey(parameters, outputPrefix, idRequirement);
    }

    public static LegacyKmsAeadKey create(LegacyKmsAeadParameters parameters) throws GeneralSecurityException {
        return LegacyKmsAeadKey.create(parameters, null);
    }

    @Override
    public Bytes getOutputPrefix() {
        return this.outputPrefix;
    }

    @Override
    public LegacyKmsAeadParameters getParameters() {
        return this.parameters;
    }

    @Override
    public Integer getIdRequirementOrNull() {
        return this.idRequirement;
    }

    @Override
    public boolean equalsKey(Key o) {
        if (!(o instanceof LegacyKmsAeadKey)) {
            return false;
        }
        LegacyKmsAeadKey that = (LegacyKmsAeadKey)o;
        return that.parameters.equals(this.parameters) && Objects.equals(that.idRequirement, this.idRequirement);
    }
}

