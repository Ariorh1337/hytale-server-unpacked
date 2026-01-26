/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.prf;

import com.google.crypto.tink.prf.PrfParameters;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class HkdfPrfParameters
extends PrfParameters {
    private static final int MIN_KEY_SIZE = 16;
    private final int keySizeBytes;
    private final HashType hashType;
    @Nullable
    private final Bytes salt;

    private HkdfPrfParameters(int keySizeBytes, HashType hashType, Bytes salt) {
        this.keySizeBytes = keySizeBytes;
        this.hashType = hashType;
        this.salt = salt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getKeySizeBytes() {
        return this.keySizeBytes;
    }

    public HashType getHashType() {
        return this.hashType;
    }

    @Nullable
    public Bytes getSalt() {
        return this.salt;
    }

    public boolean equals(Object o) {
        if (!(o instanceof HkdfPrfParameters)) {
            return false;
        }
        HkdfPrfParameters that = (HkdfPrfParameters)o;
        return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getHashType() == this.getHashType() && Objects.equals(that.getSalt(), this.getSalt());
    }

    public int hashCode() {
        return Objects.hash(HkdfPrfParameters.class, this.keySizeBytes, this.hashType, this.salt);
    }

    @Override
    public boolean hasIdRequirement() {
        return false;
    }

    public String toString() {
        return "HKDF PRF Parameters (hashType: " + this.hashType + ", salt: " + this.salt + ", and " + this.keySizeBytes + "-byte key)";
    }

    @Immutable
    public static final class HashType {
        public static final HashType SHA1 = new HashType("SHA1");
        public static final HashType SHA224 = new HashType("SHA224");
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
        private Integer keySizeBytes = null;
        @Nullable
        private HashType hashType = null;
        @Nullable
        private Bytes salt = null;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
            if (keySizeBytes < 16) {
                throw new InvalidAlgorithmParameterException(String.format("Invalid key size %d; only 128-bit or larger are supported", keySizeBytes * 8));
            }
            this.keySizeBytes = keySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHashType(HashType hashType) {
            this.hashType = hashType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setSalt(Bytes salt) {
            if (salt.size() == 0) {
                this.salt = null;
                return this;
            }
            this.salt = salt;
            return this;
        }

        public HkdfPrfParameters build() throws GeneralSecurityException {
            if (this.keySizeBytes == null) {
                throw new GeneralSecurityException("key size is not set");
            }
            if (this.hashType == null) {
                throw new GeneralSecurityException("hash type is not set");
            }
            return new HkdfPrfParameters(this.keySizeBytes, this.hashType, this.salt);
        }
    }
}

