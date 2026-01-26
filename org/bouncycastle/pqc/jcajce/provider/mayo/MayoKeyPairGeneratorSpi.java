/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.mayo;

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
import org.bouncycastle.pqc.crypto.mayo.MayoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.mayo.BCMayoPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.mayo.BCMayoPublicKey;
import org.bouncycastle.pqc.jcajce.spec.MayoParameterSpec;
import org.bouncycastle.util.Strings;

public class MayoKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    MayoKeyGenerationParameters param;
    private MayoParameters mayoParameters;
    MayoKeyPairGenerator engine = new MayoKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public MayoKeyPairGeneratorSpi() {
        super("Mayo");
    }

    protected MayoKeyPairGeneratorSpi(MayoParameters mayoParameters) {
        super(mayoParameters.getName());
        this.mayoParameters = mayoParameters;
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = MayoKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new MayoKeyGenerationParameters(secureRandom, (MayoParameters)parameters.get(string));
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof MayoParameterSpec) {
            MayoParameterSpec mayoParameterSpec = (MayoParameterSpec)algorithmParameterSpec;
            return mayoParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new MayoKeyGenerationParameters(this.random, MayoParameters.mayo1);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        MayoPublicKeyParameters mayoPublicKeyParameters = (MayoPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        MayoPrivateKeyParameters mayoPrivateKeyParameters = (MayoPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCMayoPublicKey(mayoPublicKeyParameters), new BCMayoPrivateKey(mayoPrivateKeyParameters));
    }

    static {
        parameters.put("MAYO_1", MayoParameters.mayo1);
        parameters.put("MAYO_2", MayoParameters.mayo2);
        parameters.put("MAYO_3", MayoParameters.mayo3);
        parameters.put("MAYO_5", MayoParameters.mayo5);
        parameters.put(MayoParameterSpec.mayo1.getName(), MayoParameters.mayo1);
        parameters.put(MayoParameterSpec.mayo2.getName(), MayoParameters.mayo2);
        parameters.put(MayoParameterSpec.mayo3.getName(), MayoParameters.mayo3);
        parameters.put(MayoParameterSpec.mayo5.getName(), MayoParameters.mayo5);
    }

    public static class Mayo1
    extends MayoKeyPairGeneratorSpi {
        public Mayo1() {
            super(MayoParameters.mayo1);
        }
    }

    public static class Mayo2
    extends MayoKeyPairGeneratorSpi {
        public Mayo2() {
            super(MayoParameters.mayo2);
        }
    }

    public static class Mayo3
    extends MayoKeyPairGeneratorSpi {
        public Mayo3() {
            super(MayoParameters.mayo3);
        }
    }

    public static class Mayo5
    extends MayoKeyPairGeneratorSpi {
        public Mayo5() {
            super(MayoParameters.mayo5);
        }
    }
}

