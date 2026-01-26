/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.Utils;
import org.bouncycastle.util.Arrays;

public class NTRULPRimeKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private NTRULPRimeKeyGenerationParameters params;

    public NTRULPRimeKeyGenerationParameters getParams() {
        return this.params;
    }

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.params = (NTRULPRimeKeyGenerationParameters)keyGenerationParameters;
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        int n = this.params.getNtrulprParams().getP();
        int n2 = this.params.getNtrulprParams().getQ();
        int n3 = this.params.getNtrulprParams().getW();
        byte[] byArray = new byte[32];
        this.params.getRandom().nextBytes(byArray);
        short[] sArray = new short[n];
        Utils.generatePolynomialInRQFromSeed(sArray, byArray, n, n2);
        byte[] byArray2 = new byte[n];
        Utils.getRandomShortPolynomial(this.params.getRandom(), byArray2, n, n3);
        short[] sArray2 = new short[n];
        Utils.multiplicationInRQ(sArray2, sArray, byArray2, n, n2);
        short[] sArray3 = new short[n];
        Utils.roundPolynomial(sArray3, sArray2);
        byte[] byArray3 = new byte[this.params.getNtrulprParams().getPublicKeyBytes() - 32];
        Utils.getRoundedEncodedPolynomial(byArray3, sArray3, n, n2);
        NTRULPRimePublicKeyParameters nTRULPRimePublicKeyParameters = new NTRULPRimePublicKeyParameters(this.params.getNtrulprParams(), byArray, byArray3);
        byte[] byArray4 = new byte[(n + 3) / 4];
        Utils.getEncodedSmallPolynomial(byArray4, byArray2, n);
        byte[] byArray5 = new byte[32];
        this.params.getRandom().nextBytes(byArray5);
        byte[] byArray6 = new byte[]{4};
        byte[] byArray7 = Utils.getHashWithPrefix(byArray6, nTRULPRimePublicKeyParameters.getEncoded());
        NTRULPRimePrivateKeyParameters nTRULPRimePrivateKeyParameters = new NTRULPRimePrivateKeyParameters(this.params.getNtrulprParams(), byArray4, nTRULPRimePublicKeyParameters.getEncoded(), byArray5, Arrays.copyOfRange(byArray7, 0, byArray7.length / 2));
        return new AsymmetricCipherKeyPair(nTRULPRimePublicKeyParameters, nTRULPRimePrivateKeyParameters);
    }
}

