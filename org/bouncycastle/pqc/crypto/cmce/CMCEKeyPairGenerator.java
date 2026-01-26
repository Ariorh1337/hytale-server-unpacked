/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEEngine;
import org.bouncycastle.pqc.crypto.cmce.CMCEKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;

public class CMCEKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private CMCEKeyGenerationParameters cmceParams;
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.cmceParams = (CMCEKeyGenerationParameters)keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        CMCEEngine cMCEEngine = this.cmceParams.getParameters().getEngine();
        byte[] byArray = new byte[cMCEEngine.getPrivateKeySize()];
        byte[] byArray2 = new byte[cMCEEngine.getPublicKeySize()];
        cMCEEngine.kem_keypair(byArray2, byArray, this.random);
        CMCEPublicKeyParameters cMCEPublicKeyParameters = new CMCEPublicKeyParameters(this.cmceParams.getParameters(), byArray2);
        CMCEPrivateKeyParameters cMCEPrivateKeyParameters = new CMCEPrivateKeyParameters(this.cmceParams.getParameters(), byArray);
        return new AsymmetricCipherKeyPair(cMCEPublicKeyParameters, cMCEPrivateKeyParameters);
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

