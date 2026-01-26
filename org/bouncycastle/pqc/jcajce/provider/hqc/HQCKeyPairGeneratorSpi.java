/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.hqc.HQCKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCKeyPairGenerator;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPublicKey;
import org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;
import org.bouncycastle.util.Strings;

public class HQCKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    HQCKeyGenerationParameters param;
    HQCKeyPairGenerator engine = new HQCKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public HQCKeyPairGeneratorSpi() {
        super("HQC");
    }

    protected HQCKeyPairGeneratorSpi(HQCParameterSpec hQCParameterSpec) {
        super(Strings.toUpperCase(hQCParameterSpec.getName()));
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = HQCKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new HQCKeyGenerationParameters(secureRandom, (HQCParameters)parameters.get(string));
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof HQCParameterSpec) {
            HQCParameterSpec hQCParameterSpec = (HQCParameterSpec)algorithmParameterSpec;
            return hQCParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new HQCKeyGenerationParameters(this.random, HQCParameters.hqc128);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        HQCPublicKeyParameters hQCPublicKeyParameters = (HQCPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        HQCPrivateKeyParameters hQCPrivateKeyParameters = (HQCPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCHQCPublicKey(hQCPublicKeyParameters), new BCHQCPrivateKey(hQCPrivateKeyParameters));
    }

    static {
        parameters.put("hqc-128", HQCParameters.hqc128);
        parameters.put("hqc-192", HQCParameters.hqc192);
        parameters.put("hqc-256", HQCParameters.hqc256);
        parameters.put(HQCParameterSpec.hqc128.getName(), HQCParameters.hqc128);
        parameters.put(HQCParameterSpec.hqc192.getName(), HQCParameters.hqc192);
        parameters.put(HQCParameterSpec.hqc256.getName(), HQCParameters.hqc256);
    }

    public static class HQC128
    extends HQCKeyPairGeneratorSpi {
        public HQC128() throws NoSuchAlgorithmException {
            super(HQCParameterSpec.hqc128);
        }
    }

    public static class HQC192
    extends HQCKeyPairGeneratorSpi {
        public HQC192() throws NoSuchAlgorithmException {
            super(HQCParameterSpec.hqc192);
        }
    }

    public static class HQC256
    extends HQCKeyPairGeneratorSpi {
        public HQC256() throws NoSuchAlgorithmException {
            super(HQCParameterSpec.hqc256);
        }
    }
}

