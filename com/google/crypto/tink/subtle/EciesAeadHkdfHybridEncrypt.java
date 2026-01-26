/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.hybrid.EciesParameters;
import com.google.crypto.tink.hybrid.EciesPublicKey;
import com.google.crypto.tink.hybrid.internal.EciesDemHelper;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.subtle.EciesHkdfSenderKem;
import com.google.crypto.tink.subtle.EllipticCurves;
import java.security.GeneralSecurityException;
import java.security.interfaces.ECPublicKey;

public final class EciesAeadHkdfHybridEncrypt
implements HybridEncrypt {
    private final EciesHkdfSenderKem senderKem;
    private final String hkdfHmacAlgo;
    private final byte[] hkdfSalt;
    private final EllipticCurves.PointFormatType ecPointFormat;
    private final EciesDemHelper.Dem dem;
    private final byte[] outputPrefix;
    static final EnumTypeProtoConverter<EllipticCurves.CurveType, EciesParameters.CurveType> CURVE_TYPE_CONVERTER = EnumTypeProtoConverter.builder().add(EllipticCurves.CurveType.NIST_P256, EciesParameters.CurveType.NIST_P256).add(EllipticCurves.CurveType.NIST_P384, EciesParameters.CurveType.NIST_P384).add(EllipticCurves.CurveType.NIST_P521, EciesParameters.CurveType.NIST_P521).build();
    static final EnumTypeProtoConverter<EllipticCurves.PointFormatType, EciesParameters.PointFormat> POINT_FORMAT_TYPE_CONVERTER = EnumTypeProtoConverter.builder().add(EllipticCurves.PointFormatType.UNCOMPRESSED, EciesParameters.PointFormat.UNCOMPRESSED).add(EllipticCurves.PointFormatType.COMPRESSED, EciesParameters.PointFormat.COMPRESSED).add(EllipticCurves.PointFormatType.DO_NOT_USE_CRUNCHY_UNCOMPRESSED, EciesParameters.PointFormat.LEGACY_UNCOMPRESSED).build();

    static final String toHmacAlgo(EciesParameters.HashType hash) throws GeneralSecurityException {
        if (hash.equals(EciesParameters.HashType.SHA1)) {
            return "HmacSha1";
        }
        if (hash.equals(EciesParameters.HashType.SHA224)) {
            return "HmacSha224";
        }
        if (hash.equals(EciesParameters.HashType.SHA256)) {
            return "HmacSha256";
        }
        if (hash.equals(EciesParameters.HashType.SHA384)) {
            return "HmacSha384";
        }
        if (hash.equals(EciesParameters.HashType.SHA512)) {
            return "HmacSha512";
        }
        throw new GeneralSecurityException("hash unsupported for EciesAeadHkdf: " + hash);
    }

    private EciesAeadHkdfHybridEncrypt(ECPublicKey recipientPublicKey, byte[] hkdfSalt, String hkdfHmacAlgo, EllipticCurves.PointFormatType ecPointFormat, EciesDemHelper.Dem dem, byte[] outputPrefix) throws GeneralSecurityException {
        EllipticCurves.checkPublicKey(recipientPublicKey);
        this.senderKem = new EciesHkdfSenderKem(recipientPublicKey);
        this.hkdfSalt = hkdfSalt;
        this.hkdfHmacAlgo = hkdfHmacAlgo;
        this.ecPointFormat = ecPointFormat;
        this.dem = dem;
        this.outputPrefix = outputPrefix;
    }

    @AccessesPartialKey
    public static HybridEncrypt create(EciesPublicKey key) throws GeneralSecurityException {
        EllipticCurves.CurveType curveType = CURVE_TYPE_CONVERTER.toProtoEnum(key.getParameters().getCurveType());
        ECPublicKey recipientPublicKey = EllipticCurves.getEcPublicKey(curveType, key.getNistCurvePoint().getAffineX().toByteArray(), key.getNistCurvePoint().getAffineY().toByteArray());
        byte[] hkdfSalt = new byte[]{};
        if (key.getParameters().getSalt() != null) {
            hkdfSalt = key.getParameters().getSalt().toByteArray();
        }
        return new EciesAeadHkdfHybridEncrypt(recipientPublicKey, hkdfSalt, EciesAeadHkdfHybridEncrypt.toHmacAlgo(key.getParameters().getHashType()), POINT_FORMAT_TYPE_CONVERTER.toProtoEnum(key.getParameters().getNistCurvePointFormat()), EciesDemHelper.getDem(key.getParameters()), key.getOutputPrefix().toByteArray());
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] contextInfo) throws GeneralSecurityException {
        EciesHkdfSenderKem.KemKey kemKey = this.senderKem.generateKey(this.hkdfHmacAlgo, this.hkdfSalt, contextInfo, this.dem.getSymmetricKeySizeInBytes(), this.ecPointFormat);
        return this.dem.encrypt(kemKey.getSymmetricKey(), this.outputPrefix, kemKey.getKemBytes(), plaintext);
    }
}

