/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.signature.RsaSsaPssParameters;
import com.google.crypto.tink.signature.RsaSsaPssPrivateKey;
import com.google.crypto.tink.signature.RsaSsaPssPublicKey;
import com.google.crypto.tink.signature.internal.RsaSsaPssSignConscrypt;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Enums;
import com.google.crypto.tink.subtle.Random;
import com.google.crypto.tink.subtle.RsaSsaPssVerifyJce;
import com.google.crypto.tink.subtle.SubtleUtil;
import com.google.crypto.tink.subtle.Validators;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

@Immutable
public final class RsaSsaPssSignJce
implements PublicKeySign {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
    private static final byte[] EMPTY = new byte[0];
    private static final byte[] legacyMessageSuffix = new byte[]{0};
    private final PublicKeySign sign;

    @AccessesPartialKey
    public static PublicKeySign create(RsaSsaPssPrivateKey key) throws GeneralSecurityException {
        try {
            return RsaSsaPssSignConscrypt.create(key);
        }
        catch (NoSuchProviderException noSuchProviderException) {
            KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("RSA");
            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey)kf.generatePrivate(new RSAPrivateCrtKeySpec(key.getPublicKey().getModulus(), key.getParameters().getPublicExponent(), key.getPrivateExponent().getBigInteger(InsecureSecretKeyAccess.get()), key.getPrimeP().getBigInteger(InsecureSecretKeyAccess.get()), key.getPrimeQ().getBigInteger(InsecureSecretKeyAccess.get()), key.getPrimeExponentP().getBigInteger(InsecureSecretKeyAccess.get()), key.getPrimeExponentQ().getBigInteger(InsecureSecretKeyAccess.get()), key.getCrtCoefficient().getBigInteger(InsecureSecretKeyAccess.get())));
            RsaSsaPssParameters params = key.getParameters();
            return new InternalImpl(privateKey, RsaSsaPssVerifyJce.HASH_TYPE_CONVERTER.toProtoEnum(params.getSigHashType()), RsaSsaPssVerifyJce.HASH_TYPE_CONVERTER.toProtoEnum(params.getMgf1HashType()), params.getSaltLengthBytes(), key.getOutputPrefix().toByteArray(), key.getParameters().getVariant().equals(RsaSsaPssParameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY);
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
    private RsaSsaPssPrivateKey convertKey(RSAPrivateCrtKey key, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength) throws GeneralSecurityException {
        RsaSsaPssParameters parameters = RsaSsaPssParameters.builder().setModulusSizeBits(key.getModulus().bitLength()).setPublicExponent(key.getPublicExponent()).setSigHashType(RsaSsaPssSignJce.getHashType(sigHash)).setMgf1HashType(RsaSsaPssSignJce.getHashType(mgf1Hash)).setSaltLengthBytes(saltLength).setVariant(RsaSsaPssParameters.Variant.NO_PREFIX).build();
        return RsaSsaPssPrivateKey.builder().setPublicKey(RsaSsaPssPublicKey.builder().setParameters(parameters).setModulus(key.getModulus()).build()).setPrimes(SecretBigInteger.fromBigInteger(key.getPrimeP(), InsecureSecretKeyAccess.get()), SecretBigInteger.fromBigInteger(key.getPrimeQ(), InsecureSecretKeyAccess.get())).setPrivateExponent(SecretBigInteger.fromBigInteger(key.getPrivateExponent(), InsecureSecretKeyAccess.get())).setPrimeExponents(SecretBigInteger.fromBigInteger(key.getPrimeExponentP(), InsecureSecretKeyAccess.get()), SecretBigInteger.fromBigInteger(key.getPrimeExponentQ(), InsecureSecretKeyAccess.get())).setCrtCoefficient(SecretBigInteger.fromBigInteger(key.getCrtCoefficient(), InsecureSecretKeyAccess.get())).build();
    }

    public RsaSsaPssSignJce(RSAPrivateCrtKey priv, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength) throws GeneralSecurityException {
        this.sign = RsaSsaPssSignJce.create(this.convertKey(priv, sigHash, mgf1Hash, saltLength));
    }

    @Override
    public byte[] sign(byte[] data) throws GeneralSecurityException {
        return this.sign.sign(data);
    }

    private static final class InternalImpl
    implements PublicKeySign {
        private final RSAPrivateCrtKey privateKey;
        private final RSAPublicKey publicKey;
        private final Enums.HashType sigHash;
        private final Enums.HashType mgf1Hash;
        private final int saltLength;
        private final byte[] outputPrefix;
        private final byte[] messageSuffix;
        private static final String RAW_RSA_ALGORITHM = "RSA/ECB/NOPADDING";

        private InternalImpl(RSAPrivateCrtKey priv, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength, byte[] outputPrefix, byte[] messageSuffix) throws GeneralSecurityException {
            if (TinkFipsUtil.useOnlyFips()) {
                throw new GeneralSecurityException("Can not use RSA PSS in FIPS-mode, as BoringCrypto module is not available.");
            }
            Validators.validateSignatureHash(sigHash);
            if (!sigHash.equals((Object)mgf1Hash)) {
                throw new GeneralSecurityException("sigHash and mgf1Hash must be the same");
            }
            Validators.validateRsaModulusSize(priv.getModulus().bitLength());
            Validators.validateRsaPublicExponent(priv.getPublicExponent());
            this.privateKey = priv;
            KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("RSA");
            this.publicKey = (RSAPublicKey)kf.generatePublic(new RSAPublicKeySpec(priv.getModulus(), priv.getPublicExponent()));
            this.sigHash = sigHash;
            this.mgf1Hash = mgf1Hash;
            this.saltLength = saltLength;
            this.outputPrefix = outputPrefix;
            this.messageSuffix = messageSuffix;
        }

        private byte[] noPrefixSign(byte[] data) throws GeneralSecurityException {
            int modBits = this.publicKey.getModulus().bitLength();
            byte[] em = this.emsaPssEncode(data, modBits - 1);
            return this.rsasp1(em);
        }

        @Override
        public byte[] sign(byte[] data) throws GeneralSecurityException {
            byte[] signature = this.noPrefixSign(data);
            if (this.outputPrefix.length == 0) {
                return signature;
            }
            return Bytes.concat(this.outputPrefix, signature);
        }

        private byte[] rsasp1(byte[] m) throws GeneralSecurityException {
            Cipher decryptCipher = EngineFactory.CIPHER.getInstance(RAW_RSA_ALGORITHM);
            decryptCipher.init(2, this.privateKey);
            byte[] c = decryptCipher.doFinal(m);
            Cipher encryptCipher = EngineFactory.CIPHER.getInstance(RAW_RSA_ALGORITHM);
            encryptCipher.init(1, this.publicKey);
            byte[] m0 = encryptCipher.doFinal(c);
            if (!new BigInteger(1, m).equals(new BigInteger(1, m0))) {
                throw new IllegalStateException("Security bug: RSA signature computation error");
            }
            return c;
        }

        private byte[] emsaPssEncode(byte[] message, int emBits) throws GeneralSecurityException {
            int i;
            Validators.validateSignatureHash(this.sigHash);
            MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance(SubtleUtil.toDigestAlgo(this.sigHash));
            digest.update(message);
            if (this.messageSuffix.length != 0) {
                digest.update(this.messageSuffix);
            }
            byte[] mHash = digest.digest();
            int emLen = (emBits - 1) / 8 + 1;
            int hLen = digest.getDigestLength();
            if (emLen < hLen + this.saltLength + 2) {
                throw new GeneralSecurityException("encoding error");
            }
            byte[] salt = Random.randBytes(this.saltLength);
            byte[] mPrime = new byte[8 + hLen + this.saltLength];
            System.arraycopy(mHash, 0, mPrime, 8, hLen);
            System.arraycopy(salt, 0, mPrime, 8 + hLen, salt.length);
            byte[] h = digest.digest(mPrime);
            byte[] db = new byte[emLen - hLen - 1];
            db[emLen - this.saltLength - hLen - 2] = 1;
            System.arraycopy(salt, 0, db, emLen - this.saltLength - hLen - 1, salt.length);
            byte[] dbMask = SubtleUtil.mgf1(h, emLen - hLen - 1, this.mgf1Hash);
            byte[] maskedDb = new byte[emLen - hLen - 1];
            for (i = 0; i < maskedDb.length; ++i) {
                maskedDb[i] = (byte)(db[i] ^ dbMask[i]);
            }
            i = 0;
            while ((long)i < (long)emLen * 8L - (long)emBits) {
                int bytePos = i / 8;
                int bitPos = 7 - i % 8;
                maskedDb[bytePos] = (byte)(maskedDb[bytePos] & ~(1 << bitPos));
                ++i;
            }
            byte[] em = new byte[maskedDb.length + hLen + 1];
            System.arraycopy(maskedDb, 0, em, 0, maskedDb.length);
            System.arraycopy(h, 0, em, maskedDb.length, h.length);
            em[maskedDb.length + hLen] = -68;
            return em;
        }
    }
}

