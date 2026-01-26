/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.BCMLKEMPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.Utils;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.util.Strings;

public class MLKEMKeyPairGeneratorSpi
extends KeyPairGenerator {
    MLKEMKeyGenerationParameters param;
    MLKEMKeyPairGenerator engine = new MLKEMKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;
    private MLKEMParameters mlkemParameters;

    public MLKEMKeyPairGeneratorSpi() {
        super("ML-KEM");
    }

    protected MLKEMKeyPairGeneratorSpi(MLKEMParameterSpec mLKEMParameterSpec) {
        super(Strings.toUpperCase(mLKEMParameterSpec.getName()));
        this.mlkemParameters = Utils.getParameters(mLKEMParameterSpec.getName());
        if (this.param == null) {
            this.param = new MLKEMKeyGenerationParameters(this.random, this.mlkemParameters);
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
        MLKEMParameters mLKEMParameters;
        String string = MLKEMKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string != null) {
            mLKEMParameters = Utils.getParameters(string);
            if (mLKEMParameters == null) {
                throw new InvalidAlgorithmParameterException("unknown parameter set name: " + string);
            }
            if (this.mlkemParameters != null && !mLKEMParameters.getName().equals(this.mlkemParameters.getName())) {
                throw new InvalidAlgorithmParameterException("key pair generator locked to " + this.getAlgorithm());
            }
        } else {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new MLKEMKeyGenerationParameters(secureRandom, mLKEMParameters);
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new MLKEMKeyGenerationParameters(this.random, MLKEMParameters.ml_kem_768);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        MLKEMPublicKeyParameters mLKEMPublicKeyParameters = (MLKEMPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters = (MLKEMPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCMLKEMPublicKey(mLKEMPublicKeyParameters), new BCMLKEMPrivateKey(mLKEMPrivateKeyParameters));
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof MLKEMParameterSpec) {
            MLKEMParameterSpec mLKEMParameterSpec = (MLKEMParameterSpec)algorithmParameterSpec;
            return mLKEMParameterSpec.getName();
        }
        return Strings.toUpperCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    public static class MLKEM1024
    extends MLKEMKeyPairGeneratorSpi {
        public MLKEM1024() {
            super(MLKEMParameterSpec.ml_kem_1024);
        }
    }

    public static class MLKEM512
    extends MLKEMKeyPairGeneratorSpi {
        public MLKEM512() {
            super(MLKEMParameterSpec.ml_kem_512);
        }
    }

    public static class MLKEM768
    extends MLKEMKeyPairGeneratorSpi {
        public MLKEM768() {
            super(MLKEMParameterSpec.ml_kem_768);
        }
    }
}

