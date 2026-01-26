/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.newhope.BCNHPublicKey;

public class NHKeyPairGeneratorSpi
extends KeyPairGenerator {
    NHKeyPairGenerator engine = new NHKeyPairGenerator();
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public NHKeyPairGeneratorSpi() {
        super("NH");
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        if (n != 1024) {
            throw new IllegalArgumentException("strength must be 1024 bits");
        }
        this.engine.init(new KeyGenerationParameters(secureRandom, 1024));
        this.initialised = true;
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("parameter object not recognised");
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.engine.init(new KeyGenerationParameters(this.random, 1024));
            this.initialised = true;
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        NHPublicKeyParameters nHPublicKeyParameters = (NHPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        NHPrivateKeyParameters nHPrivateKeyParameters = (NHPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        return new KeyPair(new BCNHPublicKey(nHPublicKeyParameters), new BCNHPrivateKey(nHPrivateKeyParameters));
    }
}

