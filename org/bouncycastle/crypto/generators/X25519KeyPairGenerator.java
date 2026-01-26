/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;

public class X25519KeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("X25519KeyGen", 128, null, CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        X25519PrivateKeyParameters x25519PrivateKeyParameters = new X25519PrivateKeyParameters(this.random);
        X25519PublicKeyParameters x25519PublicKeyParameters = x25519PrivateKeyParameters.generatePublicKey();
        return new AsymmetricCipherKeyPair(x25519PublicKeyParameters, x25519PrivateKeyParameters);
    }
}

