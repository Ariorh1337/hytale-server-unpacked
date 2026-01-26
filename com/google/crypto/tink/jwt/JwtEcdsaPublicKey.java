/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.jwt.JwtEcdsaParameters;
import com.google.crypto.tink.jwt.JwtSignaturePublicKey;
import com.google.crypto.tink.signature.EcdsaParameters;
import com.google.crypto.tink.signature.EcdsaPublicKey;
import com.google.crypto.tink.subtle.Base64;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;
import java.util.Optional;
import javax.annotation.Nullable;

@Immutable
public final class JwtEcdsaPublicKey
extends JwtSignaturePublicKey {
    private final JwtEcdsaParameters parameters;
    private final EcdsaPublicKey ecdsaPublicKey;
    private final Optional<String> kid;
    private final Optional<Integer> idRequirement;

    private JwtEcdsaPublicKey(JwtEcdsaParameters parameters, EcdsaPublicKey ecdsaPublicKey, Optional<String> kid, Optional<Integer> idRequirement) {
        this.parameters = parameters;
        this.ecdsaPublicKey = ecdsaPublicKey;
        this.kid = kid;
        this.idRequirement = idRequirement;
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public static Builder builder() {
        return new Builder();
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    @AccessesPartialKey
    public ECPoint getPublicPoint() {
        return this.ecdsaPublicKey.getPublicPoint();
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
    public JwtEcdsaParameters getParameters() {
        return this.parameters;
    }

    @Override
    public boolean equalsKey(Key o) {
        if (!(o instanceof JwtEcdsaPublicKey)) {
            return false;
        }
        JwtEcdsaPublicKey that = (JwtEcdsaPublicKey)o;
        return that.parameters.equals(this.parameters) && that.ecdsaPublicKey.equalsKey(this.ecdsaPublicKey) && that.kid.equals(this.kid);
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    EcdsaPublicKey getEcdsaPublicKey() {
        return this.ecdsaPublicKey;
    }

    public static class Builder {
        private Optional<JwtEcdsaParameters> parameters = Optional.empty();
        private Optional<ECPoint> publicPoint = Optional.empty();
        private Optional<Integer> idRequirement = Optional.empty();
        private Optional<String> customKid = Optional.empty();

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setParameters(JwtEcdsaParameters parameters) {
            this.parameters = Optional.of(parameters);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setPublicPoint(ECPoint publicPoint) {
            this.publicPoint = Optional.of(publicPoint);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setIdRequirement(Integer idRequirement) {
            this.idRequirement = Optional.of(idRequirement);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setCustomKid(String customKid) {
            this.customKid = Optional.of(customKid);
            return this;
        }

        private Optional<String> computeKid() throws GeneralSecurityException {
            if (this.parameters.get().getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
                if (this.customKid.isPresent()) {
                    throw new GeneralSecurityException("customKid must not be set for KidStrategy BASE64_ENCODED_KEY_ID");
                }
                byte[] bigEndianKeyId = ByteBuffer.allocate(4).putInt(this.idRequirement.get()).array();
                return Optional.of(Base64.urlSafeEncode(bigEndianKeyId));
            }
            if (this.parameters.get().getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.CUSTOM)) {
                if (!this.customKid.isPresent()) {
                    throw new GeneralSecurityException("customKid needs to be set for KidStrategy CUSTOM");
                }
                return this.customKid;
            }
            if (this.parameters.get().getKidStrategy().equals(JwtEcdsaParameters.KidStrategy.IGNORED)) {
                if (this.customKid.isPresent()) {
                    throw new GeneralSecurityException("customKid must not be set for KidStrategy IGNORED");
                }
                return Optional.empty();
            }
            throw new IllegalStateException("Unknown kid strategy");
        }

        private static EcdsaParameters.CurveType getCurveType(JwtEcdsaParameters parameters) throws GeneralSecurityException {
            if (parameters.getAlgorithm().equals(JwtEcdsaParameters.Algorithm.ES256)) {
                return EcdsaParameters.CurveType.NIST_P256;
            }
            if (parameters.getAlgorithm().equals(JwtEcdsaParameters.Algorithm.ES384)) {
                return EcdsaParameters.CurveType.NIST_P384;
            }
            if (parameters.getAlgorithm().equals(JwtEcdsaParameters.Algorithm.ES512)) {
                return EcdsaParameters.CurveType.NIST_P521;
            }
            throw new GeneralSecurityException("unknown algorithm in parameters: " + parameters);
        }

        private static EcdsaParameters.HashType getHashType(JwtEcdsaParameters parameters) throws GeneralSecurityException {
            if (parameters.getAlgorithm().equals(JwtEcdsaParameters.Algorithm.ES256)) {
                return EcdsaParameters.HashType.SHA256;
            }
            if (parameters.getAlgorithm().equals(JwtEcdsaParameters.Algorithm.ES384)) {
                return EcdsaParameters.HashType.SHA384;
            }
            if (parameters.getAlgorithm().equals(JwtEcdsaParameters.Algorithm.ES512)) {
                return EcdsaParameters.HashType.SHA512;
            }
            throw new GeneralSecurityException("unknown algorithm in parameters: " + parameters);
        }

        @AccessesPartialKey
        public JwtEcdsaPublicKey build() throws GeneralSecurityException {
            if (!this.parameters.isPresent()) {
                throw new GeneralSecurityException("Cannot build without parameters");
            }
            if (!this.publicPoint.isPresent()) {
                throw new GeneralSecurityException("Cannot build without public point");
            }
            if (this.parameters.get().hasIdRequirement() && !this.idRequirement.isPresent()) {
                throw new GeneralSecurityException("Cannot create key without ID requirement with parameters with ID requirement");
            }
            if (!this.parameters.get().hasIdRequirement() && this.idRequirement.isPresent()) {
                throw new GeneralSecurityException("Cannot create key with ID requirement with parameters without ID requirement");
            }
            EcdsaParameters ecdsaParameters = EcdsaParameters.builder().setSignatureEncoding(EcdsaParameters.SignatureEncoding.IEEE_P1363).setCurveType(Builder.getCurveType(this.parameters.get())).setHashType(Builder.getHashType(this.parameters.get())).build();
            EcdsaPublicKey ecdsaPublicKey = EcdsaPublicKey.builder().setParameters(ecdsaParameters).setPublicPoint(this.publicPoint.get()).build();
            return new JwtEcdsaPublicKey(this.parameters.get(), ecdsaPublicKey, this.computeKid(), this.idRequirement);
        }
    }
}

