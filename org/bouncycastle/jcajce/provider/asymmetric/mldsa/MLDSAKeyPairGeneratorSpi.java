/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.BCMLDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.Utils;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.util.Strings;

public class MLDSAKeyPairGeneratorSpi
extends KeyPairGenerator {
    private final MLDSAParameters mldsaParameters;
    MLDSAKeyGenerationParameters param;
    MLDSAKeyPairGenerator engine = new MLDSAKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public MLDSAKeyPairGeneratorSpi(String string) {
        super(string);
        this.mldsaParameters = null;
    }

    protected MLDSAKeyPairGeneratorSpi(MLDSAParameterSpec mLDSAParameterSpec) {
        super(Strings.toUpperCase(mLDSAParameterSpec.getName()));
        this.mldsaParameters = Utils.getParameters(mLDSAParameterSpec.getName());
        if (this.param == null) {
            this.param = new MLDSAKeyGenerationParameters(this.random, this.mldsaParameters);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        try {
            this.initialize(algorithmParameterSpec, new BCJcaJceHelper().createSecureRandom("DEFAULT"));
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new IllegalStateException("unable to find DEFAULT DRBG");
        }
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = MLDSAKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string != null) {
            MLDSAParameters mLDSAParameters = Utils.getParameters(string);
            if (mLDSAParameters == null) {
                throw new InvalidAlgorithmParameterException("unknown parameter set name: " + string);
            }
            this.param = new MLDSAKeyGenerationParameters(secureRandom, mLDSAParameters);
            if (this.mldsaParameters != null && !mLDSAParameters.getName().equals(this.mldsaParameters.getName())) {
                throw new InvalidAlgorithmParameterException("key pair generator locked to " + MLDSAParameterSpec.fromName(this.mldsaParameters.getName()).getName());
            }
        } else {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = this.getAlgorithm().startsWith("HASH") ? new MLDSAKeyGenerationParameters(this.random, MLDSAParameters.ml_dsa_87_with_sha512) : new MLDSAKeyGenerationParameters(this.random, MLDSAParameters.ml_dsa_87);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        MLDSAPublicKeyParameters mLDSAPublicKeyParameters = (MLDSAPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters = (MLDSAPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCMLDSAPublicKey(mLDSAPublicKeyParameters), new BCMLDSAPrivateKey(mLDSAPrivateKeyParameters));
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof MLDSAParameterSpec) {
            MLDSAParameterSpec mLDSAParameterSpec = (MLDSAParameterSpec)algorithmParameterSpec;
            return mLDSAParameterSpec.getName();
        }
        return Strings.toUpperCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    public static class Hash
    extends MLDSAKeyPairGeneratorSpi {
        public Hash() throws NoSuchAlgorithmException {
            super("HASH-ML-DSA");
        }
    }

    public static class MLDSA44
    extends MLDSAKeyPairGeneratorSpi {
        public MLDSA44() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_44);
        }
    }

    public static class MLDSA44withSHA512
    extends MLDSAKeyPairGeneratorSpi {
        public MLDSA44withSHA512() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_44_with_sha512);
        }
    }

    public static class MLDSA65
    extends MLDSAKeyPairGeneratorSpi {
        public MLDSA65() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_65);
        }
    }

    public static class MLDSA65withSHA512
    extends MLDSAKeyPairGeneratorSpi {
        public MLDSA65withSHA512() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_65_with_sha512);
        }
    }

    public static class MLDSA87
    extends MLDSAKeyPairGeneratorSpi {
        public MLDSA87() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_87);
        }
    }

    public static class MLDSA87withSHA512
    extends MLDSAKeyPairGeneratorSpi {
        public MLDSA87withSHA512() throws NoSuchAlgorithmException {
            super(MLDSAParameterSpec.ml_dsa_87_with_sha512);
        }
    }

    public static class Pure
    extends MLDSAKeyPairGeneratorSpi {
        public Pure() throws NoSuchAlgorithmException {
            super("ML-DSA");
        }
    }
}

