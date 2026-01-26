/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.ntru;

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
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.ntru.BCNTRUPublicKey;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.bouncycastle.util.Strings;

public class NTRUKeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Map parameters = new HashMap();
    NTRUKeyGenerationParameters param;
    NTRUKeyPairGenerator engine = new NTRUKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public NTRUKeyPairGeneratorSpi() {
        super("NTRU");
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = NTRUKeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (string == null) {
            throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + algorithmParameterSpec);
        }
        this.param = new NTRUKeyGenerationParameters(secureRandom, (NTRUParameters)parameters.get(string));
        this.engine.init(this.param);
        this.initialised = true;
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof NTRUParameterSpec) {
            NTRUParameterSpec nTRUParameterSpec = (NTRUParameterSpec)algorithmParameterSpec;
            return nTRUParameterSpec.getName();
        }
        return Strings.toLowerCase(SpecUtil.getNameFrom(algorithmParameterSpec));
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new NTRUKeyGenerationParameters(this.random, NTRUParameters.ntruhps2048509);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        NTRUPublicKeyParameters nTRUPublicKeyParameters = (NTRUPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        NTRUPrivateKeyParameters nTRUPrivateKeyParameters = (NTRUPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCNTRUPublicKey(nTRUPublicKeyParameters), new BCNTRUPrivateKey(nTRUPrivateKeyParameters));
    }

    static {
        parameters.put(NTRUParameterSpec.ntruhps2048509.getName(), NTRUParameters.ntruhps2048509);
        parameters.put(NTRUParameterSpec.ntruhps2048677.getName(), NTRUParameters.ntruhps2048677);
        parameters.put(NTRUParameterSpec.ntruhps4096821.getName(), NTRUParameters.ntruhps4096821);
        parameters.put(NTRUParameterSpec.ntruhps40961229.getName(), NTRUParameters.ntruhps40961229);
        parameters.put(NTRUParameterSpec.ntruhrss701.getName(), NTRUParameters.ntruhrss701);
        parameters.put(NTRUParameterSpec.ntruhrss1373.getName(), NTRUParameters.ntruhrss1373);
    }
}

