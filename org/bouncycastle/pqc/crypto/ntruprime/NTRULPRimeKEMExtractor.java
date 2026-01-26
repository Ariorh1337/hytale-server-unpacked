/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.Utils;
import org.bouncycastle.util.Arrays;

public class NTRULPRimeKEMExtractor
implements EncapsulatedSecretExtractor {
    private final NTRULPRimePrivateKeyParameters privateKey;

    public NTRULPRimeKEMExtractor(NTRULPRimePrivateKeyParameters nTRULPRimePrivateKeyParameters) {
        this.privateKey = nTRULPRimePrivateKeyParameters;
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        NTRULPRimeParameters nTRULPRimeParameters = this.privateKey.getParameters();
        int n = nTRULPRimeParameters.getP();
        int n2 = nTRULPRimeParameters.getQ();
        int n3 = nTRULPRimeParameters.getW();
        int n4 = nTRULPRimeParameters.getRoundedPolynomialBytes();
        int n5 = nTRULPRimeParameters.getTau0();
        int n6 = nTRULPRimeParameters.getTau1();
        int n7 = nTRULPRimeParameters.getTau2();
        int n8 = nTRULPRimeParameters.getTau3();
        byte[] byArray2 = new byte[n];
        Utils.getDecodedSmallPolynomial(byArray2, this.privateKey.getEncoded(), n);
        byte[] byArray3 = new byte[n4];
        System.arraycopy(byArray, 0, byArray3, 0, n4);
        short[] sArray = new short[n];
        Utils.getRoundedDecodedPolynomial(sArray, byArray3, n, n2);
        byte[] byArray4 = new byte[128];
        System.arraycopy(byArray, n4, byArray4, 0, byArray4.length);
        byte[] byArray5 = new byte[256];
        Utils.getTopDecodedPolynomial(byArray5, byArray4);
        short[] sArray2 = new short[n];
        Utils.multiplicationInRQ(sArray2, sArray, byArray2, n, n2);
        byte[] byArray6 = new byte[256];
        Utils.right(byArray6, sArray2, byArray5, n2, n3, n7, n8);
        byte[] byArray7 = new byte[32];
        Utils.getEncodedInputs(byArray7, byArray6);
        byte[] byArray8 = new byte[nTRULPRimeParameters.getPublicKeyBytes() - 32];
        System.arraycopy(this.privateKey.getPk(), 32, byArray8, 0, byArray8.length);
        short[] sArray3 = new short[n];
        Utils.getRoundedDecodedPolynomial(sArray3, byArray8, n, n2);
        byte[] byArray9 = new byte[32];
        System.arraycopy(this.privateKey.getPk(), 0, byArray9, 0, byArray9.length);
        short[] sArray4 = new short[n];
        Utils.generatePolynomialInRQFromSeed(sArray4, byArray9, n, n2);
        byte[] byArray10 = new byte[]{5};
        byte[] byArray11 = Utils.getHashWithPrefix(byArray10, byArray7);
        byte[] byArray12 = Arrays.copyOfRange(byArray11, 0, byArray11.length / 2);
        int[] nArray = new int[n];
        Utils.expand(nArray, byArray12);
        byte[] byArray13 = new byte[n];
        Utils.sortGenerateShortPolynomial(byArray13, nArray, n, n3);
        short[] sArray5 = new short[n];
        Utils.multiplicationInRQ(sArray5, sArray4, byArray13, n, n2);
        short[] sArray6 = new short[n];
        Utils.roundPolynomial(sArray6, sArray5);
        byte[] byArray14 = new byte[n4];
        Utils.getRoundedEncodedPolynomial(byArray14, sArray6, n, n2);
        short[] sArray7 = new short[n];
        Utils.multiplicationInRQ(sArray7, sArray3, byArray13, n, n2);
        byte[] byArray15 = new byte[256];
        Utils.top(byArray15, sArray7, byArray6, n2, n5, n6);
        byte[] byArray16 = new byte[128];
        Utils.getTopEncodedPolynomial(byArray16, byArray5);
        byte[] byArray17 = new byte[byArray7.length + this.privateKey.getHash().length];
        System.arraycopy(byArray7, 0, byArray17, 0, byArray7.length);
        System.arraycopy(this.privateKey.getHash(), 0, byArray17, byArray7.length, this.privateKey.getHash().length);
        byte[] byArray18 = new byte[]{2};
        byte[] byArray19 = Utils.getHashWithPrefix(byArray18, byArray17);
        byte[] byArray20 = new byte[byArray3.length + byArray4.length + byArray19.length / 2];
        System.arraycopy(byArray3, 0, byArray20, 0, byArray3.length);
        System.arraycopy(byArray4, 0, byArray20, byArray3.length, byArray4.length);
        System.arraycopy(byArray19, 0, byArray20, byArray3.length + byArray4.length, byArray19.length / 2);
        int n9 = Arrays.areEqual(byArray, byArray20) ? 0 : -1;
        Utils.updateDiffMask(byArray7, this.privateKey.getRho(), n9);
        byte[] byArray21 = new byte[byArray7.length + byArray20.length];
        System.arraycopy(byArray7, 0, byArray21, 0, byArray7.length);
        System.arraycopy(byArray20, 0, byArray21, byArray7.length, byArray20.length);
        byte[] byArray22 = new byte[]{1};
        byte[] byArray23 = Utils.getHashWithPrefix(byArray22, byArray21);
        return Arrays.copyOfRange(byArray23, 0, nTRULPRimeParameters.getSessionKeySize() / 8);
    }

    @Override
    public int getEncapsulationLength() {
        return this.privateKey.getParameters().getRoundedPolynomialBytes() + 128 + 32;
    }
}

