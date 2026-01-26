/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.RsaSsaPssParameters;
import com.google.crypto.tink.signature.RsaSsaPssPublicKey;
import com.google.crypto.tink.signature.internal.RsaSsaPssVerifyConscrypt;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Enums;
import com.google.crypto.tink.subtle.SubtleUtil;
import com.google.crypto.tink.subtle.Validators;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

@Immutable
public final class RsaSsaPssVerifyJce
implements PublicKeyVerify {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
    static final EnumTypeProtoConverter<Enums.HashType, RsaSsaPssParameters.HashType> HASH_TYPE_CONVERTER = EnumTypeProtoConverter.builder().add(Enums.HashType.SHA256, RsaSsaPssParameters.HashType.SHA256).add(Enums.HashType.SHA384, RsaSsaPssParameters.HashType.SHA384).add(Enums.HashType.SHA512, RsaSsaPssParameters.HashType.SHA512).build();
    private static final byte[] EMPTY = new byte[0];
    private static final byte[] legacyMessageSuffix = new byte[]{0};
    private final PublicKeyVerify verify;

    @AccessesPartialKey
    public static PublicKeyVerify create(RsaSsaPssPublicKey key) throws GeneralSecurityException {
        try {
            return RsaSsaPssVerifyConscrypt.create(key);
        }
        catch (NoSuchProviderException noSuchProviderException) {
            KeyFactory keyFactory = EngineFactory.KEY_FACTORY.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(new RSAPublicKeySpec(key.getModulus(), key.getParameters().getPublicExponent()));
            RsaSsaPssParameters params = key.getParameters();
            return new InternalImpl(publicKey, HASH_TYPE_CONVERTER.toProtoEnum(params.getSigHashType()), HASH_TYPE_CONVERTER.toProtoEnum(params.getMgf1HashType()), params.getSaltLengthBytes(), key.getOutputPrefix().toByteArray(), key.getParameters().getVariant().equals(RsaSsaPssParameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY);
        }
    }

    private static RsaSsaPssParameters.HashType getHashType(Enums.HashType hash) throws GeneralSecurityException {
        switch (hash) {
            case SHA256: {
                return RsaSsaPssParameters.HashType.SHA256;
            }
            case SHA384: {
                return RsaSsaPssParameters.HashType.SHA384;
            }
            case SHA512: {
                return RsaSsaPssParameters.HashType.SHA512;
            }
        }
        throw new GeneralSecurityException("Unsupported hash: " + (Object)((Object)hash));
    }

    @AccessesPartialKey
    private RsaSsaPssPublicKey convertKey(RSAPublicKey pubKey, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength) throws GeneralSecurityException {
        RsaSsaPssParameters parameters = RsaSsaPssParameters.builder().setModulusSizeBits(pubKey.getModulus().bitLength()).setPublicExponent(pubKey.getPublicExponent()).setSigHashType(RsaSsaPssVerifyJce.getHashType(sigHash)).setMgf1HashType(RsaSsaPssVerifyJce.getHashType(mgf1Hash)).setSaltLengthBytes(saltLength).setVariant(RsaSsaPssParameters.Variant.NO_PREFIX).build();
        return RsaSsaPssPublicKey.builder().setParameters(parameters).setModulus(pubKey.getModulus()).build();
    }

    public RsaSsaPssVerifyJce(RSAPublicKey pubKey, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength) throws GeneralSecurityException {
        this.verify = RsaSsaPssVerifyJce.create(this.convertKey(pubKey, sigHash, mgf1Hash, saltLength));
    }

    @Override
    public void verify(byte[] signature, byte[] data) throws GeneralSecurityException {
        this.verify.verify(signature, data);
    }

    private static final class InternalImpl
    implements PublicKeyVerify {
        private final RSAPublicKey publicKey;
        private final Enums.HashType sigHash;
        private final Enums.HashType mgf1Hash;
        private final int saltLength;
        private final byte[] outputPrefix;
        private final byte[] messageSuffix;

        private InternalImpl(RSAPublicKey pubKey, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength, byte[] outputPrefix, byte[] messageSuffix) throws GeneralSecurityException {
            if (TinkFipsUtil.useOnlyFips()) {
                throw new GeneralSecurityException("Can not use RSA PSS in FIPS-mode, as BoringCrypto module is not available.");
            }
            Validators.validateSignatureHash(sigHash);
            if (!sigHash.equals((Object)mgf1Hash)) {
                throw new GeneralSecurityException("sigHash and mgf1Hash must be the same");
            }
            Validators.validateRsaModulusSize(pubKey.getModulus().bitLength());
            Validators.validateRsaPublicExponent(pubKey.getPublicExponent());
            this.publicKey = pubKey;
            this.sigHash = sigHash;
            this.mgf1Hash = mgf1Hash;
            this.saltLength = saltLength;
            this.outputPrefix = outputPrefix;
            this.messageSuffix = messageSuffix;
        }

        private void noPrefixVerify(byte[] signature, byte[] data) throws GeneralSecurityException {
            BigInteger e = this.publicKey.getPublicExponent();
            BigInteger n = this.publicKey.getModulus();
            int nLengthInBytes = (n.bitLength() + 7) / 8;
            int mLen = (n.bitLength() - 1 + 7) / 8;
            if (nLengthInBytes != signature.length) {
                throw new GeneralSecurityException("invalid signature's length");
            }
            BigInteger s = SubtleUtil.bytes2Integer(signature);
            if (s.compareTo(n) >= 0) {
                throw new GeneralSecurityException("signature out of range");
            }
            BigInteger m = s.modPow(e, n);
            byte[] em = SubtleUtil.integer2Bytes(m, mLen);
            this.emsaPssVerify(data, em, n.bitLength() - 1);
        }

        private void emsaPssVerify(byte[] message, byte[] em, int emBits) throws GeneralSecurityException {
            int i;
            Validators.validateSignatureHash(this.sigHash);
            MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance(SubtleUtil.toDigestAlgo(this.sigHash));
            digest.update(message);
            if (this.messageSuffix.length != 0) {
                digest.update(this.messageSuffix);
            }
            byte[] mHash = digest.digest();
            int emLen = em.length;
            int hLen = digest.getDigestLength();
            if (emLen < hLen + this.saltLength + 2) {
                throw new GeneralSecurityException("inconsistent");
            }
            if (em[em.length - 1] != -68) {
                throw new GeneralSecurityException("inconsistent");
            }
            byte[] maskedDb = Arrays.copyOf(em, emLen - hLen - 1);
            byte[] h = Arrays.copyOfRange(em, maskedDb.length, maskedDb.length + hLen);
            int i2 = 0;
            while ((long)i2 < (long)emLen * 8L - (long)emBits) {
                int bytePos = i2 / 8;
                int bitPos = 7 - i2 % 8;
                if ((maskedDb[bytePos] >> bitPos & 1) != 0) {
                    throw new GeneralSecurityException("inconsistent");
                }
                ++i2;
            }
            byte[] dbMask = SubtleUtil.mgf1(h, emLen - hLen - 1, this.mgf1Hash);
            byte[] db = new byte[dbMask.length];
            for (i = 0; i < db.length; ++i) {
                db[i] = (byte)(dbMask[i] ^ maskedDb[i]);
            }
            i = 0;
            while ((long)i <= (long)emLen * 8L - (long)emBits) {
                int bytePos = i / 8;
                int bitPos = 7 - i % 8;
                db[bytePos] = (byte)(db[bytePos] & ~(1 << bitPos));
                ++i;
            }
            for (i = 0; i < emLen - hLen - this.saltLength - 2; ++i) {
                if (db[i] == 0) continue;
                throw new GeneralSecurityException("inconsistent");
            }
            if (db[emLen - hLen - this.saltLength - 2] != 1) {
                throw new GeneralSecurityException("inconsistent");
            }
            byte[] salt = Arrays.copyOfRange(db, db.length - this.saltLength, db.length);
            byte[] mPrime = new byte[8 + hLen + this.saltLength];
            System.arraycopy(mHash, 0, mPrime, 8, mHash.length);
            System.arraycopy(salt, 0, mPrime, 8 + hLen, salt.length);
            byte[] hPrime = digest.digest(mPrime);
            if (!Bytes.equal(hPrime, h)) {
                throw new GeneralSecurityException("inconsistent");
            }
        }

        @Override
        public void verify(byte[] signature, byte[] data) throws GeneralSecurityException {
            if (this.outputPrefix.length == 0) {
                this.noPrefixVerify(signature, data);
                return;
            }
            if (!Util.isPrefix(this.outputPrefix, signature)) {
                throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
            }
            byte[] signatureNoPrefix = Arrays.copyOfRange(signature, this.outputPrefix.length, signature.length);
            this.noPrefixVerify(signatureNoPrefix, data);
        }
    }
}

