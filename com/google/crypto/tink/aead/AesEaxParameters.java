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

public final class AesEaxParameters
extends AeadParameters {
    private final int keySizeBytes;
    private final int ivSizeBytes;
    private final int tagSizeBytes;
    private final Variant variant;

    private AesEaxParameters(int keySizeBytes, int ivSizeBytes, int tagSizeBytes, Variant variant) {
        this.keySizeBytes = keySizeBytes;
        this.ivSizeBytes = ivSizeBytes;
        this.tagSizeBytes = tagSizeBytes;
        this.variant = variant;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getKeySizeBytes() {
        return this.keySizeBytes;
    }

    public int getIvSizeBytes() {
        return this.ivSizeBytes;
    }

    public int getTagSizeBytes() {
        return this.tagSizeBytes;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AesEaxParameters)) {
            return false;
        }
        AesEaxParameters that = (AesEaxParameters)o;
        return that.getKeySizeBytes() == this.getKeySizeBytes() && that.getIvSizeBytes() == this.getIvSizeBytes() && that.getTagSizeBytes() == this.getTagSizeBytes() && that.getVariant() == this.getVariant();
    }

    public int hashCode() {
        return Objects.hash(AesEaxParameters.class, this.keySizeBytes, this.ivSizeBytes, this.tagSizeBytes, this.variant);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public String toString() {
        return "AesEax Parameters (variant: " + this.variant + ", " + this.ivSizeBytes + "-byte IV, " + this.tagSizeBytes + "-byte tag, and " + this.keySizeBytes + "-byte key)";
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

    public static final class Builder {
        @Nullable
        private Integer keySizeBytes = null;
        @Nullable
        private Integer ivSizeBytes = null;
        @Nullable
        private Integer tagSizeBytes = null;
        private Variant variant = Variant.NO_PREFIX;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setKeySizeBytes(int keySizeBytes) throws GeneralSecurityException {
            if (keySizeBytes != 16 && keySizeBytes != 24 && keySizeBytes != 32) {
                throw new InvalidAlgorithmParameterException(String.format("Invalid key size %d; only 16-byte, 24-byte and 32-byte AES keys are supported", keySizeBytes));
            }
            this.keySizeBytes = keySizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setIvSizeBytes(int ivSizeBytes) throws GeneralSecurityException {
            if (ivSizeBytes != 12 && ivSizeBytes != 16) {
                throw new GeneralSecurityException(String.format("Invalid IV size in bytes %d; acceptable values have 12 or 16 bytes", ivSizeBytes));
            }
            this.ivSizeBytes = ivSizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setTagSizeBytes(int tagSizeBytes) throws GeneralSecurityException {
            if (tagSizeBytes < 0 || tagSizeBytes > 16) {
                throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; value must be at most 16 bytes", tagSizeBytes));
            }
            this.tagSizeBytes = tagSizeBytes;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setVariant(Variant variant) {
            this.variant = variant;
            return this;
        }

        public AesEaxParameters build() throws GeneralSecurityException {
            if (this.keySizeBytes == null) {
                throw new GeneralSecurityException("Key size is not set");
            }
            if (this.ivSizeBytes == null) {
                throw new GeneralSecurityException("IV size is not set");
            }
            if (this.variant == null) {
                throw new GeneralSecurityException("Variant is not set");
            }
            if (this.tagSizeBytes == null) {
                throw new GeneralSecurityException("Tag size is not set");
            }
            return new AesEaxParameters(this.keySizeBytes, this.ivSizeBytes, this.tagSizeBytes, this.variant);
        }
    }
}

