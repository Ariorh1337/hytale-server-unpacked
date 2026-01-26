/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

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
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.Utils;
import org.bouncycastle.jcajce.spec.MLKEMPrivateKeySpec;
import org.bouncycastle.jcajce.spec.MLKEMPublicKeySpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;
import org.bouncycastle.util.Arrays;

public class MLKEMKeyFactorySpi
extends BaseKeyFactorySpi {
    private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet<ASN1ObjectIdentifier>();

    public MLKEMKeyFactorySpi() {
        super(keyOids);
    }

    public MLKEMKeyFactorySpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(aSN1ObjectIdentifier);
    }

    public final KeySpec engineGetKeySpec(Key key, Class clazz) throws InvalidKeySpecException {
        if (key instanceof BCMLKEMPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
            if (MLKEMPrivateKeySpec.class.isAssignableFrom(clazz)) {
                BCMLKEMPrivateKey bCMLKEMPrivateKey = (BCMLKEMPrivateKey)key;
                byte[] byArray = bCMLKEMPrivateKey.getSeed();
                if (byArray != null) {
                    return new MLKEMPrivateKeySpec(bCMLKEMPrivateKey.getParameterSpec(), byArray);
                }
                return new MLKEMPrivateKeySpec(bCMLKEMPrivateKey.getParameterSpec(), bCMLKEMPrivateKey.getPrivateData(), bCMLKEMPrivateKey.getPublicKey().getPublicData());
            }
            if (MLKEMPublicKeySpec.class.isAssignableFrom(clazz)) {
                BCMLKEMPrivateKey bCMLKEMPrivateKey = (BCMLKEMPrivateKey)key;
                return new MLKEMPublicKeySpec(bCMLKEMPrivateKey.getParameterSpec(), bCMLKEMPrivateKey.getPublicKey().getPublicData());
            }
        } else if (key instanceof BCMLKEMPublicKey) {
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
            if (MLKEMPublicKeySpec.class.isAssignableFrom(clazz)) {
                BCMLKEMPublicKey bCMLKEMPublicKey = (BCMLKEMPublicKey)key;
                return new MLKEMPublicKeySpec(bCMLKEMPublicKey.getParameterSpec(), bCMLKEMPublicKey.getPublicData());
            }
        } else {
            throw new InvalidKeySpecException("Unsupported key type: " + key.getClass() + ".");
        }
        throw new InvalidKeySpecException("unknown key specification: " + clazz + ".");
    }

    @Override
    public final Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof BCMLKEMPrivateKey || key instanceof BCMLKEMPublicKey) {
            return key;
        }
        throw new InvalidKeyException("unsupported key type");
    }

    @Override
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof MLKEMPrivateKeySpec) {
            MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters;
            MLKEMPrivateKeySpec mLKEMPrivateKeySpec = (MLKEMPrivateKeySpec)keySpec;
            MLKEMParameters mLKEMParameters = Utils.getParameters(mLKEMPrivateKeySpec.getParameterSpec().getName());
            if (mLKEMPrivateKeySpec.isSeed()) {
                mLKEMPrivateKeyParameters = new MLKEMPrivateKeyParameters(mLKEMParameters, mLKEMPrivateKeySpec.getSeed());
            } else {
                mLKEMPrivateKeyParameters = new MLKEMPrivateKeyParameters(mLKEMParameters, mLKEMPrivateKeySpec.getPrivateData());
                byte[] byArray = mLKEMPrivateKeySpec.getPublicData();
                if (byArray != null && !Arrays.constantTimeAreEqual(byArray, mLKEMPrivateKeyParameters.getPublicKey())) {
                    throw new InvalidKeySpecException("public key data does not match private key data");
                }
            }
            return new BCMLKEMPrivateKey(mLKEMPrivateKeyParameters);
        }
        return super.engineGeneratePrivate(keySpec);
    }

    @Override
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof MLKEMPublicKeySpec) {
            MLKEMPublicKeySpec mLKEMPublicKeySpec = (MLKEMPublicKeySpec)keySpec;
            return new BCMLKEMPublicKey(new MLKEMPublicKeyParameters(Utils.getParameters(mLKEMPublicKeySpec.getParameterSpec().getName()), mLKEMPublicKeySpec.getPublicData()));
        }
        return super.engineGeneratePublic(keySpec);
    }

    @Override
    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        return new BCMLKEMPrivateKey(privateKeyInfo);
    }

    @Override
    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return new BCMLKEMPublicKey(subjectPublicKeyInfo);
    }

    static {
        keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_512);
        keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_768);
        keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_1024);
    }

    public static class MLKEM1024
    extends MLKEMKeyFactorySpi {
        public MLKEM1024() {
            super(NISTObjectIdentifiers.id_alg_ml_kem_1024);
        }
    }

    public static class MLKEM512
    extends MLKEMKeyFactorySpi {
        public MLKEM512() {
            super(NISTObjectIdentifiers.id_alg_ml_kem_512);
        }
    }

    public static class MLKEM768
    extends MLKEMKeyFactorySpi {
        public MLKEM768() {
            super(NISTObjectIdentifiers.id_alg_ml_kem_768);
        }
    }
}

