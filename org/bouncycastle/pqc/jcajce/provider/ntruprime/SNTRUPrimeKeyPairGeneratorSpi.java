/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.ntruprime;

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
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.BCSNTRUPrimePrivateKey;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.BCSNTRUPrimePublicKey;
import org.bouncycastle.pqc.jcajce.spec.SNTRUPrimeParameterSpec;
import org.bouncycastle.util.Strings;

public class SNTRUPrimeKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    SNTRUPrimeKeyGenerationParameters param;
    SNTRUPrimeKeyPairGenerator engine = new SNTRUPrimeKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public SNTRUPrimeKeyPairGeneratorSpi() {
        super("SNTRUPrime");
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = SNTRUPrimeKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new SNTRUPrimeKeyGenerationParameters(secureRandom, (SNTRUPrimeParameters)parameters.get(string));
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof SNTRUPrimeParameterSpec) {
            SNTRUPrimeParameterSpec sNTRUPrimeParameterSpec = (SNTRUPrimeParameterSpec)algorithmParameterSpec;
            return sNTRUPrimeParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new SNTRUPrimeKeyGenerationParameters(this.random, SNTRUPrimeParameters.sntrup953);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        SNTRUPrimePublicKeyParameters sNTRUPrimePublicKeyParameters = (SNTRUPrimePublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        SNTRUPrimePrivateKeyParameters sNTRUPrimePrivateKeyParameters = (SNTRUPrimePrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCSNTRUPrimePublicKey(sNTRUPrimePublicKeyParameters), new BCSNTRUPrimePrivateKey(sNTRUPrimePrivateKeyParameters));
    }

    static {
        parameters.put(SNTRUPrimeParameterSpec.sntrup653.getName(), SNTRUPrimeParameters.sntrup653);
        parameters.put(SNTRUPrimeParameterSpec.sntrup761.getName(), SNTRUPrimeParameters.sntrup761);
        parameters.put(SNTRUPrimeParameterSpec.sntrup857.getName(), SNTRUPrimeParameters.sntrup857);
        parameters.put(SNTRUPrimeParameterSpec.sntrup953.getName(), SNTRUPrimeParameters.sntrup953);
        parameters.put(SNTRUPrimeParameterSpec.sntrup1013.getName(), SNTRUPrimeParameters.sntrup1013);
        parameters.put(SNTRUPrimeParameterSpec.sntrup1277.getName(), SNTRUPrimeParameters.sntrup1277);
    }
}

