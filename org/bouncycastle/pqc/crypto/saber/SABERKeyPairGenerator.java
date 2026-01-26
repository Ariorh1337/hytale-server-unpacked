/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.saber.SABEREngine;
import org.bouncycastle.pqc.crypto.saber.SABERKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;

public class SABERKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SABERKeyGenerationParameters saberParams;
    private int l;
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.saberParams = (SABERKeyGenerationParameters)keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
        this.l = this.saberParams.getParameters().getL();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        SABEREngine sABEREngine = this.saberParams.getParameters().getEngine();
        byte[] byArray = new byte[sABEREngine.getPrivateKeySize()];
        byte[] byArray2 = new byte[sABEREngine.getPublicKeySize()];
        sABEREngine.crypto_kem_keypair(byArray2, byArray, this.random);
        SABERPublicKeyParameters sABERPublicKeyParameters = new SABERPublicKeyParameters(this.saberParams.getParameters(), byArray2);
        SABERPrivateKeyParameters sABERPrivateKeyParameters = new SABERPrivateKeyParameters(this.saberParams.getParameters(), byArray);
        return new AsymmetricCipherKeyPair(sABERPublicKeyParameters, sABERPrivateKeyParameters);
    }

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.initialize(keyGenerationParameters);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        return this.genKeyPair();
    }
}

