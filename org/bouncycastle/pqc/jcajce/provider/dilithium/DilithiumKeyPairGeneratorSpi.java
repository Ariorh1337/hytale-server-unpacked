/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.dilithium;

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
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyPairGenerator;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.dilithium.BCDilithiumPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.dilithium.BCDilithiumPublicKey;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.util.Strings;

public class DilithiumKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    private final DilithiumParameters dilithiumParameters;
    DilithiumKeyGenerationParameters param;
    DilithiumKeyPairGenerator engine = new DilithiumKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public DilithiumKeyPairGeneratorSpi() {
        super("DILITHIUM");
        this.dilithiumParameters = null;
    }

    protected DilithiumKeyPairGeneratorSpi(DilithiumParameters dilithiumParameters) {
        super(Strings.toUpperCase(dilithiumParameters.getName()));
        this.dilithiumParameters = dilithiumParameters;
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = DilithiumKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string != null && parameters.containsKey(string)) {
            DilithiumParameters dilithiumParameters = (DilithiumParameters)parameters.get(string);
            this.param = new DilithiumKeyGenerationParameters(secureRandom, dilithiumParameters);
            if (this.dilithiumParameters != null && !dilithiumParameters.getName().equals(this.dilithiumParameters.getName())) {
                throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.dilithiumParameters.getName()));
            }
        } else {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof DilithiumParameterSpec) {
            DilithiumParameterSpec dilithiumParameterSpec = (DilithiumParameterSpec)algorithmParameterSpec;
            return dilithiumParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = this.dilithiumParameters != null ? new DilithiumKeyGenerationParameters(this.random, this.dilithiumParameters) : new DilithiumKeyGenerationParameters(this.random, DilithiumParameters.dilithium3);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        DilithiumPublicKeyParameters dilithiumPublicKeyParameters = (DilithiumPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        DilithiumPrivateKeyParameters dilithiumPrivateKeyParameters = (DilithiumPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCDilithiumPublicKey(dilithiumPublicKeyParameters), new BCDilithiumPrivateKey(dilithiumPrivateKeyParameters));
    }

    static {
        parameters.put(DilithiumParameterSpec.dilithium2.getName(), DilithiumParameters.dilithium2);
        parameters.put(DilithiumParameterSpec.dilithium3.getName(), DilithiumParameters.dilithium3);
        parameters.put(DilithiumParameterSpec.dilithium5.getName(), DilithiumParameters.dilithium5);
    }

    public static class Base2
    extends DilithiumKeyPairGeneratorSpi {
        public Base2() throws NoSuchAlgorithmException {
            super(DilithiumParameters.dilithium2);
        }
    }

    public static class Base3
    extends DilithiumKeyPairGeneratorSpi {
        public Base3() throws NoSuchAlgorithmException {
            super(DilithiumParameters.dilithium3);
        }
    }

    public static class Base5
    extends DilithiumKeyPairGeneratorSpi {
        public Base5() throws NoSuchAlgorithmException {
            super(DilithiumParameters.dilithium5);
        }
    }
}

