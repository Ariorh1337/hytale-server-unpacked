/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.jwt.JwtHmacParameters;
import com.google.crypto.tink.jwt.JwtMacKey;
import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.RestrictedApi;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Optional;
import javax.annotation.Nullable;

public final class JwtHmacKey
extends JwtMacKey {
    private final JwtHmacParameters parameters;
    private final SecretBytes key;
    private final Optional<Integer> idRequirement;
    private final Optional<String> kid;

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public static Builder builder() {
        return new Builder();
    }

    private JwtHmacKey(JwtHmacParameters parameters, SecretBytes key, Optional<Integer> idRequirement, Optional<String> kid) {
        this.parameters = parameters;
        this.key = key;
        this.idRequirement = idRequirement;
        this.kid = kid;
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public SecretBytes getKeyBytes() {
        return this.key;
    }

    @Override
    public Optional<String> getKid() {
        return this.kid;
    }

    @Override
    @Nullable
    public Integer getIdRequirementOrNull() {
        return this.idRequirement.orElse(null);
    }

    @Override
    public JwtHmacParameters getParameters() {
        return this.parameters;
    }

    @Override
    public boolean equalsKey(Key o) {
        if (!(o instanceof JwtHmacKey)) {
            return false;
        }
        JwtHmacKey that = (JwtHmacKey)o;
        return that.parameters.equals(this.parameters) && that.key.equalsSecretBytes(this.key) && that.kid.equals(this.kid) && that.idRequirement.equals(this.idRequirement);
    }

    public static class Builder {
        private Optional<JwtHmacParameters> parameters = Optional.empty();
        private Optional<SecretBytes> keyBytes = Optional.empty();
        private Optional<Integer> idRequirement = Optional.empty();
        private Optional<String> customKid = Optional.empty();

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setParameters(JwtHmacParameters parameters) {
            this.parameters = Optional.of(parameters);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setKeyBytes(SecretBytes keyBytes) {
            this.keyBytes = Optional.of(keyBytes);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setIdRequirement(int idRequirement) {
            this.idRequirement = Optional.of(idRequirement);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setCustomKid(String customKid) {
            this.customKid = Optional.of(customKid);
            return this;
        }

        private Optional<String> computeKid() throws GeneralSecurityException {
            if (this.parameters.get().getKidStrategy().equals(JwtHmacParameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
                byte[] bigEndianKeyId = ByteBuffer.allocate(4).putInt(this.idRequirement.get()).array();
                if (this.customKid.isPresent()) {
                    throw new GeneralSecurityException("customKid must not be set for KidStrategy BASE64_ENCODED_KEY_ID");
                }
                return Optional.of(Base64.urlSafeEncode(bigEndianKeyId));
            }
            if (this.parameters.get().getKidStrategy().equals(JwtHmacParameters.KidStrategy.CUSTOM)) {
                if (!this.customKid.isPresent()) {
                    throw new GeneralSecurityException("customKid needs to be set for KidStrategy CUSTOM");
                }
                return this.customKid;
            }
            if (this.parameters.get().getKidStrategy().equals(JwtHmacParameters.KidStrategy.IGNORED)) {
                if (this.customKid.isPresent()) {
                    throw new GeneralSecurityException("customKid must not be set for KidStrategy IGNORED");
                }
                return Optional.empty();
            }
            throw new IllegalStateException("Unknown kid strategy");
        }

        public JwtHmacKey build() throws GeneralSecurityException {
            if (!this.parameters.isPresent()) {
                throw new GeneralSecurityException("Parameters are required");
            }
            if (!this.keyBytes.isPresent()) {
                throw new GeneralSecurityException("KeyBytes are required");
            }
            if (this.parameters.get().getKeySizeBytes() != this.keyBytes.get().size()) {
                throw new GeneralSecurityException("Key size mismatch");
            }
            if (this.parameters.get().hasIdRequirement() && !this.idRequirement.isPresent()) {
                throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            }
            if (!this.parameters.get().hasIdRequirement() && this.idRequirement.isPresent()) {
                throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            }
            return new JwtHmacKey(this.parameters.get(), this.keyBytes.get(), this.idRequirement, this.computeKid());
        }
    }
}

