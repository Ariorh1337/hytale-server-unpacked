/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;

public class MLKEMKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private MLKEMParameters mlkemParams;
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.mlkemParams = ((MLKEMKeyGenerationParameters)keyGenerationParameters).getParameters();
        this.random = keyGenerationParameters.getRandom();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        MLKEMEngine mLKEMEngine = this.mlkemParams.getEngine();
        mLKEMEngine.init(this.random);
        byte[][] byArray = mLKEMEngine.generateKemKeyPair();
        MLKEMPublicKeyParameters mLKEMPublicKeyParameters = new MLKEMPublicKeyParameters(this.mlkemParams, byArray[0], byArray[1]);
        MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters = new MLKEMPrivateKeyParameters(this.mlkemParams, byArray[2], byArray[3], byArray[4], byArray[0], byArray[1], byArray[5]);
        return new AsymmetricCipherKeyPair(mLKEMPublicKeyParameters, mLKEMPrivateKeyParameters);
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

