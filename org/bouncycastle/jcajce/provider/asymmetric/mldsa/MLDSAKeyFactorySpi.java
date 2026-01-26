/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.Utils;
import org.bouncycastle.jcajce.spec.MLDSAPrivateKeySpec;
import org.bouncycastle.jcajce.spec.MLDSAPublicKeySpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;
import org.bouncycastle.util.Arrays;

public class MLDSAKeyFactorySpi
extends BaseKeyFactorySpi {
    private static final Set<ASN1ObjectIdentifier> pureKeyOids = new HashSet<ASN1ObjectIdentifier>();
    private static final Set<ASN1ObjectIdentifier> hashKeyOids = new HashSet<ASN1ObjectIdentifier>();
    private final boolean isHashOnly;

    public MLDSAKeyFactorySpi(Set<ASN1ObjectIdentifier> set) {
        super(set);
        this.isHashOnly = false;
    }

    public MLDSAKeyFactorySpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(aSN1ObjectIdentifier);
        this.isHashOnly = aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
    }

    public final KeySpec engineGetKeySpec(Key key, Class clazz) throws InvalidKeySpecException {
        if (key instanceof BCMLDSAPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
            if (MLDSAPrivateKeySpec.class.isAssignableFrom(clazz)) {
                BCMLDSAPrivateKey bCMLDSAPrivateKey = (BCMLDSAPrivateKey)key;
                byte[] byArray = bCMLDSAPrivateKey.getSeed();
                if (byArray != null) {
                    return new MLDSAPrivateKeySpec(bCMLDSAPrivateKey.getParameterSpec(), byArray);
                }
                return new MLDSAPrivateKeySpec(bCMLDSAPrivateKey.getParameterSpec(), bCMLDSAPrivateKey.getPrivateData(), bCMLDSAPrivateKey.getPublicKey().getPublicData());
            }
            if (MLDSAPublicKeySpec.class.isAssignableFrom(clazz)) {
                BCMLDSAPrivateKey bCMLDSAPrivateKey = (BCMLDSAPrivateKey)key;
                return new MLDSAPublicKeySpec(bCMLDSAPrivateKey.getParameterSpec(), bCMLDSAPrivateKey.getPublicKey().getPublicData());
            }
        } else if (key instanceof BCMLDSAPublicKey) {
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
            if (MLDSAPublicKeySpec.class.isAssignableFrom(clazz)) {
                BCMLDSAPublicKey bCMLDSAPublicKey = (BCMLDSAPublicKey)key;
                return new MLDSAPublicKeySpec(bCMLDSAPublicKey.getParameterSpec(), bCMLDSAPublicKey.getPublicData());
            }
        } else {
            throw new InvalidKeySpecException("unsupported key type: " + key.getClass() + ".");
        }
        throw new InvalidKeySpecException("unknown key specification: " + clazz + ".");
    }

    @Override
    public final Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof BCMLDSAPrivateKey || key instanceof BCMLDSAPublicKey) {
            return key;
        }
        throw new InvalidKeyException("unsupported key type");
    }

    @Override
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof MLDSAPrivateKeySpec) {
            MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters;
            MLDSAPrivateKeySpec mLDSAPrivateKeySpec = (MLDSAPrivateKeySpec)keySpec;
            MLDSAParameters mLDSAParameters = Utils.getParameters(mLDSAPrivateKeySpec.getParameterSpec().getName());
            if (mLDSAPrivateKeySpec.isSeed()) {
                mLDSAPrivateKeyParameters = new MLDSAPrivateKeyParameters(mLDSAParameters, mLDSAPrivateKeySpec.getSeed());
            } else {
                mLDSAPrivateKeyParameters = new MLDSAPrivateKeyParameters(mLDSAParameters, mLDSAPrivateKeySpec.getPrivateData(), null);
                byte[] byArray = mLDSAPrivateKeySpec.getPublicData();
                if (byArray != null && !Arrays.constantTimeAreEqual(byArray, mLDSAPrivateKeyParameters.getPublicKey())) {
                    throw new InvalidKeySpecException("public key data does not match private key data");
                }
            }
            return new BCMLDSAPrivateKey(mLDSAPrivateKeyParameters);
        }
        return super.engineGeneratePrivate(keySpec);
    }

    @Override
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof MLDSAPublicKeySpec) {
            MLDSAPublicKeySpec mLDSAPublicKeySpec = (MLDSAPublicKeySpec)keySpec;
            return new BCMLDSAPublicKey(new MLDSAPublicKeyParameters(Utils.getParameters(mLDSAPublicKeySpec.getParameterSpec().getName()), mLDSAPublicKeySpec.getPublicData()));
        }
        return super.engineGeneratePublic(keySpec);
    }

    @Override
    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        BCMLDSAPrivateKey bCMLDSAPrivateKey = new BCMLDSAPrivateKey(privateKeyInfo);
        if (!this.isHashOnly || bCMLDSAPrivateKey.getAlgorithm().indexOf("WITH") > 0) {
            return bCMLDSAPrivateKey;
        }
        MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters = bCMLDSAPrivateKey.getKeyParams();
        MLDSAParameters mLDSAParameters = null;
        if (mLDSAPrivateKeyParameters.getParameters().equals(MLDSAParameters.ml_dsa_44)) {
            mLDSAParameters = MLDSAParameters.ml_dsa_44_with_sha512;
        } else if (mLDSAPrivateKeyParameters.getParameters().equals(MLDSAParameters.ml_dsa_65)) {
            mLDSAParameters = MLDSAParameters.ml_dsa_65_with_sha512;
        } else if (mLDSAPrivateKeyParameters.getParameters().equals(MLDSAParameters.ml_dsa_87)) {
            mLDSAParameters = MLDSAParameters.ml_dsa_87_with_sha512;
        } else {
            throw new IllegalStateException("unknown ML-DSA parameters");
        }
        MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters2 = new MLDSAPrivateKeyParameters(mLDSAParameters, mLDSAPrivateKeyParameters.getRho(), mLDSAPrivateKeyParameters.getK(), mLDSAPrivateKeyParameters.getTr(), mLDSAPrivateKeyParameters.getS1(), mLDSAPrivateKeyParameters.getS2(), mLDSAPrivateKeyParameters.getT0(), mLDSAPrivateKeyParameters.getT1(), mLDSAPrivateKeyParameters.getSeed());
        return new BCMLDSAPrivateKey(mLDSAPrivateKeyParameters2);
    }

    @Override
    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return new BCMLDSAPublicKey(subjectPublicKeyInfo);
    }

    static {
        pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_44);
        pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_65);
        pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_87);
        hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_44);
        hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_65);
        hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_87);
        hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
        hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
        hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
    }

    public static class Hash
    extends MLDSAKeyFactorySpi {
        public Hash() {
            super(hashKeyOids);
        }
    }

    public static class HashMLDSA44
    extends MLDSAKeyFactorySpi {
        public HashMLDSA44() {
            super(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
        }
    }

    public static class HashMLDSA65
    extends MLDSAKeyFactorySpi {
        public HashMLDSA65() {
            super(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
        }
    }

    public static class HashMLDSA87
    extends MLDSAKeyFactorySpi {
        public HashMLDSA87() {
            super(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
        }
    }

    public static class MLDSA44
    extends MLDSAKeyFactorySpi {
        public MLDSA44() {
            super(NISTObjectIdentifiers.id_ml_dsa_44);
        }
    }

    public static class MLDSA65
    extends MLDSAKeyFactorySpi {
        public MLDSA65() {
            super(NISTObjectIdentifiers.id_ml_dsa_65);
        }
    }

    public static class MLDSA87
    extends MLDSAKeyFactorySpi {
        public MLDSA87() {
            super(NISTObjectIdentifiers.id_ml_dsa_87);
        }
    }

    public static class Pure
    extends MLDSAKeyFactorySpi {
        public Pure() {
            super(pureKeyOids);
        }
    }
}

