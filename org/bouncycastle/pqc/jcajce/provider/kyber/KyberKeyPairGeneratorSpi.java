/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.kyber;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.kyber.BCKyberPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.kyber.BCKyberPublicKey;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.bouncycastle.util.Strings;

public class KyberKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    MLKEMKeyGenerationParameters param;
    MLKEMKeyPairGenerator engine = new MLKEMKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;
    private MLKEMParameters kyberParameters;

    public KyberKeyPairGeneratorSpi() {
        super("KYBER");
        this.kyberParameters = null;
    }

    protected KyberKeyPairGeneratorSpi(MLKEMParameters mLKEMParameters) {
        super(Strings.toUpperCase(mLKEMParameters.getName()));
        this.kyberParameters = mLKEMParameters;
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = KyberKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string != null && parameters.containsKey(string)) {
            MLKEMParameters mLKEMParameters = (MLKEMParameters)parameters.get(string);
            this.param = new MLKEMKeyGenerationParameters(secureRandom, mLKEMParameters);
            if (this.kyberParameters != null && !mLKEMParameters.getName().equals(this.kyberParameters.getName())) {
                throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.kyberParameters.getName()));
            }
        } else {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof KyberParameterSpec) {
            KyberParameterSpec kyberParameterSpec = (KyberParameterSpec)algorithmParameterSpec;
            return kyberParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = this.kyberParameters != null ? new MLKEMKeyGenerationParameters(this.random, this.kyberParameters) : new MLKEMKeyGenerationParameters(this.random, MLKEMParameters.ml_kem_1024);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        MLKEMPublicKeyParameters mLKEMPublicKeyParameters = (MLKEMPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters = (MLKEMPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCKyberPublicKey(mLKEMPublicKeyParameters), new BCKyberPrivateKey(mLKEMPrivateKeyParameters));
    }

    static {
        parameters.put(KyberParameterSpec.kyber512.getName(), MLKEMParameters.ml_kem_512);
        parameters.put(KyberParameterSpec.kyber768.getName(), MLKEMParameters.ml_kem_768);
        parameters.put(KyberParameterSpec.kyber1024.getName(), MLKEMParameters.ml_kem_1024);
    }

    public static class Kyber1024
    extends KyberKeyPairGeneratorSpi {
        public Kyber1024() {
            super(MLKEMParameters.ml_kem_1024);
        }
    }

    public static class Kyber512
    extends KyberKeyPairGeneratorSpi {
        public Kyber512() {
            super(MLKEMParameters.ml_kem_512);
        }
    }

    public static class Kyber768
    extends KyberKeyPairGeneratorSpi {
        public Kyber768() {
            super(MLKEMParameters.ml_kem_768);
        }
    }
}

