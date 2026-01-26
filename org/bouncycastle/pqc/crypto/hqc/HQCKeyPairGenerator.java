/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCEngine;
import org.bouncycastle.pqc.crypto.hqc.HQCKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;

public class HQCKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private HQCKeyGenerationParameters hqcKeyGenerationParameters;
    private SecureRandom random;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.hqcKeyGenerationParameters = (HQCKeyGenerationParameters)keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        HQCEngine hQCEngine = this.hqcKeyGenerationParameters.getParameters().getEngine();
        byte[] byArray = new byte[this.hqcKeyGenerationParameters.getParameters().getPublicKeyBytes()];
        byte[] byArray2 = new byte[this.hqcKeyGenerationParameters.getParameters().getSecretKeyBytes()];
        hQCEngine.genKeyPair(byArray, byArray2, this.random);
        HQCPublicKeyParameters hQCPublicKeyParameters = new HQCPublicKeyParameters(this.hqcKeyGenerationParameters.getParameters(), byArray);
        HQCPrivateKeyParameters hQCPrivateKeyParameters = new HQCPrivateKeyParameters(this.hqcKeyGenerationParameters.getParameters(), byArray2);
        return new AsymmetricCipherKeyPair(hQCPublicKeyParameters, hQCPrivateKeyParameters);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        return this.genKeyPair();
    }
}

