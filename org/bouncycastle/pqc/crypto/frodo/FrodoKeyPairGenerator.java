/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoEngine;
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;

public class FrodoKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private FrodoKeyGenerationParameters frodoParams;
    private int n;
    private int D;
    private int B;
    private SecureRandom random;

    private void initialize(KeyGenerationParameters keyGenerationParameters) {
        this.frodoParams = (FrodoKeyGenerationParameters)keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
        this.n = this.frodoParams.getParameters().getN();
        this.D = this.frodoParams.getParameters().getD();
        this.B = this.frodoParams.getParameters().getB();
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        FrodoEngine frodoEngine = this.frodoParams.getParameters().getEngine();
        byte[] byArray = new byte[frodoEngine.getPrivateKeySize()];
        byte[] byArray2 = new byte[frodoEngine.getPublicKeySize()];
        frodoEngine.kem_keypair(byArray2, byArray, this.random);
        FrodoPublicKeyParameters frodoPublicKeyParameters = new FrodoPublicKeyParameters(this.frodoParams.getParameters(), byArray2);
        FrodoPrivateKeyParameters frodoPrivateKeyParameters = new FrodoPrivateKeyParameters(this.frodoParams.getParameters(), byArray);
        return new AsymmetricCipherKeyPair(frodoPublicKeyParameters, frodoPrivateKeyParameters);
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

