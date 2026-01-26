/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.internal.AesGcmHpkeAead;
import com.google.crypto.tink.hybrid.internal.ChaCha20Poly1305HpkeAead;
import com.google.crypto.tink.hybrid.internal.HkdfHpkeKdf;
import com.google.crypto.tink.hybrid.internal.HpkeAead;
import com.google.crypto.tink.hybrid.internal.HpkeKdf;
import com.google.crypto.tink.hybrid.internal.HpkeKem;
import com.google.crypto.tink.hybrid.internal.HpkeUtil;
import com.google.crypto.tink.hybrid.internal.NistCurvesHpkeKem;
import com.google.crypto.tink.hybrid.internal.X25519HpkeKem;
import com.google.crypto.tink.subtle.EllipticCurves;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public final class HpkePrimitiveFactory {
    public static HpkeKem createKem(byte[] kemId) throws GeneralSecurityException {
        if (Arrays.equals(kemId, HpkeUtil.X25519_HKDF_SHA256_KEM_ID)) {
            return new X25519HpkeKem(new HkdfHpkeKdf("HmacSha256"));
        }
        if (Arrays.equals(kemId, HpkeUtil.P256_HKDF_SHA256_KEM_ID)) {
            return NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P256);
        }
        if (Arrays.equals(kemId, HpkeUtil.P384_HKDF_SHA384_KEM_ID)) {
            return NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P384);
        }
        if (Arrays.equals(kemId, HpkeUtil.P521_HKDF_SHA512_KEM_ID)) {
            return NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P521);
        }
        throw new IllegalArgumentException("Unrecognized HPKE KEM identifier");
    }

    public static HpkeKem createKem(HpkeParameters.KemId kemId) throws GeneralSecurityException {
        if (kemId == HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) {
            return new X25519HpkeKem(new HkdfHpkeKdf("HmacSha256"));
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
            return NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P256);
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
            return NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P384);
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
            return NistCurvesHpkeKem.fromCurve(EllipticCurves.CurveType.NIST_P521);
        }
        throw new IllegalArgumentException("Unrecognized HPKE KEM identifier");
    }

    public static HpkeKdf createKdf(byte[] kdfId) {
        if (Arrays.equals(kdfId, HpkeUtil.HKDF_SHA256_KDF_ID)) {
            return new HkdfHpkeKdf("HmacSha256");
        }
        if (Arrays.equals(kdfId, HpkeUtil.HKDF_SHA384_KDF_ID)) {
            return new HkdfHpkeKdf("HmacSha384");
        }
        if (Arrays.equals(kdfId, HpkeUtil.HKDF_SHA512_KDF_ID)) {
            return new HkdfHpkeKdf("HmacSha512");
        }
        throw new IllegalArgumentException("Unrecognized HPKE KDF identifier");
    }

    public static HpkeKdf createKdf(HpkeParameters.KdfId kdfId) {
        if (kdfId == HpkeParameters.KdfId.HKDF_SHA256) {
            return new HkdfHpkeKdf("HmacSha256");
        }
        if (kdfId == HpkeParameters.KdfId.HKDF_SHA384) {
            return new HkdfHpkeKdf("HmacSha384");
        }
        if (kdfId == HpkeParameters.KdfId.HKDF_SHA512) {
            return new HkdfHpkeKdf("HmacSha512");
        }
        throw new IllegalArgumentException("Unrecognized HPKE KDF identifier");
    }

    public static HpkeAead createAead(byte[] aeadId) throws GeneralSecurityException {
        if (Arrays.equals(aeadId, HpkeUtil.AES_128_GCM_AEAD_ID)) {
            return new AesGcmHpkeAead(16);
        }
        if (Arrays.equals(aeadId, HpkeUtil.AES_256_GCM_AEAD_ID)) {
            return new AesGcmHpkeAead(32);
        }
        if (Arrays.equals(aeadId, HpkeUtil.CHACHA20_POLY1305_AEAD_ID)) {
            return new ChaCha20Poly1305HpkeAead();
        }
        throw new IllegalArgumentException("Unrecognized HPKE AEAD identifier");
    }

    public static HpkeAead createAead(HpkeParameters.AeadId aeadId) throws GeneralSecurityException {
        if (aeadId == HpkeParameters.AeadId.AES_128_GCM) {
            return new AesGcmHpkeAead(16);
        }
        if (aeadId == HpkeParameters.AeadId.AES_256_GCM) {
            return new AesGcmHpkeAead(32);
        }
        if (aeadId == HpkeParameters.AeadId.CHACHA20_POLY1305) {
            return new ChaCha20Poly1305HpkeAead();
        }
        throw new IllegalArgumentException("Unrecognized HPKE AEAD identifier");
    }

    private HpkePrimitiveFactory() {
    }
}

