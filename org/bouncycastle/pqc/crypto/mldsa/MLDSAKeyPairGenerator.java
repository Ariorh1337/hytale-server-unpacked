/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;

public class MLDSAKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private MLDSAParameters parameters;
    private SecureRandom random;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.parameters = ((MLDSAKeyGenerationParameters)keyGenerationParameters).getParameters();
        this.random = keyGenerationParameters.getRandom();
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        MLDSAEngine mLDSAEngine = this.parameters.getEngine(this.random);
        byte[][] byArray = mLDSAEngine.generateKeyPair();
        MLDSAPublicKeyParameters mLDSAPublicKeyParameters = new MLDSAPublicKeyParameters(this.parameters, byArray[0], byArray[6]);
        MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters = new MLDSAPrivateKeyParameters(this.parameters, byArray[0], byArray[1], byArray[2], byArray[3], byArray[4], byArray[5], byArray[6], byArray[7]);
        return new AsymmetricCipherKeyPair(mLDSAPublicKeyParameters, mLDSAPrivateKeyParameters);
    }
}

