/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.streamingaead.StreamingAeadParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public class AesCtrHmacStreamingParameters
extends StreamingAeadParameters {
    private final Integer keySizeBytes;
    private final Integer derivedKeySizeBytes;
    private final HashType hkdfHashType;
    private final HashType hmacHashType;
    private final Integer hmacTagSizeBytes;
    private final Integer ciphertextSegmentSizeBytes;

    public static Builder builder() {
        return new Builder();
    }

    private AesCtrHmacStreamingParameters(Integer keySizeBytes, Integer derivedKeySizeBytes, HashType hkdfHashType, HashType hmacHashType, Integer hmacTagSizeBytes, Integer ciphertextSegmentSizeBytes) {
        this.keySizeBytes = keySizeBytes;
        this.derivedKeySizeBytes = derivedKeySizeBytes;
        this.hkdfHashType = hkdfHashType;
        this.hmacHashType = hmacHashType;
        this.hmacTagSizeBytes = hmacTagSizeBytes;
        this.ciphertextSegmentSizeBytes = ciphertextSegmentSizeBytes;
    }

    public int getKeySizeBytes() {
        return this.keySizeBytes;
    }

    public int getDerivedKeySizeBytes() {
        return this.derivedKeySizeBytes;
    }

    public HashType getHkdfHashType() {
        return this.hkdfHashType;
    }

    public HashType getHmacHashType() {
        return this.hmacHashType;
    }

    public int getHmacTagSizeBytes() {
        return this.hmacTagSizeBytes;
    }

    public int getCiphertextSegmentSizeBytes() {
        return this.ciphertextSegmentSizeBytes;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AesCtrHmacStreamingParameters)) {
            return false;
        }
        AesCtrHmacStreamingParameters that = (AesCtrHmacStreamingParameters)o;
        return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getDerivedKeySizeBytes() == this.getDerivedKeySizeBytes() && that.getHkdfHashType() == this.getHkdfHashType() && that.getHmacHashType() == this.getHmacHashType() && that.getHmacTagSizeBytes() == this.getHmacTagSizeBytes() && that.getCiphertextSegmentSizeBytes() == this.getCiphertextSegmentSizeBytes();
    }

    public int hashCode() {
        return Objects.hash(AesCtrHmacStreamingParameters.class, this.keySizeBytes, this.derivedKeySizeBytes, this.hkdfHashType, this.hmacHashType, this.hmacTagSizeBytes, this.ciphertextSegmentSizeBytes);
    }

    public String toString() {
        return "AesCtrHmacStreaming Parameters (IKM size: " + this.keySizeBytes + ", " + this.derivedKeySizeBytes + "-byte AES key, " + this.hkdfHashType + " for HKDF, " + this.hkdfHashType + " for HMAC, " + this.hmacTagSizeBytes + "-byte tags, " + this.ciphertextSegmentSizeBytes + "-byte ciphertexts)";
    }

    @Immutable
    public static final class HashType {
        public static final HashType SHA1 = new HashType("SHA1");
        public static final HashType SHA256 = new HashType("SHA256");
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
        private Integer derivedKeySizeBytes = null;
        @Nullable
        private HashType hkdfHashType = null;
        @Nullable
        private HashType hmacHashType = null;
        @Nullable
        private Integer hmacTagSizeBytes = null;
        @Nullable
        private Integer ciphertextSegmentSizeBytes = null;

        @CanIgnoreReturnValue
        public Builder setKeySizeBytes(int keySizeBytes) {
            this.keySizeBytes = keySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setDerivedKeySizeBytes(int derivedKeySizeBytes) {
            this.derivedKeySizeBytes = derivedKeySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHkdfHashType(HashType hkdfHashType) {
            this.hkdfHashType = hkdfHashType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHmacHashType(HashType hmacHashType) {
            this.hmacHashType = hmacHashType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHmacTagSizeBytes(Integer hmacTagSizeBytes) {
            this.hmacTagSizeBytes = hmacTagSizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setCiphertextSegmentSizeBytes(int ciphertextSegmentSizeBytes) {
            this.ciphertextSegmentSizeBytes = ciphertextSegmentSizeBytes;
            return this;
        }

        public AesCtrHmacStreamingParameters build() throws GeneralSecurityException {
            if (this.keySizeBytes == null) {
                throw new GeneralSecurityException("keySizeBytes needs to be set");
            }
            if (this.derivedKeySizeBytes == null) {
                throw new GeneralSecurityException("derivedKeySizeBytes needs to be set");
            }
            if (this.hkdfHashType == null) {
                throw new GeneralSecurityException("hkdfHashType needs to be set");
            }
            if (this.hmacHashType == null) {
                throw new GeneralSecurityException("hmacHashType needs to be set");
            }
            if (this.hmacTagSizeBytes == null) {
                throw new GeneralSecurityException("hmacTagSizeBytes needs to be set");
            }
            if (this.ciphertextSegmentSizeBytes == null) {
                throw new GeneralSecurityException("ciphertextSegmentSizeBytes needs to be set");
            }
            if (this.derivedKeySizeBytes != 16 && this.derivedKeySizeBytes != 32) {
                throw new GeneralSecurityException("derivedKeySizeBytes needs to be 16 or 32, not " + this.derivedKeySizeBytes);
            }
            if (this.keySizeBytes < this.derivedKeySizeBytes) {
                throw new GeneralSecurityException("keySizeBytes needs to be at least derivedKeySizeBytes, i.e., " + this.derivedKeySizeBytes);
            }
            if (this.ciphertextSegmentSizeBytes <= this.derivedKeySizeBytes + this.hmacTagSizeBytes + 8) {
                throw new GeneralSecurityException("ciphertextSegmentSizeBytes needs to be at least derivedKeySizeBytes + hmacTagSizeBytes + 9, i.e., " + (this.derivedKeySizeBytes + this.hmacTagSizeBytes + 9));
            }
            int hmacTagSizeLowerBound = 10;
            int hmacTagSizeUpperBound = 0;
            if (this.hmacHashType == HashType.SHA1) {
                hmacTagSizeUpperBound = 20;
            }
            if (this.hmacHashType == HashType.SHA256) {
                hmacTagSizeUpperBound = 32;
            }
            if (this.hmacHashType == HashType.SHA512) {
                hmacTagSizeUpperBound = 64;
            }
            if (this.hmacTagSizeBytes < hmacTagSizeLowerBound || this.hmacTagSizeBytes > hmacTagSizeUpperBound) {
                throw new GeneralSecurityException("hmacTagSize must be in range [" + hmacTagSizeLowerBound + ", " + hmacTagSizeUpperBound + "], but is " + this.hmacTagSizeBytes);
            }
            return new AesCtrHmacStreamingParameters(this.keySizeBytes, this.derivedKeySizeBytes, this.hkdfHashType, this.hmacHashType, this.hmacTagSizeBytes, this.ciphertextSegmentSizeBytes);
        }
    }
}

