/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.aead.AeadParameters;
import com.google.crypto.tink.aead.AesCtrHmacAeadParameters;
import com.google.crypto.tink.aead.AesEaxParameters;
import com.google.crypto.tink.aead.AesGcmParameters;
import com.google.crypto.tink.aead.AesGcmSivParameters;
import com.google.crypto.tink.aead.ChaCha20Poly1305Parameters;
import com.google.crypto.tink.aead.XChaCha20Poly1305Parameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class LegacyKmsEnvelopeAeadParameters
extends AeadParameters {
    private final Variant variant;
    private final String kekUri;
    private final DekParsingStrategy dekParsingStrategy;
    private final AeadParameters dekParametersForNewKeys;

    private LegacyKmsEnvelopeAeadParameters(Variant variant, String kekUri, DekParsingStrategy dekParsingStrategy, AeadParameters dekParametersForNewKeys) {
        this.variant = variant;
        this.kekUri = kekUri;
        this.dekParsingStrategy = dekParsingStrategy;
        this.dekParametersForNewKeys = dekParametersForNewKeys;
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public static Builder builder() {
        return new Builder();
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public String getKekUri() {
        return this.kekUri;
    }

    public Variant getVariant() {
        return this.variant;
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public DekParsingStrategy getDekParsingStrategy() {
        return this.dekParsingStrategy;
    }

    public AeadParameters getDekParametersForNewKeys() {
        return this.dekParametersForNewKeys;
    }

    public boolean equals(Object o) {
        if (!(o instanceof LegacyKmsEnvelopeAeadParameters)) {
            return false;
        }
        LegacyKmsEnvelopeAeadParameters that = (LegacyKmsEnvelopeAeadParameters)o;
        return that.dekParsingStrategy.equals(this.dekParsingStrategy) && that.dekParametersForNewKeys.equals(this.dekParametersForNewKeys) && that.kekUri.equals(this.kekUri) && that.variant.equals(this.variant);
    }

    public int hashCode() {
        return Objects.hash(LegacyKmsEnvelopeAeadParameters.class, this.kekUri, this.dekParsingStrategy, this.dekParametersForNewKeys, this.variant);
    }

    public String toString() {
        return "LegacyKmsEnvelopeAead Parameters (kekUri: " + this.kekUri + ", dekParsingStrategy: " + this.dekParsingStrategy + ", dekParametersForNewKeys: " + this.dekParametersForNewKeys + ", variant: " + this.variant + ")";
    }

    @Immutable
    public static final class Variant {
        public static final Variant TINK = new Variant("TINK");
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
    public static final class DekParsingStrategy {
        public static final DekParsingStrategy ASSUME_AES_GCM = new DekParsingStrategy("ASSUME_AES_GCM");
        public static final DekParsingStrategy ASSUME_XCHACHA20POLY1305 = new DekParsingStrategy("ASSUME_XCHACHA20POLY1305");
        public static final DekParsingStrategy ASSUME_CHACHA20POLY1305 = new DekParsingStrategy("ASSUME_CHACHA20POLY1305");
        public static final DekParsingStrategy ASSUME_AES_CTR_HMAC = new DekParsingStrategy("ASSUME_AES_CTR_HMAC");
        public static final DekParsingStrategy ASSUME_AES_EAX = new DekParsingStrategy("ASSUME_AES_EAX");
        public static final DekParsingStrategy ASSUME_AES_GCM_SIV = new DekParsingStrategy("ASSUME_AES_GCM_SIV");
        private final String name;

        private DekParsingStrategy(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public static class Builder {
        @Nullable
        private Variant variant;
        @Nullable
        private String kekUri;
        @Nullable
        private DekParsingStrategy dekParsingStrategy;
        @Nullable
        private AeadParameters dekParametersForNewKeys;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setVariant(Variant variant) {
            this.variant = variant;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setKekUri(String kekUri) {
            this.kekUri = kekUri;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setDekParsingStrategy(DekParsingStrategy dekParsingStrategy) {
            this.dekParsingStrategy = dekParsingStrategy;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setDekParametersForNewKeys(AeadParameters aeadParameters) {
            this.dekParametersForNewKeys = aeadParameters;
            return this;
        }

        private static boolean parsingStrategyAllowed(DekParsingStrategy parsingStrategy, AeadParameters aeadParameters) {
            if (parsingStrategy.equals(DekParsingStrategy.ASSUME_AES_GCM) && aeadParameters instanceof AesGcmParameters) {
                return true;
            }
            if (parsingStrategy.equals(DekParsingStrategy.ASSUME_CHACHA20POLY1305) && aeadParameters instanceof ChaCha20Poly1305Parameters) {
                return true;
            }
            if (parsingStrategy.equals(DekParsingStrategy.ASSUME_XCHACHA20POLY1305) && aeadParameters instanceof XChaCha20Poly1305Parameters) {
                return true;
            }
            if (parsingStrategy.equals(DekParsingStrategy.ASSUME_AES_CTR_HMAC) && aeadParameters instanceof AesCtrHmacAeadParameters) {
                return true;
            }
            if (parsingStrategy.equals(DekParsingStrategy.ASSUME_AES_EAX) && aeadParameters instanceof AesEaxParameters) {
                return true;
            }
            return parsingStrategy.equals(DekParsingStrategy.ASSUME_AES_GCM_SIV) && aeadParameters instanceof AesGcmSivParameters;
        }

        public LegacyKmsEnvelopeAeadParameters build() throws GeneralSecurityException {
            if (this.variant == null) {
                this.variant = Variant.NO_PREFIX;
            }
            if (this.kekUri == null) {
                throw new GeneralSecurityException("kekUri must be set");
            }
            if (this.dekParsingStrategy == null) {
                throw new GeneralSecurityException("dekParsingStrategy must be set");
            }
            if (this.dekParametersForNewKeys == null) {
                throw new GeneralSecurityException("dekParametersForNewKeys must be set");
            }
            if (this.dekParametersForNewKeys.hasIdRequirement()) {
                throw new GeneralSecurityException("dekParametersForNewKeys must not have ID Requirements");
            }
            if (!Builder.parsingStrategyAllowed(this.dekParsingStrategy, this.dekParametersForNewKeys)) {
                throw new GeneralSecurityException("Cannot use parsing strategy " + this.dekParsingStrategy.toString() + " when new keys are picked according to " + this.dekParametersForNewKeys + ".");
            }
            return new LegacyKmsEnvelopeAeadParameters(this.variant, this.kekUri, this.dekParsingStrategy, this.dekParametersForNewKeys);
        }
    }
}

