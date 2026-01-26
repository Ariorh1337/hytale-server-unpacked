/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature;

import com.google.crypto.tink.signature.SignatureParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class RsaSsaPssParameters
extends SignatureParameters {
    public static final BigInteger F4 = BigInteger.valueOf(65537L);
    private final int modulusSizeBits;
    private final BigInteger publicExponent;
    private final Variant variant;
    private final HashType sigHashType;
    private final HashType mgf1HashType;
    private final int saltLengthBytes;

    private RsaSsaPssParameters(int modulusSizeBits, BigInteger publicExponent, Variant variant, HashType sigHashType, HashType mgf1HashType, int saltLengthBytes) {
        this.modulusSizeBits = modulusSizeBits;
        this.publicExponent = publicExponent;
        this.variant = variant;
        this.sigHashType = sigHashType;
        this.mgf1HashType = mgf1HashType;
        this.saltLengthBytes = saltLengthBytes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getModulusSizeBits() {
        return this.modulusSizeBits;
    }

    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public HashType getSigHashType() {
        return this.sigHashType;
    }

    public HashType getMgf1HashType() {
        return this.mgf1HashType;
    }

    public int getSaltLengthBytes() {
        return this.saltLengthBytes;
    }

    public boolean equals(Object o) {
        if (!(o instanceof RsaSsaPssParameters)) {
            return false;
        }
        RsaSsaPssParameters that = (RsaSsaPssParameters)o;
        return that.getModulusSizeBits() == this.getModulusSizeBits() && Objects.equals(that.getPublicExponent(), this.getPublicExponent()) && Objects.equals(that.getVariant(), this.getVariant()) && Objects.equals(that.getSigHashType(), this.getSigHashType()) && Objects.equals(that.getMgf1HashType(), this.getMgf1HashType()) && that.getSaltLengthBytes() == this.getSaltLengthBytes();
    }

    public int hashCode() {
        return Objects.hash(RsaSsaPssParameters.class, this.modulusSizeBits, this.publicExponent, this.variant, this.sigHashType, this.mgf1HashType, this.saltLengthBytes);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public String toString() {
        return "RSA SSA PSS Parameters (variant: " + this.variant + ", signature hashType: " + this.sigHashType + ", mgf1 hashType: " + this.mgf1HashType + ", saltLengthBytes: " + this.saltLengthBytes + ", publicExponent: " + this.publicExponent + ", and " + this.modulusSizeBits + "-bit modulus)";
    }

    @Immutable
    public static final class Variant {
        public static final Variant TINK = new Variant("TINK");
        public static final Variant CRUNCHY = new Variant("CRUNCHY");
        public static final Variant LEGACY = new Variant("LEGACY");
        public static final Variant NO_PREFIX = new Variant("NO_PREFIX");
        private final String name;

        private Variant(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Immutable
    public static final class HashType {
        public static final HashType SHA256 = new HashType("SHA256");
        public static final HashType SHA384 = new HashType("SHA384");
        public static final HashType SHA512 = new HashType("SHA512");
        private final String name;

        private HashType(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public static final class Builder {
        @Nullable
        private Integer modulusSizeBits = null;
        @Nullable
        private BigInteger publicExponent = F4;
        @Nullable
        private HashType sigHashType = null;
        @Nullable
        private HashType mgf1HashType = null;
        @Nullable
        private Integer saltLengthBytes = null;
        private Variant variant = Variant.NO_PREFIX;
        private static final BigInteger TWO = BigInteger.valueOf(2L);
        private static final BigInteger PUBLIC_EXPONENT_UPPER_BOUND = TWO.pow(256);
        private static final int MIN_RSA_MODULUS_SIZE = 2048;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setModulusSizeBits(int modulusSizeBits) {
            this.modulusSizeBits = modulusSizeBits;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setPublicExponent(BigInteger e) {
            this.publicExponent = e;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setVariant(Variant variant) {
            this.variant = variant;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setSigHashType(HashType sigHashType) {
            this.sigHashType = sigHashType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setMgf1HashType(HashType mgf1HashType) {
            this.mgf1HashType = mgf1HashType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setSaltLengthBytes(int saltLengthBytes) throws GeneralSecurityException {
            if (saltLengthBytes < 0) {
                throw new GeneralSecurityException(String.format("Invalid salt length in bytes %d; salt length must be positive", saltLengthBytes));
            }
            this.saltLengthBytes = saltLengthBytes;
            return this;
        }

        private void validatePublicExponent(BigInteger publicExponent) throws InvalidAlgorithmParameterException {
            int c = publicExponent.compareTo(F4);
            if (c == 0) {
                return;
            }
            if (c < 0) {
                throw new InvalidAlgorithmParameterException("Public exponent must be at least 65537.");
            }
            if (publicExponent.mod(TWO).equals(BigInteger.ZERO)) {
                throw new InvalidAlgorithmParameterException("Invalid public exponent");
            }
            if (publicExponent.compareTo(PUBLIC_EXPONENT_UPPER_BOUND) > 0) {
                throw new InvalidAlgorithmParameterException("Public exponent cannot be larger than 2^256.");
            }
        }

        public RsaSsaPssParameters build() throws GeneralSecurityException {
            if (this.modulusSizeBits == null) {
                throw new GeneralSecurityException("key size is not set");
            }
            if (this.publicExponent == null) {
                throw new GeneralSecurityException("publicExponent is not set");
            }
            if (this.sigHashType == null) {
                throw new GeneralSecurityException("signature hash type is not set");
            }
            if (this.mgf1HashType == null) {
                throw new GeneralSecurityException("mgf1 hash type is not set");
            }
            if (this.variant == null) {
                throw new GeneralSecurityException("variant is not set");
            }
            if (this.saltLengthBytes == null) {
                throw new GeneralSecurityException("salt length is not set");
            }
            if (this.modulusSizeBits < 2048) {
                throw new InvalidAlgorithmParameterException(String.format("Invalid key size in bytes %d; must be at least %d bits", this.modulusSizeBits, 2048));
            }
            if (this.sigHashType != this.mgf1HashType) {
                throw new GeneralSecurityException("MGF1 hash is different from signature hash");
            }
            this.validatePublicExponent(this.publicExponent);
            return new RsaSsaPssParameters(this.modulusSizeBits, this.publicExponent, this.variant, this.sigHashType, this.mgf1HashType, this.saltLengthBytes);
        }
    }
}

