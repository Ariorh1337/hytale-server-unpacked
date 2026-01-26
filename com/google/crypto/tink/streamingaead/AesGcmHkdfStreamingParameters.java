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

public class AesGcmHkdfStreamingParameters
extends StreamingAeadParameters {
    private final Integer keySizeBytes;
    private final Integer derivedAesGcmKeySizeBytes;
    private final HashType hkdfHashType;
    private final Integer ciphertextSegmentSizeBytes;

    public static Builder builder() {
        return new Builder();
    }

    private AesGcmHkdfStreamingParameters(Integer keySizeBytes, Integer derivedAesGcmKeySizeBytes, HashType hkdfHashType, Integer ciphertextSegmentSizeBytes) {
        this.keySizeBytes = keySizeBytes;
        this.derivedAesGcmKeySizeBytes = derivedAesGcmKeySizeBytes;
        this.hkdfHashType = hkdfHashType;
        this.ciphertextSegmentSizeBytes = ciphertextSegmentSizeBytes;
    }

    public int getKeySizeBytes() {
        return this.keySizeBytes;
    }

    public int getDerivedAesGcmKeySizeBytes() {
        return this.derivedAesGcmKeySizeBytes;
    }

    public HashType getHkdfHashType() {
        return this.hkdfHashType;
    }

    public int getCiphertextSegmentSizeBytes() {
        return this.ciphertextSegmentSizeBytes;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AesGcmHkdfStreamingParameters)) {
            return false;
        }
        AesGcmHkdfStreamingParameters that = (AesGcmHkdfStreamingParameters)o;
        return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getDerivedAesGcmKeySizeBytes() == this.getDerivedAesGcmKeySizeBytes() && that.getHkdfHashType() == this.getHkdfHashType() && that.getCiphertextSegmentSizeBytes() == this.getCiphertextSegmentSizeBytes();
    }

    public int hashCode() {
        return Objects.hash(AesGcmHkdfStreamingParameters.class, this.keySizeBytes, this.derivedAesGcmKeySizeBytes, this.hkdfHashType, this.ciphertextSegmentSizeBytes);
    }

    public String toString() {
        return "AesGcmHkdfStreaming Parameters (IKM size: " + this.keySizeBytes + ", " + this.derivedAesGcmKeySizeBytes + "-byte AES GCM key, " + this.hkdfHashType + " for HKDF " + this.ciphertextSegmentSizeBytes + "-byte ciphertexts)";
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
        private Integer derivedAesGcmKeySizeBytes = null;
        @Nullable
        private HashType hkdfHashType = null;
        @Nullable
        private Integer ciphertextSegmentSizeBytes = null;

        @CanIgnoreReturnValue
        public Builder setKeySizeBytes(int keySizeBytes) {
            this.keySizeBytes = keySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setDerivedAesGcmKeySizeBytes(int derivedAesGcmKeySizeBytes) {
            this.derivedAesGcmKeySizeBytes = derivedAesGcmKeySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHkdfHashType(HashType hkdfHashType) {
            this.hkdfHashType = hkdfHashType;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setCiphertextSegmentSizeBytes(int ciphertextSegmentSizeBytes) {
            this.ciphertextSegmentSizeBytes = ciphertextSegmentSizeBytes;
            return this;
        }

        public AesGcmHkdfStreamingParameters build() throws GeneralSecurityException {
            if (this.keySizeBytes == null) {
                throw new GeneralSecurityException("keySizeBytes needs to be set");
            }
            if (this.derivedAesGcmKeySizeBytes == null) {
                throw new GeneralSecurityException("derivedAesGcmKeySizeBytes needs to be set");
            }
            if (this.hkdfHashType == null) {
                throw new GeneralSecurityException("hkdfHashType needs to be set");
            }
            if (this.ciphertextSegmentSizeBytes == null) {
                throw new GeneralSecurityException("ciphertextSegmentSizeBytes needs to be set");
            }
            if (this.derivedAesGcmKeySizeBytes != 16 && this.derivedAesGcmKeySizeBytes != 32) {
                throw new GeneralSecurityException("derivedAesGcmKeySizeBytes needs to be 16 or 32, not " + this.derivedAesGcmKeySizeBytes);
            }
            if (this.keySizeBytes < this.derivedAesGcmKeySizeBytes) {
                throw new GeneralSecurityException("keySizeBytes needs to be at least derivedAesGcmKeySizeBytes, i.e., " + this.derivedAesGcmKeySizeBytes);
            }
            if (this.ciphertextSegmentSizeBytes <= this.derivedAesGcmKeySizeBytes + 24) {
                throw new GeneralSecurityException("ciphertextSegmentSizeBytes needs to be at least derivedAesGcmKeySizeBytes + 25, i.e., " + (this.derivedAesGcmKeySizeBytes + 25));
            }
            return new AesGcmHkdfStreamingParameters(this.keySizeBytes, this.derivedAesGcmKeySizeBytes, this.hkdfHashType, this.ciphertextSegmentSizeBytes);
        }
    }
}

