/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconNIST;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;

public class FalconKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private FalconKeyGenerationParameters params;
    private FalconNIST nist;
    private int pk_size;
    private int sk_size;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (FalconKeyGenerationParameters)keyGenerationParameters;
        SecureRandom secureRandom = keyGenerationParameters.getRandom();
        int n = ((FalconKeyGenerationParameters)keyGenerationParameters).getParameters().getLogN();
        int n2 = ((FalconKeyGenerationParameters)keyGenerationParameters).getParameters().getNonceLength();
        this.nist = new FalconNIST(n, n2, secureRandom);
        int n3 = 1 << n;
        int n4 = 8;
        if (n3 == 1024) {
            n4 = 5;
        } else if (n3 == 256 || n3 == 512) {
            n4 = 6;
        } else if (n3 == 64 || n3 == 128) {
            n4 = 7;
        }
        this.pk_size = 1 + 14 * n3 / 8;
        this.sk_size = 1 + 2 * n4 * n3 / 8 + n3;
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        byte[] byArray = new byte[this.pk_size];
        byte[] byArray2 = new byte[this.sk_size];
        byte[][] byArray3 = this.nist.crypto_sign_keypair(byArray, byArray2);
        FalconParameters falconParameters = this.params.getParameters();
        FalconPrivateKeyParameters falconPrivateKeyParameters = new FalconPrivateKeyParameters(falconParameters, byArray3[1], byArray3[2], byArray3[3], byArray3[0]);
        FalconPublicKeyParameters falconPublicKeyParameters = new FalconPublicKeyParameters(falconParameters, byArray3[0]);
        return new AsymmetricCipherKeyPair(falconPublicKeyParameters, falconPrivateKeyParameters);
    }
}

