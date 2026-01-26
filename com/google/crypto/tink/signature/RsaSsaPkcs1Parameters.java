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

public final class RsaSsaPkcs1Parameters
extends SignatureParameters {
    public static final BigInteger F4 = BigInteger.valueOf(65537L);
    private final int modulusSizeBits;
    private final BigInteger publicExponent;
    private final Variant variant;
    private final HashType hashType;

    private RsaSsaPkcs1Parameters(int modulusSizeBits, BigInteger publicExponent, Variant variant, HashType hashType) {
        this.modulusSizeBits = modulusSizeBits;
        this.publicExponent = publicExponent;
        this.variant = variant;
        this.hashType = hashType;
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

    public HashType getHashType() {
        return this.hashType;
    }

    public boolean equals(Object o) {
        if (!(o instanceof RsaSsaPkcs1Parameters)) {
            return false;
        }
        RsaSsaPkcs1Parameters that = (RsaSsaPkcs1Parameters)o;
        return that.getModulusSizeBits() == this.getModulusSizeBits() && Objects.equals(that.getPublicExponent(), this.getPublicExponent()) && that.getVariant() == this.getVariant() && that.getHashType() == this.getHashType();
    }

    public int hashCode() {
        return Objects.hash(RsaSsaPkcs1Parameters.class, this.modulusSizeBits, this.publicExponent, this.variant, this.hashType);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public String toString() {
        return "RSA SSA PKCS1 Parameters (variant: " + this.variant + ", hashType: " + this.hashType + ", publicExponent: " + this.publicExponent + ", and " + this.modulusSizeBits + "-bit modulus)";
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
        private HashType hashType = null;
        private Variant variant = Variant.NO_PREFIX;
        private static final BigInteger TWO = BigInteger.valueOf(2L);
        private static final BigInteger PUBLIC_EXPONENT_UPPER_BOUND = TWO.pow(256);

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
        public Builder setHashType(HashType hashType) {
            this.hashType = hashType;
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

        public RsaSsaPkcs1Parameters build() throws GeneralSecurityException {
            if (this.modulusSizeBits == null) {
                throw new GeneralSecurityException("key size is not set");
            }
            if (this.publicExponent == null) {
                throw new GeneralSecurityException("publicExponent is not set");
            }
            if (this.hashType == null) {
                throw new GeneralSecurityException("hash type is not set");
            }
            if (this.variant == null) {
                throw new GeneralSecurityException("variant is not set");
            }
            if (this.modulusSizeBits < 2048) {
                throw new InvalidAlgorithmParameterException(String.format("Invalid key size in bytes %d; must be at least 2048 bits", this.modulusSizeBits));
            }
            this.validatePublicExponent(this.publicExponent);
            return new RsaSsaPkcs1Parameters(this.modulusSizeBits, this.publicExponent, this.variant, this.hashType);
        }
    }
}

