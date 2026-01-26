/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EllipticCurves;
import java.security.GeneralSecurityException;

public final class HpkeUtil {
    public static final byte[] BASE_MODE = HpkeUtil.intToByteArray(1, 0);
    public static final byte[] AUTH_MODE = HpkeUtil.intToByteArray(1, 2);
    public static final byte[] X25519_HKDF_SHA256_KEM_ID = HpkeUtil.intToByteArray(2, 32);
    public static final byte[] P256_HKDF_SHA256_KEM_ID = HpkeUtil.intToByteArray(2, 16);
    public static final byte[] P384_HKDF_SHA384_KEM_ID = HpkeUtil.intToByteArray(2, 17);
    public static final byte[] P521_HKDF_SHA512_KEM_ID = HpkeUtil.intToByteArray(2, 18);
    public static final byte[] HKDF_SHA256_KDF_ID = HpkeUtil.intToByteArray(2, 1);
    public static final byte[] HKDF_SHA384_KDF_ID = HpkeUtil.intToByteArray(2, 2);
    public static final byte[] HKDF_SHA512_KDF_ID = HpkeUtil.intToByteArray(2, 3);
    public static final byte[] AES_128_GCM_AEAD_ID = HpkeUtil.intToByteArray(2, 1);
    public static final byte[] AES_256_GCM_AEAD_ID = HpkeUtil.intToByteArray(2, 2);
    public static final byte[] CHACHA20_POLY1305_AEAD_ID = HpkeUtil.intToByteArray(2, 3);
    public static final byte[] EMPTY_SALT = new byte[0];
    private static final byte[] KEM = "KEM".getBytes(Util.UTF_8);
    private static final byte[] HPKE = "HPKE".getBytes(Util.UTF_8);
    private static final byte[] HPKE_V1 = "HPKE-v1".getBytes(Util.UTF_8);

    public static byte[] intToByteArray(int capacity, int value) {
        if (capacity > 4 || capacity < 0) {
            throw new IllegalArgumentException("capacity must be between 0 and 4");
        }
        if (value < 0 || capacity < 4 && value >= 1 << 8 * capacity) {
            throw new IllegalArgumentException("value too large");
        }
        byte[] result = new byte[capacity];
        for (int i = 0; i < capacity; ++i) {
            result[i] = (byte)(value >> 8 * (capacity - i - 1) & 0xFF);
        }
        return result;
    }

    static byte[] kemSuiteId(byte[] kemId) throws GeneralSecurityException {
        return Bytes.concat(KEM, kemId);
    }

    static byte[] hpkeSuiteId(byte[] kemId, byte[] kdfId, byte[] aeadId) throws GeneralSecurityException {
        return Bytes.concat(HPKE, kemId, kdfId, aeadId);
    }

    static byte[] labelIkm(String label, byte[] ikm, byte[] suiteId) throws GeneralSecurityException {
        return Bytes.concat(HPKE_V1, suiteId, label.getBytes(Util.UTF_8), ikm);
    }

    static byte[] labelInfo(String label, byte[] info, byte[] suiteId, int length) throws GeneralSecurityException {
        return Bytes.concat(HpkeUtil.intToByteArray(2, length), HPKE_V1, suiteId, label.getBytes(Util.UTF_8), info);
    }

    static EllipticCurves.CurveType nistHpkeKemToCurve(HpkeParameters.KemId kemId) throws GeneralSecurityException {
        if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
            return EllipticCurves.CurveType.NIST_P256;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
            return EllipticCurves.CurveType.NIST_P384;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
            return EllipticCurves.CurveType.NIST_P521;
        }
        throw new GeneralSecurityException("Unrecognized NIST HPKE KEM identifier");
    }

    public static int getEncodedPublicKeyLength(HpkeParameters.KemId kemId) throws GeneralSecurityException {
        if (kemId == HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) {
            return 32;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
            return 65;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
            return 97;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
            return 133;
        }
        throw new GeneralSecurityException("Unrecognized HPKE KEM identifier");
    }

    public static int encodingSizeInBytes(HpkeParameters.KemId kemId) {
        if (kemId == HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) {
            return 32;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
            return 65;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
            return 97;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
            return 133;
        }
        throw new IllegalArgumentException("Unable to determine KEM-encoding length for " + kemId);
    }

    public static int getEncodedPrivateKeyLength(HpkeParameters.KemId kemId) throws GeneralSecurityException {
        if (kemId == HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) {
            return 32;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) {
            return 32;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) {
            return 48;
        }
        if (kemId == HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512) {
            return 66;
        }
        throw new GeneralSecurityException("Unrecognized HPKE KEM identifier");
    }

    private HpkeUtil() {
    }
}

