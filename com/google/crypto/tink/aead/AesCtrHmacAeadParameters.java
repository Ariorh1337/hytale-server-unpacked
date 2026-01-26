/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.aead.AeadParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class AesCtrHmacAeadParameters
extends AeadParameters {
    private static final int PREFIX_SIZE_IN_BYTES = 5;
    private final int aesKeySizeBytes;
    private final int hmacKeySizeBytes;
    private final int ivSizeBytes;
    private final int tagSizeBytes;
    private final Variant variant;
    private final HashType hashType;

    private AesCtrHmacAeadParameters(int aesKeySizeBytes, int hmacKeySizeBytes, int ivSizeBytes, int tagSizeBytes, Variant variant, HashType hashType) {
        this.aesKeySizeBytes = aesKeySizeBytes;
        this.hmacKeySizeBytes = hmacKeySizeBytes;
        this.ivSizeBytes = ivSizeBytes;
        this.tagSizeBytes = tagSizeBytes;
        this.variant = variant;
        this.hashType = hashType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getAesKeySizeBytes() {
        return this.aesKeySizeBytes;
    }

    public int getHmacKeySizeBytes() {
        return this.hmacKeySizeBytes;
    }

    public int getTagSizeBytes() {
        return this.tagSizeBytes;
    }

    public int getIvSizeBytes() {
        return this.ivSizeBytes;
    }

    public int getCiphertextOverheadSizeBytes() {
        if (this.variant == Variant.NO_PREFIX) {
            return this.getTagSizeBytes() + this.getIvSizeBytes();
        }
        if (this.variant == Variant.TINK || this.variant == Variant.CRUNCHY) {
            return this.getTagSizeBytes() + this.getIvSizeBytes() + 5;
        }
        throw new IllegalStateException("Unknown variant");
    }

    public Variant getVariant() {
        return this.variant;
    }

    public HashType getHashType() {
        return this.hashType;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AesCtrHmacAeadParameters)) {
            return false;
        }
        AesCtrHmacAeadParameters that = (AesCtrHmacAeadParameters)o;
        return that.getAesKeySizeBytes() == this.getAesKeySizeBytes() && that.getHmacKeySizeBytes() == this.getHmacKeySizeBytes() && that.getIvSizeBytes() == this.getIvSizeBytes() && that.getTagSizeBytes() == this.getTagSizeBytes() && that.getVariant() == this.getVariant() && that.getHashType() == this.getHashType();
    }

    public int hashCode() {
        return Objects.hash(AesCtrHmacAeadParameters.class, this.aesKeySizeBytes, this.hmacKeySizeBytes, this.ivSizeBytes, this.tagSizeBytes, this.variant, this.hashType);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public String toString() {
        return "AesCtrHmacAead Parameters (variant: " + this.variant + ", hashType: " + this.hashType + ", " + this.ivSizeBytes + "-byte IV, and " + this.tagSizeBytes + "-byte tags, and " + this.aesKeySizeBytes + "-byte AES key, and " + this.hmacKeySizeBytes + "-byte HMAC key)";
    }

    @Immutable
    public static final class Variant {
        public static final Variant TINK = new Variant("TINK");
        public static final Variant CRUNCHY = new Variant("CRUNCHY");
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
        private Integer aesKeySizeBytes = null;
        @Nullable
        private Integer hmacKeySizeBytes = null;
        @Nullable
        private Integer ivSizeBytes = null;
        @Nullable
        private Integer tagSizeBytes = null;
        private HashType hashType = null;
        private Variant variant = Variant.NO_PREFIX;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setAesKeySizeBytes(int aesKeySizeBytes) throws GeneralSecurityException {
            if (aesKeySizeBytes != 16 && aesKeySizeBytes != 24 && aesKeySizeBytes != 32) {
                throw new InvalidAlgorithmParameterException(String.format("Invalid key size %d; only 16-byte, 24-byte and 32-byte AES keys are supported", aesKeySizeBytes));
            }
            this.aesKeySizeBytes = aesKeySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setHmacKeySizeBytes(int hmacKeySizeBytes) throws GeneralSecurityException {
            if (hmacKeySizeBytes < 16) {
                throw new InvalidAlgorithmParameterException(String.format("Invalid key size in bytes %d; HMAC key must be at least 16 bytes", hmacKeySizeBytes));
            }
            this.hmacKeySizeBytes = hmacKeySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setIvSizeBytes(int ivSizeBytes) throws GeneralSecurityException {
            if (ivSizeBytes < 12 || ivSizeBytes > 16) {
                throw new GeneralSecurityException(String.format("Invalid IV size in bytes %d; IV size must be between 12 and 16 bytes", ivSizeBytes));
            }
            this.ivSizeBytes = ivSizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setTagSizeBytes(int tagSizeBytes) throws GeneralSecurityException {
            if (tagSizeBytes < 10) {
                throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; must be at least 10 bytes", tagSizeBytes));
            }
            this.tagSizeBytes = tagSizeBytes;
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

        private static void validateTagSizeBytes(int tagSizeBytes, HashType hashType) throws GeneralSecurityException {
            if (hashType == HashType.SHA1) {
                if (tagSizeBytes > 20) {
                    throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 20 bytes for SHA1", tagSizeBytes));
                }
                return;
            }
            if (hashType == HashType.SHA224) {
                if (tagSizeBytes > 28) {
                    throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 28 bytes for SHA224", tagSizeBytes));
                }
                return;
            }
            if (hashType == HashType.SHA256) {
                if (tagSizeBytes > 32) {
                    throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 32 bytes for SHA256", tagSizeBytes));
                }
                return;
            }
            if (hashType == HashType.SHA384) {
                if (tagSizeBytes > 48) {
                    throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 48 bytes for SHA384", tagSizeBytes));
                }
                return;
            }
            if (hashType == HashType.SHA512) {
                if (tagSizeBytes > 64) {
                    throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 64 bytes for SHA512", tagSizeBytes));
                }
                return;
            }
            throw new GeneralSecurityException("unknown hash type; must be SHA1, SHA224, SHA256, SHA384 or SHA512");
        }

        public AesCtrHmacAeadParameters build() throws GeneralSecurityException {
            if (this.aesKeySizeBytes == null) {
                throw new GeneralSecurityException("AES key size is not set");
            }
            if (this.hmacKeySizeBytes == null) {
                throw new GeneralSecurityException("HMAC key size is not set");
            }
            if (this.ivSizeBytes == null) {
                throw new GeneralSecurityException("iv size is not set");
            }
            if (this.tagSizeBytes == null) {
                throw new GeneralSecurityException("tag size is not set");
            }
            if (this.hashType == null) {
                throw new GeneralSecurityException("hash type is not set");
            }
            if (this.variant == null) {
                throw new GeneralSecurityException("variant is not set");
            }
            Builder.validateTagSizeBytes(this.tagSizeBytes, this.hashType);
            return new AesCtrHmacAeadParameters(this.aesKeySizeBytes, this.hmacKeySizeBytes, this.ivSizeBytes, this.tagSizeBytes, this.variant, this.hashType);
        }
    }
}

