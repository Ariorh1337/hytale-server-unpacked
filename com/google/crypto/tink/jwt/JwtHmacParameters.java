/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.JwtMacParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;

public class JwtHmacParameters
extends JwtMacParameters {
    private final int keySizeBytes;
    private final KidStrategy kidStrategy;
    private final Algorithm algorithm;

    public static Builder builder() {
        return new Builder();
    }

    private JwtHmacParameters(int keySizeBytes, KidStrategy kidStrategy, Algorithm algorithm) {
        this.keySizeBytes = keySizeBytes;
        this.kidStrategy = kidStrategy;
        this.algorithm = algorithm;
    }

    public int getKeySizeBytes() {
        return this.keySizeBytes;
    }

    public KidStrategy getKidStrategy() {
        return this.kidStrategy;
    }

    public Algorithm getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public boolean hasIdRequirement() {
        return this.kidStrategy.equals(KidStrategy.BASE64_ENCODED_KEY_ID);
    }

    @Override
    public boolean allowKidAbsent() {
        return this.kidStrategy.equals(KidStrategy.CUSTOM) || this.kidStrategy.equals(KidStrategy.IGNORED);
    }

    public boolean equals(Object o) {
        if (!(o instanceof JwtHmacParameters)) {
            return false;
        }
        JwtHmacParameters that = (JwtHmacParameters)o;
        return that.keySizeBytes == this.keySizeBytes && that.kidStrategy.equals(this.kidStrategy) && that.algorithm.equals(this.algorithm);
    }

    public int hashCode() {
        return Objects.hash(JwtHmacParameters.class, this.keySizeBytes, this.kidStrategy, this.algorithm);
    }

    public String toString() {
        return "JWT HMAC Parameters (kidStrategy: " + this.kidStrategy + ", Algorithm " + this.algorithm + ", and " + this.keySizeBytes + "-byte key)";
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
        public static final Algorithm HS256 = new Algorithm("HS256");
        public static final Algorithm HS384 = new Algorithm("HS384");
        public static final Algorithm HS512 = new Algorithm("HS512");
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
        Optional<Integer> keySizeBytes = Optional.empty();
        Optional<KidStrategy> kidStrategy = Optional.empty();
        Optional<Algorithm> algorithm = Optional.empty();

        @CanIgnoreReturnValue
        public Builder setKeySizeBytes(int keySizeBytes) {
            this.keySizeBytes = Optional.of(keySizeBytes);
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

        public JwtHmacParameters build() throws GeneralSecurityException {
            if (!this.keySizeBytes.isPresent()) {
                throw new GeneralSecurityException("Key Size must be set");
            }
            if (!this.algorithm.isPresent()) {
                throw new GeneralSecurityException("Algorithm must be set");
            }
            if (!this.kidStrategy.isPresent()) {
                throw new GeneralSecurityException("KidStrategy must be set");
            }
            if (this.keySizeBytes.get() < 16) {
                throw new GeneralSecurityException("Key size must be at least 16 bytes");
            }
            return new JwtHmacParameters(this.keySizeBytes.get(), this.kidStrategy.get(), this.algorithm.get());
        }

        private Builder() {
        }
    }
}

