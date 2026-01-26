/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.aead.AeadKey;
import com.google.crypto.tink.aead.ChaCha20Poly1305Parameters;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class ChaCha20Poly1305Key
extends AeadKey {
    private final ChaCha20Poly1305Parameters parameters;
    private final SecretBytes keyBytes;
    private final Bytes outputPrefix;
    @Nullable
    private final Integer idRequirement;

    private ChaCha20Poly1305Key(ChaCha20Poly1305Parameters parameters, SecretBytes keyBytes, Bytes outputPrefix, @Nullable Integer idRequirement) {
        this.parameters = parameters;
        this.keyBytes = keyBytes;
        this.outputPrefix = outputPrefix;
        this.idRequirement = idRequirement;
    }

    private static Bytes getOutputPrefix(ChaCha20Poly1305Parameters parameters, @Nullable Integer idRequirement) {
        if (parameters.getVariant() == ChaCha20Poly1305Parameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
        }
        if (parameters.getVariant() == ChaCha20Poly1305Parameters.Variant.CRUNCHY) {
            return OutputPrefixUtil.getLegacyOutputPrefix(idRequirement);
        }
        if (parameters.getVariant() == ChaCha20Poly1305Parameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(idRequirement);
        }
        throw new IllegalStateException("Unknown Variant: " + parameters.getVariant());
    }

    @Override
    public Bytes getOutputPrefix() {
        return this.outputPrefix;
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    @AccessesPartialKey
    public static ChaCha20Poly1305Key create(SecretBytes secretBytes) throws GeneralSecurityException {
        return ChaCha20Poly1305Key.create(ChaCha20Poly1305Parameters.Variant.NO_PREFIX, secretBytes, null);
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public static ChaCha20Poly1305Key create(ChaCha20Poly1305Parameters.Variant variant, SecretBytes secretBytes, @Nullable Integer idRequirement) throws GeneralSecurityException {
        if (variant != ChaCha20Poly1305Parameters.Variant.NO_PREFIX && idRequirement == null) {
            throw new GeneralSecurityException("For given Variant " + variant + " the value of idRequirement must be non-null");
        }
        if (variant == ChaCha20Poly1305Parameters.Variant.NO_PREFIX && idRequirement != null) {
            throw new GeneralSecurityException("For given Variant NO_PREFIX the value of idRequirement must be null");
        }
        if (secretBytes.size() != 32) {
            throw new GeneralSecurityException("ChaCha20Poly1305 key must be constructed with key of length 32 bytes, not " + secretBytes.size());
        }
        ChaCha20Poly1305Parameters parameters = ChaCha20Poly1305Parameters.create(variant);
        return new ChaCha20Poly1305Key(parameters, secretBytes, ChaCha20Poly1305Key.getOutputPrefix(parameters, idRequirement), idRequirement);
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public SecretBytes getKeyBytes() {
        return this.keyBytes;
    }

    @Override
    public ChaCha20Poly1305Parameters getParameters() {
        return this.parameters;
    }

    @Override
    @Nullable
    public Integer getIdRequirementOrNull() {
        return this.idRequirement;
    }

    @Override
    public boolean equalsKey(Key o) {
        if (!(o instanceof ChaCha20Poly1305Key)) {
            return false;
        }
        ChaCha20Poly1305Key that = (ChaCha20Poly1305Key)o;
        return that.parameters.equals(this.parameters) && that.keyBytes.equalsSecretBytes(this.keyBytes) && Objects.equals(that.idRequirement, this.idRequirement);
    }
}

