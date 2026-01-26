/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.JwtSignatureParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import java.util.Optional;

public final class JwtRsaSsaPkcs1Parameters
extends JwtSignatureParameters {
    public static final BigInteger F4 = BigInteger.valueOf(65537L);
    private final int modulusSizeBits;
    private final BigInteger publicExponent;
    private final KidStrategy kidStrategy;
    private final Algorithm algorithm;

    private JwtRsaSsaPkcs1Parameters(int modulusSizeBits, BigInteger publicExponent, KidStrategy kidStrategy, Algorithm algorithm) {
        this.modulusSizeBits = modulusSizeBits;
        this.publicExponent = publicExponent;
        this.kidStrategy = kidStrategy;
        this.algorithm = algorithm;
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

    public KidStrategy getKidStrategy() {
        return this.kidStrategy;
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public boolean allowKidAbsent() {
        return this.kidStrategy.equals(KidStrategy.CUSTOM) || this.kidStrategy.equals(KidStrategy.IGNORED);
    }

    public boolean equals(Object o) {
        if (!(o instanceof JwtRsaSsaPkcs1Parameters)) {
            return false;
        }
        JwtRsaSsaPkcs1Parameters that = (JwtRsaSsaPkcs1Parameters)o;
        return that.getModulusSizeBits() == this.getModulusSizeBits() && Objects.equals(that.getPublicExponent(), this.getPublicExponent()) && that.kidStrategy.equals(this.kidStrategy) && that.algorithm.equals(this.algorithm);
    }

    public int hashCode() {
        return Objects.hash(JwtRsaSsaPkcs1Parameters.class, this.modulusSizeBits, this.publicExponent, this.kidStrategy, this.algorithm);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.kidStrategy.equals(KidStrategy.BASE64_ENCODED_KEY_ID);
    }

    public String toString() {
        return "JWT RSA SSA PKCS1 Parameters (kidStrategy: " + this.kidStrategy + ", algorithm " + this.algorithm + ", publicExponent: " + this.publicExponent + ", and " + this.modulusSizeBits + "-bit modulus)";
    }

    @Immutable
    public static final class KidStrategy {
        public static final KidStrategy BASE64_ENCODED_KEY_ID = new KidStrategy("BASE64_ENCODED_KEY_ID");
        public static final KidStrategy IGNORED = new KidStrategy("IGNORED");
        public static final KidStrategy CUSTOM = new KidStrategy("CUSTOM");
        private final String name;

        private KidStrategy(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    @Immutable
    public static final class Algorithm {
        public static final Algorithm RS256 = new Algorithm("RS256");
        public static final Algorithm RS384 = new Algorithm("RS384");
        public static final Algorithm RS512 = new Algorithm("RS512");
        private final String name;

        private Algorithm(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getStandardName() {
            return this.name;
        }
    }

    public static final class Builder {
        Optional<Integer> modulusSizeBits = Optional.empty();
        Optional<BigInteger> publicExponent = Optional.of(F4);
        Optional<KidStrategy> kidStrategy = Optional.empty();
        Optional<Algorithm> algorithm = Optional.empty();
        private static final BigInteger TWO = BigInteger.valueOf(2L);
        private static final BigInteger PUBLIC_EXPONENT_UPPER_BOUND = TWO.pow(256);

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setModulusSizeBits(int modulusSizeBits) {
            this.modulusSizeBits = Optional.of(modulusSizeBits);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setPublicExponent(BigInteger e) {
            this.publicExponent = Optional.of(e);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setKidStrategy(KidStrategy kidStrategy) {
            this.kidStrategy = Optional.of(kidStrategy);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setAlgorithm(Algorithm algorithm) {
            this.algorithm = Optional.of(algorithm);
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

        public JwtRsaSsaPkcs1Parameters build() throws GeneralSecurityException {
            if (!this.modulusSizeBits.isPresent()) {
                throw new GeneralSecurityException("key size is not set");
            }
            if (!this.publicExponent.isPresent()) {
                throw new GeneralSecurityException("publicExponent is not set");
            }
            if (!this.algorithm.isPresent()) {
                throw new GeneralSecurityException("Algorithm must be set");
            }
            if (!this.kidStrategy.isPresent()) {
                throw new GeneralSecurityException("KidStrategy must be set");
            }
            if (this.modulusSizeBits.get() < 2048) {
                throw new InvalidAlgorithmParameterException(String.format("Invalid modulus size in bits %d; must be at least 2048 bits", this.modulusSizeBits.get()));
            }
            this.validatePublicExponent(this.publicExponent.get());
            return new JwtRsaSsaPkcs1Parameters(this.modulusSizeBits.get(), this.publicExponent.get(), this.kidStrategy.get(), this.algorithm.get());
        }
    }
}

