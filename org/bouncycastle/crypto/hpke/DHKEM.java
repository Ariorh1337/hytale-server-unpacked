/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.BasicRawAgreement;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.hpke.HKDF;
import org.bouncycastle.crypto.hpke.KEM;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.math.ec.rfc7748.X448;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

class DHKEM
extends KEM {
    private AsymmetricCipherKeyPairGenerator kpGen;
    private RawAgreement rawAgreement;
    private final short kemId;
    private HKDF hkdf;
    private byte bitmask;
    private int Nsk;
    private int Nsecret;
    private int Nenc;
    ECDomainParameters domainParams;

    protected DHKEM(short s) {
        this.kemId = s;
        switch (s) {
            case 16: {
                this.hkdf = new HKDF(1);
                this.domainParams = DHKEM.getDomainParameters("P-256");
                this.rawAgreement = new BasicRawAgreement(new ECDHCBasicAgreement());
                this.bitmask = (byte)-1;
                this.Nsk = 32;
                this.Nsecret = 32;
                this.Nenc = 65;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, DHKEM.getSecureRandom()));
                break;
            }
            case 17: {
                this.hkdf = new HKDF(2);
                this.domainParams = DHKEM.getDomainParameters("P-384");
                this.rawAgreement = new BasicRawAgreement(new ECDHCBasicAgreement());
                this.bitmask = (byte)-1;
                this.Nsk = 48;
                this.Nsecret = 48;
                this.Nenc = 97;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, DHKEM.getSecureRandom()));
                break;
            }
            case 18: {
                this.hkdf = new HKDF(3);
                this.domainParams = DHKEM.getDomainParameters("P-521");
                this.rawAgreement = new BasicRawAgreement(new ECDHCBasicAgreement());
                this.bitmask = 1;
                this.Nsk = 66;
                this.Nsecret = 64;
                this.Nenc = 133;
                this.kpGen = new ECKeyPairGenerator();
                this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, DHKEM.getSecureRandom()));
                break;
            }
            case 32: {
                this.hkdf = new HKDF(1);
                this.rawAgreement = new X25519Agreement();
                this.Nsecret = 32;
                this.Nsk = 32;
                this.Nenc = 32;
                this.kpGen = new X25519KeyPairGenerator();
                this.kpGen.init(new X25519KeyGenerationParameters(DHKEM.getSecureRandom()));
                break;
            }
            case 33: {
                this.hkdf = new HKDF(3);
                this.rawAgreement = new X448Agreement();
                this.Nsecret = 64;
                this.Nsk = 56;
                this.Nenc = 56;
                this.kpGen = new X448KeyPairGenerator();
                this.kpGen.init(new X448KeyGenerationParameters(DHKEM.getSecureRandom()));
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid kem id");
            }
        }
    }

    @Override
    public byte[] SerializePublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                return ((ECPublicKeyParameters)asymmetricKeyParameter).getQ().getEncoded(false);
            }
            case 33: {
                return ((X448PublicKeyParameters)asymmetricKeyParameter).getEncoded();
            }
            case 32: {
                return ((X25519PublicKeyParameters)asymmetricKeyParameter).getEncoded();
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    @Override
    public byte[] SerializePrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                return BigIntegers.asUnsignedByteArray(this.Nsk, ((ECPrivateKeyParameters)asymmetricKeyParameter).getD());
            }
            case 33: {
                byte[] byArray = ((X448PrivateKeyParameters)asymmetricKeyParameter).getEncoded();
                X448.clampPrivateKey(byArray);
                return byArray;
            }
            case 32: {
                byte[] byArray = ((X25519PrivateKeyParameters)asymmetricKeyParameter).getEncoded();
                X25519.clampPrivateKey(byArray);
                return byArray;
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    @Override
    public AsymmetricKeyParameter DeserializePublicKey(byte[] byArray) {
        if (byArray == null) {
            throw new NullPointerException("'pkEncoded' cannot be null");
        }
        if (byArray.length != this.Nenc) {
            throw new IllegalArgumentException("'pkEncoded' has invalid length");
        }
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                if (byArray[0] != 4) {
                    throw new IllegalArgumentException("'pkEncoded' has invalid format");
                }
                ECPoint eCPoint = this.domainParams.getCurve().decodePoint(byArray);
                return new ECPublicKeyParameters(eCPoint, this.domainParams);
            }
            case 33: {
                return new X448PublicKeyParameters(byArray);
            }
            case 32: {
                return new X25519PublicKeyParameters(byArray);
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    @Override
    public AsymmetricCipherKeyPair DeserializePrivateKey(byte[] byArray, byte[] byArray2) {
        if (byArray == null) {
            throw new NullPointerException("'skEncoded' cannot be null");
        }
        if (byArray.length != this.Nsk) {
            throw new IllegalArgumentException("'skEncoded' has invalid length");
        }
        AsymmetricKeyParameter asymmetricKeyParameter = null;
        if (byArray2 != null) {
            asymmetricKeyParameter = this.DeserializePublicKey(byArray2);
        }
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                BigInteger bigInteger = new BigInteger(1, byArray);
                ECPrivateKeyParameters eCPrivateKeyParameters = new ECPrivateKeyParameters(bigInteger, this.domainParams);
                if (asymmetricKeyParameter == null) {
                    ECPoint eCPoint = new FixedPointCombMultiplier().multiply(this.domainParams.getG(), eCPrivateKeyParameters.getD());
                    asymmetricKeyParameter = new ECPublicKeyParameters(eCPoint, this.domainParams);
                }
                return new AsymmetricCipherKeyPair(asymmetricKeyParameter, eCPrivateKeyParameters);
            }
            case 33: {
                X448PrivateKeyParameters x448PrivateKeyParameters = new X448PrivateKeyParameters(byArray);
                if (asymmetricKeyParameter == null) {
                    asymmetricKeyParameter = x448PrivateKeyParameters.generatePublicKey();
                }
                return new AsymmetricCipherKeyPair(asymmetricKeyParameter, x448PrivateKeyParameters);
            }
            case 32: {
                X25519PrivateKeyParameters x25519PrivateKeyParameters = new X25519PrivateKeyParameters(byArray);
                if (asymmetricKeyParameter == null) {
                    asymmetricKeyParameter = x25519PrivateKeyParameters.generatePublicKey();
                }
                return new AsymmetricCipherKeyPair(asymmetricKeyParameter, x25519PrivateKeyParameters);
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    @Override
    int getEncryptionSize() {
        return this.Nenc;
    }

    private boolean validateSk(BigInteger bigInteger) {
        BigInteger bigInteger2 = this.domainParams.getN();
        int n = bigInteger2.bitLength();
        int n2 = n >>> 2;
        if (bigInteger.compareTo(BigInteger.valueOf(1L)) < 0 || bigInteger.compareTo(bigInteger2) >= 0) {
            return false;
        }
        return WNafUtil.getNafWeight(bigInteger) >= n2;
    }

    @Override
    public AsymmetricCipherKeyPair GeneratePrivateKey() {
        return this.kpGen.generateKeyPair();
    }

    @Override
    public AsymmetricCipherKeyPair DeriveKeyPair(byte[] byArray) {
        byte[] byArray2 = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
        switch (this.kemId) {
            case 16: 
            case 17: 
            case 18: {
                byte[] byArray3 = this.hkdf.LabeledExtract(null, byArray2, "dkp_prk", byArray);
                byte[] byArray4 = new byte[1];
                for (int i = 0; i < 256; ++i) {
                    byArray4[0] = (byte)i;
                    byte[] byArray5 = this.hkdf.LabeledExpand(byArray3, byArray2, "candidate", byArray4, this.Nsk);
                    byArray5[0] = (byte)(byArray5[0] & this.bitmask);
                    BigInteger bigInteger = new BigInteger(1, byArray5);
                    if (!this.validateSk(bigInteger)) continue;
                    ECPoint eCPoint = new FixedPointCombMultiplier().multiply(this.domainParams.getG(), bigInteger);
                    ECPrivateKeyParameters eCPrivateKeyParameters = new ECPrivateKeyParameters(bigInteger, this.domainParams);
                    ECPublicKeyParameters eCPublicKeyParameters = new ECPublicKeyParameters(eCPoint, this.domainParams);
                    return new AsymmetricCipherKeyPair(eCPublicKeyParameters, eCPrivateKeyParameters);
                }
                throw new IllegalStateException("DeriveKeyPairError");
            }
            case 33: {
                byte[] byArray6 = this.hkdf.LabeledExtract(null, byArray2, "dkp_prk", byArray);
                byte[] byArray7 = this.hkdf.LabeledExpand(byArray6, byArray2, "sk", null, this.Nsk);
                X448PrivateKeyParameters x448PrivateKeyParameters = new X448PrivateKeyParameters(byArray7);
                return new AsymmetricCipherKeyPair(x448PrivateKeyParameters.generatePublicKey(), x448PrivateKeyParameters);
            }
            case 32: {
                byte[] byArray8 = this.hkdf.LabeledExtract(null, byArray2, "dkp_prk", byArray);
                byte[] byArray9 = this.hkdf.LabeledExpand(byArray8, byArray2, "sk", null, this.Nsk);
                X25519PrivateKeyParameters x25519PrivateKeyParameters = new X25519PrivateKeyParameters(byArray9);
                return new AsymmetricCipherKeyPair(x25519PrivateKeyParameters.generatePublicKey(), x25519PrivateKeyParameters);
            }
        }
        throw new IllegalStateException("invalid kem id");
    }

    @Override
    protected byte[][] Encap(AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.Encap(asymmetricKeyParameter, this.kpGen.generateKeyPair());
    }

    @Override
    protected byte[][] Encap(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] byArrayArray = new byte[2][];
        byte[] byArray = DHKEM.calculateRawAgreement(this.rawAgreement, asymmetricCipherKeyPair.getPrivate(), asymmetricKeyParameter);
        byte[] byArray2 = this.SerializePublicKey(asymmetricCipherKeyPair.getPublic());
        byte[] byArray3 = this.SerializePublicKey(asymmetricKeyParameter);
        byte[] byArray4 = Arrays.concatenate(byArray2, byArray3);
        byte[] byArray5 = this.ExtractAndExpand(byArray, byArray4);
        byArrayArray[0] = byArray5;
        byArrayArray[1] = byArray2;
        return byArrayArray;
    }

    @Override
    protected byte[] Decap(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        AsymmetricKeyParameter asymmetricKeyParameter = this.DeserializePublicKey(byArray);
        byte[] byArray2 = DHKEM.calculateRawAgreement(this.rawAgreement, asymmetricCipherKeyPair.getPrivate(), asymmetricKeyParameter);
        byte[] byArray3 = this.SerializePublicKey(asymmetricCipherKeyPair.getPublic());
        byte[] byArray4 = Arrays.concatenate(byArray, byArray3);
        return this.ExtractAndExpand(byArray2, byArray4);
    }

    @Override
    protected byte[][] AuthEncap(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricCipherKeyPair asymmetricCipherKeyPair) {
        byte[][] byArrayArray = new byte[2][];
        AsymmetricCipherKeyPair asymmetricCipherKeyPair2 = this.kpGen.generateKeyPair();
        this.rawAgreement.init(asymmetricCipherKeyPair2.getPrivate());
        int n = this.rawAgreement.getAgreementSize();
        byte[] byArray = new byte[n * 2];
        this.rawAgreement.calculateAgreement(asymmetricKeyParameter, byArray, 0);
        this.rawAgreement.init(asymmetricCipherKeyPair.getPrivate());
        if (n != this.rawAgreement.getAgreementSize()) {
            throw new IllegalStateException();
        }
        this.rawAgreement.calculateAgreement(asymmetricKeyParameter, byArray, n);
        byte[] byArray2 = this.SerializePublicKey(asymmetricCipherKeyPair2.getPublic());
        byte[] byArray3 = this.SerializePublicKey(asymmetricKeyParameter);
        byte[] byArray4 = this.SerializePublicKey(asymmetricCipherKeyPair.getPublic());
        byte[] byArray5 = Arrays.concatenate(byArray2, byArray3, byArray4);
        byte[] byArray6 = this.ExtractAndExpand(byArray, byArray5);
        byArrayArray[0] = byArray6;
        byArrayArray[1] = byArray2;
        return byArrayArray;
    }

    @Override
    protected byte[] AuthDecap(byte[] byArray, AsymmetricCipherKeyPair asymmetricCipherKeyPair, AsymmetricKeyParameter asymmetricKeyParameter) {
        AsymmetricKeyParameter asymmetricKeyParameter2 = this.DeserializePublicKey(byArray);
        this.rawAgreement.init(asymmetricCipherKeyPair.getPrivate());
        int n = this.rawAgreement.getAgreementSize();
        byte[] byArray2 = new byte[n * 2];
        this.rawAgreement.calculateAgreement(asymmetricKeyParameter2, byArray2, 0);
        this.rawAgreement.calculateAgreement(asymmetricKeyParameter, byArray2, n);
        byte[] byArray3 = this.SerializePublicKey(asymmetricCipherKeyPair.getPublic());
        byte[] byArray4 = this.SerializePublicKey(asymmetricKeyParameter);
        byte[] byArray5 = Arrays.concatenate(byArray, byArray3, byArray4);
        return this.ExtractAndExpand(byArray2, byArray5);
    }

    private byte[] ExtractAndExpand(byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
        byte[] byArray4 = this.hkdf.LabeledExtract(null, byArray3, "eae_prk", byArray);
        return this.hkdf.LabeledExpand(byArray4, byArray3, "shared_secret", byArray2, this.Nsecret);
    }

    private static byte[] calculateRawAgreement(RawAgreement rawAgreement, AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricKeyParameter asymmetricKeyParameter2) {
        rawAgreement.init(asymmetricKeyParameter);
        byte[] byArray = new byte[rawAgreement.getAgreementSize()];
        rawAgreement.calculateAgreement(asymmetricKeyParameter2, byArray, 0);
        return byArray;
    }

    private static ECDomainParameters getDomainParameters(String string) {
        return new ECDomainParameters(CustomNamedCurves.getByName(string));
    }

    private static SecureRandom getSecureRandom() {
        return CryptoServicesRegistrar.getSecureRandom();
    }
}

