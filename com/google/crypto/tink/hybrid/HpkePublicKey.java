/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.HybridPublicKey;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.internal.OutputPrefixUtil;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.RestrictedApi;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Objects;
import javax.annotation.Nullable;

@Immutable
public final class HpkePublicKey
extends HybridPublicKey {
    private final HpkeParameters parameters;
    private final Bytes publicKeyBytes;
    private final Bytes outputPrefix;
    @Nullable
    private final Integer idRequirement;

    private HpkePublicKey(HpkeParameters parameters, Bytes publicKeyBytes, Bytes outputPrefix, @Nullable Integer idRequirement) {
        this.parameters = parameters;
        this.publicKeyBytes = publicKeyBytes;
        this.outputPrefix = outputPrefix;
        this.idRequirement = idRequirement;
    }

    private static void validateIdRequirement(HpkeParameters.Variant variant, @Nullable Integer idRequirement) throws GeneralSecurityException {
        if (!variant.equals(HpkeParameters.Variant.NO_PREFIX) && idRequirement == null) {
            throw new GeneralSecurityException("'idRequirement' must be non-null for " + variant + " variant.");
        }
        if (variant.equals(HpkeParameters.Variant.NO_PREFIX) && idRequirement != null) {
            throw new GeneralSecurityException("'idRequirement' must be null for NO_PREFIX variant.");
        }
    }

    private static void validatePublicKeyByteLength(HpkeParameters.KemId kemId, Bytes publicKeyBytes) throws GeneralSecurityException {
        int keyLengthInBytes = publicKeyBytes.size();
        String parameterizedErrorMessage = "Encoded public key byte length for " + kemId + " must be %d, not " + keyLengthInBytes;
        if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
            if (keyLengthInBytes != 65) {
                throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 65));
            }
            return;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
            if (keyLengthInBytes != 97) {
                throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 97));
            }
            return;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
            if (keyLengthInBytes != 133) {
                throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 133));
            }
            return;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) {
            if (keyLengthInBytes != 32) {
                throw new GeneralSecurityException(String.format(parameterizedErrorMessage, 32));
            }
            return;
        }
        throw new GeneralSecurityException("Unable to validate public key length for " + kemId);
    }

    private static boolean isNistKem(HpkeParameters.KemId kemId) {
        return kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256 || kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384 || kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512;
    }

    private static EllipticCurve getNistCurve(HpkeParameters.KemId kemId) {
        if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
            return EllipticCurves.getNistP256Params().getCurve();
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
            return EllipticCurves.getNistP384Params().getCurve();
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
            return EllipticCurves.getNistP521Params().getCurve();
        }
        throw new IllegalArgumentException("Unable to determine NIST curve type for " + kemId);
    }

    private static void validatePublicKeyOnCurve(HpkeParameters.KemId kemId, Bytes publicKeyBytes) throws GeneralSecurityException {
        if (!HpkePublicKey.isNistKem(kemId)) {
            return;
        }
        EllipticCurve curve = HpkePublicKey.getNistCurve(kemId);
        ECPoint point = EllipticCurves.pointDecode(curve, EllipticCurves.PointFormatType.UNCOMPRESSED, publicKeyBytes.toByteArray());
        EllipticCurvesUtil.checkPointOnCurve(point, curve);
    }

    private static void validatePublicKey(HpkeParameters.KemId kemId, Bytes publicKeyBytes) throws GeneralSecurityException {
        HpkePublicKey.validatePublicKeyByteLength(kemId, publicKeyBytes);
        HpkePublicKey.validatePublicKeyOnCurve(kemId, publicKeyBytes);
    }

    private static Bytes createOutputPrefix(HpkeParameters.Variant variant, @Nullable Integer idRequirement) {
        if (variant == HpkeParameters.Variant.NO_PREFIX) {
            return OutputPrefixUtil.EMPTY_PREFIX;
        }
        if (idRequirement == null) {
            throw new IllegalStateException("idRequirement must be non-null for HpkeParameters.Variant " + variant);
        }
        if (variant == HpkeParameters.Variant.CRUNCHY) {
            return OutputPrefixUtil.getLegacyOutputPrefix(idRequirement);
        }
        if (variant == HpkeParameters.Variant.TINK) {
            return OutputPrefixUtil.getTinkOutputPrefix(idRequirement);
        }
        throw new IllegalStateException("Unknown HpkeParameters.Variant: " + variant);
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public static HpkePublicKey create(HpkeParameters parameters, Bytes publicKeyBytes, @Nullable Integer idRequirement) throws GeneralSecurityException {
        HpkePublicKey.validateIdRequirement(parameters.getVariant(), idRequirement);
        HpkePublicKey.validatePublicKey(parameters.getKemId(), publicKeyBytes);
        Bytes prefix = HpkePublicKey.createOutputPrefix(parameters.getVariant(), idRequirement);
        return new HpkePublicKey(parameters, publicKeyBytes, prefix, idRequirement);
    }

    @RestrictedApi(explanation="Accessing parts of keys can produce unexpected incompatibilities, annotate the function with @AccessesPartialKey", link="https://developers.google.com/tink/design/access_control#accessing_partial_keys", allowedOnPath=".*Test\\.java", allowlistAnnotations={AccessesPartialKey.class})
    public Bytes getPublicKeyBytes() {
        return this.publicKeyBytes;
    }

    @Override
    public Bytes getOutputPrefix() {
        return this.outputPrefix;
    }

    @Override
    public HpkeParameters getParameters() {
        return this.parameters;
    }

    @Override
    @Nullable
    public Integer getIdRequirementOrNull() {
        return this.idRequirement;
    }

    @Override
    public boolean equalsKey(Key o) {
        if (!(o instanceof HpkePublicKey)) {
            return false;
        }
        HpkePublicKey other = (HpkePublicKey)o;
        return this.parameters.equals(other.parameters) && this.publicKeyBytes.equals(other.publicKeyBytes) && Objects.equals(this.idRequirement, other.idRequirement);
    }
}

