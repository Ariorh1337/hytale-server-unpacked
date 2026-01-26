/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.LMS;
import org.bouncycastle.pqc.crypto.lms.LMSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

public class LMSKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    LMSKeyGenerationParameters param;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.param = (LMSKeyGenerationParameters)keyGenerationParameters;
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        SecureRandom secureRandom = this.param.getRandom();
        byte[] byArray = new byte[16];
        secureRandom.nextBytes(byArray);
        LMSigParameters lMSigParameters = this.param.getParameters().getLMSigParam();
        byte[] byArray2 = new byte[lMSigParameters.getM()];
        secureRandom.nextBytes(byArray2);
        LMSPrivateKeyParameters lMSPrivateKeyParameters = LMS.generateKeys(lMSigParameters, this.param.getParameters().getLMOTSParam(), 0, byArray, byArray2);
        return new AsymmetricCipherKeyPair(lMSPrivateKeyParameters.getPublicKey(), lMSPrivateKeyParameters);
    }
}

