/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.signature.RsaSsaPssParameters;
import com.google.crypto.tink.signature.SignaturePublicKey;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class RsaSsaPssPublicKey
extends SignaturePublicKey {
    private final RsaSsaPssParameters parameters;
    private final BigInteger modulus;
    private final Bytes outputPrefix;
    @Nullable
    private final Integer idRequirement;

    private RsaSsaPssPublicKey(RsaSsaPssParameters parameters, BigInteger modulus, Bytes outputPrefix, @Nullable Integer idRequirement) {
        this.parameters = parameters;
        this.modulus = modulus;
        this.outputPrefix = outputPrefix;
        this.idRequirement = idRequirement;
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public static Builder builder() {
        return new Builder();
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public BigInteger getModulus() {
        return this.modulus;
    }

    @Override
    public Bytes getOutputPrefix() {
        return this.outputPrefix;
    }

    @Override
    public RsaSsaPssParameters getParameters() {
        return this.parameters;
    }

    @Override
    @Nullable
    public Integer getIdRequirementOrNull() {
        return this.idRequirement;
    }

    @Override
    public boolean equalsKey(Key o) {
        if (!(o instanceof RsaSsaPssPublicKey)) {
            return false;
        }
        RsaSsaPssPublicKey that = (RsaSsaPssPublicKey)o;
        return that.parameters.equals(this.parameters) && that.modulus.equals(this.modulus) && Objects.equals(that.idRequirement, this.idRequirement);
    }

    public static class Builder {
        @Nullable
        private RsaSsaPssParameters parameters = null;
        @Nullable
        private BigInteger modulus = null;
        @Nullable
        private Integer idRequirement = null;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setParameters(RsaSsaPssParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setModulus(BigInteger modulus) {
            this.modulus = modulus;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setIdRequirement(@Nullable Integer idRequirement) {
            this.idRequirement = idRequirement;
            return this;
        }

        private Bytes getOutputPrefix() {
            if (this.parameters.getVariant() == RsaSsaPssParameters.Variant.NO_PREFIX) {
                return OutputPrefixUtil.EMPTY_PREFIX;
            }
            if (this.parameters.getVariant() == RsaSsaPssParameters.Variant.LEGACY || this.parameters.getVariant() == RsaSsaPssParameters.Variant.CRUNCHY) {
                return OutputPrefixUtil.getLegacyOutputPrefix(this.idRequirement);
            }
            if (this.parameters.getVariant() == RsaSsaPssParameters.Variant.TINK) {
                return OutputPrefixUtil.getTinkOutputPrefix(this.idRequirement);
            }
            throw new IllegalStateException("Unknown RsaSsaPssParameters.Variant: " + this.parameters.getVariant());
        }

        public RsaSsaPssPublicKey build() throws GeneralSecurityException {
            int paramModulusSize;
            if (this.parameters == null) {
                throw new GeneralSecurityException("Cannot build without parameters");
            }
            if (this.modulus == null) {
                throw new GeneralSecurityException("Cannot build without modulus");
            }
            int modulusSize = this.modulus.bitLength();
            if (modulusSize != (paramModulusSize = this.parameters.getModulusSizeBits())) {
                throw new GeneralSecurityException("Got modulus size " + modulusSize + ", but parameters requires modulus size " + paramModulusSize);
            }
            if (this.parameters.hasIdRequirement() && this.idRequirement == null) {
                throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            }
            if (!this.parameters.hasIdRequirement() && this.idRequirement != null) {
                throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            }
            Bytes outputPrefix = this.getOutputPrefix();
            return new RsaSsaPssPublicKey(this.parameters, this.modulus, outputPrefix, this.idRequirement);
        }
    }
}

