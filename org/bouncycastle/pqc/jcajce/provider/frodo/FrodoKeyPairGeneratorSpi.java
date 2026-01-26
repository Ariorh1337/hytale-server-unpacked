/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.frodo;

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
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyPairGenerator;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.frodo.BCFrodoPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.frodo.BCFrodoPublicKey;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;
import org.bouncycastle.util.Strings;

public class FrodoKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    FrodoKeyGenerationParameters param;
    FrodoKeyPairGenerator engine = new FrodoKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public FrodoKeyPairGeneratorSpi() {
        super("Frodo");
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = FrodoKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new FrodoKeyGenerationParameters(secureRandom, (FrodoParameters)parameters.get(string));
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof FrodoParameterSpec) {
            FrodoParameterSpec frodoParameterSpec = (FrodoParameterSpec)algorithmParameterSpec;
            return frodoParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new FrodoKeyGenerationParameters(this.random, FrodoParameters.frodokem1344shake);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        FrodoPublicKeyParameters frodoPublicKeyParameters = (FrodoPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        FrodoPrivateKeyParameters frodoPrivateKeyParameters = (FrodoPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCFrodoPublicKey(frodoPublicKeyParameters), new BCFrodoPrivateKey(frodoPrivateKeyParameters));
    }

    static {
        parameters.put("frodokem19888r3", FrodoParameters.frodokem640aes);
        parameters.put("frodokem19888shaker3", FrodoParameters.frodokem640shake);
        parameters.put("frodokem31296r3", FrodoParameters.frodokem976aes);
        parameters.put("frodokem31296shaker3", FrodoParameters.frodokem976shake);
        parameters.put("frodokem43088r3", FrodoParameters.frodokem1344aes);
        parameters.put("frodokem43088shaker3", FrodoParameters.frodokem1344shake);
        parameters.put(FrodoParameterSpec.frodokem640aes.getName(), FrodoParameters.frodokem640aes);
        parameters.put(FrodoParameterSpec.frodokem640shake.getName(), FrodoParameters.frodokem640shake);
        parameters.put(FrodoParameterSpec.frodokem976aes.getName(), FrodoParameters.frodokem976aes);
        parameters.put(FrodoParameterSpec.frodokem976shake.getName(), FrodoParameters.frodokem976shake);
        parameters.put(FrodoParameterSpec.frodokem1344aes.getName(), FrodoParameters.frodokem1344aes);
        parameters.put(FrodoParameterSpec.frodokem1344shake.getName(), FrodoParameters.frodokem1344shake);
    }
}

