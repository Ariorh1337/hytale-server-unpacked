/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.prf;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.prf.HkdfPrfParameters;
import com.google.crypto.tink.prf.PrfKey;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@Immutable
public final class HkdfPrfKey
extends PrfKey {
    private final HkdfPrfParameters parameters;
    private final SecretBytes keyBytes;

    private HkdfPrfKey(HkdfPrfParameters parameters, SecretBytes keyBytes) {
        this.parameters = parameters;
        this.keyBytes = keyBytes;
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public static Builder builder() {
        return new Builder();
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public SecretBytes getKeyBytes() {
        return this.keyBytes;
    }

    @Override
    public HkdfPrfParameters getParameters() {
        return this.parameters;
    }

    @Override
    @Nullable
    public Integer getIdRequirementOrNull() {
        return null;
    }

    @Override
    public boolean equalsKey(Key o) {
        if (!(o instanceof HkdfPrfKey)) {
            return false;
        }
        HkdfPrfKey that = (HkdfPrfKey)o;
        return that.parameters.equals(this.parameters) && that.keyBytes.equalsSecretBytes(this.keyBytes);
    }

    public static final class Builder {
        @Nullable
        private HkdfPrfParameters parameters = null;
        @Nullable
        private SecretBytes keyBytes = null;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setParameters(HkdfPrfParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setKeyBytes(SecretBytes keyBytes) {
            this.keyBytes = keyBytes;
            return this;
        }

        public HkdfPrfKey build() throws GeneralSecurityException {
            if (this.parameters == null || this.keyBytes == null) {
                throw new GeneralSecurityException("Cannot build without parameters and/or key material");
            }
            if (this.parameters.getKeySizeBytes() != this.keyBytes.size()) {
                throw new GeneralSecurityException("Key size mismatch");
            }
            return new HkdfPrfKey(this.parameters, this.keyBytes);
        }
    }
}

