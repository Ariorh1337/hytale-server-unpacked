/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.falcon;

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
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyPairGenerator;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.falcon.BCFalconPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.falcon.BCFalconPublicKey;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;
import org.bouncycastle.util.Strings;

public class FalconKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    private final FalconParameters falconParameters;
    FalconKeyGenerationParameters param;
    FalconKeyPairGenerator engine = new FalconKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public FalconKeyPairGeneratorSpi() {
        super("FALCON");
        this.falconParameters = null;
    }

    protected FalconKeyPairGeneratorSpi(FalconParameters falconParameters) {
        super(falconParameters.getName());
        this.falconParameters = falconParameters;
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = FalconKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string != null && parameters.containsKey(string)) {
            FalconParameters falconParameters = (FalconParameters)parameters.get(string);
            this.param = new FalconKeyGenerationParameters(secureRandom, falconParameters);
            if (this.falconParameters != null && !falconParameters.getName().equals(this.falconParameters.getName())) {
                throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.falconParameters.getName()));
            }
        } else {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof FalconParameterSpec) {
            FalconParameterSpec falconParameterSpec = (FalconParameterSpec)algorithmParameterSpec;
            return falconParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = this.falconParameters != null ? new FalconKeyGenerationParameters(this.random, this.falconParameters) : new FalconKeyGenerationParameters(this.random, FalconParameters.falcon_512);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        FalconPublicKeyParameters falconPublicKeyParameters = (FalconPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        FalconPrivateKeyParameters falconPrivateKeyParameters = (FalconPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCFalconPublicKey(falconPublicKeyParameters), new BCFalconPrivateKey(falconPrivateKeyParameters));
    }

    static {
        parameters.put(FalconParameterSpec.falcon_512.getName(), FalconParameters.falcon_512);
        parameters.put(FalconParameterSpec.falcon_1024.getName(), FalconParameters.falcon_1024);
    }

    public static class Falcon1024
    extends FalconKeyPairGeneratorSpi {
        public Falcon1024() {
            super(FalconParameters.falcon_1024);
        }
    }

    public static class Falcon512
    extends FalconKeyPairGeneratorSpi {
        public Falcon512() {
            super(FalconParameters.falcon_512);
        }
    }
}

