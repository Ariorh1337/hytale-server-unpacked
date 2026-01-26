/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEEngine;
import org.bouncycastle.pqc.crypto.bike.BIKEKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;

public class BIKEKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;
    private int r;
    private int l;
    private int L_BYTE;
    private int R_BYTE;
    private BIKEKeyGenerationParameters bikeKeyGenerationParameters;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.bikeKeyGenerationParameters = (BIKEKeyGenerationParameters)keyGenerationParameters;
        this.random = keyGenerationParameters.getRandom();
        this.r = this.bikeKeyGenerationParameters.getParameters().getR();
        this.l = this.bikeKeyGenerationParameters.getParameters().getL();
        this.L_BYTE = this.l / 8;
        this.R_BYTE = (this.r + 7) / 8;
    }

    private AsymmetricCipherKeyPair genKeyPair() {
        BIKEEngine bIKEEngine = this.bikeKeyGenerationParameters.getParameters().getEngine();
        byte[] byArray = new byte[this.R_BYTE];
        byte[] byArray2 = new byte[this.R_BYTE];
        byte[] byArray3 = new byte[this.R_BYTE];
        byte[] byArray4 = new byte[this.L_BYTE];
        bIKEEngine.genKeyPair(byArray, byArray2, byArray4, byArray3, this.random);
        BIKEPublicKeyParameters bIKEPublicKeyParameters = new BIKEPublicKeyParameters(this.bikeKeyGenerationParameters.getParameters(), byArray3);
        BIKEPrivateKeyParameters bIKEPrivateKeyParameters = new BIKEPrivateKeyParameters(this.bikeKeyGenerationParameters.getParameters(), byArray, byArray2, byArray4);
        return new AsymmetricCipherKeyPair(bIKEPublicKeyParameters, bIKEPrivateKeyParameters);
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        return this.genKeyPair();
    }
}

