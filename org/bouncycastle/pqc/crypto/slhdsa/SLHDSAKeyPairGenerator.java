/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.slhdsa.HT;
import org.bouncycastle.pqc.crypto.slhdsa.PK;
import org.bouncycastle.pqc.crypto.slhdsa.SK;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAEngine;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;

public class SLHDSAKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;
    private SLHDSAParameters parameters;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        this.parameters = ((SLHDSAKeyGenerationParameters)keyGenerationParameters).getParameters();
    }

    public AsymmetricCipherKeyPair internalGenerateKeyPair(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        return this.implGenerateKeyPair(this.parameters.getEngine(), byArray, byArray2, byArray3);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        SLHDSAEngine sLHDSAEngine = this.parameters.getEngine();
        byte[] byArray = this.sec_rand(sLHDSAEngine.N);
        byte[] byArray2 = this.sec_rand(sLHDSAEngine.N);
        byte[] byArray3 = this.sec_rand(sLHDSAEngine.N);
        return this.implGenerateKeyPair(sLHDSAEngine, byArray, byArray2, byArray3);
    }

    private AsymmetricCipherKeyPair implGenerateKeyPair(SLHDSAEngine sLHDSAEngine, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        SK sK = new SK(byArray, byArray2);
        sLHDSAEngine.init(byArray3);
        PK pK = new PK(byArray3, new HT((SLHDSAEngine)sLHDSAEngine, (byte[])sK.seed, (byte[])byArray3).htPubKey);
        return new AsymmetricCipherKeyPair(new SLHDSAPublicKeyParameters(this.parameters, pK), new SLHDSAPrivateKeyParameters(this.parameters, sK, pK));
    }

    private byte[] sec_rand(int n) {
        byte[] byArray = new byte[n];
        this.random.nextBytes(byArray);
        return byArray;
    }
}

