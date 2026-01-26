/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.HT;
import org.bouncycastle.pqc.crypto.sphincsplus.PK;
import org.bouncycastle.pqc.crypto.sphincsplus.SK;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusEngine;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;

public class SPHINCSPlusKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;
    private SPHINCSPlusParameters parameters;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        this.parameters = ((SPHINCSPlusKeyGenerationParameters)keyGenerationParameters).getParameters();
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        SK sK;
        byte[] byArray;
        Object object;
        SPHINCSPlusEngine sPHINCSPlusEngine = this.parameters.getEngine();
        if (sPHINCSPlusEngine instanceof SPHINCSPlusEngine.HarakaSEngine) {
            object = this.sec_rand(sPHINCSPlusEngine.N * 3);
            byte[] byArray2 = new byte[sPHINCSPlusEngine.N];
            byte[] byArray3 = new byte[sPHINCSPlusEngine.N];
            byArray = new byte[sPHINCSPlusEngine.N];
            System.arraycopy(object, 0, byArray2, 0, sPHINCSPlusEngine.N);
            System.arraycopy(object, sPHINCSPlusEngine.N, byArray3, 0, sPHINCSPlusEngine.N);
            System.arraycopy(object, sPHINCSPlusEngine.N << 1, byArray, 0, sPHINCSPlusEngine.N);
            sK = new SK(byArray2, byArray3);
        } else {
            sK = new SK(this.sec_rand(sPHINCSPlusEngine.N), this.sec_rand(sPHINCSPlusEngine.N));
            byArray = this.sec_rand(sPHINCSPlusEngine.N);
        }
        sPHINCSPlusEngine.init(byArray);
        object = new PK(byArray, new HT((SPHINCSPlusEngine)sPHINCSPlusEngine, (byte[])sK.seed, (byte[])byArray).htPubKey);
        return new AsymmetricCipherKeyPair(new SPHINCSPlusPublicKeyParameters(this.parameters, (PK)object), new SPHINCSPlusPrivateKeyParameters(this.parameters, sK, (PK)object));
    }

    private byte[] sec_rand(int n) {
        byte[] byArray = new byte[n];
        this.random.nextBytes(byArray);
        return byArray;
    }
}

